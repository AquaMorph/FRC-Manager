package com.aquamorph.frcmanager;

import android.os.Bundle;

public class SettingsFragment  extends android.preference.PreferenceFragment {
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}
}