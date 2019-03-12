package com.aquamorph.frcmanager.activities

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.fragments.setup.EventSlide
import com.aquamorph.frcmanager.fragments.setup.Slide
import com.aquamorph.frcmanager.fragments.setup.TeamNumberSlide
import com.aquamorph.frcmanager.utils.Logging
import com.github.paolorotolo.appintro.AppIntro

/**
 * App set up activity gets the desired year, team, and event.
 *
 * @author Christian Colglazier
 * @version 4/2/2018
 */
class Setup : AppIntro() {

    private lateinit var teamNumberSlide: TeamNumberSlide
    private lateinit var eventSlide: EventSlide

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        teamNumberSlide = TeamNumberSlide()
        eventSlide = EventSlide()

        addSlide(Slide.newInstance(R.layout.first_slide))
        addSlide(teamNumberSlide)
        addSlide(eventSlide)

        setBarColor(ContextCompat.getColor(this, R.color.primary))
        setSeparatorColor(ContextCompat.getColor(this, R.color.primary_dark))
        setOffScreenPageLimit(4)

        // Hide Skip/Done button.
        showSkipButton(false)
        isProgressButtonEnabled = true

        // Turn vibration on and set intensity.
        setVibrate(true)
        setVibrateIntensity(30)
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        Logging.info(this, "Team Number: " + teamNumberSlide.teamNumber, 0)
        val prefs = PreferenceManager.getDefaultSharedPreferences(baseContext)
        val editor = prefs.edit()
        editor.putString("teamRank", "")
        editor.putString("teamRecord", "")
        editor.apply()
        MainActivity.refresh()
        this.finish()
    }

    override fun onSlideChanged(oldFragment: Fragment?, newFragment: Fragment?) {
        super.onSlideChanged(oldFragment, newFragment)
        if (teamNumberSlide.getTeamNumber().matches("[0-9]+".toRegex())) {
            try {
                Logging.info(this, "Getting events for Setup", 0)
                teamNumberSlide.setTeamNumber()
                eventSlide.load()
            } catch (e: Exception) {}
        }
    }
}