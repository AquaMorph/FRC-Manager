package com.aquamorph.frcmanager.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.aquamorph.frcmanager.Constants;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

/**
 * Parses date of type T.
 *
 * @author Christian Colglazier
 * @version 10/20/2016
 */

public class Parser<T> {

	public volatile boolean parsingComplete = true;
	public Boolean online;
	private String TAG = "Parser";
	private T data;
	private Gson gson = new Gson();
	private Type type;
	private String name, url;
	private Context context;
	private SharedPreferences prefs;

	/**
	 * Initializes Parser.
	 *
	 * @param name    The name of date being collected
	 * @param url     The url where the date will be parsed
	 * @param type    The data type of the data to be collected
	 * @param context The current state of the application
	 */
	public Parser(String name, String url, Type type, Context context) {
		this.name = name;
		this.url = url;
		this.type = type;
		this.context = context;
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		TAG = name + "." + TAG;
	}

	/**
	 * Updates data. Checks if data has already been collected and if it
	 * had loads from saved data.
	 *
	 * @param storeData Determines if data should be stored in memory
	 */
	public void fetchJSON(final Boolean storeData) {
		try {
			if (Constants.TRACING_LEVEL > 0) {
				Log.d(TAG, "Loading " + name);
			}
			online = Constants.isNetworkAvailable(context);

			// Checks for internet connection
			if (online) {
				if (Constants.TRACING_LEVEL > 0) {
					Log.d(TAG, "Online");
				}
				BlueAlliance blueAlliance = new BlueAlliance();
				InputStream stream;
				if (storeData) {
					stream = blueAlliance.connect(url, getLastModified(), context);
				} else {
					stream = blueAlliance.connect(url, "", context);
				}

				// Checks for change in data
				if (blueAlliance.getStatus() == 200 || getStoredData() == null
						|| Constants.FORCE_DATA_RELOAD || !storeData) {
					if (Constants.TRACING_LEVEL > 0) {
						Log.d(TAG, "Loading new data for " + name);
					}
					BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
					data = gson.fromJson(reader, type);
					if (storeData) {
						storeLastModified(blueAlliance.getLastUpdated());
						storeData(gson.toJson(data));
					}
					blueAlliance.close();
				}
				// Date Not Changed
				else {
					data = getStoredData();
				}
			}
			// Not Online
			else {
				data = getStoredData();
			}
			parsingComplete = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns a list of parsed data.
	 *
	 * @return data
	 */
	public T getData() {
		return data;
	}

	/**
	 * Stores the last modified date.
	 *
	 * @param date last modified date
	 */
	private void storeLastModified(String date) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(name + "Last", date);
		editor.apply();
	}

	/**
	 * Gets the last modified date.
	 *
	 * @return last modified
	 */
	private String getLastModified() {
		return prefs.getString(name + "Last", "");
	}

	/**
	 * Sets the date to a json string.
	 *
	 * @param data parsed values
	 */
	public void storeData(String data) {
		if (Constants.TRACING_LEVEL > 0) {
			Log.d(TAG, "Storing Data");
		}
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(name, data);
		editor.apply();
	}

	/**
	 * getData() returns data from a stored json string.
	 *
	 * @return data
	 */
	private T getStoredData() {
		if (Constants.TRACING_LEVEL > 0) {
			Log.d(TAG, "Loading Data from a save");
		}
		String json = prefs.getString(name, "");
		return gson.fromJson(json, type);
	}
}
