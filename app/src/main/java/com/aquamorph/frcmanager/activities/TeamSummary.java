package com.aquamorph.frcmanager.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.aquamorph.frcmanager.R;
import com.aquamorph.frcmanager.fragments.TeamScheduleFragment;

/**
 * Activiy with a summary of the schedule for a given team number.
 *
 * @author Christian Colglazier
 * @version 3/29/2016
 */
public class TeamSummary extends AppCompatActivity {

	private String teamNumber = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			teamNumber = extras.getString("teamNumber").replaceAll("[^\\d.]", "");
		}

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		if (toolbar != null) {
			toolbar.setTitle(teamNumber);
		}
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		TeamScheduleFragment teamScheduleFragment = TeamScheduleFragment.newInstance();
		teamScheduleFragment.setTeamNumber(teamNumber);
		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, teamScheduleFragment).commit();
		MainActivity.theme(this);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
			default:
				this.finish();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}