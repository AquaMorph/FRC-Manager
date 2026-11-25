package com.aquamorph.frcmanager.activities

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.fragments.setup.EventSlide
import com.aquamorph.frcmanager.fragments.setup.Slide
import com.aquamorph.frcmanager.fragments.setup.TeamNumberSlide
import com.aquamorph.frcmanager.utils.Logging
import com.aquamorph.frcmanager.utils.applyEdgeToEdge
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
        applyEdgeToEdge(drawBehindNavigation = false)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        teamNumberSlide = TeamNumberSlide()
        eventSlide = EventSlide()

        addSlide(Slide.newInstance(R.layout.first_slide))
        addSlide(teamNumberSlide)
        addSlide(eventSlide)
        configureSlideBackNavigation()

        setBarColor(ContextCompat.getColor(this, R.color.primary))
        setSeparatorColor(ContextCompat.getColor(this, R.color.primary_dark))

        // Hide Skip/Done button.
        showSkipButton(false)
        isProgressButtonEnabled = true

        // Turn vibration on and set intensity.
        setVibrate(true)
        setVibrateIntensity(30)
    }

    private fun configureSlideBackNavigation() {
        val introPager = getPager()
        val slideAdapter = checkNotNull(introPager.adapter)
        // AppIntro's legacy onBackPressed override is bypassed by predictive Back.
        val slideBack = object : OnBackPressedCallback(!introPager.isFirstSlide(slideAdapter.count)) {
            override fun handleOnBackPressed() {
                introPager.goToPreviousSlide()
            }
        }
        onBackPressedDispatcher.addCallback(this, slideBack)
        introPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                slideBack.isEnabled = !introPager.isFirstSlide(slideAdapter.count)
            }
        })
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        Logging.info(this, "Team Number: " + teamNumberSlide.teamNumber, 0)
        this.finish()
    }

    override fun onSlideChanged(oldFragment: Fragment?, newFragment: Fragment?) {
        super.onSlideChanged(oldFragment, newFragment)
        if (teamNumberSlide.getTeamNumber().matches("[0-9]+".toRegex())) {
            try {
                Logging.info(this, "Getting events for Setup", 0)
                teamNumberSlide.setTeamNumber()
                eventSlide.load()
            } catch (_: Exception) {}
        }
    }
}
