package com.aquamorph.frcmanager.utils;

import android.app.Activity;
import android.os.AsyncTask;

import com.aquamorph.frcmanager.models.Alliance;
import com.aquamorph.frcmanager.network.Parser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by aquam on 2/19/2018.
 */

public class Data {
	private Activity activity;
	public static String eventKey = "";
	public static ArrayList<Alliance> alliances = new ArrayList<>();
	public static boolean allianceParsingComplete = false;
	private Parser<ArrayList<Alliance>> parser;

	public Data(Activity activity) {
		this.activity = activity;
	}

	public void refresh(boolean force) {
		if (!eventKey.equals("")) {
			new LoadAlliances(force).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}

	class LoadAlliances extends AsyncTask<Void, Void, Void> {

		boolean force;

		public LoadAlliances(boolean force) {
			this.force = force;
		}

		@Override
		protected void onPreExecute() {
			allianceParsingComplete = false;
			parser = new Parser<>("Alliance",	Constants.getAlliancesURL(eventKey), new
					TypeToken<ArrayList<Alliance>>() {}.getType(), activity, force);
		}

		@Override
		protected Void doInBackground(Void... params) {
			parser.fetchJSON(true);
			while (parser.parsingComplete) ;
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (alliances != null && parser.getData() != null) {
				alliances.clear();
				alliances.addAll(parser.getData());
			}
			allianceParsingComplete = true;
		}
	}
}
