package com.aquamorph.frcmanager.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceFragment

import com.aquamorph.frcmanager.R

/**
 * Settings fragment for all user settings.
 *
 * @author Christian Colglazier
 * @version 3/31/2018
 */
class SettingsFragment : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.settings)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if ("theme" == key) {
            activity.recreate()
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }
}