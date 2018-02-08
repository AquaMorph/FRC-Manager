package com.aquamorph.frcmanager.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.aquamorph.frcmanager.utils.Constants;
import com.aquamorph.frcmanager.R;
import com.aquamorph.frcmanager.adapters.SectionsPagerAdapter;

/**
 * Default activity of the app.
 *
 * @author Christian Colglazier
 * @version 3/29/2016
 */
public class MainActivity extends AppCompatActivity implements OnSharedPreferenceChangeListener {

	private String TAG = "MainActivity";
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private String teamNumber, eventName, teamRank, teamRecord;
	private ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(MainActivity.this);
		teamNumber = prefs.getString("teamNumber", "");
		eventName = prefs.getString("eventShortName", "");
		teamRank = prefs.getString("teamRank", "");
		teamRecord = prefs.getString("teamRecord", "");

		if (teamNumber.equals("")) openSetup();

		listener();
		theme(this);
	}

	/**
	 * theme() sets the app theme based on user selection from settings.
	 *
	 * @param activity
	 */
	public static void theme(Activity activity) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
		switch (prefs.getString("theme", "")) {
			case "amoled":
				activity.setTheme(R.style.OMOLEDTheme);
				break;
			case "dark":
				activity.setTheme(R.style.DarkTheme);
				break;
			default:
				activity.setTheme(R.style.LightTheme);
				break;
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.activity_main);
		Log.i(TAG, "onConfigurationChanged");
		listener();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_settings:
				openSettings();
				break;
			case R.id.refresh_all:
				mSectionsPagerAdapter.refreshAll();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * openSetup() launches the set up activity.
	 */
	private void openSetup() {
		Intent intent = new Intent(this, Setup.class);
		startActivity(intent);
	}

	/**
	 * openSettings() launches the settings activity.
	 */
	public void openSettings() {
		Intent intent = new Intent(this, Settings.class);
		startActivity(intent);
	}

	/**
	 * getTeamNumber() returns the string tag for Blue Alliance requests of the current
	 * team number.
	 *
	 * @return team number string
	 */
	public String getTeamNumber() {
		return "frc" + teamNumber;
	}

	/**
	 * getAppTitle() returns the text for the app.
	 * @return title for the app
	 */
	public String getAppTitle() {
		return String.format("%s - %s", teamNumber,
				shorten(eventName, Constants.MAX_EVENT_TITLE_LENGTH));
	}

	/**
	 * getAppSubTitle() returns the subtitle string for the toolbar with the event name,
	 * team number and current rank if available.
	 *
	 * @return subtitle string
	 */
	public String getAppSubTitle() {
		if (teamRank.equals("")) {
			return "";
		} else {
			return String.format("Rank #%s %s", teamRank, teamRecord);
		}
	}

	/**
	 * shorten() returns a shortened string with ... at the end.
	 *
	 * @param text   to be shortened
	 * @param amount length to shorten
	 * @return shorten string with ... at the end
	 */
	public String shorten(String text, int amount) {
		if (text.length() > amount) {
			return text.substring(0, amount) + "...";
		} else {
			return text;
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		teamNumber = sharedPreferences.getString("teamNumber", "0000");
		eventName = sharedPreferences.getString("eventShortName", "North Carolina");
		teamRank = sharedPreferences.getString("teamRank", "");
		teamRecord = sharedPreferences.getString("teamRecord", "");
		if (getSupportActionBar() != null) {
			getSupportActionBar().setTitle(getAppTitle());
			getSupportActionBar().setSubtitle(getAppSubTitle());
		}
		if (key.equals("theme")) {
			this.recreate();
		}
	}

	/**
	 * listener() connects layout to view.
	 */
	private void listener() {
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle(getAppTitle());
		getSupportActionBar().setSubtitle(getAppSubTitle());

		// Set up the ViewPager with the sections adapter.
		mViewPager = findViewById(R.id.container);
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		mViewPager.setOffscreenPageLimit(mSectionsPagerAdapter.getCount());
		mViewPager.setAdapter(mSectionsPagerAdapter);

		TabLayout tabLayout = findViewById(R.id.tabs);
		if (tabLayout != null) {
			tabLayout.setupWithViewPager(mViewPager);
			tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
		}
	}
}
