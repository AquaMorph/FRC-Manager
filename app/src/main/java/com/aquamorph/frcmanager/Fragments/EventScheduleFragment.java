package com.aquamorph.frcmanager.fragments;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aquamorph.frcmanager.R;
import com.aquamorph.frcmanager.adapters.TeamScheduleAdapter;
import com.aquamorph.frcmanager.models.Match;
import com.aquamorph.frcmanager.parsers.EventMatchesParsers;

import java.util.ArrayList;
import java.util.Collections;

public class EventScheduleFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

	private String TAG = "TeamScheduleFragment";
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private RecyclerView recyclerView;
	private RecyclerView.Adapter adapter;
	private ArrayList<Match> eventMatches = new ArrayList<>();
	private String teamNumber, eventKey;
	private EventMatchesParsers eventMatchesParsers = new EventMatchesParsers();

	/**
	 * newInstance creates and returns a new EventScheduleFragment
	 *
	 * @return EventScheduleFragment
	 */
	public static EventScheduleFragment newInstance() {
		EventScheduleFragment fragment = new EventScheduleFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		prefs.registerOnSharedPreferenceChangeListener(EventScheduleFragment.this);
		teamNumber = prefs.getString("teamNumber", "");
		eventKey = prefs.getString("eventKey", "");

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
		adapter = new TeamScheduleAdapter(getContext(), eventMatches, teamNumber);
		LinearLayoutManager llm = new LinearLayoutManager(getContext());
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(llm);

		refresh();

		return view;
	}

	/**
	 * refresh reloads the event schedule and repopulates the listview
	 */
	private void refresh() {
		if (!eventKey.equals("")) {
			final LoadEventSchedule loadEventSchedule = new LoadEventSchedule();
			loadEventSchedule.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals("teamNumber") || key.equals("eventKey")) {
			teamNumber = sharedPreferences.getString("teamNumber", "");
			eventKey = sharedPreferences.getString("eventKey", "");
			refresh();
		}
	}

	/**
	 *
	 */
	class LoadEventSchedule extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			mSwipeRefreshLayout.setRefreshing(true);
		}

		@Override
		protected Void doInBackground(Void... params) {
			eventMatchesParsers.fetchJSON(eventKey, getContext());
			while (eventMatchesParsers.parsingComplete) ;
			eventMatches.clear();
			eventMatches.addAll(eventMatchesParsers.getEventMatches());
			Collections.sort(eventMatches);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			adapter.notifyDataSetChanged();
			mSwipeRefreshLayout.setRefreshing(false);
		}
	}
}