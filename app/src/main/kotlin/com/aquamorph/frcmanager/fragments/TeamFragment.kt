package com.aquamorph.frcmanager.fragments

import android.os.AsyncTask
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.adapters.TeamAdapter
import com.aquamorph.frcmanager.decoration.Divider
import com.aquamorph.frcmanager.models.Rank
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
    private var ranks: ArrayList<Rank> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_team_schedule, container, false)
        super.onCreateView(view, teams, TeamAdapter(context!!, teams, ranks),
                Divider(context!!, 2f, 72))
        return view
    }

    override fun onResume() {
        super.onResume()
        if (teams.isEmpty())
            refresh()
    }

    override fun dataUpdate() {
        teams.clear()
        teams.addAll(DataLoader.teamDC.data)
        ranks.clear()
        ranks.addAll(DataLoader.rankDC.data)
        adapter.notifyDataSetChanged()
    }

    /**
     * refresh() loads dataLoader needed for this fragment.
     */
    override fun refresh() {
        if (DataLoader.eventKey != "" && DataLoader.teamNumber != "") {
            LoadEventTeams().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }
    }

    internal inner class LoadEventTeams() : AsyncTask<Void?, Void?, Void?>() {

        override fun onPreExecute() {
            mSwipeRefreshLayout.isRefreshing = true
        }

        override fun doInBackground(vararg params: Void?): Void? {
            while (!DataLoader.teamDC.complete) SystemClock.sleep(Constants.THREAD_WAIT_TIME.toLong())
            while (!DataLoader.rankDC.complete) SystemClock.sleep(Constants.THREAD_WAIT_TIME.toLong())
            return null
        }

        override fun onPostExecute(result: Void?) {
            if (context != null) {
                dataUpdate()
                Constants.checkNoDataScreen(teams, recyclerView, emptyView)
                mSwipeRefreshLayout.isRefreshing = false
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
