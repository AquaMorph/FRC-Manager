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
import com.aquamorph.frcmanager.models.Rank;
import com.aquamorph.frcmanager.utils.Constants;
import com.aquamorph.frcmanager.utils.Data;

/**
 * Displays the ranks of all the teams at an event.
 *
 * @author Christian Colglazier
 * @version 10/20/2016
 */
public class RankFragment extends Fragment implements RefreshFragment {

	private SwipeRefreshLayout mSwipeRefreshLayout;
	private RecyclerView recyclerView;
	private TextView emptyView;
	private RecyclerView.Adapter adapter;
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
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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
		adapter = new RankAdapter(getContext(), Data.ranks, Data.teams);
		LinearLayoutManager llm = new LinearLayoutManager(getContext());
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerView.setAdapter(adapter);
		if (Constants.isLargeScreen(getContext())) {
			recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
		} else {
			recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
		}

		if (savedInstanceState == null) refresh(false);
		Constants.checkNoDataScreen(Data.ranks, recyclerView, emptyView);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (Data.ranks.size() == 0)
			refresh(false);
	}

	/**
	 * refrest() loads data needed for this fragment.
	 */
	public void refresh(boolean force) {
		if (!Data.eventKey.equals("") && !Data.teamNumber.equals("")) {
			new LoadRanks(force).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}

	private class LoadRanks extends AsyncTask<Void, Void, Void> {

		boolean force;

		LoadRanks(boolean force) {
			this.force = force;
		}

		@Override
		protected void onPreExecute() {
			mSwipeRefreshLayout.setRefreshing(true);
		}

		@Override
		protected Void doInBackground(Void... params) {
			while (!Data.teamParsingComplete) SystemClock.sleep(Constants.THREAD_WAIT_TIME);
			while (!Data.rankParsingComplete) SystemClock.sleep(Constants.THREAD_WAIT_TIME);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString("teamRank", "");
				for (int i = 0; i < Data.ranks.get(0).rankings.length; i++) {
					if (Data.ranks.get(0).rankings[i].team_key.equals("frc" + Data.teamNumber)) {
						editor.putString("teamRank",
								Integer.toString(Data.ranks.get(0).rankings[i].rank));
						editor.putString("teamRecord",
								Rank.recordToString(Data.ranks.get(0).rankings[i].record));
						editor.apply();
					}
				}
				Constants.checkNoDataScreen(Data.ranks, recyclerView, emptyView);
				Animations.loadAnimation(getContext(), recyclerView, adapter, firstLoad, true);
				if (firstLoad) firstLoad = false;
				mSwipeRefreshLayout.setRefreshing(false);
			}

	}
}
