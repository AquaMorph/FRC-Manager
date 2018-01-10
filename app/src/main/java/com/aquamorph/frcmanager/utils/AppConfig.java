package com.aquamorph.frcmanager.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * <p></p>
 *
 * @author Christian Colglazier
 * @version 2/24/2017
 */

public class AppConfig {
	private static String TAG = "AppConfig";

	/**
	 * Stores the year so SharedPreferences
	 *
	 * @param year
	 * @param context
	 */
	public static void setYear(String year, Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("year", year);
		if (Constants.TRACING_LEVEL > 0) {
			Log.i(TAG, "Year set to: " + year);
		}
		editor.apply();
	}

	/**
	 * setTeamNumber() saves the team number to shared preferences.
	 *
	 * @param team number
	 * @param context
	 */
	public static void setTeamNumber(String team, Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("teamNumber", team);
		editor.apply();
	}

	/**
	 * setEventKey set the shared variable of the event key.
	 *
	 * @param key identification key of an event
	 * @param context
	 */
	public static void setEventKey(String key, Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("eventKey", key);
		editor.apply();
	}

	/**
	 * setEventShortName() set the shared variable of the events name.
	 *
	 * @param name event name
	 * @param context
	 */
	public static void setEventShortName(String name, Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("eventShortName", name);
		editor.apply();
	}
}
