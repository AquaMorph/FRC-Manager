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

import com.aquamorph.frcmanager.Divider;
import com.aquamorph.frcmanager.R;
import com.aquamorph.frcmanager.adapters.AwardAdapter;
import com.aquamorph.frcmanager.models.Award;
import com.aquamorph.frcmanager.parsers.AwardParser;

import java.util.ArrayList;

/**
 * Displays a list of awards at a event
 *
 * @author Christian Colglazier
 * @version 2/13/2016
 */
public class AwardFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {
	private String TAG = "AwardFragment";
	private SwipeRefreshLayout swipeRefreshLayout;
	private RecyclerView recyclerView;
	private RecyclerView.Adapter adapter;
	private ArrayList<Award> awards = new ArrayList<>();
	private String eventKey;

	public static AwardFragment newInstance() {
		AwardFragment fragment = new AwardFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_team_schedule, container, false);
		swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
		swipeRefreshLayout.setColorSchemeResources(R.color.accent);
		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				refresh();
			}
		});

		recyclerView = (RecyclerView) view.findViewById(R.id.rv);
		adapter = new AwardAdapter(getContext(), awards);
		LinearLayoutManager llm = new LinearLayoutManager(getContext());
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(llm);
		recyclerView.addItemDecoration(new Divider(getContext()));

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		prefs.registerOnSharedPreferenceChangeListener(AwardFragment.this);
		eventKey = prefs.getString("eventKey", "");

		refresh();
		return view;
	}

	private void refresh() {
		if (!eventKey.equals("")) {
			final LoadAwards loadAwards = new LoadAwards();
			loadAwards.execute();
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals("eventKey")) {
			eventKey = sharedPreferences.getString("eventKey", "");
			refresh();
		}
	}

	class LoadAwards extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			swipeRefreshLayout.setRefreshing(true);
		}

		@Override
		protected Void doInBackground(Void... params) {
			AwardParser awardParser = new AwardParser();
			awardParser.fetchJSON(eventKey, getContext());
			while (awardParser.parsingComplete) ;
			awards.clear();
			awards.addAll(awardParser.getAwards());
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			adapter.notifyDataSetChanged();
			swipeRefreshLayout.setRefreshing(false);
		}
	}
}
