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
import com.aquamorph.frcmanager.network.DataLoader;
import com.aquamorph.frcmanager.utils.Constants;

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
				MainActivity.Companion.refresh();
			}
		});

		recyclerView = view.findViewById(R.id.rv);
		recyclerView.addItemDecoration(new Divider(getContext(), 2, 72));
		emptyView = view.findViewById(R.id.empty_view);
		adapter = new RankAdapter(getContext(), DataLoader.rankDC.data, DataLoader.teamDC.data);
		LinearLayoutManager llm = new LinearLayoutManager(getContext());
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerView.setAdapter(adapter);
		if (Constants.INSTANCE.isLargeScreen(getContext())) {
			recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
		} else {
			recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
		}

		if (savedInstanceState == null) refresh(false);
		Constants.INSTANCE.checkNoDataScreen(DataLoader.rankDC.data, recyclerView, emptyView);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (DataLoader.rankDC.data.size() == 0)
			refresh(false);
	}

	/**
	 * refrest() loads dataLoader needed for this fragment.
	 */
	public void refresh(boolean force) {
		if (!DataLoader.eventKey.equals("") && !DataLoader.teamNumber.equals("")
				&& getContext() != null) {
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
			if (mSwipeRefreshLayout != null) {
				mSwipeRefreshLayout.setRefreshing(true);
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			while (!DataLoader.teamDC.complete) SystemClock.sleep(Constants.THREAD_WAIT_TIME);
			while (!DataLoader.rankDC.complete) SystemClock.sleep(Constants.THREAD_WAIT_TIME);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if(!DataLoader.rankDC.data.isEmpty() && DataLoader.rankDC.data.get(0).getRankings() != null) {
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString("teamRank", "");
				for (int i = 0; i < DataLoader.rankDC.data.get(0).getRankings().length; i++) {
					if (DataLoader.rankDC.data.get(0).getRankings()[i].getTeam_key().equals("frc" + DataLoader.teamNumber)) {
						if (Integer.toString(DataLoader.rankDC.data.get(0).getRankings()[i].getRank()) != null) {
							editor.putString("teamRank",
									Integer.toString(DataLoader.rankDC.data.get(0).getRankings()[i].getRank()));
						}
						if (DataLoader.rankDC.data.get(0).getRankings()[i].getRecord() != null) {
							editor.putString("teamRecord", Rank.Companion.recordToString(
									DataLoader.rankDC.data.get(0).getRankings()[i].getRecord()));
						}
						editor.apply();
					}
				}
			}
			if (getContext() != null) {
				Constants.INSTANCE.checkNoDataScreen(DataLoader.rankDC.data, recyclerView, emptyView);
				Animations.INSTANCE.loadAnimation(getContext(), recyclerView, adapter, firstLoad,
					DataLoader.rankDC.parser.isNewData());
				if (firstLoad) firstLoad = false;
				adapter = new RankAdapter(getContext(), DataLoader.rankDC.data, DataLoader.teamDC.data);
				LinearLayoutManager llm = new LinearLayoutManager(getContext());
				llm.setOrientation(LinearLayoutManager.VERTICAL);
				recyclerView.setAdapter(adapter);
				mSwipeRefreshLayout.setRefreshing(false);
			}
		}
	}
}
