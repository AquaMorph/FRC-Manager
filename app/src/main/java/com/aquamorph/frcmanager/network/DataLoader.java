package com.aquamorph.frcmanager.network;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.SystemClock;

import com.aquamorph.frcmanager.adapters.SectionsPagerAdapter;
import com.aquamorph.frcmanager.fragments.AllianceFragment;
import com.aquamorph.frcmanager.fragments.AwardFragment;
import com.aquamorph.frcmanager.fragments.EventScheduleFragment;
import com.aquamorph.frcmanager.fragments.RankFragment;
import com.aquamorph.frcmanager.fragments.TeamFragment;
import com.aquamorph.frcmanager.fragments.TeamScheduleFragment;
import com.aquamorph.frcmanager.models.Alliance;
import com.aquamorph.frcmanager.models.Award;
import com.aquamorph.frcmanager.models.Match;
import com.aquamorph.frcmanager.models.Rank;
import com.aquamorph.frcmanager.models.Tab;
import com.aquamorph.frcmanager.models.Team;
import com.aquamorph.frcmanager.utils.Constants;
import com.aquamorph.frcmanager.utils.Logging;
import com.google.gson.JsonSyntaxException;
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
	private static SectionsPagerAdapter adapter;
	public static String eventKey = "";
	public static String teamNumber = "";
	public static DataContainer<Team> teamDC;
	public static DataContainer<Rank> rankDC;
	public static DataContainer<Award> awardDC;
	public static DataContainer<Match> matchDC;
	public static DataContainer<Alliance> allianceDC;
	private static ArrayList<Tab> teamTabs = new ArrayList<>();
	private static ArrayList<Tab> rankTabs = new ArrayList<>();
	private static ArrayList<Tab> awardTabs = new ArrayList<>();
	private static ArrayList<Tab> matchTabs = new ArrayList<>();
	private static ArrayList<Tab> allianceTabs = new ArrayList<>();


	public DataLoader(Activity activity, SectionsPagerAdapter adapter) {
		this.activity = activity;
		this.adapter = adapter;
		matchTabs.add(new Tab("Team Schedule", TeamScheduleFragment.newInstance()));
		matchTabs.add(new Tab("Event Schedule", EventScheduleFragment.newInstance()));
		rankTabs.add(new Tab("Rankings", RankFragment.newInstance()));
		teamTabs.add(new Tab("Teams", TeamFragment.Companion.newInstance()));
		allianceTabs.add(new Tab("Alliances", AllianceFragment.Companion.newInstance()));
		awardTabs.add(new Tab("Awards", AwardFragment.Companion.newInstance()));
		setDataContainers(false);
	}

	public static void refresh(boolean force) {
		if (!eventKey.equals("")) {
			setDataContainers(force);
			new Load(matchDC, false, true, matchTabs, adapter).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			new Load(teamDC, false, true, teamTabs, adapter).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			new Load(rankDC, true, false, rankTabs, adapter).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			new Load(awardDC, awardTabs, adapter).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			new Load(allianceDC, allianceTabs, adapter).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}

	private static void setDataContainers(boolean force) {
		teamDC = new DataContainer(force, activity,
				new TypeToken<ArrayList<Team>>(){}.getType(), Constants.INSTANCE.getEventTeams(eventKey),
				"eventTeams");
		rankDC = new DataContainer(force, activity,
				new TypeToken<Rank>(){}.getType(), Constants.INSTANCE.getEventRanks(eventKey),
				"eventRank");
		awardDC = new DataContainer(force, activity,
				new TypeToken<ArrayList<Award>>(){}.getType(), Constants.INSTANCE.getEventAwards(eventKey),
				"eventAwards");
		matchDC = new DataContainer(force, activity,
				new TypeToken<ArrayList<Match>>(){}.getType(), Constants.INSTANCE.getEventMatches(eventKey),
				"eventMatches");
		allianceDC = new DataContainer(force, activity,
				new TypeToken<ArrayList<Alliance>>(){}.getType(), Constants.INSTANCE.getAlliancesURL(eventKey),
				"Alliance");
	}


	static class Load extends AsyncTask<Void, Void, Void> {

		private DataContainer dataContainer;
		private ArrayList<Tab> tabs;
		private SectionsPagerAdapter adapter;
		private boolean isRank = false;
		private boolean isSortable = false;

		Load(DataContainer dataContainer, ArrayList<Tab> tabs, SectionsPagerAdapter adapter) {
			this.dataContainer = dataContainer;
			this.tabs = tabs;
			this.adapter = adapter;
		}

		Load(DataContainer dataContainer, boolean isRank, boolean isSortable, ArrayList<Tab> tabs,
			 SectionsPagerAdapter adapter) {
			this.dataContainer = dataContainer;
			this.isRank = isRank;
			this.isSortable = isSortable;
			this.tabs = tabs;
			this.adapter = adapter;
		}

		@Override
		protected void onPreExecute() {
			dataContainer.complete = false;
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				dataContainer.parser.fetchJSON(true);
			} catch (JsonSyntaxException exception) {
				Logging.INSTANCE.error(this, "JSON Parsing Error", 0);
				Logging.INSTANCE.error(this, exception.getMessage(), 0);
			}
			while (dataContainer.parser.getParsingComplete()) {
				SystemClock.sleep(Constants.THREAD_WAIT_TIME);
			}
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
			if (dataContainer.data.isEmpty()) {
				for (Tab tab: tabs) {
					if (adapter.isTab(tab.getName())) {
						adapter.removeFrag(adapter.tabPosition(tab.getName()));
					}
				}
			} else {
				for (Tab tab: tabs) {
					if (!adapter.isTab(tab.getName())) {
						adapter.addFrag(tab);
					}
				}
			}
			dataContainer.complete = true;
		}
	}
}
