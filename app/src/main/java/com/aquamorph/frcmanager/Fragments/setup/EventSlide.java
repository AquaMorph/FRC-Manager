package com.aquamorph.frcmanager.fragments.setup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.aquamorph.frcmanager.R;

import java.util.ArrayList;

public class EventSlide extends Fragment {

	Spinner eventSpinnder;
	private ArrayAdapter dataAdapter;
	ArrayList<String> eventList = new ArrayList<>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.event_slide, container, false);

		eventSpinnder = (Spinner) view.findViewById(R.id.event_spinner);
		eventList.add("NC Regional");
		eventList.add("SC Regional");

		dataAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, eventList);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		eventSpinnder.setAdapter(dataAdapter);

		return view;
	}
}
