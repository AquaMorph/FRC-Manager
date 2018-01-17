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

import com.aquamorph.frcmanager.R;
import com.aquamorph.frcmanager.activities.MainActivity;
import com.aquamorph.frcmanager.adapters.AwardAdapter;
import com.aquamorph.frcmanager.decoration.Animations;
import com.aquamorph.frcmanager.decoration.Divider;
import com.aquamorph.frcmanager.models.Award;
import com.aquamorph.frcmanager.network.Parser;
import com.aquamorph.frcmanager.utils.Constants;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Displays a list of awards at a event
 *
 * @author Christian Colglazier
 * @version 2/13/2016
 */
public class AwardFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

	SharedPreferences prefs;
	private String TAG = "AwardFragment";
	private SwipeRefreshLayout swipeRefreshLayout;
	private RecyclerView recyclerView;
	private TextView emptyView;
	private RecyclerView.Adapter adapter;
	private ArrayList<Award> awards = new ArrayList<>();
	private String eventKey = "";
	private Parser<ArrayList<Award>> parser;
	private Boolean firstLoad = true;

	/**
	 * newInstance creates and returns a new AwardFragment
	 *
	 * @return AwardFragment
	 */
	public static AwardFragment newInstance() {
		return new AwardFragment();
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
		swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
		swipeRefreshLayout.setColorSchemeResources(R.color.accent);
		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				MainActivity.refresh();
			}
		});

		recyclerView = view.findViewById(R.id.rv);
		emptyView = view.findViewById(R.id.empty_view);
		adapter = new AwardAdapter(getContext(), awards);
		LinearLayoutManager llm = new LinearLayoutManager(getContext());
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(llm);
		recyclerView.addItemDecoration(new Divider(getContext(), 2, 0));

		prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		prefs.registerOnSharedPreferenceChangeListener(AwardFragment.this);
		eventKey = prefs.getString("eventKey", "");

		if (savedInstanceState == null) refresh();
		Constants.checkNoDataScreen(awards, recyclerView, emptyView);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (awards.size() == 0)
			refresh();
	}

	/**
	 * refrest() loads data needed for this fragment.
	 */
	public void refresh() {
		if (!eventKey.equals("")) {
			new LoadAwards().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
			parser = new Parser<>("eventAwards", Constants.getEventAwards(eventKey),
					new TypeToken<ArrayList<Award>>() {
					}.getType(), getActivity());
		}

		@Override
		protected Void doInBackground(Void... params) {
			parser.fetchJSON(true);
			while (parser.parsingComplete) ;
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			awards.clear();
			awards.addAll(parser.getData());
			Constants.checkNoDataScreen(awards, recyclerView, emptyView);
			Animations.loadAnimation(getContext(), recyclerView, adapter, firstLoad, true);
			if (firstLoad) firstLoad = false;
			swipeRefreshLayout.setRefreshing(false);
		}
	}
}
