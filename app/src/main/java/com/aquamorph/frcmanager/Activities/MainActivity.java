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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.aquamorph.frcmanager.R;
import com.aquamorph.frcmanager.adapters.SectionsPagerAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends AppCompatActivity implements OnSharedPreferenceChangeListener {


	private String TAG = "MainActivity";
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	public String teamNumber, eventName;
	public Boolean paidUser = false;
	public String year = "2015";
	public String teamRank;
	Toolbar toolbar;
	SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(MainActivity.this);
		teamNumber = prefs.getString("teamNumber", "");
		eventName = prefs.getString("eventShortName", "");
		year = prefs.getString("teamNumber", "2015");
		teamRank = prefs.getString("teamRank", "");

		if (teamNumber.equals("")) openSetup();


		listener();
		theme(this);
	}

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
		listener();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			openSettings();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void openSetup() {
		Intent intent = new Intent(this, Setup.class);
		startActivity(intent);
	}

	public void openSettings() {
		Intent intent = new Intent(this, Settings.class);
		startActivity(intent);
	}

	public String getTeamNumber() {
		return "frc" + teamNumber;
	}

	public String getSubTitle() {
		if(teamRank.equals("")) {
			return String.format("%s (%s)", eventName, teamNumber);
		} else {
			return String.format("%s (%s) Rank #%s", eventName, teamNumber, teamRank);
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		teamNumber = sharedPreferences.getString("teamNumber", "0000");
		eventName = sharedPreferences.getString("eventShortName", "North Carolina");
		teamRank = sharedPreferences.getString("teamRank", "");
		if (getSupportActionBar() != null) {
			getSupportActionBar().setSubtitle(getSubTitle());
		}
		if (key.equals("theme")) {
			this.recreate();
		}
	}

	private void listener() {
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		toolbar.setSubtitle(getSubTitle());

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.container);
		mViewPager.setOffscreenPageLimit(mSectionsPagerAdapter.getCount());
		mViewPager.setAdapter(mSectionsPagerAdapter);

		TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
		tabLayout.setupWithViewPager(mViewPager);
		tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

		//Load ads
		AdView mAdView = (AdView) findViewById(R.id.adView);
		if (paidUser) {
			mAdView.setVisibility(View.GONE);
		} else {
			AdRequest adRequest = new AdRequest.Builder()
					.addTestDevice(getResources().getString(R.string.nexus_5_test_id))
					.addTestDevice(getResources().getString(R.string.moto_g_test_id))
					.addTestDevice(getResources().getString(R.string.neux_6p_test_id))
					.build();
			mAdView.loadAd(adRequest);
		}
	}
}
