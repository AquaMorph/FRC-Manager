package com.aquamorph.frcmanager.activities;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.aquamorph.frcmanager.R;
import com.aquamorph.frcmanager.fragments.setup.EventSlide;
import com.aquamorph.frcmanager.fragments.setup.FirstSlide;
import com.aquamorph.frcmanager.fragments.setup.TeamNumberSlide;
import com.github.paolorotolo.appintro.AppIntro;

public class Setup extends AppIntro {

	private String TAG = "Setup";
	private TeamNumberSlide teamNumberSlide;
	private EventSlide eventSlide;

	// Please DO NOT override onCreate. Use init.
	@Override
	public void init(Bundle savedInstanceState) {
		getSupportActionBar().hide();

		teamNumberSlide = new TeamNumberSlide();
		eventSlide = new EventSlide();

		// Add your slide's fragments here.
		// AppIntro will automatically generate the dots indicator and buttons.
		addSlide(FirstSlide.newInstance(R.layout.first_slide));
		addSlide(teamNumberSlide);
		addSlide(eventSlide);

//		secondSlide.getTeamNum

//		Log.i(TAG, secondSlide.getTeamNumber());

		// Instead of fragments, you can also use our default slide
		// Just set a title, description, background and image. AppIntro will do the rest.
//		addSlide(AppIntroFragment.newInstance(title, description, image, background_colour));

		// OPTIONAL METHODS
		// Override bar/separator color.
		setBarColor(ContextCompat.getColor(this ,R.color.accent));
		setSeparatorColor(ContextCompat.getColor(this ,R.color.accent));

		// Hide Skip/Done button.
		showSkipButton(false);
		setProgressButtonEnabled(true);

		// Turn vibration on and set intensity.
		// NOTE: you will probably need to ask VIBRATE permisssion in Manifest.
		setVibrate(true);
		setVibrateIntensity(30);
	}

	@Override
	public void onSkipPressed() {
		// Do something when users tap on Skip button.
	}

	@Override
	public void onDonePressed() {
		Log.i(TAG, "Team Number: " + teamNumberSlide.getTeamNumber());
		this.finish();
	}

	@Override
	public void onSlideChanged() {
		// Do something when the slide changes.
		if(teamNumberSlide != null) {
			if (!teamNumberSlide.getTeamNumber().equals(null)) {
				teamNumberSlide.setTeamNumber(teamNumberSlide.getTeamNumber());
				Log.i(TAG, "Team Number changed");
			}
		}
	}

	@Override
	public void onNextPressed() {
		// Do something when users tap on Next button.

	}

}