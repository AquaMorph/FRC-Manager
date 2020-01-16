package com.aquamorph.frcmanager.fragments

import android.os.AsyncTask
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.adapters.DistrictRankAdapter
import com.aquamorph.frcmanager.decoration.Divider
import com.aquamorph.frcmanager.models.DistrictRank
import com.aquamorph.frcmanager.models.Team
import com.aquamorph.frcmanager.network.DataLoader
import com.aquamorph.frcmanager.utils.Constants

/**
 * Displays the ranks of all the teams at an event.
 *
 * @author Christian Colglazier
 * @version 10/29/2018
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
                DistrictRankAdapter(context!!, ranks, teams),
                Divider(context!!, Constants.DIVIDER_WIDTH, 72))
        return view
    }

    override fun dataUpdate() {
        ranks.clear()
        ranks.addAll(DataLoader.districtRankDC.data)
        teams.clear()
        teams.addAll(DataLoader.districtTeamDC.data)
        adapter.notifyDataSetChanged()
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
            LoadRanks().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }
    }

    internal inner class LoadRanks : AsyncTask<Void?, Void?, Void?>() {

        override fun onPreExecute() {
            mSwipeRefreshLayout.isRefreshing = true
        }

        override fun doInBackground(vararg params: Void?): Void? {
            while (!DataLoader.districtRankDC.complete) SystemClock.sleep(Constants.THREAD_WAIT_TIME.toLong())
            while (!DataLoader.districtTeamDC.complete) SystemClock.sleep(Constants.THREAD_WAIT_TIME.toLong())
            return null
        }

        override fun onPostExecute(result: Void?) {
            if (context != null) {
                dataUpdate()
                Constants.checkNoDataScreen(DataLoader.districtRankDC.data, recyclerView, emptyView)
                mSwipeRefreshLayout.isRefreshing = false
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
