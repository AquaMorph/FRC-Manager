package com.aquamorph.frcmanager.parsers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.aquamorph.frcmanager.Constants;
import com.aquamorph.frcmanager.network.BlueAlliance;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * <p></p>
 *
 * @author Christian Colglazier
 * @version 10/18/2016
 */

public class Parser<T> {

	public String TAG = "Parser";
	public volatile boolean parsingComplete = true;
	private ArrayList<T> teamArray;
	public Boolean online;
	private Gson gson = new Gson();
	private Type type;
	private String name, url;

	public Parser(String name, String url, Type type) {
		this.name = name;
		this.url = url;
		this.type = type;
	}

	public void fetchJSON(final Context context, final Boolean isTeamNumber) {
		try {
			if (Constants.TRACTING_LEVEL > 0) {
				Log.d(TAG, "Loading");
				Log.d(TAG, "isTeamNumber " + isTeamNumber);
			}
			online = Constants.isNetworkAvailable(context);

			//Checks for internet connection
			if (online) {
				if (Constants.TRACTING_LEVEL > 0) {
					Log.d(TAG, "Online");
				}
				BlueAlliance blueAlliance = new BlueAlliance();
				InputStream stream;
				if (isTeamNumber) {
					stream = blueAlliance.connect(url, getLastModified(context), context);
				} else {
					stream = blueAlliance.connect(url, "", context);
				}

				//Checks for change in data
				if (blueAlliance.getStatus() == 200 || getData(context) == null
						|| Constants.FORCE_DATA_RELOAD || !isTeamNumber) {
					if (Constants.TRACTING_LEVEL > 0) {
						Log.d(TAG, "Loading new data");
					}
					BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
					teamArray = gson.fromJson(reader, type);
					if (isTeamNumber) {
						storeLastModified(context, blueAlliance.getLastUpdated());
						storeData(context, gson.toJson(teamArray));
					}
					blueAlliance.close();
				} else {
					teamArray = getData(context);
				}
			} else {
				teamArray = getData(context);
			}
			parsingComplete = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * getTeamEventMatches() returns match data as an arraylist.
	 *
	 * @return Match
	 */
	public ArrayList<T> getTeamEventMatches() {
		return teamArray;
	}

	/**
	 * setLastModified() stores the last modified date.
	 *
	 * @param context
	 * @param date
	 */
	private void storeLastModified(Context context, String date) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(name + "Last", date);
		editor.apply();
	}

	/**
	 * getLastModified() returns the last modified date.
	 *
	 * @param context
	 * @return last modified
	 */
	private String getLastModified(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString(name + "Last", "");
	}

	/**
	 * setData() stores the date to a json string.
	 *
	 * @param context
	 * @param data
	 */
	public void storeData(Context context, String data) {
		if (Constants.TRACTING_LEVEL > 0) {
			Log.d(TAG, "Storing Data");
		}
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(name, data);
		editor.apply();
	}

	/**
	 * getData() returns data from a stored json string.
	 *
	 * @param context the context
	 * @return Match
	 */
	private ArrayList<T> getData(Context context) {
		if (Constants.TRACTING_LEVEL > 0) {
			Log.d(TAG, "Loading Data from a save");
		}
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String json = prefs.getString(name, "");
		return gson.fromJson(json, type);
	}
}
