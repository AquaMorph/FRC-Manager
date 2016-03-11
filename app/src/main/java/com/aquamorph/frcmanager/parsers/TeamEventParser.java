package com.aquamorph.frcmanager.parsers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.aquamorph.frcmanager.Constants;
import com.aquamorph.frcmanager.models.EventTeam;
import com.aquamorph.frcmanager.network.BlueAlliance;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * <p></p>
 *
 * @author Christian Colglazier
 * @version 3/11/2016
 */
public class TeamEventParser {
	public String TAG = "TeamEventParser";
	public volatile boolean parsingComplete = true;
	private EventTeam[] eventTeams;
	private ArrayList<EventTeam> teamArray;
	public Boolean online;
	Gson gson = new Gson();

	public void fetchJSON(final String event, final Context context) {
		try {
			online = Constants.isNetworkAvailable(context);

			//Checks for internet connection
			if (online) {
				BlueAlliance blueAlliance = new BlueAlliance();
				InputStream stream = blueAlliance.connect(Constants.getEventTeams(event),
						getLastModified(context));

				//Checks for change in data
				if (blueAlliance.getStatus() == 200 ||  getData(context) == null
						|| Constants.FORCE_DATA_RELOAD) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
					eventTeams = gson.fromJson(reader, EventTeam[].class);
					storeLastModified(context, blueAlliance.getLastUpdated());
					storeData(context, gson.toJson(eventTeams));
					blueAlliance.close();
				} else {
					eventTeams = getData(context);
				}
			} else {
				eventTeams = getData(context);
			}
			teamArray = new ArrayList<>(Arrays.asList(eventTeams));
			parsingComplete = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<EventTeam> getTeams() {
		return teamArray;
	}

	/**
	 * setLastModified() stores the last modified date
	 *
	 * @param context
	 * @param date
	 */
	public void storeLastModified(Context context, String date) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("eventTeamsLast", date);
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
		return prefs.getString("eventTeamsLast", "");
	}

	/**
	 * setData() stores the date to a json string
	 *
	 * @param context
	 * @param data
	 */
	public void storeData(Context context, String data) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("eventTeams", data);
		editor.commit();
	}

	/**
	 * getData() returns data from a stored json string
	 *
	 * @param context
	 * @return
	 */
	public EventTeam[] getData(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String json = prefs.getString("eventTeams", "");
		return gson.fromJson(json, EventTeam[].class);
	}
}
