package com.aquamorph.frcmanager.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.aquamorph.frcmanager.R;
import com.aquamorph.frcmanager.fragments.TeamScheduleFragment;

/**
 * <p></p>
 *
 * @author Christian Colglazier
 * @version 3/25/2016
 */
public class TeamSummary extends AppCompatActivity {
	private String teamNumber = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			teamNumber = extras.getString("teamNumber");
		}

		MainActivity.theme(this);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		if (toolbar != null) {
			toolbar.setTitle(teamNumber);
		}
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		TeamScheduleFragment teamScheduleFragment = TeamScheduleFragment.newInstance();
				teamScheduleFragment.setTeamNumber(teamNumber);
		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, teamScheduleFragment).commit();
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		switch (item.getItemId()) {
			case android.R.id.home:
				this.finish();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}