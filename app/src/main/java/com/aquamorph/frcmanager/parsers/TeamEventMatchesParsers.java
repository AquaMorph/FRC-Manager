package com.aquamorph.frcmanager.parsers;

import com.aquamorph.frcmanager.Constants;
import com.aquamorph.frcmanager.models.TeamEventMatches;
import com.aquamorph.frcmanager.network.BlueAlliance;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class TeamEventMatchesParsers {

	public String TAG = "TeamEventMatchesParsers";
	public volatile boolean parsingComplete = true;
	private TeamEventMatches[] teamEventMatches;
	private ArrayList<TeamEventMatches> teamArray = new ArrayList<>();

	public void fetchJSON(final String team, final String event) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Gson gson = new Gson();
					BlueAlliance blueAlliance = new BlueAlliance();
					BufferedReader reader = new BufferedReader(new InputStreamReader(blueAlliance
							.connect(Constants.getEventTeamMatches(team, event))));
					teamEventMatches = gson.fromJson(reader, TeamEventMatches[].class);
					teamArray = new ArrayList<>(Arrays.asList(teamEventMatches));
					blueAlliance.close();
					parsingComplete = false;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}

	public ArrayList<TeamEventMatches> getTeamEventMatches() {
		return teamArray;
	}
}
