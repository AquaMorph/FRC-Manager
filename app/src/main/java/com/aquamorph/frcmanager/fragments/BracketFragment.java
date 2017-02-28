package com.aquamorph.frcmanager.fragments;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aquamorph.frcmanager.R;
import com.aquamorph.frcmanager.models.Events;
import com.aquamorph.frcmanager.models.Match;
import com.aquamorph.frcmanager.network.Parser;
import com.aquamorph.frcmanager.utils.Constants;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * <p></p>
 *
 * @author Christian Colglazier
 * @version 2/27/2017
 */

public class BracketFragment extends Fragment implements
		SharedPreferences.OnSharedPreferenceChangeListener {

	private String TAG = "BracketFragment";
	private SharedPreferences prefs;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private TextView emptyView;
	private ArrayList<Match> eventMatches = new ArrayList<>();
	private ArrayList<Events.Alliances> alliances = new ArrayList<>();
	private String teamNumber = "", eventKey = "";
	private Parser<ArrayList<Match>> parserMatch;
	private Parser<Events> parserEvents;

	/**
	 * newInstance creates and returns a new BracketFragment
	 *
	 * @return BracketFragment
	 */
	public static BracketFragment newInstance() {
		return new BracketFragment();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		prefs.registerOnSharedPreferenceChangeListener(BracketFragment.this);
		teamNumber = prefs.getString("teamNumber", "");
		eventKey = prefs.getString("eventKey", "");

		View view = inflater.inflate(R.layout.bracket, container, false);
		mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.accent);
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				refresh();
			}
		});

		if (savedInstanceState == null) refresh();
//		Constants.checkNoDataScreen(eventMatches, view, emptyView);
		return view;
	}

	public void refresh() {
		if (!eventKey.equals("")) {
			new BracketFragment.LoadBracket().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
	 * Filters out qualification matches
	 */
	public void filterMatches() {
		ArrayList<Match> temp = new ArrayList<>();
		for (int i = 0; i < eventMatches.size(); i++) {
			String compLevel = eventMatches.get(i).comp_level;
			if (compLevel.equals("qf") || compLevel.equals("sf") || compLevel.equals("f")) {
				temp.add(eventMatches.get(i));
			}
		}
		eventMatches.clear();
		eventMatches.addAll(temp);
	}

	class LoadBracket extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			mSwipeRefreshLayout.setRefreshing(true);
			parserMatch = new Parser<>("eventMatches", Constants.getEventMatches(eventKey),
					new TypeToken<ArrayList<Match>>() {
					}.getType(), getActivity());
		}

		@Override
		protected Void doInBackground(Void... params) {
			parserMatch.fetchJSON(true);
			while (parserMatch.parsingComplete) ;
			eventMatches.clear();
			eventMatches.addAll(parserMatch.getData());
			Collections.sort(eventMatches);

			parserEvents = new Parser<>("Events",
					Constants.getEvent(eventKey), new
					TypeToken<Events>() {
					}.getType(), getActivity());
			parserEvents.fetchJSON(true);
			while (parserEvents.parsingComplete) ;
			alliances.clear();
			alliances.addAll(new ArrayList<>(Arrays.asList(parserEvents.getData().alliances)));
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
//			Constants.checkNoDataScreen(eventMatches, recyclerView, emptyView);
			filterMatches();
			mSwipeRefreshLayout.setRefreshing(false);
		}
	}
}
