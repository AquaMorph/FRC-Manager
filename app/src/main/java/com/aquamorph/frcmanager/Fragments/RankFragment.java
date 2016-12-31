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

import com.aquamorph.frcmanager.Constants;
import com.aquamorph.frcmanager.R;
import com.aquamorph.frcmanager.adapters.RankAdapter;
import com.aquamorph.frcmanager.decoration.Divider;
import com.aquamorph.frcmanager.models.EventTeam;
import com.aquamorph.frcmanager.network.Parser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

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
	private RecyclerView.Adapter<RankAdapter.MyViewHolder> adapter;
	private ArrayList<String[]> ranks = new ArrayList<>();
	private ArrayList<EventTeam> teams = new ArrayList<>();
	private String eventKey = "", teamNumber = "";
	private Parser<ArrayList<String[]>> parser;
	private Parser<ArrayList<EventTeam>> teamEventParser;
	private SharedPreferences prefs;

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
		recyclerView.addItemDecoration(new Divider(getContext(), 2, 72));
		emptyView = (TextView) view.findViewById(R.id.empty_view);
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

	private class LoadRanks extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			mSwipeRefreshLayout.setRefreshing(true);
			parser = new Parser<>("eventRank", Constants.getEventRanks(eventKey),
					new TypeToken<ArrayList<String[]>>() {
					}.getType(), getContext());
			teamEventParser = new Parser<>("eventTeams", Constants.getEventTeams(eventKey),
					new TypeToken<ArrayList<EventTeam>>() {
					}.getType(), getContext());
		}

		@Override
		protected Void doInBackground(Void... params) {
			teamEventParser.fetchJSON(true);
			while (teamEventParser.parsingComplete) ;
			teams.clear();
			teams.addAll(teamEventParser.getData());
			sort(teams);

			parser.fetchJSON(true);
			while (parser.parsingComplete) ;
			if (parser.getData() != null) {
				ranks.clear();
				ranks.addAll(parser.getData());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			SharedPreferences.Editor editor = prefs.edit();
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
			} else {
				recyclerView.setVisibility(View.VISIBLE);
				emptyView.setVisibility(View.GONE);
			}
			mSwipeRefreshLayout.setRefreshing(false);
		}
	}
}
