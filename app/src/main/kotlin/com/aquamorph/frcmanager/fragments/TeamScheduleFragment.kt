package com.aquamorph.frcmanager.fragments

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.content.res.Configuration
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.adapters.ScheduleAdapter
import com.aquamorph.frcmanager.decoration.Animations
import com.aquamorph.frcmanager.models.Match
import com.aquamorph.frcmanager.network.DataLoader
import com.aquamorph.frcmanager.network.RetrofitInstance
import com.aquamorph.frcmanager.network.TbaApi
import com.aquamorph.frcmanager.utils.Constants
import com.aquamorph.frcmanager.utils.Logging
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.Collections.sort
import kotlin.collections.ArrayList

/**
 * Displays a list of matches at an event for a given team.
 *
 * @author Christian Colglazier
 * @version 4/14/2018
 */
class TeamScheduleFragment : TabFragment(), OnSharedPreferenceChangeListener, RefreshFragment {
    override fun dataUpdate() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var teamEventMatches = ArrayList<Match>()
    private var teamNumber: String = ""
    private var getTeamFromSettings: Boolean = true
    private var disposable: Disposable? = null

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
            refresh(false)
    }

    /**
     * refrest() loads dataLoader needed for this fragment.
     * @param force
     */
    override fun refresh(force: Boolean) {
        if (teamNumber != "" && DataLoader.eventKey != "") {
            LoadTeamSchedule(force).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        } else {
            Logging.error(this, "Team or event key not set", 0)
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == "teamNumber" || key == "eventKey") {
            if (getTeamFromSettings) {
                teamNumber = sharedPreferences.getString("teamNumber", "")
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
                teamNumber = prefs.getString("teamNumber", "")
            }
        }
    }

    fun dataUpdate(data: ArrayList<Match>) {
        teamEventMatches.clear()
        for (match in data) {
            if (isTeamInMatch(match, "frc$teamNumber")) teamEventMatches.add(match)
        }
        teamEventMatches.sort()
    }

    internal inner class LoadTeamSchedule(var force: Boolean) : AsyncTask<Void?, Void?, Void?>() {

        override fun onPreExecute() {
            mSwipeRefreshLayout.isRefreshing = true
        }

        override fun doInBackground(vararg params: Void?): Void? {
            disposable = RetrofitInstance.getRetrofit().create(TbaApi::class.java).getEventMatches(DataLoader.eventKey)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { result -> dataUpdate(result)}
            return null
        }

        override fun onPostExecute(result: Void?) {
            if (context != null) {
//                dataUpdate()
                sort(teamEventMatches)
                Constants.checkNoDataScreen(teamEventMatches, recyclerView, emptyView)
                Animations.loadAnimation(context, recyclerView, adapter, firstLoad,
                        DataLoader.matchDC.parser.isNewData)
                if (firstLoad) firstLoad = false
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