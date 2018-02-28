package com.aquamorph.frcmanager.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
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
import com.aquamorph.frcmanager.adapters.AllianceAdapter;
import com.aquamorph.frcmanager.decoration.Animations;
import com.aquamorph.frcmanager.decoration.Divider;
import com.aquamorph.frcmanager.network.DataLoader;
import com.aquamorph.frcmanager.utils.Constants;

/**
 * Displays a list of alliance for eliminations.
 *
 * @author Christian Colglazier
 * @version 2/20/2018
 */
public class AllianceFragment extends Fragment implements RefreshFragment {

	private SwipeRefreshLayout swipeRefreshLayout;
	private RecyclerView recyclerView;
	private TextView emptyView;
	private RecyclerView.Adapter adapter;
	private Boolean firstLoad = true;

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
		recyclerView.addItemDecoration(new Divider(getContext(), 2, 72));
		emptyView = view.findViewById(R.id.empty_view);
		adapter = new AllianceAdapter(getContext(), DataLoader.allianceDC.data);
		LinearLayoutManager llm = new LinearLayoutManager(getContext());
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(llm);
		Constants.checkNoDataScreen(DataLoader.allianceDC.data, recyclerView, emptyView);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (DataLoader.allianceDC.data.size() == 0) {
			refresh(false);
		}
	}

	/**
	 * refresh() loads dataLoader needed for this fragment.
	 */
	public void refresh(boolean force) {
		new LoadAlliances(force).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	class LoadAlliances extends AsyncTask<Void, Void, Void> {

		boolean force;

		public LoadAlliances(boolean force) {
			this.force = force;
		}

		@Override
		protected void onPreExecute() {
			if (swipeRefreshLayout != null) {
				swipeRefreshLayout.setRefreshing(true);
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			while (!DataLoader.allianceDC.complete) SystemClock.sleep(Constants.THREAD_WAIT_TIME);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (getContext() != null) {
				Constants.checkNoDataScreen(DataLoader.allianceDC.data, recyclerView, emptyView);
				Animations.loadAnimation(getContext(), recyclerView, adapter, firstLoad,
						DataLoader.allianceDC.parser.isNewData());
				if (firstLoad) firstLoad = false;
				adapter = new AllianceAdapter(getContext(), DataLoader.allianceDC.data);
				recyclerView.setAdapter(adapter);
				swipeRefreshLayout.setRefreshing(false);
			}
		}
	}
}
