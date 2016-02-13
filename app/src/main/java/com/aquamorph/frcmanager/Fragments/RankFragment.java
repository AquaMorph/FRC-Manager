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

import com.aquamorph.frcmanager.DividerIndented;
import com.aquamorph.frcmanager.R;
import com.aquamorph.frcmanager.adapters.RankAdapter;
import com.aquamorph.frcmanager.parsers.RankParser;

import java.util.ArrayList;

public class RankFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

	private static String TAG = "RankFragment";
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private RecyclerView recyclerView;
	private RecyclerView.Adapter adapter;
	private ArrayList<String[]> ranks = new ArrayList<>();
	private String eventKey;
	RankParser rankParser = new RankParser();

	public static RankFragment newInstance() {
		RankFragment fragment = new RankFragment();
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
		adapter = new RankAdapter(getContext(), ranks);
		LinearLayoutManager llm = new LinearLayoutManager(getContext());
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(llm);
		recyclerView.addItemDecoration(new DividerIndented(getContext()) {
		});

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		prefs.registerOnSharedPreferenceChangeListener(RankFragment.this);
		eventKey = prefs.getString("eventKey", "");

		refresh();

		return view;
	}

	private void refresh() {
		if (!eventKey.equals("")) {
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
	}

	class LoadRanks extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			mSwipeRefreshLayout.setRefreshing(true);
		}

		@Override
		protected Void doInBackground(Void... params) {
			rankParser.fetchJSON(eventKey, getContext());
			while (rankParser.parsingComplete) ;
			ranks.clear();
			ranks.addAll(rankParser.getRankings());
//			Collections.sort(ranks);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			adapter.notifyDataSetChanged();
			mSwipeRefreshLayout.setRefreshing(false);
		}
	}
}
