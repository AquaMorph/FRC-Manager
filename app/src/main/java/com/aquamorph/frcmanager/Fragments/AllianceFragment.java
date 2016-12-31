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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aquamorph.frcmanager.Constants;
import com.aquamorph.frcmanager.R;
import com.aquamorph.frcmanager.adapters.AllianceAdapter;
import com.aquamorph.frcmanager.decoration.Divider;
import com.aquamorph.frcmanager.models.Events;
import com.aquamorph.frcmanager.network.Parser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Displays a list of alliance for eliminations.
 *
 * @author Christian Colglazier
 * @version 10/20/2016
 */
public class AllianceFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

	private String TAG = "AllianceFragment";
	private SwipeRefreshLayout swipeRefreshLayout;
	private RecyclerView recyclerView;
	private TextView emptyView;
	private RecyclerView.Adapter adapter;
	private ArrayList<Events.Alliances> alliances = new ArrayList<>();
	private String eventKey = "";
	private Parser<Events> parser;

	/**
	 * newInstance creates and returns a new AllianceFragment
	 *
	 * @return AllianceFragment
	 */
	public static AllianceFragment newInstance() {
		return new AllianceFragment();
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
		swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
		swipeRefreshLayout.setColorSchemeResources(R.color.accent);
		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				refresh();
			}
		});

		recyclerView = (RecyclerView) view.findViewById(R.id.rv);
		recyclerView.addItemDecoration(new Divider(getContext(), 2, 72));
		emptyView = (TextView) view.findViewById(R.id.empty_view);
		adapter = new AllianceAdapter(getContext(), alliances);
		LinearLayoutManager llm = new LinearLayoutManager(getContext());
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(llm);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		prefs.registerOnSharedPreferenceChangeListener(AllianceFragment.this);
		eventKey = prefs.getString("eventKey", "");

		refresh();
		return view;
	}

	/**
	 * refrest() loads data needed for this fragment.
	 */
	public void refresh() {
		if (!eventKey.equals("")) {
			final LoadAlliances loadAlliances = new LoadAlliances();
			loadAlliances.execute();
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals("eventKey")) {
			eventKey = sharedPreferences.getString("eventKey", "");
			refresh();
		}
	}

	class LoadAlliances extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			swipeRefreshLayout.setRefreshing(true);
		}

		@Override
		protected Void doInBackground(Void... params) {
			parser = new Parser<>("Events",
					Constants.getEvent(eventKey), new
					TypeToken<Events>() {
					}.getType(), getContext());
			parser.fetchJSON(true);
			while (parser.parsingComplete) ;
			alliances.clear();
			alliances.addAll(new ArrayList<>(Arrays.asList(parser.getData().alliances)));
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			adapter.notifyDataSetChanged();
			if (alliances.isEmpty()) {
				recyclerView.setVisibility(View.GONE);
				emptyView.setVisibility(View.VISIBLE);
			} else {
				recyclerView.setVisibility(View.VISIBLE);
				emptyView.setVisibility(View.GONE);

			}
			swipeRefreshLayout.setRefreshing(false);
		}
	}
}
