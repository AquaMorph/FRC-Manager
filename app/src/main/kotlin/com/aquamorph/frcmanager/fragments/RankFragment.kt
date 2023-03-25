package com.aquamorph.frcmanager.fragments

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.adapters.RankAdapter
import com.aquamorph.frcmanager.decoration.Animations
import com.aquamorph.frcmanager.decoration.Divider
import com.aquamorph.frcmanager.models.Rank
import com.aquamorph.frcmanager.models.Team
import com.aquamorph.frcmanager.network.DataLoader
import com.aquamorph.frcmanager.utils.Constants

/**
 * Displays the ranks of all the teams at an event.
 *
 * @author Christian Colglazier
 * @version 3/25/2023
 */
class RankFragment : TabFragment(), RefreshFragment {

    private var ranks: ArrayList<Rank> = ArrayList()
    private var teams: ArrayList<Team> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_team_schedule, container, false)
        super.onCreateView(view, ranks,
                RankAdapter(requireContext(), ranks, teams),
                Divider(requireContext(), Constants.DIVIDER_WIDTH, 72))
        return view
    }

    override fun dataUpdate() {
        ranks.clear()
        ranks.addAll(DataLoader.rankDC.data)
        teams.clear()
        teams.addAll(DataLoader.teamDC.data)
        Constants.checkNoDataScreen(ranks, recyclerView, emptyView)
        Animations.loadAnimation(context, recyclerView, adapter, firstLoad,
                DataLoader.rankDC.newData || DataLoader.teamDC.newData)
        firstLoad = false
    }

    override fun onResume() {
        super.onResume()
        if (ranks.isEmpty())
            refresh()
    }

    /**
     * refresh() loads dataLoader needed for this fragment.
     */
    override fun refresh() {
        if (DataLoader.eventKey != "" && DataLoader.teamNumber != "" && context != null) {
            mSwipeRefreshLayout.isRefreshing = true
            executor.execute {
                while (!DataLoader.teamDC.complete) {
                    SystemClock.sleep(Constants.THREAD_WAIT_TIME.toLong())
                }
                while (!DataLoader.rankDC.complete) {
                    SystemClock.sleep(Constants.THREAD_WAIT_TIME.toLong())
                }
                handler.post {
                    if (DataLoader.rankDC.data != null &&
                        DataLoader.rankDC.data.isNotEmpty() &&
                        DataLoader.rankDC.data[0].rankings != null) {
                        val editor = prefs.edit()
                        editor.putString("teamRank", "")
                        for (i in 0 until DataLoader.rankDC.data[0].rankings!!.size) {
                            if (DataLoader.rankDC.data[0].rankings!![i].teamKey == "frc" +
                                DataLoader.teamNumber) {
                                editor.putString("teamRank",
                                    DataLoader.rankDC.data[0].rankings!![i].rank.toString())
                                editor.putString("teamRecord",
                                    Rank.recordToString(DataLoader.rankDC.data[0].rankings!![i].record))
                                editor.apply()
                            }
                        }
                    }
                    if (context != null) {
                        dataUpdate()
                        mSwipeRefreshLayout.isRefreshing = false
                    }
                }
            }
        }
    }

    companion object {

        /**
         * newInstance creates and returns a new RankFragment
         *
         * @return RankFragment
         */
        fun newInstance(): RankFragment {
            return RankFragment()
        }
    }
}
