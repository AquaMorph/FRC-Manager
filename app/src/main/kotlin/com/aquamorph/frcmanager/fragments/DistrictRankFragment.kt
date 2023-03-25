package com.aquamorph.frcmanager.fragments

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.adapters.DistrictRankAdapter
import com.aquamorph.frcmanager.decoration.Animations
import com.aquamorph.frcmanager.decoration.Divider
import com.aquamorph.frcmanager.models.DistrictRank
import com.aquamorph.frcmanager.models.Team
import com.aquamorph.frcmanager.network.DataLoader
import com.aquamorph.frcmanager.utils.Constants

/**
 * Displays the ranks of all the teams at an event.
 *
 * @author Christian Colglazier
 * @version 3/25/2023
 */
class DistrictRankFragment : TabFragment(), RefreshFragment {

    private var ranks: ArrayList<DistrictRank> = ArrayList()
    private var teams: ArrayList<Team> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_team_schedule, container, false)
        super.onCreateView(view, ranks,
                DistrictRankAdapter(requireContext(), ranks, teams),
                Divider(requireContext(), Constants.DIVIDER_WIDTH, 72))
        return view
    }

    override fun dataUpdate() {
        ranks.clear()
        ranks.addAll(DataLoader.districtRankDC.data)
        teams.clear()
        teams.addAll(DataLoader.districtTeamDC.data)
        Constants.checkNoDataScreen(DataLoader.districtRankDC.data, recyclerView, emptyView)
        Animations.loadAnimation(context, recyclerView, adapter, firstLoad,
                DataLoader.districtRankDC.newData || DataLoader.districtTeamDC.newData)
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
        if (DataLoader.districtKey != "" && context != null) {
            mSwipeRefreshLayout.isRefreshing = true
            executor.execute {
                while (!DataLoader.districtRankDC.complete) {
                    SystemClock.sleep(Constants.THREAD_WAIT_TIME.toLong())
                }
                while (!DataLoader.districtTeamDC.complete) {
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

    companion object {

        /**
         * newInstance creates and returns a new RankFragment
         *
         * @return RankFragment
         */
        fun newInstance(): DistrictRankFragment {
            return DistrictRankFragment()
        }
    }
}
