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

public class TeamEventMatchesParsers {

	public String TAG = "TeamEventMatchesParsers";
	public volatile boolean parsingComplete = true;
	private Match[] teamEventMatches;
	private ArrayList<Match> teamArray = new ArrayList<>();
	public Boolean online;


	public void fetchJSON(final String team, final String event, final Context context) {
		try {
			Gson gson = new Gson();
			online = Constants.isNetworkAvailable(context);

			//Checks for internet connection
			if (online) {
				BlueAlliance blueAlliance = new BlueAlliance();
				InputStream stream = blueAlliance.connect(Constants.getEventTeamMatches(team, event),
						getLastModified(context));

				//Checks for change in data
				if (blueAlliance.getStatus() == 200) {
					Log.d(TAG, "Loading from web");
					BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
					teamEventMatches = gson.fromJson(reader, Match[].class);
					storeLastModified(context, blueAlliance.getLastUpdated());
					storeData(context, gson.toJson(teamEventMatches));
					blueAlliance.close();
				}

				else {
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

	public ArrayList<Match> getTeamEventMatches() {
		return teamArray;
	}

	/**
	 * storeLastModified() stores the last modified date
	 *
	 * @param context
	 * @param date
	 */
	public void storeLastModified(Context context, String date) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("teamEventMatchesLast", date);
		editor.commit();
	}

	/**
	 * getLastModified() returns the last modified date
	 *
	 * @param context
	 * @return
	 */
	public String getLastModified(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString("teamEventMatchesLast", "");
	}

	/**
	 * storeData() stores the date to a json string
	 *
	 * @param context
	 * @param data
	 */
	public void storeData(Context context, String data) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("teamEventMatches", data);
		editor.commit();
	}

	/**
	 * getData() returns data from a stored json string
	 *
	 * @param context
	 * @return
	 */
	public Match[] getData(Context context) {
		Log.d(TAG, "Loading from save");
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Gson gson = new Gson();
		String json = prefs.getString("teamEventMatches", "");
		return gson.fromJson(json, Match[].class);
	}
}
