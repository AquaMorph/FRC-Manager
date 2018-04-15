package com.aquamorph.frcmanager.fragments

import android.os.AsyncTask
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.adapters.TeamAdapter
import com.aquamorph.frcmanager.decoration.Animations
import com.aquamorph.frcmanager.models.Team
import com.aquamorph.frcmanager.network.DataLoader
import com.aquamorph.frcmanager.utils.Constants

/**
 * Displays a list of teams at an event.
 *
 * @author Christian Colglazier
 * @version 4/14/2018
 */
open class TeamFragment : TabFragment(), RefreshFragment {

    private var teams: ArrayList<Team> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_team_schedule, container, false)
        super.onCreateView(view, teams,
                TeamAdapter(context!!, teams, DataLoader.rankDC.data))
        return view
    }

    override fun onResume() {
        super.onResume()
        if (DataLoader.teamDC.data.size == 0)
            refresh(false)
    }

    override fun dataUpdate() {
        teams.clear()
        teams.addAll(DataLoader.teamDC.data)
    }

    /**
     * refresh() loads dataLoader needed for this fragment.
     */
    override fun refresh(force: Boolean) {
        if (DataLoader.eventKey != "" && DataLoader.teamNumber != "") {
            LoadEventTeams(force).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }
    }

    internal inner class LoadEventTeams(var force: Boolean) : AsyncTask<Void?, Void?, Void?>() {

        override fun onPreExecute() {
            if (mSwipeRefreshLayout != null) mSwipeRefreshLayout.isRefreshing = true
        }

        override fun doInBackground(vararg params: Void?): Void? {
            while (!DataLoader.teamDC.complete) SystemClock.sleep(Constants.THREAD_WAIT_TIME.toLong())
            while (!DataLoader.rankDC.complete) SystemClock.sleep(Constants.THREAD_WAIT_TIME.toLong())
            return null
        }

        override fun onPostExecute(result: Void?) {
            if (context != null) {
                dataUpdate()
                Constants.checkNoDataScreen(DataLoader.teamDC.data, recyclerView, emptyView)
                Animations.loadAnimation(context, recyclerView, adapter, firstLoad,
                        DataLoader.teamDC.parser.isNewData)
                if (firstLoad) firstLoad = false
                if (mSwipeRefreshLayout != null) mSwipeRefreshLayout.isRefreshing = false
            }
        }
    }

    companion object {

        /**
         * newInstance creates and returns a new TeamFragment
         *
         * @return TeamFragment
         */
        fun newInstance(): TeamFragment {
            return TeamFragment()
        }
    }
}
