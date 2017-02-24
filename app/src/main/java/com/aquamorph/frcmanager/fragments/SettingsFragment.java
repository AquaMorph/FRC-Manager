package com.aquamorph.frcmanager.fragments;

import android.os.Bundle;

import com.aquamorph.frcmanager.R;

/**
 * Settings fragment for all user settings.
 *
 * @author Christian Colglazier
 * @version 3/29/2016
 */
public class SettingsFragment  extends android.preference.PreferenceFragment {

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}
}