package com.aquamorph.frcmanager.fragments

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.content.res.Configuration
import android.os.AsyncTask
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.activities.MainActivity
import com.aquamorph.frcmanager.adapters.ScheduleAdapter
import com.aquamorph.frcmanager.decoration.Animations
import com.aquamorph.frcmanager.decoration.Divider
import com.aquamorph.frcmanager.models.Match
import com.aquamorph.frcmanager.models.TBAPrediction
import com.aquamorph.frcmanager.network.DataLoader
import com.aquamorph.frcmanager.utils.Constants
import com.aquamorph.frcmanager.utils.Logging
import com.aquamorph.frcmanager.utils.MatchSort

/**
 * Displays a list of matches at an event for a given team.
 *
 * @author Christian Colglazier
 * @version 3/25/2023
 */
class TeamScheduleFragment : TabFragment(), OnSharedPreferenceChangeListener, RefreshFragment {

    private var teamEventMatches = ArrayList<Match>()
    private var predictions: ArrayList<TBAPrediction.PredMatch> = ArrayList()
    private var teamNumber: String = ""
    private var getTeamFromSettings: Boolean = true

    /**
     * setTeamNumber() set team schedule to display.
     *
     * @param teamNumber team key
     */
    fun setTeamNumber(teamNumber: String) {
        this.teamNumber = teamNumber
        getTeamFromSettings = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_team_schedule, container, false)

        if (savedInstanceState != null) {
            if (getTeamFromSettings) {
                teamNumber = savedInstanceState.getString("teamNumber", "")
            }
            Logging.info(this, "savedInstanceState teamNumber: $teamNumber", 2)
        }
        if (MainActivity.appTheme == Constants.Theme.BATTERY_SAVER) {
            super.onCreateView(view, teamEventMatches,
                    ScheduleAdapter(requireContext(), teamEventMatches, predictions, teamNumber),
                    Divider(requireContext(), Constants.DIVIDER_WIDTH, 0))
        } else {
            super.onCreateView(view, teamEventMatches,
                    ScheduleAdapter(requireContext(), teamEventMatches, predictions, teamNumber))
        }
        if (!getTeamFromSettings) {
            mSwipeRefreshLayout.isEnabled = false
        }
        listener()
        Constants.checkNoDataScreen(teamEventMatches, recyclerView, emptyView)
        prefs.registerOnSharedPreferenceChangeListener(this)
        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("teamNumber", teamNumber)
        Logging.info(this, "onSaveInstanceState", 3)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        listener()
        Logging.info(this, "Configuration Changed", 3)
    }

    override fun onResume() {
        super.onResume()
        if (teamEventMatches.size == 0)
            refresh()
    }

    /**
     * refresh() loads dataLoader needed for this fragment.
     */
    override fun refresh() {
        if (teamNumber != "" && DataLoader.eventKey != "") {
            mSwipeRefreshLayout.isRefreshing = true
            executor.execute {
                while (!DataLoader.matchDC.complete) {
                    SystemClock.sleep(Constants.THREAD_WAIT_TIME.toLong())
                }
                while (MainActivity.predEnabled && !DataLoader.tbaPredictionsDC.complete) {
                    SystemClock.sleep(Constants.THREAD_WAIT_TIME.toLong())
                }
                handler.post {
                    if (context != null) {
                        dataUpdate()
                        mSwipeRefreshLayout.isRefreshing = false
                    }
                }
            }
        } else {
            Logging.error(this, "Team or event key not set", 0)
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == "teamNumber" || key == "eventKey") {
            if (getTeamFromSettings) {
                teamNumber = DataLoader.teamNumber
            }
            listener()
        } else if (key == "matchSort") {
            refresh()
        }
    }

    /**
     * listener() initializes all needed types on creation
     */
    private fun listener() {
        if (context != null) {
            if (getTeamFromSettings) {
                teamNumber = DataLoader.teamNumber
            }
        }
    }

    override fun dataUpdate() {
        if (MainActivity.predEnabled) {
            predictions.clear()
            predictions.addAll(DataLoader.tbaPredictionsDC.data)
        }
        teamEventMatches.clear()
        for (match in DataLoader.matchDC.data) {
            if (isTeamInMatch(match, "frc$teamNumber")) teamEventMatches.add(match)
        }
        MatchSort.sortMatches(teamEventMatches, prefs.getString("matchSort", ""))
        Constants.checkNoDataScreen(teamEventMatches, recyclerView, emptyView)
        Animations.loadAnimation(context, recyclerView, adapter, firstLoad,
                DataLoader.matchDC.newData)
        firstLoad = false
    }


    companion object {

        /**
         * newInstance creates and returns a new TeamScheduleFragment
         *
         * @return TeamScheduleFragment
         */
        fun newInstance(): TeamScheduleFragment {
            return TeamScheduleFragment()
        }
    }

    /**
     * isTeamInMatch() checks if a team is in a match.
     *
     * @param match event match
     * @param team team key
     * @return robot in match state
     */
    private fun isTeamInMatch(match: Match, team: String): Boolean {
        return if (match.alliances.red.teamKeys.contains(team))
            true
        else match.alliances.blue.teamKeys.contains(team)
    }
}
