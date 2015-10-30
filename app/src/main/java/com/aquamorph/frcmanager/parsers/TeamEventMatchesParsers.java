package com.aquamorph.frcmanager.parsers;

import com.aquamorph.frcmanager.Constants;
import com.aquamorph.frcmanager.models.TeamEventMatches;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class TeamEventMatchesParsers {

	public String TAG = "TeamEventMatchesParsers";
	public volatile boolean parsingComplete = true;
	private TeamEventMatches[] teamEventMatches;
	private ArrayList<TeamEventMatches> teamArray = new ArrayList<>();

	public void fetchJSON() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					URL url = new URL(Constants.getEventTeamMatches("frc2642","ncre"));
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();

					conn.setReadTimeout(10000);
					conn.setConnectTimeout(15000);
					conn.setRequestMethod("GET");
					conn.setDoInput(true);
					conn.setRequestProperty(Constants.TBA_HEADER, Constants.API_HEADER);
					conn.connect();

					InputStream stream = conn.getInputStream();

					Gson gson = new Gson();
					BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
					teamEventMatches = gson.fromJson(reader, TeamEventMatches[].class);
					teamArray = new ArrayList<>(Arrays.asList(teamEventMatches));
					stream.close();
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
