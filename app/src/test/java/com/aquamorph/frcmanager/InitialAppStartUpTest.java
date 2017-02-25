package com.aquamorph.frcmanager;

import android.content.Intent;

import com.aquamorph.frcmanager.activities.MainActivity;
import com.aquamorph.frcmanager.activities.Setup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import static junit.framework.Assert.assertTrue;

/**
 * Test setup launch on first open
 *
 * @author Christian Colglazier
 * @version 2/24/2017
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, manifest = "../../../../../src/main/AndroidManifest.xml",
		sdk = 21)
public class InitialAppStartUpTest {
	private MainActivity activity;

	@Before
	public void setup() {
		activity = Robolectric.buildActivity(MainActivity.class).create().get();
	}

	@Test
	public void openAppSetUp() throws Exception {
		// Tests the add habit menu button
		Intent expectedIntent = new Intent(activity, Setup.class);
		ShadowActivity shadowActivity = Shadows.shadowOf(activity);
		Intent actualIntent = shadowActivity.getNextStartedActivity();
		assertTrue(actualIntent.filterEquals(expectedIntent));
	}
}
