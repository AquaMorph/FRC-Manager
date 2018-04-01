package com.aquamorph.frcmanager.activities

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.content.res.Configuration
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem

import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.adapters.SectionsPagerAdapter
import com.aquamorph.frcmanager.network.DataLoader
import com.aquamorph.frcmanager.utils.Constants
import com.aquamorph.frcmanager.utils.Logging

/**
 * Default activity of the app.
 *
 * @author Christian Colglazier
 * @version 3/29/2016
 */
class MainActivity : AppCompatActivity(), OnSharedPreferenceChangeListener {
    private var eventName: String? = null
    private var teamRank: String? = null
    private var teamRecord: String? = null
    lateinit var dataLoader: DataLoader
    private lateinit var mViewPager: ViewPager
    private var tabLayout: TabLayout? = null

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
    val appTitle: String
        get() = String.format("%s - %s", DataLoader.teamNumber,
                shorten(eventName, Constants.MAX_EVENT_TITLE_LENGTH))

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
            String.format("Rank #%s %s", teamRank, teamRecord)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.registerOnSharedPreferenceChangeListener(this@MainActivity)
        DataLoader.teamNumber = prefs.getString("teamNumber", "")
        eventName = prefs.getString("eventShortName", "")
        teamRank = prefs.getString("teamRank", "")
        teamRecord = prefs.getString("teamRecord", "")
        DataLoader.eventKey = prefs.getString("eventKey", "")


        if (DataLoader.teamNumber == "") openSetup()

        listener()
        theme(this)
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
            R.id.refresh_all -> mSectionsPagerAdapter!!.refreshAll(false)
            else -> mSectionsPagerAdapter!!.refreshAll(false)
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * openSetup() launches the set up activity.
     */
    private fun openSetup() {
        val intent = Intent(this, Setup::class.java)
        startActivity(intent)
    }

    /**
     * openSettings() launches the settings activity.
     */
    fun openSettings() {
        val intent = Intent(this, Settings::class.java)
        startActivity(intent)
    }

    /**
     * shorten() returns a shortened string with ... at the end.
     *
     * @param text   to be shortened
     * @param amount length to shorten
     * @return shorten string with ... at the end
     */
    fun shorten(text: String?, amount: Int): String {
        return if (text!!.length > amount) {
            text.substring(0, amount) + "..."
        } else {
            text
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        eventName = sharedPreferences.getString("eventShortName", "North Carolina")
        teamRank = sharedPreferences.getString("teamRank", "")
        teamRecord = sharedPreferences.getString("teamRecord", "")
        when (key) {
            "eventKey" -> DataLoader.eventKey = sharedPreferences.getString("eventKey", "")
            "teamNumber" -> DataLoader.teamNumber = sharedPreferences.getString("teamNumber", "0000")
            else -> {
            }
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
        if (tabLayout != null) {
            tabLayout!!.setupWithViewPager(mViewPager)
            tabLayout!!.tabMode = TabLayout.MODE_SCROLLABLE
        }
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager,
                mViewPager, tabLayout!!, this)
        dataLoader = DataLoader(this, mSectionsPagerAdapter!!)
        mViewPager.offscreenPageLimit = mSectionsPagerAdapter!!.count
        mViewPager.adapter = mSectionsPagerAdapter
        refreshData(false)
    }

    companion object {

        private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

        /**
         * theme() sets the app theme based on user selection from settings.
         *
         * @param activity
         */
        fun theme(activity: Activity) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
            when (prefs.getString("theme", "")) {
                "amoled" -> activity.setTheme(R.style.OMOLEDTheme)
                "dark" -> activity.setTheme(R.style.DarkTheme)
                else -> activity.setTheme(R.style.LightTheme)
            }
        }

        fun refreshData(force: Boolean) {
            mSectionsPagerAdapter!!.refrestData(force)
        }

        fun refresh() {
            mSectionsPagerAdapter!!.refreshAll(false)
        }

        fun refresh(force: Boolean?) {
            mSectionsPagerAdapter!!.refreshAll(force!!)
        }
    }
}
