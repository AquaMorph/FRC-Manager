package com.aquamorph.frcmanager.utils;

import android.app.Activity;
import android.os.AsyncTask;

import com.aquamorph.frcmanager.decoration.Animations;
import com.aquamorph.frcmanager.models.Alliance;
import com.aquamorph.frcmanager.models.Award;
import com.aquamorph.frcmanager.models.Match;
import com.aquamorph.frcmanager.network.Parser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by aquam on 2/19/2018.
 */

public class Data {
	private static Activity activity;
	public static String eventKey = "";
	public static String teamNumber = "";
	public static ArrayList<Alliance> alliances = new ArrayList<>();
	public static ArrayList<Match> eventMatches = new ArrayList<>();
	public static ArrayList<Award> awards = new ArrayList<>();
	public static boolean awardParsingComplete = false;
	public static boolean eventMatchesParsingComplete = false;
	public static boolean allianceParsingComplete = false;
	private static Parser<ArrayList<Award>> parser;
	private static Parser<ArrayList<Alliance>> allianceParser;
	private static Parser<ArrayList<Match>> eventMatchParser;


	public Data(Activity activity) {
		this.activity = activity;
	}

	public static void refresh(boolean force) {
		if (!eventKey.equals("")) {
			new LoadAwards(force).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			new LoadAlliances(force).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			new LoadEventSchedule(force).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
			parser = new Parser<>("eventAwards", Constants.getEventAwards(Data.eventKey),
					new TypeToken<ArrayList<Award>>() {}.getType(), activity, force);
		}

		@Override
		protected Void doInBackground(Void... params) {
			parser.fetchJSON(true);
			while (parser.parsingComplete) ;
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			awards.clear();
			awards.addAll(parser.getData());
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
