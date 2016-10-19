package com.aquamorph.frcmanager.parsers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.aquamorph.frcmanager.Constants;
import com.aquamorph.frcmanager.models.Match;
import com.aquamorph.frcmanager.network.BlueAlliance;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Fetches and parses match data for an event.
 *
 * @author Christian Colglazier
 * @version 3/26/2016
 */
public class EventMatchesParsers {

	private String TAG = "EventMatchesParsers";
	public volatile boolean parsingComplete = true;
//	private Match[] eventMatches;
	private ArrayList<Match> teamArray;
	public Boolean online;
	private Type type = new TypeToken<ArrayList<Match>>(){}.getType();
	Gson gson = new Gson();

	public void fetchJSON(final String event, final Context context) {
		try {
			online = Constants.isNetworkAvailable(context);

			//Checks for internet connection
			if (online) {
				BlueAlliance blueAlliance = new BlueAlliance();
				InputStream stream = blueAlliance.connect(Constants.getEventMatches(event),
						getLastModified(context), context);

				//Checks for change in data
				if (blueAlliance.getStatus() == 200 || getData(context) == null
						|| Constants.FORCE_DATA_RELOAD) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
					teamArray = gson.fromJson(reader, type);
					setLastModified(context, blueAlliance.getLastUpdated());
					setData(context, gson.toJson(teamArray));
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
	 * getEventMatches() returns the match data as an arraylist.
	 *
	 * @return Match
	 */
	public ArrayList<Match> getEventMatches() {
		return teamArray;
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
		editor.putString("eventMatchesLast", date);
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
		return prefs.getString("eventMatchesLast", "");
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
		editor.putString("eventMatches", data);
		editor.apply();
	}

	/**
	 * getData() returns data from a stored json string.
	 *
	 * @param context
	 * @return
	 */
	public ArrayList<Match> getData(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String json = prefs.getString("eventMatches", "");
		return gson.fromJson(json, type);
	}
}
