package com.aquamorph.frcmanager.fragments.setup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.aquamorph.frcmanager.BuildConfig;
import com.aquamorph.frcmanager.R;
import com.aquamorph.frcmanager.utils.AppConfig;

/**
 * Fragment slide that asks the user their team number.
 *
 * @author Christian Colglazier
 * @version 3/29/2016
 */
public class TeamNumberSlide extends Fragment  implements AdapterView.OnItemSelectedListener {

	Spinner yearSpinnder;
	private Boolean numberSet = false;
	private EditText teamNumber;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.team_number_slide, container, false);
		teamNumber = view.findViewById(R.id.teamNumberEditText);
		yearSpinnder = view.findViewById(R.id.year_spinner);
		yearSpinnder.setOnItemSelectedListener(this);
		if(BuildConfig.APP_DEBUG) yearSpinnder.setVisibility(View.VISIBLE);
		return view;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		((TextView) yearSpinnder.getSelectedView()).setTextColor(getResources().getColor(R.color.icons));
		AppConfig.setYear(parent.getItemAtPosition(position).toString(), getContext());
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {}

	/**
	 * getTeamNumber() gets the team number entered by the user.
	 *
	 * @return team number
	 */
	public String getTeamNumber() {
		return teamNumber.getText().toString();
	}

	/**
	 * setTeamNumber() saves the team number to shared preferences.
	 *
	 * @param team number
	 */
	public void setTeamNumber(String team) {
		AppConfig.setTeamNumber(team, getContext());
		numberSet = false;
	}

	/**
	 * isNumberSet() returns if the team number has been set.
	 *
	 * @return is team number set
	 */
	public Boolean isNumberSet() {
		return numberSet;
	}
}
