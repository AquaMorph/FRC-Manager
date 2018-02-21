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
import com.aquamorph.frcmanager.adapters.ScheduleAdapter;
import com.aquamorph.frcmanager.decoration.Animations;
import com.aquamorph.frcmanager.network.DataLoader;
import com.aquamorph.frcmanager.utils.Constants;

/**
 * Displays a list of matches at an event.
 *
 * @author Christian Colglazier
 * @version 2/20/2018
 */
public class EventScheduleFragment extends Fragment
		implements SharedPreferences.OnSharedPreferenceChangeListener, RefreshFragment {

	SharedPreferences prefs;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private RecyclerView recyclerView;
	private TextView emptyView;
	private RecyclerView.Adapter adapter;
	private Boolean firstLoad = true;

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
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		prefs.registerOnSharedPreferenceChangeListener(EventScheduleFragment.this);

		View view = inflater.inflate(R.layout.fragment_fastscroll, container, false);
		mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.accent);
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				MainActivity.refresh();
			}
		});

		recyclerView = view.findViewById(R.id.rv);
		emptyView = view.findViewById(R.id.empty_view);
		adapter = new ScheduleAdapter(getContext(), DataLoader.matchDC.data, DataLoader.teamNumber);
		LinearLayoutManager llm = new LinearLayoutManager(getContext());
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(llm);

		Constants.checkNoDataScreen(DataLoader.matchDC.data, recyclerView, emptyView);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (DataLoader.matchDC.data.size() == 0)
			refresh(false);
	}

	/**
	 * refresh reloads the event schedule and repopulates the listview
	 */
	public void refresh(boolean force) {
		if (!DataLoader.eventKey.equals("")) {
			new LoadEventSchedule(force).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals("teamNumber") || key.equals("eventKey")) {
			adapter = new ScheduleAdapter(getContext(), DataLoader.matchDC.data, DataLoader.teamNumber);
			LinearLayoutManager llm = new LinearLayoutManager(getContext());
			llm.setOrientation(LinearLayoutManager.VERTICAL);
			recyclerView.setAdapter(adapter);
			recyclerView.setLayoutManager(llm);
			refresh(true);
		}
	}

	class LoadEventSchedule extends AsyncTask<Void, Void, Void> {

		boolean force;

		public LoadEventSchedule(boolean force) {
			this.force = force;
		}

		@Override
		protected void onPreExecute() {
			mSwipeRefreshLayout.setRefreshing(true);
		}

		@Override
		protected Void doInBackground(Void... params) {
			while (!DataLoader.matchDC.complete) SystemClock.sleep(Constants.THREAD_WAIT_TIME);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Constants.checkNoDataScreen(DataLoader.matchDC.data, recyclerView, emptyView);
			Animations.loadAnimation(getContext(), recyclerView, adapter, firstLoad,
					DataLoader.matchDC.parser.isNewData());
			if (firstLoad) firstLoad = false;
			mSwipeRefreshLayout.setRefreshing(false);
		}
	}
}