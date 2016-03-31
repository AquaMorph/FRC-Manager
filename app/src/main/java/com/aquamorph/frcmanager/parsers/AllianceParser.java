package com.aquamorph.frcmanager.parsers;

import android.content.Context;
import android.util.Log;

import com.aquamorph.frcmanager.Constants;
import com.aquamorph.frcmanager.models.Events;
import com.aquamorph.frcmanager.network.BlueAlliance;
import com.google.gson.Gson;

import java.io.BufferedReader;
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

	public void fetchJSON(final String event, final Context context) {
		try {
			Gson gson = new Gson();
			BlueAlliance blueAlliance = new BlueAlliance();
			if (Constants.TRACTING_LEVEL > 3) {
				Log.i(TAG, "URL: " + Constants.getEvent(event));
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(blueAlliance
					.connect(Constants.getEvent(event), "", context)));
			eventData = gson.fromJson(reader, Events.class);
			allianceArray = new ArrayList<>(Arrays.asList(eventData.alliances));
			blueAlliance.close();
			parsingComplete = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * getEvents() returns the events as an arraylist.
	 *
	 * @return events
	 */
	public ArrayList<Events.Alliances> getAlliances() {
		return allianceArray;
	}
}
