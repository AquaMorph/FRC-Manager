package com.aquamorph.frcmanager.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Handles storing and receiving keys.
 *
 * @author Christian Colglazier
 * @version 2/24/2017
 */

public class AppConfig {

	/**
	 * Stores the year so SharedPreferences
	 *
	 * @param year year of event being tracked
	 * @param context app context
	 */
	public static void setYear(String year, Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("year", year);
		Logging.info(context, "Year set to: " + year, 0);
		editor.apply();
	}

	/**
	 * setTeamNumber() saves the team number to shared preferences.
	 *
	 * @param team number
	 * @param context app context
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
	 * @param context app context
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
	 * @param context app context
	 */
	public static void setEventShortName(String name, Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("eventShortName", name);
		editor.apply();
	}
}
