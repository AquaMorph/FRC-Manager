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
import com.aquamorph.frcmanager.adapters.EventTeamAdapter;
import com.aquamorph.frcmanager.decoration.Divider;
import com.aquamorph.frcmanager.models.EventTeam;
import com.aquamorph.frcmanager.parsers.Parser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * Displays a list of teams at an event.
 *
 * @author Christian Colglazier
 * @version 3/11/2016
 */
public class TeamEventFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

	private static String TAG = "TeamEventFragment";
	Parser<EventTeam> parser;
	SharedPreferences prefs;
	SharedPreferences.Editor editor;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private RecyclerView recyclerView;
	private TextView emptyView;
	private RecyclerView.Adapter adapter;
	private ArrayList<EventTeam> teams = new ArrayList<>();
	private String eventKey = "";

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
		recyclerView.setItemAnimator(new SlideInLeftAnimator());
		emptyView = (TextView) view.findViewById(R.id.empty_view);
		adapter = new EventTeamAdapter(getContext(), teams);
		LinearLayoutManager llm = new LinearLayoutManager(getContext());
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerView.setAdapter(new AlphaInAnimationAdapter(adapter));
		if (Constants.isLargeScreen(getContext())) {
			recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
		} else {
			recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
		}
		recyclerView.addItemDecoration(new Divider(getContext(), 2, 72));

		prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		prefs.registerOnSharedPreferenceChangeListener(TeamEventFragment.this);
		eventKey = prefs.getString("eventKey", "");

		parser = new Parser<>("eventTeams", Constants.getEventTeams(eventKey),
				new TypeToken<ArrayList<EventTeam>>() {
				}.getType(), getContext());

		refresh();

		return view;
	}

	/**
	 * refrest() loads data needed for this fragment.
	 */
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
			parser.fetchJSON(true);
			while (parser.parsingComplete) ;
			teams.clear();
			teams.addAll(parser.getData());
			Collections.sort(teams);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			adapter.notifyDataSetChanged();
			if (teams.isEmpty()) {
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
