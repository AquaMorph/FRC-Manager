package com.aquamorph.frcmanager.fragments

import android.os.AsyncTask
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
 * @version 4/14/2018
 */
class RankFragment : TabFragment(), RefreshFragment {

    private var ranks : ArrayList<Rank> = ArrayList()
    private var teams : ArrayList<Team> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_team_schedule, container, false)
        super.onCreateView(view, DataLoader.rankDC.data,
                RankAdapter(context!!, DataLoader.rankDC.data, DataLoader.teamDC.data),
                Divider(context!!, 2f, 72))
        return view
    }

    override fun dataUpdate() {
        ranks.clear()
        ranks.addAll(DataLoader.rankDC.data)
        teams.clear()
        teams.addAll(DataLoader.teamDC.data)
    }

    override fun onResume() {
        super.onResume()
        if (DataLoader.rankDC.data.size == 0)
            refresh(false)
    }

    /**
     * refrest() loads dataLoader needed for this fragment.
     */
    override fun refresh(force: Boolean) {
        if (DataLoader.eventKey != "" && DataLoader.teamNumber != "" && context != null) {
            LoadRanks().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }
    }

    internal inner class LoadRanks(): AsyncTask<Void?, Void?, Void?>() {

        override fun onPreExecute() {
            if (mSwipeRefreshLayout != null) mSwipeRefreshLayout.isRefreshing = true
        }

        override fun doInBackground(vararg params: Void?): Void? {
            while (!DataLoader.teamDC.complete) SystemClock.sleep(Constants.THREAD_WAIT_TIME.toLong())
            while (!DataLoader.rankDC.complete) SystemClock.sleep(Constants.THREAD_WAIT_TIME.toLong())
            return null
        }

        override fun onPostExecute(result: Void?) {
            if (!DataLoader.rankDC.data.isEmpty()) {
                val editor = prefs.edit()
                editor.putString("teamRank", "")
                for (i in 0 until DataLoader.rankDC.data[0].rankings.size) {
                    if (DataLoader.rankDC.data[0].rankings[i]!!.team_key == "frc" + DataLoader.teamNumber) {
                        if (Integer.toString(DataLoader.rankDC.data[0].rankings[i]!!.rank) != null) {
                            editor.putString("teamRank",
                                    Integer.toString(DataLoader.rankDC.data[0].rankings[i]!!.rank))
                        }
                        editor.putString("teamRecord",
                                Rank.recordToString(DataLoader.rankDC.data[0].rankings[i]!!.record))
                        editor.apply()
                    }
                }
            }
            if (context != null) {
                Constants.checkNoDataScreen(DataLoader.rankDC.data, recyclerView, emptyView)
                Animations.loadAnimation(context, recyclerView, recyclerView.adapter, firstLoad,
                        DataLoader.rankDC.parser.isNewData)
                if (firstLoad) firstLoad = false
                if (DataLoader.rankDC.parser.isNewData || DataLoader.teamDC.parser.isNewData) {
                    dataUpdate()
                }
                if (mSwipeRefreshLayout != null) mSwipeRefreshLayout.isRefreshing = false
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
