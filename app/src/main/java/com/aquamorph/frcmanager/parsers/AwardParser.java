package com.aquamorph.frcmanager.parsers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.aquamorph.frcmanager.Constants;
import com.aquamorph.frcmanager.models.Award;
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
 * @version 1/26/2016
 */
public class AwardParser {
	public String TAG = "AwardParser";
	public volatile boolean parsingComplete = true;
	private Award[] awards;
	private ArrayList<Award> awardsList = new ArrayList<>();
	public Boolean online;
	Gson gson = new Gson();

	public void fetchJSON(final String event, final Context context) {
		try {
			online = Constants.isNetworkAvailable(context);

			//Checks for internet connection
			if (online) {
				BlueAlliance blueAlliance = new BlueAlliance();
				InputStream stream = blueAlliance.connect(Constants.getEventAwards(event),
						getLastModified(context));

				//Checks for change in data
				if (blueAlliance.getStatus() == 200 || getData(context) == null
						|| Constants.FORCE_DATA_RELOAD) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
					awards = gson.fromJson(reader, Award[].class);
					setLastModified(context, blueAlliance.getLastUpdated());
					setData(context, gson.toJson(awards));
					blueAlliance.close();
				} else {
					awards = getData(context);
				}
			} else {
				awards = getData(context);
			}
			awardsList = new ArrayList<>(Arrays.asList(awards));
			parsingComplete = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<Award> getAwards() {
		return awardsList;
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
		editor.putString("awardsLast", date);
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
		return prefs.getString("awardsLast", "");
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
		editor.putString("awards", data);
		editor.commit();
	}

	/**
	 * getData() returns data from a stored json string
	 *
	 * @param context
	 * @return
	 */
	public Award[] getData(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String json = prefs.getString("awards", "");
		return gson.fromJson(json, Award[].class);
	}
}
