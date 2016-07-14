package com.aquamorph.frcmanager.fragments;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aquamorph.frcmanager.R;
import com.aquamorph.frcmanager.adapters.RankAdapter;
import com.aquamorph.frcmanager.decoration.DividerIndented;
import com.aquamorph.frcmanager.models.EventTeam;
import com.aquamorph.frcmanager.parsers.RankParser;
import com.aquamorph.frcmanager.parsers.TeamEventParser;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Displays the ranks of all the teams at an event.
 *
 * @author Christian Colglazier
 * @version 3/29/2016
 */
public class RankFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

	private static String TAG = "RankFragment";
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private RecyclerView recyclerView;
	private TextView emptyView;
	private RecyclerView.Adapter adapter;
	private ArrayList<String[]> ranks = new ArrayList<>();
	private ArrayList<EventTeam> teams = new ArrayList<>();
	private String eventKey = "", teamNumber = "";
	RankParser rankParser = new RankParser();
	TeamEventParser teamEventParser = new TeamEventParser();
	SharedPreferences prefs;
	SharedPreferences.Editor editor;

	/**
	 * newInstance creates and returns a new RankFragment
	 *
	 * @return RankFragment
	 */
	public static RankFragment newInstance() {
		return new RankFragment();
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

		prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

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
		adapter = new RankAdapter(getContext(), ranks, teams);
		LinearLayoutManager llm = new LinearLayoutManager(getContext());
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerView.setAdapter(adapter);
//		recyclerView.setLayoutManager(llm);
		recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
		recyclerView.addItemDecoration(new DividerIndented(getContext()) {
		});

		prefs.registerOnSharedPreferenceChangeListener(RankFragment.this);
		eventKey = prefs.getString("eventKey", "");
		teamNumber = prefs.getString("teamNumber", "0000");

		refresh();

		return view;
	}

	/**
	 * refrest() loads data needed for this fragment.
	 */
	public void refresh() {
		if (!eventKey.equals("") && !teamNumber.equals("")) {
			final LoadRanks loadRanks = new LoadRanks();
			loadRanks.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals("eventKey")) {
			eventKey = sharedPreferences.getString("eventKey", "");
			rankParser.setData(getContext(), "");
			refresh();
		}
		if (key.equals("teamNumber")) {
			teamNumber = sharedPreferences.getString("teamNumber", "");
			refresh();
		}
	}

	class LoadRanks extends AsyncTask<Void, Void, Void> {

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

			rankParser.fetchJSON(eventKey, getContext());
			while (rankParser.parsingComplete) ;
			ranks.clear();
			ranks.addAll(rankParser.getRankings());
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			editor = prefs.edit();
			editor.putString("teamRank", "");
			for (int i = 0; i < ranks.size(); i++) {
				if (ranks.get(i)[1].equals(teamNumber)) {
					editor.putString("teamRank", ranks.get(i)[0]);
				}
				editor.apply();
			}
			adapter.notifyDataSetChanged();
			if (ranks.isEmpty()) {
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
