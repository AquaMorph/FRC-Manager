package com.aquamorph.frcmanager.fragments

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.content.res.Configuration
import android.os.AsyncTask
import android.os.Bundle
import android.os.SystemClock
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.activities.MainActivity
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
 * @version 10/19/2016
 */
class TeamScheduleFragment : Fragment(), OnSharedPreferenceChangeListener, RefreshFragment {

    internal lateinit var prefs: SharedPreferences
    private var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    private var recyclerView: RecyclerView? = null
    private var emptyView: TextView? = null
    private var adapter: Adapter<*>? = null
    private val teamEventMatches = ArrayList<Match>()
    private var teamNumber: String? = ""
    private lateinit var view2: View
    private var getTeamFromSettings: Boolean? = true
    private var firstLoad: Boolean? = true

    fun setTeamNumber(teamNumber: String) {
        this.teamNumber = teamNumber
        getTeamFromSettings = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        Logging.info(this, "TeamScheduleFragment created", 3)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        view2 = inflater.inflate(R.layout.fragment_team_schedule, container, false)
        if (savedInstanceState != null) {
            if (getTeamFromSettings!!) {
                teamNumber = savedInstanceState.getString("teamNumber")
            }
            Logging.info(this, "savedInstanceState teamNumber: $teamNumber", 2)
        }
        listener()
        Constants.checkNoDataScreen(teamEventMatches, recyclerView!!, emptyView!!)
        return view2
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("teamNumber", teamNumber)
        Logging.info(this, "onSaveInstanceState", 3)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        view2 = (context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                .inflate(R.layout.fragment_team_schedule, null)
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
        Logging.info(this, "teamNumber: " + teamNumber!!, 2)
        Logging.info(this, "DataLoader is being refreshed", 0)
        if (teamNumber != "" && DataLoader.eventKey != "") {
            LoadTeamSchedule(force).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        } else {
            Logging.error(this, "Team or event key not set", 0)
        }
    }


    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == "teamNumber" || key == "eventKey") {
            if (getTeamFromSettings!!) {
                teamNumber = sharedPreferences.getString("teamNumber", "")
            }
            listener()
        }
    }

    /**
     * listener() initializes all needed types on creation
     */
    fun listener() {
        if (context != null) {
            prefs = PreferenceManager.getDefaultSharedPreferences(context)
            prefs.registerOnSharedPreferenceChangeListener(this@TeamScheduleFragment)
            if (getTeamFromSettings!!) {
                teamNumber = prefs.getString("teamNumber", "")
            }
            mSwipeRefreshLayout = view2!!.findViewById(R.id.swipeRefreshLayout)
            mSwipeRefreshLayout!!.setColorSchemeResources(R.color.accent)
            mSwipeRefreshLayout!!.setOnRefreshListener {
                if (getTeamFromSettings!!) {
                    MainActivity.refresh()
                } else {
                    refresh(false)
                }
            }

            recyclerView = view2!!.findViewById(R.id.rv)
            emptyView = view2!!.findViewById(R.id.empty_view)
            adapter = ScheduleAdapter(context!!, teamEventMatches, teamNumber)
            val llm = LinearLayoutManager(context)
            llm.orientation = LinearLayoutManager.VERTICAL
            recyclerView!!.layoutManager = llm
            recyclerView!!.adapter = adapter
        }
    }

    private fun isTeamInMatch(match: Match, team: String): Boolean {
        return if (Arrays.asList(*match.alliances.red.team_keys).contains(team))
            true
        else Arrays.asList(*match.alliances.blue.team_keys).contains(team)
    }

    internal inner class LoadTeamSchedule(var force: Boolean) : AsyncTask<Void?, Void?, Void?>() {

        override fun onPreExecute() {
            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout!!.isRefreshing = true
            }
        }

        override fun doInBackground(vararg params: Void?): Void? {
            while (!DataLoader.matchDC.complete) SystemClock.sleep(Constants.THREAD_WAIT_TIME.toLong())
            return null
        }

        override fun onPostExecute(result: Void?) {
            if (context != null) {
                teamEventMatches.clear()
                for (match in DataLoader.matchDC.data) {
                    if (isTeamInMatch(match, "frc" + teamNumber!!)) teamEventMatches.add(match)
                }
                sort(teamEventMatches)
                Constants.checkNoDataScreen(teamEventMatches, recyclerView!!, emptyView!!)
                Animations.loadAnimation(context, recyclerView, adapter, firstLoad,
                        DataLoader.matchDC.parser.isNewData)
                if (firstLoad!!) firstLoad = false
                mSwipeRefreshLayout!!.isRefreshing = false
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
}