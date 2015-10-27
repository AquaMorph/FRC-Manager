package com.aquamorph.frcmanager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TeamScheduleFragment extends Fragment {

	private String TAG = "TeamScheduleFragment";
//	private SwipeRefreshLayout mSwipeRefreshLayout;
	private RecyclerView recyclerView;
	private Adapter adapter;
	public String[] data = {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16"};

	public static TeamScheduleFragment newInstance() {
		TeamScheduleFragment fragment = new TeamScheduleFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_team_schedule, container, false);
//		mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
//		mSwipeRefreshLayout.setOnRefreshListener(this);

		recyclerView = (RecyclerView) view.findViewById(R.id.rv);
		adapter = new TeamScheduleAdapter(getContext(), data);
		LinearLayoutManager llm = new LinearLayoutManager(getContext());
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(llm);

		return view;
	}

}