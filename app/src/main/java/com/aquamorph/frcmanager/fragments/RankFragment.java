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
import com.aquamorph.frcmanager.activities.MainActivity;
import com.aquamorph.frcmanager.adapters.RankAdapter;
import com.aquamorph.frcmanager.decoration.Animations;
import com.aquamorph.frcmanager.decoration.Divider;
import com.aquamorph.frcmanager.models.Team;
import com.aquamorph.frcmanager.models.Rank;
import com.aquamorph.frcmanager.network.Parser;
import com.aquamorph.frcmanager.utils.Constants;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Locale;

import static java.util.Collections.sort;

/**
 * Displays the ranks of all the teams at an event.
 *
 * @author Christian Colglazier
 * @version 10/20/2016
 */
public class RankFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

	private static String TAG = "RankFragment";
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private RecyclerView recyclerView;
	private TextView emptyView;
	private RecyclerView.Adapter adapter;
	private ArrayList<Rank> ranks = new ArrayList<>();
	private ArrayList<Team> teams = new ArrayList<>();
	private String eventKey = "", teamNumber = "";
	private Parser<Rank> parser;
	private Parser<ArrayList<Team>> teamEventParser;
	private SharedPreferences prefs;
	private Boolean firstLoad = true;

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

		mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.accent);
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				MainActivity.refresh();
			}
		});

		recyclerView = view.findViewById(R.id.rv);
		recyclerView.addItemDecoration(new Divider(getContext(), 2, 72));
		emptyView = view.findViewById(R.id.empty_view);
		adapter = new RankAdapter(getContext(), ranks, teams);
		LinearLayoutManager llm = new LinearLayoutManager(getContext());
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerView.setAdapter(adapter);
		if (Constants.isLargeScreen(getContext())) {
			recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
		} else {
			recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
		}

		prefs.registerOnSharedPreferenceChangeListener(RankFragment.this);
		eventKey = prefs.getString("eventKey", "");
		teamNumber = prefs.getString("teamNumber", "0000");

		if (savedInstanceState == null) refresh(false);
		Constants.checkNoDataScreen(ranks, recyclerView, emptyView);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (ranks.size() == 0)
			refresh(false);
	}

	/**
	 * refrest() loads data needed for this fragment.
	 */
	public void refresh(Boolean force) {
		if (!eventKey.equals("") && !teamNumber.equals("")) {
			new LoadRanks(force).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
				refresh(true);
			}
		}
		if (key.equals("teamNumber")) {
			teamNumber = sharedPreferences.getString("teamNumber", "");
			if (!teamNumber.equals("")) {
				refresh(true);
			}
		}
	}

	private class LoadRanks extends AsyncTask<Void, Void, Void> {

		boolean force;

		public LoadRanks(boolean force) {
			this.force = force;
		}

		@Override
		protected void onPreExecute() {
			mSwipeRefreshLayout.setRefreshing(true);
			parser = new Parser<>("eventRank", Constants.getEventRanks(eventKey),
					new TypeToken<Rank>(){}.getType(), getActivity(), force);
			teamEventParser = new Parser<>("eventTeams", Constants.getEventTeams(eventKey),
					new TypeToken<ArrayList<Team>>() {}.getType(), getActivity(),force);
		}

		@Override
		protected Void doInBackground(Void... params) {
			teamEventParser.fetchJSON(true);
			while (teamEventParser.parsingComplete) ;
			parser.fetchJSON(true);
			while (parser.parsingComplete) ;
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			teams.clear();
			if (teamEventParser.getData() != null) {
				teams.addAll(teamEventParser.getData());
				sort(teams);

				if (parser.getData() != null) {
					ranks.clear();
					ranks.add(parser.getData());
				}

				SharedPreferences.Editor editor = prefs.edit();
				editor.putString("teamRank", "");
				for (int i = 0; i < ranks.get(0).rankings.length; i++) {
					if (ranks.get(0).rankings[i].team_key.equals("frc" + teamNumber)) {
						editor.putString("teamRank",
								Integer.toString(ranks.get(0).rankings[i].rank));
						editor.putString("teamRecord",
								Rank.recordToString(ranks.get(0).rankings[i].record));
						editor.apply();
					}
				}
				Constants.checkNoDataScreen(ranks, recyclerView, emptyView);
				Animations.loadAnimation(getContext(), recyclerView, adapter, firstLoad, true);
				if (firstLoad) firstLoad = false;
			}
			mSwipeRefreshLayout.setRefreshing(false);
		}
	}
}
