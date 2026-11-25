package com.aquamorph.frcmanager.activities

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.preference.PreferenceManager
import androidx.viewpager2.widget.ViewPager2
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.adapters.SectionsPagerAdapter
import com.aquamorph.frcmanager.models.tba.Rank
import com.aquamorph.frcmanager.network.EventState
import com.aquamorph.frcmanager.network.EventViewModel
import com.aquamorph.frcmanager.network.FeedNotice
import com.aquamorph.frcmanager.network.NetworkCheck
import com.aquamorph.frcmanager.network.eventDisplay
import com.aquamorph.frcmanager.network.eventSelection
import com.aquamorph.frcmanager.utils.Constants
import com.aquamorph.frcmanager.utils.TabJumpTransition
import com.aquamorph.frcmanager.utils.applyEdgeToEdge
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var prefs: SharedPreferences
    private lateinit var model: EventViewModel
    private lateinit var tabMediator: TabLayoutMediator
    private lateinit var tabJumpTransition: TabJumpTransition
    private var appliedTheme: String? = null
    private var shownFeedNotice: FeedNotice? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        appliedTheme = prefs.getString("theme", "")
        theme(this)
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_main)
        applyEdgeToEdge()
        setSupportActionBar(findViewById<Toolbar>(R.id.toolbar))

        model = ViewModelProvider(this, EventViewModel.factory(applicationContext))[EventViewModel::class.java]
        syncPreferences()
        val pager = findViewById<ViewPager2>(R.id.container)
        val tabs = findViewById<TabLayout>(R.id.tabs)
        val sections = SectionsPagerAdapter(this)
        sections.updateTabs(model.state.value.selection)
        pager.adapter = sections
        tabMediator = TabLayoutMediator(tabs, pager) { tab, position ->
            tab.text = sections.getPageTitle(position)
        }
        tabMediator.attach()
        tabJumpTransition = TabJumpTransition(tabs, pager)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.state.collect { state ->
                    sections.updateTabs(state.selection)
                    updateToolbar(state)
                    showFeedNotice(state.feedNotice)
                }
            }
        }

        if (savedInstanceState == null && prefs.eventDisplay().teamNumber.isEmpty()) {
            startActivity(Intent(this, Setup::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        prefs.registerOnSharedPreferenceChangeListener(this)
        syncPreferences()
    }

    override fun onPause() {
        prefs.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    override fun onDestroy() {
        tabJumpTransition.detach()
        tabMediator.detach()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> startActivity(Intent(this, Settings::class.java))
            R.id.refresh_all -> refresh()
            R.id.actionDirections -> showMap(prefs.getString("eventAddress", "").orEmpty())
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        syncPreferences()
    }

    private fun syncPreferences() {
        val selectedTheme = prefs.getString("theme", "")
        if (appliedTheme != selectedTheme) {
            appliedTheme = selectedTheme
            recreate()
            return
        }
        val selection = prefs.eventSelection()
        if (selection != model.state.value.selection) checkConnection(selection.eventKey)
        model.configure(selection, prefs.eventDisplay())
        updateToolbar(model.state.value)
    }

    internal fun refresh() {
        val selection = prefs.eventSelection()
        checkConnection(selection.eventKey)
        shownFeedNotice = null
        model.refresh(selection)
    }

    private fun checkConnection(eventKey: String) {
        if (eventKey.isNotEmpty() && !NetworkCheck.hasNetwork(this)) {
            Snackbar.make(findViewById(R.id.main_content), R.string.noConnectionMessage, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun updateToolbar(state: EventState) {
        val teamNumber = state.display.teamNumber
        val eventName = prefs.getString("eventShortName", "").orEmpty()
        supportActionBar?.title = "$teamNumber - $eventName"
        val rank = state.rankings.items.firstOrNull()?.rankings?.firstOrNull { it.teamKey == "frc$teamNumber" }
        supportActionBar?.subtitle = if (rank == null) "" else {
            val nextMatch = state.matches.items.asSequence().filterNot { it.hasResult }.minOrNull()
            val nextText = nextMatch?.let { "Playing %S-%s".format(it.compLevel, it.matchNumber) }.orEmpty()
            prefs.getString("toolbarText", getString(R.string.toolBarDefault)).orEmpty()
                .replace("\$r", rank.rank.toString())
                .replace("\$R", Rank.recordToString(rank.record))
                .replace("\$n", nextText)
        }
    }

    private fun showFeedNotice(notice: FeedNotice?) {
        if (notice == shownFeedNotice) return
        shownFeedNotice = notice
        val message = when (notice) {
            FeedNotice.FIRST_DOWN -> R.string.firstServerDown
            FeedNotice.EVENT_DOWN -> R.string.eventServerDown
            null -> return
        }
        Snackbar.make(findViewById(R.id.main_content), message, Snackbar.LENGTH_LONG).show()
    }

    private fun showMap(location: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr=%s".format(location)))
        startActivity(intent)
    }

    companion object {
        var appTheme = Constants.Theme.LIGHT
            private set

        fun theme(activity: Activity) {
            val currentNightMode = activity.applicationContext.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
            val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
            appTheme = when (prefs.getString("theme", "")) {
                "amoled" -> Constants.Theme.BATTERY_SAVER
                "dark" -> Constants.Theme.DARK
                "system" -> if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                    Constants.Theme.DARK
                } else {
                    Constants.Theme.LIGHT
                }
                else -> Constants.Theme.LIGHT
            }
            when (appTheme) {
                Constants.Theme.LIGHT -> {
                    activity.setTheme(R.style.LightTheme)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                Constants.Theme.DARK -> {
                    activity.setTheme(R.style.DarkTheme)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
                Constants.Theme.BATTERY_SAVER -> {
                    activity.setTheme(R.style.OMOLEDTheme)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }
        }
    }
}
