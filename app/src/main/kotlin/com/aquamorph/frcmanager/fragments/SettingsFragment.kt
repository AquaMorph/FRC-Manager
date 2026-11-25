package com.aquamorph.frcmanager.fragments

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.utils.scrollBehindNavigationBar

/**
 * Settings fragment for all user settings.
 *
 * @author Christian Colglazier
 * @version 1/15/2020
 */
class SettingsFragment : PreferenceFragmentCompat(),
        SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView.scrollBehindNavigationBar()
    }

    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        addPreferencesFromResource(R.xml.settings)

        // Hide system theme options for older devices
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            val theme = preferenceScreen.findPreference<ListPreference>("theme")
            theme!!.entries = resources.getStringArray(R.array.theme).copyOfRange(0, 3)
            theme.entryValues = resources.getStringArray(R.array.themeValues).copyOfRange(0, 3)
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        if ("theme" == key) {
            requireActivity().recreate()
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences!!.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences!!.unregisterOnSharedPreferenceChangeListener(this)
    }
}
