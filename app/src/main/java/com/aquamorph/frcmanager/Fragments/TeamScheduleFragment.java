package com.aquamorph.frcmanager.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aquamorph.frcmanager.R;
import com.aquamorph.frcmanager.adapters.TeamScheduleAdapter;
import com.aquamorph.frcmanager.models.TeamEventMatches;
import com.aquamorph.frcmanager.parsers.TeamEventMatchesParsers;

import java.util.ArrayList;

public class TeamScheduleFragment extends Fragment {

	private String TAG = "TeamScheduleFragment";
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private RecyclerView recyclerView;
	private Adapter adapter;
	private ArrayList<TeamEventMatches> teamEventMatches = new ArrayList<>();
	TeamEventMatchesParsers teamEventMatchesParsers = new TeamEventMatchesParsers();

	public static TeamScheduleFragment newInstance() {
		TeamScheduleFragment fragment = new TeamScheduleFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_team_schedule, container, false);
		mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
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

		refresh();

		return view;
	}

	private void refresh() {
		final LoadTeamSchedule loadTeamSchedule = new LoadTeamSchedule();
		loadTeamSchedule.execute();
	}

	class LoadTeamSchedule extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			mSwipeRefreshLayout.setRefreshing(true);
		}

		@Override
		protected Void doInBackground(Void... params) {
			teamEventMatchesParsers.fetchJSON();
			while (teamEventMatchesParsers.parsingComplete) ;

			teamEventMatches.clear();
			teamEventMatches.addAll(teamEventMatchesParsers.getTeamEventMatches());
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Log.i(TAG, "Test: " + teamEventMatches.size());
			adapter.notifyDataSetChanged();
			mSwipeRefreshLayout.setRefreshing(false);
		}
	}

}