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

import com.aquamorph.frcmanager.Constants;
import com.aquamorph.frcmanager.R;
import com.aquamorph.frcmanager.adapters.ScheduleAdapter;
import com.aquamorph.frcmanager.models.Match;
import com.aquamorph.frcmanager.network.Parser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Displays a list of matches at an event.
 *
 * @author Christian Colglazier
 * @version 3/29/2016
 */
public class EventScheduleFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

	SharedPreferences prefs;
	private String TAG = "TeamScheduleFragment";
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private RecyclerView recyclerView;
	private TextView emptyView;
	private RecyclerView.Adapter adapter;
	private ArrayList<Match> eventMatches = new ArrayList<>();
	private String teamNumber = "", eventKey = "";
	private Parser<ArrayList<Match>> parser;

	/**
	 * newInstance creates and returns a new EventScheduleFragment
	 *
	 * @return EventScheduleFragment
	 */
	public static EventScheduleFragment newInstance() {
		return new EventScheduleFragment();
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
		emptyView = (TextView) view.findViewById(R.id.empty_view);
		adapter = new ScheduleAdapter(getContext(), eventMatches, teamNumber);
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
	public void refresh() {
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
			adapter = new ScheduleAdapter(getContext(), eventMatches, teamNumber);
			LinearLayoutManager llm = new LinearLayoutManager(getContext());
			llm.setOrientation(LinearLayoutManager.VERTICAL);
			recyclerView.setAdapter(adapter);
			recyclerView.setLayoutManager(llm);
			refresh();
		}
	}

	class LoadEventSchedule extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			mSwipeRefreshLayout.setRefreshing(true);
			parser = new Parser<>("eventMatches", Constants.getEventMatches(eventKey), new TypeToken<ArrayList<Match>>() {
			}.getType(), getActivity());
		}

		@Override
		protected Void doInBackground(Void... params) {
			parser.fetchJSON(true);
			while (parser.parsingComplete) ;
			eventMatches.clear();
			eventMatches.addAll(parser.getData());
			Collections.sort(eventMatches);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			adapter.notifyDataSetChanged();
			if (eventMatches.isEmpty()) {
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