package com.aquamorph.frcmanager.fragments;

import android.os.Bundle;

import com.aquamorph.frcmanager.R;

public class SettingsFragment  extends android.preference.PreferenceFragment {
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}
}