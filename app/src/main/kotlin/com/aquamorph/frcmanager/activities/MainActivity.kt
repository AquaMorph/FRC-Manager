package com.aquamorph.frcmanager.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceManager
import androidx.viewpager.widget.ViewPager
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.adapters.SectionsPagerAdapter
import com.aquamorph.frcmanager.network.DataLoader
import com.aquamorph.frcmanager.utils.Constants
import com.aquamorph.frcmanager.utils.Logging
import com.google.android.material.tabs.TabLayout

/**
 * Default activity of the app.
 *
 * @author Christian Colglazier
 * @version 1/16/2020
 */
class MainActivity : AppCompatActivity(), OnSharedPreferenceChangeListener {
    private lateinit var eventName: String
    private lateinit var teamRank: String
    private lateinit var teamRecord: String
    private lateinit var nextMatch: String
    private lateinit var toolbarText: String
    lateinit var dataLoader: DataLoader
    private lateinit var mViewPager: ViewPager
    private lateinit var tabLayout: TabLayout

    /**
     * getTeamNumber() returns the string tag for Blue Alliance requests of the current
     * team number.
     *
     * @return team number string
     */
    val teamNumber: String
        get() = "frc" + DataLoader.teamNumber

    /**
     * getAppTitle() returns the text for the app.
     * @return title for the app
     */
    private val appTitle: String
        get() = String.format("%s - %s", DataLoader.teamNumber, eventName)

    /**
     * getAppSubTitle() returns the subtitle string for the toolbar with the event name,
     * team number and current rank if available.
     *
     * @return subtitle string
     */
    private val appSubTitle: String
        get() = if (teamRank == "") {
            ""
        } else {
            toolbarText.replace("\$r", teamRank)
                    .replace("\$R", teamRecord)
                    .replace("\$n", nextMatch)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.registerOnSharedPreferenceChangeListener(this@MainActivity)
        DataLoader.teamNumber = prefs.getString("teamNumber", "")!!
        eventName = prefs.getString("eventShortName", "")!!
        teamRank = prefs.getString("teamRank", "")!!
        teamRecord = prefs.getString("teamRecord", "")!!
        nextMatch = prefs.getString("nextMatch", "")!!
        toolbarText = prefs.getString("toolbarText", "")!!
        DataLoader.eventKey = prefs.getString("eventKey", "")!!
        DataLoader.districtKey = prefs.getString("districtKey", "")!!
        if (DataLoader.teamNumber == "") openSetup()
        listener()
        theme(this)
        super.onCreate(savedInstanceState)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setContentView(R.layout.activity_main)
        Logging.info(this, "onConfigurationChanged", 2)
        listener()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> openSettings()
            R.id.refresh_all -> refresh()
            else -> refresh()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * openSetup() launches the set up activity.
     */
    private fun openSetup() {
        startActivity(Intent(this, Setup::class.java))
    }

    /**
     * openSettings() launches the settings activity.
     */
    private fun openSettings() {
        startActivity(Intent(this, Settings::class.java))
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        eventName = sharedPreferences.getString("eventShortName", "North Carolina")!!
        teamRank = sharedPreferences.getString("teamRank", "")!!
        teamRecord = sharedPreferences.getString("teamRecord", "")!!
        nextMatch = sharedPreferences.getString("nextMatch", "")!!
        toolbarText = sharedPreferences.getString("toolbarText", "")!!

        when (key) {
            "eventKey" -> DataLoader.eventKey = sharedPreferences.getString("eventKey", "")!!
            "teamNumber" -> DataLoader.teamNumber = sharedPreferences.getString("teamNumber", "0000")!!
            "districtKey" -> DataLoader.districtKey = sharedPreferences.getString("districtKey", "")!!
        }

        if (supportActionBar != null) {
            supportActionBar!!.title = appTitle
            supportActionBar!!.subtitle = appSubTitle
        }
        if (key == "theme") {
            this.recreate()
        }
    }

    /**
     * listener() connects layout to view.
     */
    private fun listener() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = appTitle
        supportActionBar!!.subtitle = appSubTitle

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container)
        tabLayout = findViewById(R.id.tabs)
        tabLayout.setupWithViewPager(mViewPager)
        tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager,
                mViewPager, tabLayout, this)
        dataLoader = DataLoader()
        mViewPager.offscreenPageLimit = Constants.MAX_NUMBER_OF_TABS
        mViewPager.adapter = mSectionsPagerAdapter
        try {
            refresh()
        } catch (e: Exception) {
            Logging.error(this, e.toString(), 0)
        }
    }

    companion object {

        var appTheme = Constants.Theme.LIGHT

        @SuppressLint("StaticFieldLeak")
        private lateinit var mSectionsPagerAdapter: SectionsPagerAdapter

        /**
         * theme() sets the app theme based on user selection from settings.
         *
         * @param activity
         */
        fun theme(activity: Activity) {
            val currentNightMode = activity.applicationContext.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
            appTheme = when (prefs.getString("theme", "")) {
                "amoled" -> Constants.Theme.BATTERY_SAVER
                "dark" -> Constants.Theme.DARK
                "system" -> when (currentNightMode) {
                    Configuration.UI_MODE_NIGHT_YES -> Constants.Theme.DARK
                    else -> Constants.Theme.LIGHT
                }
                else -> Constants.Theme.LIGHT
            }
            when (appTheme) {
                Constants.Theme.LIGHT -> activity.setTheme(R.style.LightTheme)
                Constants.Theme.DARK -> activity.setTheme(R.style.DarkTheme)
                Constants.Theme.BATTERY_SAVER -> activity.setTheme(R.style.OMOLEDTheme)
            }
        }

        fun refresh() {
            mSectionsPagerAdapter.refreshAll()
        }
    }
}
