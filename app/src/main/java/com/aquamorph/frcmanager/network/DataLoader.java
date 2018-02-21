package com.aquamorph.frcmanager.network;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.SystemClock;

import com.aquamorph.frcmanager.models.Alliance;
import com.aquamorph.frcmanager.models.Award;
import com.aquamorph.frcmanager.models.Match;
import com.aquamorph.frcmanager.models.Rank;
import com.aquamorph.frcmanager.models.Team;
import com.aquamorph.frcmanager.utils.Constants;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collection;

import static java.util.Collections.sort;

/**
 * Loads needed dataLoader.
 *
 * @author Christian Colglazier
 * @version 2/21/2018
 */

public class DataLoader {
	private static Activity activity;
	public static String eventKey = "";
	public static String teamNumber = "";
	public static DataContainer<Team> teamDC;
	public static DataContainer<Rank> rankDC;
	public static DataContainer<Award> awardDC;
	public static DataContainer<Match> matchDC;
	public static DataContainer<Alliance> allianceDC;


	public DataLoader(Activity activity) {
		this.activity = activity;
		setDataContainers(false);
	}

	public static void refresh(boolean force) {
		if (!eventKey.equals("")) {
			setDataContainers(force);
			new Load(matchDC, false, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			new Load(teamDC, false, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			new Load(rankDC, true, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			new Load(awardDC).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			new Load(allianceDC).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}

	private static void setDataContainers(boolean force) {
		teamDC = new DataContainer(force, activity,
				new TypeToken<ArrayList<Team>>(){}.getType(), Constants.getEventTeams(eventKey),
				"eventTeams");
		rankDC = new DataContainer(false, activity,
				new TypeToken<Rank>(){}.getType(),Constants.getEventRanks(eventKey),
				"eventRank");
		awardDC = new DataContainer(force, activity,
				new TypeToken<ArrayList<Award>>(){}.getType(),Constants.getEventAwards(eventKey),
				"eventAwards");
		matchDC = new DataContainer(force, activity,
				new TypeToken<ArrayList<Match>>(){}.getType(),Constants.getEventMatches(eventKey),
				"eventMatches");
		allianceDC = new DataContainer(force, activity,
				new TypeToken<ArrayList<Alliance>>(){}.getType(),Constants.getAlliancesURL(eventKey),
				"Alliance");
	}


}
