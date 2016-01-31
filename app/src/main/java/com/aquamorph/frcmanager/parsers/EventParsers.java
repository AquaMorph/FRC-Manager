package com.aquamorph.frcmanager.parsers;

import android.util.Log;

import com.aquamorph.frcmanager.Constants;
import com.aquamorph.frcmanager.models.Events;
import com.aquamorph.frcmanager.network.BlueAlliance;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class EventParsers {
	public String TAG = "EventParsers";
	public volatile boolean parsingComplete = true;
	private Events[] events;
	private ArrayList<Events> eventArray = new ArrayList<>();

	public void fetchJSON(final String number) {
		try {
			Gson gson = new Gson();
			BlueAlliance blueAlliance = new BlueAlliance();
			BufferedReader reader = new BufferedReader(new InputStreamReader(blueAlliance
					.connect(Constants.getEventURL(number))));
			events = gson.fromJson(reader, Events[].class);
			eventArray = new ArrayList<>(Arrays.asList(events));
			blueAlliance.close();
			Log.i(TAG, "URL: " + Constants.getEventURL(number));
			parsingComplete = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<Events> getEvents() {
		return eventArray;
	}
}
