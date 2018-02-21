package com.aquamorph.frcmanager.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
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
import com.aquamorph.frcmanager.adapters.TeamAdapter;
import com.aquamorph.frcmanager.decoration.Animations;
import com.aquamorph.frcmanager.decoration.Divider;
import com.aquamorph.frcmanager.utils.Constants;
import com.aquamorph.frcmanager.utils.Data;

/**
 * Displays a list of teams at an event.
 *
 * @author Christian Colglazier
 * @version 2/20/2018
 */
public class TeamFragment extends Fragment implements RefreshFragment {

	private SwipeRefreshLayout mSwipeRefreshLayout;
	private RecyclerView recyclerView;
	private TextView emptyView;
	private RecyclerView.Adapter adapter;
	private Boolean firstLoad = true;

	/**
	 * newInstance creates and returns a new TeamFragment
	 *
	 * @return TeamFragment
	 */
	public static TeamFragment newInstance() {
		return new TeamFragment();
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
		adapter = new TeamAdapter(getContext(), Data.teamDC.data, Data.ranks);
		LinearLayoutManager llm = new LinearLayoutManager(getContext());
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerView.addItemDecoration(new Divider(getContext(), 2, 72));
		recyclerView.setAdapter(adapter);
		if (Constants.isLargeScreen(getContext())) {
			recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
		} else {
			recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
		}

		if (savedInstanceState == null) refresh(false);
		Constants.checkNoDataScreen(Data.teamDC.data, recyclerView, emptyView);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (Data.teamDC.data.size() == 0)
			refresh(false);
	}

	/**
	 * refrest() loads data needed for this fragment.
	 */
	public void refresh(boolean force) {
		if (!Data.eventKey.equals("") && !Data.teamNumber.equals("")) {
			new LoadEventTeams(force).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}


	class LoadEventTeams extends AsyncTask<Void, Void, Void> {

		boolean force;

		public LoadEventTeams(boolean force) {
			this.force = force;
		}

		@Override
		protected void onPreExecute() {
			mSwipeRefreshLayout.setRefreshing(true);
		}

		@Override
		protected Void doInBackground(Void... params) {
			while (!Data.teamDC.complete) SystemClock.sleep(Constants.THREAD_WAIT_TIME);
			while (!Data.rankParsingComplete) SystemClock.sleep(Constants.THREAD_WAIT_TIME);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Constants.checkNoDataScreen(Data.teamDC.data, recyclerView, emptyView);
			Animations.loadAnimation(getContext(), recyclerView, adapter, firstLoad, true);
			if (firstLoad) firstLoad = false;
			mSwipeRefreshLayout.setRefreshing(false);
		}

	}
}
