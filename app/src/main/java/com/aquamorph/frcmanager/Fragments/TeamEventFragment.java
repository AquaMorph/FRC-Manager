package com.aquamorph.frcmanager.fragments;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aquamorph.frcmanager.DividerIndented;
import com.aquamorph.frcmanager.R;
import com.aquamorph.frcmanager.adapters.EventTeamAdapter;
import com.aquamorph.frcmanager.models.EventTeam;
import com.aquamorph.frcmanager.parsers.TeamEventParser;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Displays a list of teams at an event.
 *
 * @author Christian Colglazier
 * @version 3/11/2016
 */
public class TeamEventFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {
	private static String TAG = "TeamEventFragment";
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private RecyclerView recyclerView;
	private TextView emptyView;
	private RecyclerView.Adapter adapter;
	private ArrayList<EventTeam> teams = new ArrayList<>();
	private String eventKey = "";
	TeamEventParser teamEventParser = new TeamEventParser();
	SharedPreferences prefs;
	SharedPreferences.Editor editor;

	public static TeamEventFragment newInstance() {
		return new TeamEventFragment();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
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
		emptyView = (TextView) view.findViewById(R.id.empty_view);
		adapter = new EventTeamAdapter(getContext(), teams);
		LinearLayoutManager llm = new LinearLayoutManager(getContext());
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(llm);
		recyclerView.addItemDecoration(new DividerIndented(getContext()) {
		});

		prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		editor = prefs.edit();
		prefs.registerOnSharedPreferenceChangeListener(TeamEventFragment.this);
		eventKey = prefs.getString("eventKey", "");

		refresh();

		return view;
	}

	public void refresh() {
		if (!eventKey.equals("")) {
			final LoadEventTeams loadEventTeams = new LoadEventTeams();
			loadEventTeams.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals("eventKey")) {
			eventKey = sharedPreferences.getString("eventKey", "");
			refresh();
		}
	}

	class LoadEventTeams extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			mSwipeRefreshLayout.setRefreshing(true);
		}

		@Override
		protected Void doInBackground(Void... params) {
			teamEventParser.fetchJSON(eventKey, getContext());
			while (teamEventParser.parsingComplete) ;
			teams.clear();
			teams.addAll(teamEventParser.getTeams());
			Collections.sort(teams);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			editor.putString("teamRank", "");
			adapter.notifyDataSetChanged();
			if (teams.isEmpty()) {
				recyclerView.setVisibility(View.GONE);
				emptyView.setVisibility(View.VISIBLE);
			}
			else {
				recyclerView.setVisibility(View.VISIBLE);
				emptyView.setVisibility(View.GONE);
			}
			mSwipeRefreshLayout.setRefreshing(false);
		}
	}
}
