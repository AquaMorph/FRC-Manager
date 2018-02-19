package com.aquamorph.frcmanager.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aquamorph.frcmanager.R;
import com.aquamorph.frcmanager.activities.MainActivity;
import com.aquamorph.frcmanager.adapters.ScheduleAdapter;
import com.aquamorph.frcmanager.decoration.Animations;
import com.aquamorph.frcmanager.models.Match;
import com.aquamorph.frcmanager.network.Parser;
import com.aquamorph.frcmanager.utils.Constants;
import com.aquamorph.frcmanager.utils.Logging;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import static java.util.Collections.sort;

/**
 * Displays a list of matches at an event for a given team.
 *
 * @author Christian Colglazier
 * @version 10/19/2016
 */
public class TeamScheduleFragment extends Fragment
		implements OnSharedPreferenceChangeListener, RefreshFragment {

	SharedPreferences prefs;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private RecyclerView recyclerView;
	private TextView emptyView;
	private Adapter adapter;
	private ArrayList<Match> teamEventMatches = new ArrayList<>();
	private String teamNumber = "", eventKey = "";
	private Parser<ArrayList<Match>> parser;
	private View view;
	private Boolean getTeamFromSettings = true;
	private Boolean firstLoad = true;

	/**
	 * newInstance creates and returns a new TeamScheduleFragment
	 *
	 * @return TeamScheduleFragment
	 */
	public static TeamScheduleFragment newInstance() {
		return new TeamScheduleFragment();
	}

	public void setTeamNumber(String teamNumber) {
		this.teamNumber = teamNumber;
		getTeamFromSettings = false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		Logging.info(this, "TeamScheduleFragment created", 3);
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_team_schedule, container, false);
		if (savedInstanceState != null) {
			if (getTeamFromSettings) {
				teamNumber = savedInstanceState.getString("teamNumber");
			}
			eventKey = savedInstanceState.getString("eventKey");
			Logging.info(this, "savedInstanceState teamNumber: " + teamNumber, 2);
		}
		listener();
		Constants.checkNoDataScreen(teamEventMatches, recyclerView, emptyView);
		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("teamNumber", teamNumber);
		outState.putString("eventKey", eventKey);
		Logging.info(this, "onSaveInstanceState", 3);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		view = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.fragment_team_schedule, null);
		listener();
		Logging.info(this, "Configuration Changed", 3);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (teamEventMatches.size() == 0)
			refresh(false);
	}

	/**
	 * refrest() loads data needed for this fragment.
	 * @param force
	 */
	public void refresh(boolean force) {
		Logging.info(this, "teamNumber: " + teamNumber, 2);
		Logging.info(this, "Data is being refreshed", 0);
		if (!teamNumber.equals("") && !eventKey.equals("")) {
			new LoadTeamSchedule(force).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			Logging.error(this, "Team or event key not set", 0);
		}
	}


	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals("teamNumber") || key.equals("eventKey")) {
			if (getTeamFromSettings) {
				teamNumber = sharedPreferences.getString("teamNumber", "");
			}
			eventKey = sharedPreferences.getString("eventKey", "");
			listener();
		}
	}

	/**
	 * listener() initializes all needed types on creation
	 */
	public void listener() {
		prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		prefs.registerOnSharedPreferenceChangeListener(TeamScheduleFragment.this);
		if (getTeamFromSettings) {
			teamNumber = prefs.getString("teamNumber", "");
		}
		eventKey = prefs.getString("eventKey", "");

		mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.accent);
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				if(getTeamFromSettings) {
					MainActivity.refresh();
				} else {
					refresh(false);
				}
			}
		});

		recyclerView = view.findViewById(R.id.rv);
		emptyView = view.findViewById(R.id.empty_view);
		adapter = new ScheduleAdapter(getContext(), teamEventMatches, teamNumber);
		LinearLayoutManager llm = new LinearLayoutManager(getContext());
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerView.setLayoutManager(llm);
		recyclerView.setAdapter(adapter);
	}

	class LoadTeamSchedule extends AsyncTask<Void, Void, Void> {

		boolean force;

		LoadTeamSchedule(boolean force) {
			this.force = force;
		}

		@Override
		protected void onPreExecute() {
			mSwipeRefreshLayout.setRefreshing(true);
			parser = new Parser<>("teamEventMatches", Constants.getEventTeamMatches
					("frc" + teamNumber, eventKey), new TypeToken<ArrayList<Match>>() {
			}.getType(),getActivity(), force);
		}

		@Override
		protected Void doInBackground(Void... params) {
			parser.fetchJSON(getTeamFromSettings);
			while (parser.parsingComplete) ;
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (parser.getData() != null) {
				teamEventMatches.clear();
				teamEventMatches.addAll(parser.getData());
				sort(teamEventMatches);
			}
			Constants.checkNoDataScreen(teamEventMatches, recyclerView, emptyView);
			Animations.loadAnimation(getContext(), recyclerView, adapter, firstLoad, true);
			if (firstLoad) firstLoad = false;
			mSwipeRefreshLayout.setRefreshing(false);
			mSwipeRefreshLayout.destroyDrawingCache();
			mSwipeRefreshLayout.clearAnimation();
		}
	}
}