package com.aquamorph.frcmanager.parsers;

import android.util.Log;

import com.aquamorph.frcmanager.Constants;
import com.aquamorph.frcmanager.models.TeamEventMatches;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TeamEventMatchesParsers {

	public String TAG = "TeamEventMatchesParsers";
	public volatile boolean parsingComplete = true;

	public void fetchJSON() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					URL url = new URL(Constants.getEventTeamMatches("frc2059","ncre"));
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
					TeamEventMatches[] teamEventMatches = gson.fromJson(reader, TeamEventMatches[].class);
					for(int i = 0; i < teamEventMatches.length; i++) {
						Log.i(TAG, "Match #: " + teamEventMatches[i].getMatch_number());
					}
					stream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}
}
