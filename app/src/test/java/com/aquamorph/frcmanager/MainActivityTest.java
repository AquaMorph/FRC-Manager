package com.aquamorph.frcmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import com.aquamorph.frcmanager.activities.MainActivity;
import com.aquamorph.frcmanager.activities.Settings;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenuItem;
import org.robolectric.shadows.ShadowActivity;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Test for main activity
 *
 * @author Christian Colglazier
 * @version 2/23/2017
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, manifest = "../../../../../src/main/AndroidManifest.xml",
		sdk = 21)
public class MainActivityTest {
	private MainActivity activity;

	@Before
	public void setup() {
		Context context = RuntimeEnvironment.application.getApplicationContext();
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		sharedPreferences.edit().putString("teamNumber", "2642").apply();
		activity = Robolectric.buildActivity(MainActivity.class).create().get();
	}

	@Test
	public void activityStartUp() throws Exception {
		// Tests creation of activity
		assertNotNull(activity);
	}

	@Test
	public void goToSettings() throws Exception {
		// Tests the add habit menu button
		MenuItem addHabit = new RoboMenuItem(R.id.action_settings);
		activity.onOptionsItemSelected(addHabit);
		Intent expectedIntent = new Intent(activity, Settings.class);
		ShadowActivity shadowActivity = Shadows.shadowOf(activity);
		Intent actualIntent = shadowActivity.getNextStartedActivity();
		assertTrue(actualIntent.filterEquals(expectedIntent));
	}
}
