package com.aquamorph.frcmanager.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
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
import com.aquamorph.frcmanager.utils.Constants;
import com.aquamorph.frcmanager.utils.Data;

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
		adapter = new AllianceAdapter(getContext(), Data.alliances);
		LinearLayoutManager llm = new LinearLayoutManager(getContext());
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(llm);
		Constants.checkNoDataScreen(Data.alliances, recyclerView, emptyView);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (Data.alliances.size() == 0) {
			refresh(false);
		}
	}

	/**
	 * refrest() loads data needed for this fragment.
	 */
	public void refresh(boolean force) {
		if (!Data.eventKey.equals("")) {
			new LoadAlliances(force).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}

	class LoadAlliances extends AsyncTask<Void, Void, Void> {

		boolean force;

		public LoadAlliances(boolean force) {
			this.force = force;
		}

		@Override
		protected void onPreExecute() {
			swipeRefreshLayout.setRefreshing(true);
		}

		@Override
		protected Void doInBackground(Void... params) {
			while (!Data.allianceParsingComplete) ;
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (Data.alliances != null) {
				Constants.checkNoDataScreen(Data.alliances, recyclerView, emptyView);
				Animations.loadAnimation(getContext(), recyclerView, adapter, firstLoad, true);
				if (firstLoad) firstLoad = false;
			}
			swipeRefreshLayout.setRefreshing(false);
		}
	}
}
