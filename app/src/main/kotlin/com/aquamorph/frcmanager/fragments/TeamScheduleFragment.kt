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
import com.aquamorph.frcmanager.adapters.ScheduleAdapter
import com.aquamorph.frcmanager.decoration.Animations
import com.aquamorph.frcmanager.models.Match
import com.aquamorph.frcmanager.network.DataLoader
import com.aquamorph.frcmanager.utils.Constants
import com.aquamorph.frcmanager.utils.Logging
import java.util.*
import java.util.Collections.sort

/**
 * Displays a list of matches at an event for a given team.
 *
 * @author Christian Colglazier
 * @version 4/14/2018
 */
class TeamScheduleFragment : TabFragment(), OnSharedPreferenceChangeListener, RefreshFragment {

    private var teamEventMatches = ArrayList<Match>()
    private var teamNumber: String = ""
    private var getTeamFromSettings: Boolean = true

    fun setTeamNumber(teamNumber: String) {
        this.teamNumber = teamNumber
        getTeamFromSettings = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_team_schedule, container, false)

        if (savedInstanceState != null) {
            if (getTeamFromSettings) {
                teamNumber = savedInstanceState.getString("teamNumber", "")
            }
            Logging.info(this, "savedInstanceState teamNumber: $teamNumber", 2)
        }
        super.onCreateView(view, teamEventMatches,
                ScheduleAdapter(context!!, teamEventMatches, teamNumber))
        if (!getTeamFromSettings) {
            mSwipeRefreshLayout.isEnabled = false
        }
        listener()
        Constants.checkNoDataScreen(teamEventMatches, recyclerView, emptyView)
        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("teamNumber", teamNumber)
        Logging.info(this, "onSaveInstanceState", 3)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
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
     * refrest() loads dataLoader needed for this fragment.
     * @param force
     */
    override fun refresh() {
        if (teamNumber != "" && DataLoader.eventKey != "") {
            LoadTeamSchedule().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
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
        teamEventMatches.clear()
        for (match in DataLoader.matchDC.data) {
            if (isTeamInMatch(match, "frc$teamNumber")) teamEventMatches.add(match)
        }
        sort(teamEventMatches)
    }

    internal inner class LoadTeamSchedule() : AsyncTask<Void?, Void?, Void?>() {

        override fun onPreExecute() {
            mSwipeRefreshLayout.isRefreshing = true
        }

        override fun doInBackground(vararg params: Void?): Void? {
            while (!DataLoader.matchDC.complete) SystemClock.sleep(Constants.THREAD_WAIT_TIME.toLong())
            return null
        }

        override fun onPostExecute(result: Void?) {
            if (context != null) {
                dataUpdate()
                Constants.checkNoDataScreen(teamEventMatches, recyclerView, emptyView)
                mSwipeRefreshLayout.isRefreshing = false
            }
        }
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

    private fun isTeamInMatch(match: Match, team: String): Boolean {
        return if (Arrays.asList(*match.alliances.red.team_keys).contains(team))
            true
        else Arrays.asList(*match.alliances.blue.team_keys).contains(team)
    }
}