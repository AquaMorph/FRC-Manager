package com.aquamorph.frcmanager.parsers;

import com.aquamorph.frcmanager.Constants;
import com.aquamorph.frcmanager.network.BlueAlliance;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class RankParser {

	public String TAG = "RankParser";
	public volatile boolean parsingComplete = true;
	private String[][] ranks;
	private ArrayList<String[]> rankArray = new ArrayList<>();

	public void fetchJSON(final String event) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Gson gson = new Gson();
					BlueAlliance blueAlliance = new BlueAlliance();
					BufferedReader reader = new BufferedReader(new InputStreamReader(blueAlliance
							.connect(Constants.getEventRanks(event))));
					ranks = gson.fromJson(reader, String[][].class);
					rankArray = new ArrayList<>(Arrays.asList(ranks));
					blueAlliance.close();
					parsingComplete = false;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}

	public ArrayList<String[]> getRankings() {
		return rankArray;
	}
}
