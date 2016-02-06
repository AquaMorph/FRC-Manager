package com.aquamorph.frcmanager.parsers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.aquamorph.frcmanager.Constants;
import com.aquamorph.frcmanager.network.BlueAlliance;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class RankParser {

	public String TAG = "RankParser";
	public volatile boolean parsingComplete = true;
	private String[][] ranks;
	private ArrayList<String[]> rankArray = new ArrayList<>();
	public Boolean online;
	Gson gson = new Gson();


	public void fetchJSON(final String event, final Context context) {
		try {
			online = Constants.isNetworkAvailable(context);

			//Checks for internet connection
			if (online) {
				BlueAlliance blueAlliance = new BlueAlliance();
				InputStream stream = blueAlliance.connect(Constants.getEventRanks(event),
						getLastModified(context));

				//Checks for change in data
				if (blueAlliance.getStatus() == 200 || getData(context).length == 0) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
					ranks = gson.fromJson(reader, String[][].class);
					setLastModified(context, blueAlliance.getLastUpdated());
					setData(context, gson.toJson(ranks));
					blueAlliance.close();
				} else {
					ranks = getData(context);
				}
			} else {
				ranks = getData(context);
			}
			rankArray = new ArrayList<>(Arrays.asList(ranks));
			parsingComplete = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<String[]> getRankings() {
		return rankArray;
	}

	/**
	 * setLastModified() stores the last modified date
	 *
	 * @param context
	 * @param date
	 */
	public void setLastModified(Context context, String date) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("rankLast", date);
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
		return prefs.getString("rankLast", "");
	}

	/**
	 * setData() stores the date to a json string
	 *
	 * @param context
	 * @param data
	 */
	public void setData(Context context, String data) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("ranks", data);
		editor.commit();
	}

	/**
	 * getData() returns data from a stored json string
	 *
	 * @param context
	 * @return
	 */
	public String[][] getData(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String json = prefs.getString("ranks", "");
		return gson.fromJson(json, String[][].class);
	}
}
