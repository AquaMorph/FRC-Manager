package com.aquamorph.frcmanager.parsers;

import com.aquamorph.frcmanager.Constants;
import com.aquamorph.frcmanager.models.Award;
import com.aquamorph.frcmanager.network.BlueAlliance;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * <p></p>
 *
 * @author Christian Colglazier
 * @version 1/26/2016
 */
public class AwardParser {
	public String TAG = "AwardParser";
	public volatile boolean parsingComplete = true;
	private Award[] awards;
	private ArrayList<Award> awardsList = new ArrayList<>();

	public void fetchJSON(final String event) {
		try {
			Gson gson = new Gson();
			BlueAlliance blueAlliance = new BlueAlliance();
			BufferedReader reader = new BufferedReader(new InputStreamReader(blueAlliance
					.connect(Constants.getEventAwards(event))));
			awards = gson.fromJson(reader, Award[].class);
			awardsList = new ArrayList<>(Arrays.asList(awards));
			blueAlliance.close();
			parsingComplete = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<Award> getAwards() {
		return awardsList;
	}
}
