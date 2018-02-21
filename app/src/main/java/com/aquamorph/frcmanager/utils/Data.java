package com.aquamorph.frcmanager.utils;

import android.app.Activity;
import android.os.AsyncTask;

import com.aquamorph.frcmanager.models.Alliance;
import com.aquamorph.frcmanager.models.Award;
import com.aquamorph.frcmanager.models.Match;
import com.aquamorph.frcmanager.models.Rank;
import com.aquamorph.frcmanager.models.Team;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collection;

import static java.util.Collections.sort;

/**
 * Created by aquam on 2/19/2018.
 */

public class Data {
	private static Activity activity;
	public static String eventKey = "";
	public static String teamNumber = "";
	public static DataContainer<Team> teamDC;
	public static DataContainer<Rank> rankDC;
	public static DataContainer<Award> awardDC;
	public static DataContainer<Match> matchDC;
	public static DataContainer<Alliance> allianceDC;


	public Data(Activity activity) {
		this.activity = activity;
		setDataContainers(false);
	}

	public static void refresh(boolean force) {
		if (!eventKey.equals("")) {
			setDataContainers(force);
			new Load(teamDC, false, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			new Load(rankDC, true, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			new Load(awardDC).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			new Load(allianceDC).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			new Load(matchDC).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}

	private static void setDataContainers(boolean force) {
		teamDC = new DataContainer(force, activity,
				new TypeToken<ArrayList<Team>>(){}.getType(),Constants.getEventTeams(eventKey),
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

	static class Load extends AsyncTask<Void, Void, Void> {

		DataContainer dataContainer;
		boolean isRank = false;
		boolean isSortable = false;

		Load(DataContainer dataContainer) {
			this.dataContainer = dataContainer;
		}

		Load(DataContainer dataContainer, boolean isRank, boolean isSortable) {
			this.dataContainer = dataContainer;
			this.isRank = isRank;
			this.isSortable = isSortable;
		}

		@Override
		protected void onPreExecute() {
			dataContainer.complete = false;
		}

		@Override
		protected Void doInBackground(Void... params) {
			dataContainer.parser.fetchJSON(true);
			while (dataContainer.parser.parsingComplete) ;
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			dataContainer.data.clear();
			if (dataContainer.parser.getData() != null) {
				if(isRank) {
					dataContainer.data.add(dataContainer.parser.getData());
				} else {
					dataContainer.data.addAll((Collection) dataContainer.parser.getData());
					if (isSortable) {
						sort(dataContainer.data);
					}
				}
			}
			dataContainer.complete = true;
		}
	}
}
