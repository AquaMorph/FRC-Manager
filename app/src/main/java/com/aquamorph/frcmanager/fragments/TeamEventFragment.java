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

import com.aquamorph.frcmanager.Animations;
import com.aquamorph.frcmanager.Constants;
import com.aquamorph.frcmanager.R;
import com.aquamorph.frcmanager.adapters.EventTeamAdapter;
import com.aquamorph.frcmanager.decoration.Divider;
import com.aquamorph.frcmanager.models.EventTeam;
import com.aquamorph.frcmanager.network.Parser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import static java.util.Collections.sort;

/**
 * Displays a list of teams at an event.
 *
 * @author Christian Colglazier
 * @version 3/11/2016
 */
public class TeamEventFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

	private static String TAG = "TeamEventFragment";
	private Parser<ArrayList<EventTeam>> parser;
	private Parser<ArrayList<String[]>> rankParser;
	private SharedPreferences prefs;
	private SharedPreferences.Editor editor;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private RecyclerView recyclerView;
	private TextView emptyView;
	private RecyclerView.Adapter adapter;
	private ArrayList<EventTeam> teams = new ArrayList<>();
	private ArrayList<String[]> ranks = new ArrayList<>();
	private String eventKey = "", teamNumber = "";
	private Boolean firstLoad = true;

	/**
	 * newInstance creates and returns a new TeamEventFragment
	 *
	 * @return TeamEventFragment
	 */
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
		adapter = new EventTeamAdapter(getContext(), teams, ranks);
		LinearLayoutManager llm = new LinearLayoutManager(getContext());
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerView.addItemDecoration(new Divider(getContext(), 2, 72));
		recyclerView.setAdapter(adapter);
		if (Constants.isLargeScreen(getContext())) {
			recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
		} else {
			recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
		}
		prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		prefs.registerOnSharedPreferenceChangeListener(TeamEventFragment.this);
		eventKey = prefs.getString("eventKey", "");
		teamNumber = prefs.getString("teamNumber", "0000");

		if (savedInstanceState == null) refresh();
		Constants.checkNoDataScreen(teams, recyclerView, emptyView);
		return view;
	}

	/**
	 * refrest() loads data needed for this fragment.
	 */
	public void refresh() {
		if (!eventKey.equals("") && !teamNumber.equals("")) {
			new LoadEventTeams().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals("eventKey")) {
			eventKey = sharedPreferences.getString("eventKey", "");
			if (parser != null) {
				parser.storeData("");
			}
			if (!eventKey.equals("")) {
				refresh();
			}
		}
		if (key.equals("teamNumber")) {
			teamNumber = sharedPreferences.getString("teamNumber", "");
			if (!teamNumber.equals("")) {
				refresh();
			}
		}
	}

	class LoadEventTeams extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			mSwipeRefreshLayout.setRefreshing(true);
			parser = new Parser<>("eventTeams", Constants.getEventTeams(eventKey),
					new TypeToken<ArrayList<EventTeam>>() {
					}.getType(), getActivity());
			rankParser = new Parser<>("eventRank", Constants.getEventRanks(eventKey),
					new TypeToken<ArrayList<String[]>>() {
					}.getType(), getActivity());
		}

		@Override
		protected Void doInBackground(Void... params) {
			parser.fetchJSON(true);
			while (parser.parsingComplete) ;
			teams.clear();
			teams.addAll(parser.getData());
			sort(teams);

			rankParser.fetchJSON(true);
			while (rankParser.parsingComplete) ;
			if (rankParser.getData() != null) {
				ranks.clear();
				ranks.addAll(rankParser.getData());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Constants.checkNoDataScreen(teams, recyclerView, emptyView);
			Animations.loadAnimation(getContext(), recyclerView, adapter, firstLoad, true);
			if (firstLoad) firstLoad = false;
			mSwipeRefreshLayout.setRefreshing(false);
		}
	}
}
