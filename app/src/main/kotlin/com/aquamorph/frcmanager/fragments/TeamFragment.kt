package com.aquamorph.frcmanager.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.adapters.TeamAdapter
import com.aquamorph.frcmanager.decoration.Animations
import com.aquamorph.frcmanager.decoration.Divider
import com.aquamorph.frcmanager.models.Rank
import com.aquamorph.frcmanager.models.Team
import com.aquamorph.frcmanager.network.DataLoader
import com.aquamorph.frcmanager.utils.Constants

/**
 * Displays a list of teams at an event.
 *
 * @author Christian Colglazier
 * @version 3/25/2023
 */
open class TeamFragment : TabFragment(), RefreshFragment,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private var teams: ArrayList<Team> = ArrayList()
    private var ranks: ArrayList<Rank> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_team_schedule, container, false)
        super.onCreateView(view, teams, TeamAdapter(requireContext(), teams, ranks),
                Divider(requireContext(), Constants.DIVIDER_WIDTH, 72))
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
        teams.sort()
        ranks.clear()
        ranks.addAll(DataLoader.rankDC.data)
        Constants.checkNoDataScreen(teams, recyclerView, emptyView)
        Animations.loadAnimation(context, recyclerView, adapter, firstLoad,
                DataLoader.teamDC.newData || DataLoader.rankDC.newData)
        firstLoad = false
    }

    /**
     * refresh() loads dataLoader needed for this fragment.
     */
    override fun refresh() {
        if (DataLoader.eventKey != "" && DataLoader.teamNumber != "") {
            mSwipeRefreshLayout.isRefreshing = true
            executor.execute {
                while (!DataLoader.teamDC.complete) {
                    SystemClock.sleep(Constants.THREAD_WAIT_TIME.toLong())
                }
                while (!DataLoader.rankDC.complete) {
                    SystemClock.sleep(Constants.THREAD_WAIT_TIME.toLong())
                }
                handler.post {
                    if (context != null) {
                        dataUpdate()
                        mSwipeRefreshLayout.isRefreshing = false
                    }
                }
            }
        }
    }

    override fun onSharedPreferenceChanged(sp: SharedPreferences?, key: String?) {
        if (key.equals("eventKey") || key.equals("teamNumber")) {
            refresh()
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
