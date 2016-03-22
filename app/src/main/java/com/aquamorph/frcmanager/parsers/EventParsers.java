package com.aquamorph.frcmanager.parsers;

import android.content.Context;

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

	public void fetchJSON(final String number, final String year, final Context context) {
		try {
			Gson gson = new Gson();
			BlueAlliance blueAlliance = new BlueAlliance();
			BufferedReader reader = new BufferedReader(new InputStreamReader(blueAlliance
					.connect(Constants.getEventURL(number, year), "", context)));
			events = gson.fromJson(reader, Events[].class);
			eventArray = new ArrayList<>(Arrays.asList(events));
			blueAlliance.close();
			parsingComplete = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<Events> getEvents() {
		return eventArray;
	}
}
