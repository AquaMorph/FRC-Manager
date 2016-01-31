package com.aquamorph.frcmanager.parsers;

import com.aquamorph.frcmanager.Constants;
import com.aquamorph.frcmanager.models.Match;
import com.aquamorph.frcmanager.network.BlueAlliance;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class TeamEventMatchesParsers {

	public String TAG = "TeamEventMatchesParsers";
	public volatile boolean parsingComplete = true;
	private Match[] teamEventMatches;
	private ArrayList<Match> teamArray = new ArrayList<>();

	public void fetchJSON(final String team, final String event) {
		try {
			Gson gson = new Gson();
			BlueAlliance blueAlliance = new BlueAlliance();
			BufferedReader reader = new BufferedReader(new InputStreamReader(blueAlliance
					.connect(Constants.getEventTeamMatches(team, event))));
			teamEventMatches = gson.fromJson(reader, Match[].class);
			teamArray = new ArrayList<>(Arrays.asList(teamEventMatches));
			blueAlliance.close();
			parsingComplete = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<Match> getTeamEventMatches() {
		return teamArray;
	}
}
