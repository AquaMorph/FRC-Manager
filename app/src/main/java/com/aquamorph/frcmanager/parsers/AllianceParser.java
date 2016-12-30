package com.aquamorph.frcmanager.parsers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.aquamorph.frcmanager.Constants;
import com.aquamorph.frcmanager.models.Events;
import com.aquamorph.frcmanager.network.BlueAlliance;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Fetches and parses alliance data for an event.
 *
 * @author Christian Colglazier
 * @version 3/31/2016
 */
public class AllianceParser {

	public String TAG = "AllianceParser";
	public volatile boolean parsingComplete = true;
	private Events eventData;
	private ArrayList<Events.Alliances> allianceArray = new ArrayList<>();
	public Boolean online;
	Gson gson = new Gson();

	public void fetchJSON(final String event, final Context context) {
		try {
			online = Constants.isNetworkAvailable(context);

			if (Constants.TRACING_LEVEL > 3) {
				Log.i(TAG, "URL: " + Constants.getEvent(event));
			}
			//Checks for internet connection
			if(online) {
				BlueAlliance blueAlliance = new BlueAlliance();
				InputStream stream = blueAlliance.connect(Constants.getEvent(event),
						getLastModified(context), context);

				//Checks for change in data
				if (blueAlliance.getStatus() == 200 || getData(context) == null
						|| Constants.FORCE_DATA_RELOAD) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
					eventData = gson.fromJson(reader, Events.class);
					setLastModified(context, blueAlliance.getLastUpdated());
					setData(context, gson.toJson(eventData));
					blueAlliance.close();
				} else {
					eventData = getData(context);
				}
			} else {
				eventData = getData(context);
			}
			allianceArray = new ArrayList<>(Arrays.asList(eventData.alliances));
			parsingComplete = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * getAlliances() returns the alliance as an arraylist.
	 *
	 * @return events
	 */
	public ArrayList<Events.Alliances> getAlliances() {
		return allianceArray;
	}

	/**
	 * setLastModified() stores the last modified date.
	 *
	 * @param context
	 * @param date
	 */
	public void setLastModified(Context context, String date) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("allianceLast", date);
		editor.apply();
	}

	/**
	 * getLastModified() returns the last modified date.
	 *
	 * @param context
	 * @return last modified
	 */
	public String getLastModified(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString("allianceLast", "");
	}

	/**
	 * setData() stores the date to a json string.
	 *
	 * @param context
	 * @param data
	 */
	public void setData(Context context, String data) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("alliance", data);
		editor.apply();
	}

	/**
	 * getData() returns data from a stored json string.
	 *
	 * @param context
	 * @return
	 */
	public Events getData(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String json = prefs.getString("alliance", "");
		return gson.fromJson(json, Events.class);
	}
}
