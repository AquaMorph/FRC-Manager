package com.aquamorph.frcmanager.fragments.setup;

/**
 * Asks the user for the year they want to get events from.
 *
 * @author Christian Colglazier
 * @version 2/13/2016
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.aquamorph.frcmanager.R;
import com.aquamorph.frcmanager.utils.AppConfig;

public class YearSlide extends Fragment implements AdapterView.OnItemSelectedListener {

	Spinner yearSpinnder;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.year_slide, container, false);
		yearSpinnder = view.findViewById(R.id.year_spinner);
		yearSpinnder.setOnItemSelectedListener(this);
		return view;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		((TextView) yearSpinnder.getSelectedView()).setTextColor(getResources().getColor(R.color.icons));
		AppConfig.setYear(parent.getItemAtPosition(position).toString(), getContext());
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {}
}
