package com.aquamorph.frcmanager.parsers;

import com.aquamorph.frcmanager.Constants;
import com.aquamorph.frcmanager.models.Rank;
import com.aquamorph.frcmanager.network.BlueAlliance;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class RankParser {

	public String TAG = "RankParser";
	public volatile boolean parsingComplete = true;
	private ArrayList<String[]> data;
	private ArrayList<Rank> teamArray = new ArrayList<>();

	public void fetchJSON(final String team, final String event) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Gson gson = new Gson();
					BlueAlliance blueAlliance = new BlueAlliance();
					BufferedReader reader = new BufferedReader(new InputStreamReader(blueAlliance
							.connect(Constants.getEventTeamMatches(team, event))));
//					teamArray = gson.fromJson(reader, S);
//					teamArray = new ArrayList<>(Arrays.asList(teamEventMatches));
					blueAlliance.close();
					parsingComplete = false;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}

	public ArrayList<Rank> getTeamEventMatches() {
		return teamArray;
	}
}
