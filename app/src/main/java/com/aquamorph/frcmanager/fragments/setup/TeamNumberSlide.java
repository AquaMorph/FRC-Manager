package com.aquamorph.frcmanager.fragments.setup;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.aquamorph.frcmanager.R;

/**
 * Fragment slide that asks the user their team number.
 *
 * @author Christian Colglazier
 * @version 3/29/2016
 */
public class TeamNumberSlide extends Fragment {

	private String TAG = "TeamNumberSlide";
	private Boolean numberSet = false;
	private static EditText teamNumber;
	private SharedPreferences prefs;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.team_number_slide, container, false);
		teamNumber = (EditText) view.findViewById(R.id.teamNumberEditText);
		return view;
	}

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
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("teamNumber", team);
		editor.apply();
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