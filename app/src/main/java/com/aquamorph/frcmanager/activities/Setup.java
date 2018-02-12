package com.aquamorph.frcmanager.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.aquamorph.frcmanager.utils.Constants;
import com.aquamorph.frcmanager.R;
import com.aquamorph.frcmanager.fragments.setup.EventSlide;
import com.aquamorph.frcmanager.fragments.setup.Slide;
import com.aquamorph.frcmanager.fragments.setup.TeamNumberSlide;
import com.aquamorph.frcmanager.fragments.setup.YearSlide;
import com.github.paolorotolo.appintro.AppIntro;

/**
 * App set up activity gets the desired year, team, and event.
 *
 * @author Christian Colglazier
 * @version 3/29/2016
 */
public class Setup extends AppIntro {

	private String TAG = "Setup";
	private YearSlide yearSlide;
	private TeamNumberSlide teamNumberSlide;
	private EventSlide eventSlide;
	private int counter = 0;

	// Please DO NOT override onCreate. Use init.
	@Override
	public void init(Bundle savedInstanceState) {
		yearSlide = new YearSlide();
		teamNumberSlide = new TeamNumberSlide();
		eventSlide = new EventSlide();

		addSlide(Slide.newInstance(R.layout.first_slide));
		addSlide(yearSlide);
		addSlide(teamNumberSlide);
		addSlide(eventSlide);

		setBarColor(ContextCompat.getColor(this, R.color.blue_primary));
		setSeparatorColor(ContextCompat.getColor(this, R.color.blue_primary));
		setOffScreenPageLimit(4);

		// Hide Skip/Done button.
		showSkipButton(false);
		setProgressButtonEnabled(true);

		// Turn vibration on and set intensity.
		setVibrate(true);
		setVibrateIntensity(30);
	}

	@Override
	public void onSkipPressed() {}

	@Override
	public void onDonePressed() {
		if(Constants.TRACING_LEVEL > 0) {
			Log.i(TAG, "Team Number: " + teamNumberSlide.getTeamNumber());
		}
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("teamRank", "");
		editor.putString("teamRecord", "");
		editor.apply();
		this.finish();
	}

	@Override
	public void onSlideChanged() {
		counter++;
		// Do something when the slide changes.
		if(teamNumberSlide != null && counter > 2) {
			if (teamNumberSlide.getTeamNumber() != null) {
				teamNumberSlide.setTeamNumber(teamNumberSlide.getTeamNumber());
				eventSlide.load();
			}
		}
	}

	@Override
	public void onNextPressed() {}
}