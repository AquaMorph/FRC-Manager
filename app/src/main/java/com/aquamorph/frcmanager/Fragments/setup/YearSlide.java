package com.aquamorph.frcmanager.fragments.setup;

/**
 * Asks the user for the year they want to get events from.
 *
 * @author Christian Colglazier
 * @version 2/13/2016
 */

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.aquamorph.frcmanager.Constants;
import com.aquamorph.frcmanager.R;

public class YearSlide extends Fragment implements AdapterView.OnItemSelectedListener {

	String TAG = "YearSlide";
	Spinner yearSpinnder;
	private SharedPreferences prefs;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.year_slide, container, false);
		yearSpinnder = (Spinner) view.findViewById(R.id.year_spinner);
		yearSpinnder.setOnItemSelectedListener(this);
		return view;
	}

	/**
	 * Stores the year so SharedPreferences
	 *
	 * @param year
	 */
	public void setYear(String year) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("year", year);
		if(Constants.TRACTING_LEVEL > 0) {
			Log.i(TAG, "Year set to: " + year);
		}
		editor.apply();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		((TextView) yearSpinnder.getSelectedView()).setTextColor(getResources().getColor(R.color.icons));
		setYear(parent.getItemAtPosition(position).toString());
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {}
}
