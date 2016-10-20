package com.aquamorph.frcmanager.parsers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.aquamorph.frcmanager.Constants;
import com.aquamorph.frcmanager.models.Match;
import com.aquamorph.frcmanager.network.BlueAlliance;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Fetches and parses team match data for an event.
 *
 * @author Christian Colglazier
 * @version 4/1/2016
 */
public class TeamEventMatchesParsers {

	public String TAG = "TeamEventMatchesParsers";
	public volatile boolean parsingComplete = true;
	public Boolean online;
	InputStream stream;
	Gson gson = new Gson();
	private Match[] teamEventMatches;
	private ArrayList<Match> teamArray = new ArrayList<>();

	public void fetchJSON(final String team, final String event, final Context context, final Boolean isTeamNumber) {
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
				if (isTeamNumber) {
					stream = blueAlliance.connect(Constants.getEventTeamMatches(team, event),
							getLastModified(context), context);
				} else {
					stream = blueAlliance.connect(Constants.getEventTeamMatches(team, event),
							"", context);
				}

				//Checks for change in data
				if (blueAlliance.getStatus() == 200 || getData(context) == null
						|| Constants.FORCE_DATA_RELOAD || !isTeamNumber) {
					if (Constants.TRACTING_LEVEL > 0) {
						Log.d(TAG, "Loading new data");
					}
					BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
					teamEventMatches = gson.fromJson(reader, Match[].class);
					if (isTeamNumber) {
						storeLastModified(context, blueAlliance.getLastUpdated());
						storeData(context, gson.toJson(teamEventMatches));
					}
					blueAlliance.close();
				} else {
					teamEventMatches = getData(context);
				}
			} else {
				teamEventMatches = getData(context);
			}
			teamArray = new ArrayList<>(Arrays.asList(teamEventMatches));
			parsingComplete = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * getData() returns match data as an arraylist.
	 *
	 * @return Match
	 */
	public ArrayList<Match> getTeamEventMatches() {
		return teamArray;
	}

	/**
	 * setLastModified() stores the last modified date.
	 *
	 * @param context
	 * @param date
	 */
	public void storeLastModified(Context context, String date) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("teamEventMatchesLast", date);
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
		return prefs.getString("teamEventMatchesLast", "");
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
		editor.putString("teamEventMatches", data);
		editor.apply();
	}

	/**
	 * getData() returns data from a stored json string.
	 *
	 * @param context
	 * @return Match
	 */
	public Match[] getData(Context context) {
		if (Constants.TRACTING_LEVEL > 0) {
			Log.d(TAG, "Loading Data from a save");
		}
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String json = prefs.getString("teamEventMatches", "");
		return gson.fromJson(json, Match[].class);
	}
}
