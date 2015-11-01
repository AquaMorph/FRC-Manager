package com.aquamorph.frcmanager.fragments;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aquamorph.frcmanager.R;
import com.aquamorph.frcmanager.adapters.TeamScheduleAdapter;
import com.aquamorph.frcmanager.models.Match;
import com.aquamorph.frcmanager.parsers.TeamEventMatchesParsers;

import java.util.ArrayList;
import java.util.Collections;

public class TeamScheduleFragment extends Fragment implements OnSharedPreferenceChangeListener {

	private String TAG = "TeamScheduleFragment";
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private RecyclerView recyclerView;
	private Adapter adapter;
	private ArrayList<Match> teamEventMatches = new ArrayList<>();
	private String teamNumber;

	public static TeamScheduleFragment newInstance() {
		TeamScheduleFragment fragment = new TeamScheduleFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_team_schedule, container, false);
		mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.accent);
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				refresh();
			}
		});

		recyclerView = (RecyclerView) view.findViewById(R.id.rv);
		adapter = new TeamScheduleAdapter(getContext(), teamEventMatches);
		LinearLayoutManager llm = new LinearLayoutManager(getContext());
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(llm);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		prefs.registerOnSharedPreferenceChangeListener(TeamScheduleFragment.this);
		teamNumber = prefs.getString("teamNumber", "0000");

		refresh();

		return view;
	}

	private void refresh() {
		final LoadTeamSchedule loadTeamSchedule = new LoadTeamSchedule();
		loadTeamSchedule.execute();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		teamNumber = sharedPreferences.getString("teamNumber", "0000");
		refresh();
	}

	class LoadTeamSchedule extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			mSwipeRefreshLayout.setRefreshing(true);
		}

		@Override
		protected Void doInBackground(Void... params) {
			TeamEventMatchesParsers teamEventMatchesParsers = new TeamEventMatchesParsers();
			teamEventMatchesParsers.fetchJSON("frc" + teamNumber, "ncre");
			while (teamEventMatchesParsers.parsingComplete) ;
			teamEventMatches.clear();
			teamEventMatches.addAll(teamEventMatchesParsers.getTeamEventMatches());
			Collections.sort(teamEventMatches);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			adapter.notifyDataSetChanged();
			mSwipeRefreshLayout.setRefreshing(false);
		}
	}
}