package com.aquamorph.frcmanager;

import com.aquamorph.frcmanager.activities.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertNotNull;

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
		activity = Robolectric.buildActivity(MainActivity.class).create().get();
	}

	@Test
	public void activityStartUp() throws Exception {
		// Tests creation of activity
		assertNotNull(activity);
	}
}
