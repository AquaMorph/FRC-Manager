package com.aquamorph.frcmanager.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.aquamorph.frcmanager.R;
import com.aquamorph.frcmanager.fragments.setup.EventSlide;
import com.aquamorph.frcmanager.fragments.setup.Slide;
import com.aquamorph.frcmanager.fragments.setup.TeamNumberSlide;
import com.aquamorph.frcmanager.utils.Logging;
import com.github.paolorotolo.appintro.AppIntro;

/**
 * App set up activity gets the desired year, team, and event.
 *
 * @author Christian Colglazier
 * @version 2/17/2018
 */
public class Setup extends AppIntro {

	private TeamNumberSlide teamNumberSlide;
	private EventSlide eventSlide;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		teamNumberSlide = new TeamNumberSlide();
		eventSlide = new EventSlide();

		addSlide(Slide.newInstance(R.layout.first_slide));
		addSlide(teamNumberSlide);
		addSlide(eventSlide);

		setBarColor(ContextCompat.getColor(this, R.color.primary));
		setSeparatorColor(ContextCompat.getColor(this, R.color.primary_dark));
		setOffScreenPageLimit(4);

		// Hide Skip/Done button.
		showSkipButton(false);
		setProgressButtonEnabled(true);

		// Turn vibration on and set intensity.
		setVibrate(true);
		setVibrateIntensity(30);
	}

	@Override
	public void onDonePressed(Fragment currentFragment) {
		super.onDonePressed(currentFragment);
		Logging.INSTANCE.info(this, "Team Number: " + teamNumberSlide.getTeamNumber(), 0);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("teamRank", "");
		editor.putString("teamRecord", "");
		editor.apply();
		MainActivity.refresh(true);
		this.finish();
	}

	@Override
	public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
		super.onSlideChanged(oldFragment, newFragment);
		if(teamNumberSlide!= null && teamNumberSlide.getTeamNumber().matches("[0-9]+")) {
			try {
				Logging.INSTANCE.info(this, "Gettings events for Setup", 0);
				teamNumberSlide.setTeamNumber(teamNumberSlide.getTeamNumber());
				eventSlide.load(true);
			} catch (Exception e) {}
		}
	}
}