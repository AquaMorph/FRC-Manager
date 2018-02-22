package com.aquamorph.frcmanager.fragments;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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
import com.aquamorph.frcmanager.network.DataLoader;
import com.aquamorph.frcmanager.utils.Constants;

/**
 * Displays a list of awards at a event
 *
 * @author Christian Colglazier
 * @version 2/13/2016
 */
public class AwardFragment extends Fragment
		implements SharedPreferences.OnSharedPreferenceChangeListener, RefreshFragment {

	SharedPreferences prefs;
	private SwipeRefreshLayout swipeRefreshLayout;
	private RecyclerView recyclerView;
	private TextView emptyView;
	private RecyclerView.Adapter adapter;
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
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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
		adapter = new AwardAdapter(getContext(), DataLoader.awardDC.data);
		LinearLayoutManager llm = new LinearLayoutManager(getContext());
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(llm);
		recyclerView.addItemDecoration(new Divider(getContext(), 2, 0));

		prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		prefs.registerOnSharedPreferenceChangeListener(AwardFragment.this);

		if (savedInstanceState == null) refresh(false);
		Constants.checkNoDataScreen(DataLoader.awardDC.data, recyclerView, emptyView);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (DataLoader.awardDC.data.size() == 0)
			refresh(false);
	}

	/**
	 * refrest() loads dataLoader needed for this fragment
	 * @param force force reload dataLoader
	 */
	public void refresh(boolean force) {
		if (!DataLoader.eventKey.equals("")) {
			new LoadAwards(force).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals("eventKey")) {
			refresh(true);
		}
	}

	class LoadAwards extends AsyncTask<Void, Void, Void> {

		boolean force;

		LoadAwards(boolean force) {
			this.force = force;
		}

		@Override
		protected void onPreExecute() {
			swipeRefreshLayout.setRefreshing(true);
		}

		@Override
		protected Void doInBackground(Void... params) {
			while (!DataLoader.awardDC.complete) SystemClock.sleep(Constants.THREAD_WAIT_TIME);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Constants.checkNoDataScreen(DataLoader.awardDC.data, recyclerView, emptyView);
			Animations.loadAnimation(getContext(), recyclerView, adapter, firstLoad,
					DataLoader.awardDC.parser.isNewData());
			if (firstLoad) firstLoad = false;
			adapter = new AwardAdapter(getContext(), DataLoader.awardDC.data);
			recyclerView.setAdapter(adapter);
			swipeRefreshLayout.setRefreshing(false);
		}
	}
}
