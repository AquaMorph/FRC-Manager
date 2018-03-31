package com.aquamorph.frcmanager.activities

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem

import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.fragments.SettingsFragment
import com.aquamorph.frcmanager.utils.Logging

/**
 * Settings activity to take users input about how the app should be set up as well as
 * general information about the app.
 *
 * @author Christian Colglazier
 * @version 3/31/2018
 */
class Settings : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivity.theme(this)
        setContentView(R.layout.settings)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        if (toolbar != null) {
            toolbar.title = "Settings"
        }
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        fragmentManager.beginTransaction().replace(R.id.content_frame,
                SettingsFragment()).commit()
        MainActivity.theme(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> this.finish()
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        Logging.info(this, "Settings Changed", 0)
        if (key == "theme") {
            Logging.info(this, "Theme Changed", 0)
            this.recreate()
        }
    }
}