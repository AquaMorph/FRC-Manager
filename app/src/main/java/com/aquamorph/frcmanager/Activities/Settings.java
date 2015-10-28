package com.aquamorph.frcmanager.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.aquamorph.frcmanager.fragments.SettingsFragment;
import com.aquamorph.frcmanager.R;

public class Settings extends AppCompatActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle("Settings");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		getFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		switch (item.getItemId()) {
			case android.R.id.home:
				this.finish();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}