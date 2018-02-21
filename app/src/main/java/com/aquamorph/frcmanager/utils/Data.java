package com.aquamorph.frcmanager.utils;

import android.app.Activity;
import android.os.AsyncTask;

import com.aquamorph.frcmanager.models.Alliance;
import com.aquamorph.frcmanager.models.Award;
import com.aquamorph.frcmanager.models.Match;
import com.aquamorph.frcmanager.models.Rank;
import com.aquamorph.frcmanager.models.Team;
import com.aquamorph.frcmanager.network.Parser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static java.util.Collections.sort;

/**
 * Created by aquam on 2/19/2018.
 */

public class Data {
	private static Activity activity;
	public static String eventKey = "";
	public static String teamNumber = "";
	public static ArrayList<Rank> ranks = new ArrayList<>();
	public static ArrayList<Alliance> alliances = new ArrayList<>();
	public static ArrayList<Match> eventMatches = new ArrayList<>();
	public static ArrayList<Award> awards = new ArrayList<>();
	public static boolean rankParsingComplete = false;
	public static boolean awardParsingComplete = false;
	public static boolean eventMatchesParsingComplete = false;
	public static boolean allianceParsingComplete = false;
	private static Parser<Rank> rankParser;
	private static Parser<ArrayList<Award>> awardParser;
	private static Parser<ArrayList<Alliance>> allianceParser;
	private static Parser<ArrayList<Match>> eventMatchParser;
	public static DataContainer<Team> teamDC;


	public Data(Activity activity) {
		this.activity = activity;
		teamDC = new DataContainer(false, activity,
				new TypeToken<ArrayList<Team>>(){}.getType(),Constants.getEventTeams(eventKey));
	}

	public static void refresh(boolean force) {
		if (!eventKey.equals("")) {
			teamDC = new DataContainer(force, activity,
					new TypeToken<ArrayList<Team>>(){}.getType(),Constants.getEventTeams(eventKey));
			new Load(teamDC).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			new LoadRanks(force).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			new LoadAwards(force).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			new LoadAlliances(force).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			new LoadEventSchedule(force).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}

	static class Load extends AsyncTask<Void, Void, Void> {

		DataContainer dataContainer;

		Load(DataContainer dataContainer) {
			this.dataContainer = dataContainer;
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
				dataContainer.data.addAll((Collection) dataContainer.parser.getData());
				sort(dataContainer.data);
			}
			dataContainer.complete = true;
		}
	}


	static class LoadRanks extends AsyncTask<Void, Void, Void> {

		boolean force;

		LoadRanks(boolean force) {
			this.force = force;
		}

		@Override
		protected void onPreExecute() {
			rankParsingComplete = false;
			rankParser = new Parser<>("eventRank", Constants.getEventRanks(eventKey),
					new TypeToken<Rank>(){}.getType(), activity, force);
		}

		@Override
		protected Void doInBackground(Void... params) {
			rankParser.fetchJSON(true);
			while (rankParser.parsingComplete) ;
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (rankParser.getData() != null) {
				ranks.clear();
				ranks.add(rankParser.getData());
			}
			rankParsingComplete = true;
		}
	}

	static class LoadAwards extends AsyncTask<Void, Void, Void> {

		boolean force;

		LoadAwards(boolean force) {
			this.force = force;
		}

		@Override
		protected void onPreExecute() {
			awardParsingComplete = false;
			awardParser = new Parser<>("eventAwards", Constants.getEventAwards(Data.eventKey),
					new TypeToken<ArrayList<Award>>() {}.getType(), activity, force);
		}

		@Override
		protected Void doInBackground(Void... params) {
			awardParser.fetchJSON(true);
			while (awardParser.parsingComplete) ;
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			awards.clear();
			awards.addAll(awardParser.getData());
			awardParsingComplete = true;
		}
	}

	static class LoadEventSchedule extends AsyncTask<Void, Void, Void> {

		boolean force;

		public LoadEventSchedule(boolean force) {
			this.force = force;
		}

		@Override
		protected void onPreExecute() {
			eventMatchesParsingComplete = false;
			eventMatchParser = new Parser<>("eventMatches", Constants.getEventMatches(eventKey),
					new TypeToken<ArrayList<Match>>() {}.getType(), activity, force);
		}

		@Override
		protected Void doInBackground(Void... params) {
			eventMatchParser.fetchJSON(true);
			while (eventMatchParser.parsingComplete) ;
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			eventMatches.clear();
			eventMatches.addAll(eventMatchParser.getData());
			Collections.sort(eventMatches);
			eventMatchesParsingComplete = true;
		}
	}

	static class LoadAlliances extends AsyncTask<Void, Void, Void> {

		boolean force;

		public LoadAlliances(boolean force) {
			this.force = force;
		}

		@Override
		protected void onPreExecute() {
			allianceParsingComplete = false;
			allianceParser = new Parser<>("Alliance",	Constants.getAlliancesURL(eventKey), new
					TypeToken<ArrayList<Alliance>>() {}.getType(), activity, force);
		}

		@Override
		protected Void doInBackground(Void... params) {
			allianceParser.fetchJSON(true);
			while (allianceParser.parsingComplete) ;
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (alliances != null && allianceParser.getData() != null) {
				alliances.clear();
				alliances.addAll(allianceParser.getData());
			}
			allianceParsingComplete = true;
		}
	}
}
