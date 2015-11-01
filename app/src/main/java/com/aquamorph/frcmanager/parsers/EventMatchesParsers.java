package com.aquamorph.frcmanager.parsers;

import com.aquamorph.frcmanager.Constants;
import com.aquamorph.frcmanager.models.Match;
import com.aquamorph.frcmanager.network.BlueAlliance;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class EventMatchesParsers {
	public String TAG = "EventMatchesParsers";
	public volatile boolean parsingComplete = true;
	private Match[] eventMatches;
	private ArrayList<Match> teamArray = new ArrayList<>();

	public void fetchJSON(final String event) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Gson gson = new Gson();
					BlueAlliance blueAlliance = new BlueAlliance();
					BufferedReader reader = new BufferedReader(new InputStreamReader(blueAlliance
							.connect(Constants.getEventMatches(event))));
					eventMatches = gson.fromJson(reader, Match[].class);
					teamArray = new ArrayList<>(Arrays.asList(eventMatches));
					blueAlliance.close();
					parsingComplete = false;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}

	public ArrayList<Match> getEventMatches() {
		return teamArray;
	}
}
