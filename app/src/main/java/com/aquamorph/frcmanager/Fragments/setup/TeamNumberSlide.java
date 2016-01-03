package com.aquamorph.frcmanager.fragments.setup;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.aquamorph.frcmanager.R;

public class TeamNumberSlide extends Fragment {

	private String TAG = "TeamNumberSlide";
	private static EditText teamNumber;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.slide2, container, false);

		teamNumber = (EditText) view.findViewById(R.id.teamNumberEditText);
		return view;
	}

	public String getTeamNumber() {
		return teamNumber.getText().toString();
	}

	public void setTeamNumber(String team) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("teamNumber", team);
		editor.commit();
		Log.i(TAG, "Team Number: " + team);
	}
}
