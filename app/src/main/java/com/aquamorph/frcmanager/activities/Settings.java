package com.aquamorph.frcmanager.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.aquamorph.frcmanager.R;
import com.aquamorph.frcmanager.fragments.SettingsFragment;

/**
 * Settings activity to take users input about how the app should be set up as well as
 * general information about the app.
 *
 * @author Christian Colglazier
 * @version 3/29/2016
 */
public class Settings extends AppCompatActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		if (toolbar != null) {
			toolbar.setTitle("Settings");
		}
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsFragment())
				.commit();
		MainActivity.theme(this);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				this.finish();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}