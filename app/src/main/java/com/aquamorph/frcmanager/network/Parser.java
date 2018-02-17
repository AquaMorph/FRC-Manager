package com.aquamorph.frcmanager.network;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.aquamorph.frcmanager.utils.Constants;
import com.aquamorph.frcmanager.R;
import com.aquamorph.frcmanager.models.Status;
import com.aquamorph.frcmanager.utils.Logging;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Arrays;

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
	private Activity activity;

	/**
	 * Initializes Parser.
	 *
	 * @param name     The name of date being collected
	 * @param url      The url where the date will be parsed
	 * @param type     The data type of the data to be collected
	 * @param activity The current state of the application
	 * @param force	   Force reload of data
	 */
	public Parser(String name, String url, Type type, Activity activity, Boolean force) {
		this.name = name;
		this.url = url;
		this.type = type;
		this.context = activity.getApplicationContext();
		this.activity = activity;
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		TAG = name + "." + TAG;
		if (force) {
			Logging.debug(this, name + " force reloading", 1);
			storeData("");
			storeLastModified("");
		}
	}

	/**
	 * Updates data. Checks if data has already been collected and if it
	 * had loads from saved data.
	 *
	 * @param storeData Determines if data should be stored in memory
	 */
	public void fetchJSON(final Boolean storeData) {

		Logging.debug(this, "Loading " + name, 0);
			online = Constants.isNetworkAvailable(context);

			// Displays message saying there is no connection
			if (!online) {
				Snackbar.make(activity.findViewById(R.id.myCoordinatorLayout),
						R.string.no_connection_message, Snackbar.LENGTH_LONG).show();
			}

			// Checks FIRST sever status
			BlueAlliance statusBlueAlliance = new BlueAlliance();
			InputStream statusStream = statusBlueAlliance.connect(Constants.getStatusURL(), "",
					context);
			if (statusBlueAlliance.getStatus() == 200) {
				BufferedReader reader = null;
				try {
					reader = new BufferedReader(new InputStreamReader(statusStream, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				Status status = gson.fromJson(reader, Status.class);
				// Displays error message when the FIRST server is down
				if (status.is_datafeed_down) {
					Snackbar.make(activity.findViewById(R.id.myCoordinatorLayout),
							R.string.first_server_down, Snackbar.LENGTH_LONG).show();
				}
				String eventKey = prefs.getString("eventKey", "");
				// Displays error message when the event server is down
				if (Arrays.asList(status.down_events).contains(eventKey)) {
					Snackbar.make(activity.findViewById(R.id.myCoordinatorLayout),
							R.string.event_server_down, Snackbar.LENGTH_LONG).show();
				}
			}
			statusBlueAlliance.close();

			// Checks for internet connection
			if (online) {
				Logging.debug(this, "Online", 0);
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
					Logging.debug(this, "Loading new data for " + name, 0);
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
		Logging.debug(this, "Storing Data", 0);
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
		Logging.debug(this, "Loading data from a save", 0);
		String json = prefs.getString(name, "");
		return gson.fromJson(json, type);
	}
}
