package com.aquamorph.frcmanager.fragments

import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.os.SystemClock
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.activities.MainActivity
import com.aquamorph.frcmanager.adapters.RankAdapter
import com.aquamorph.frcmanager.decoration.Animations
import com.aquamorph.frcmanager.decoration.Divider
import com.aquamorph.frcmanager.models.Rank
import com.aquamorph.frcmanager.network.DataLoader
import com.aquamorph.frcmanager.utils.Constants

/**
 * Displays the ranks of all the teams at an event.
 *
 * @author Christian Colglazier
 * @version 10/20/2016
 */
class RankFragment : Fragment(), RefreshFragment {

    private var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    private var recyclerView: RecyclerView? = null
    private var emptyView: TextView? = null
    private var adapter: RecyclerView.Adapter<*>? = null
    private var prefs: SharedPreferences? = null
    private var firstLoad: Boolean? = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_team_schedule, container, false)

        prefs = PreferenceManager.getDefaultSharedPreferences(context)

        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        mSwipeRefreshLayout!!.setColorSchemeResources(R.color.accent)
        mSwipeRefreshLayout!!.setOnRefreshListener { MainActivity.refresh() }

        recyclerView = view.findViewById(R.id.rv)
        recyclerView!!.addItemDecoration(Divider(context!!, 2f, 72))
        emptyView = view.findViewById(R.id.empty_view)
        adapter = RankAdapter(context!!, DataLoader.rankDC.data, DataLoader.teamDC.data)
        val llm = LinearLayoutManager(context)
        llm.orientation = LinearLayoutManager.VERTICAL
        recyclerView!!.adapter = adapter
        if (Constants.isLargeScreen(context!!)) {
            recyclerView!!.layoutManager = GridLayoutManager(context, 2)
        } else {
            recyclerView!!.layoutManager = GridLayoutManager(context, 1)
        }

        if (savedInstanceState == null) refresh(false)
        Constants.checkNoDataScreen(DataLoader.rankDC.data, recyclerView!!, emptyView!!)
        return view
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
        if (DataLoader.eventKey != "" && DataLoader.teamNumber != ""
                && context != null) {
            LoadRanks(force).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }
    }

    private inner class LoadRanks internal constructor(internal var force: Boolean) : AsyncTask<Void?, Void?, Void?>() {

        override fun onPreExecute() {
            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout!!.isRefreshing = true
            }
        }

        override fun doInBackground(vararg params: Void?): Void? {
            while (!DataLoader.teamDC.complete) SystemClock.sleep(Constants.THREAD_WAIT_TIME.toLong())
            while (!DataLoader.rankDC.complete) SystemClock.sleep(Constants.THREAD_WAIT_TIME.toLong())
            return null
        }

        override fun onPostExecute(result: Void?) {
            if (!DataLoader.rankDC.data.isEmpty()) {
                val editor = prefs!!.edit()
                editor.putString("teamRank", "")
                for (i in 0 until DataLoader.rankDC.data[0].rankings.size) {
                    if (DataLoader.rankDC.data[0].rankings[i]!!.team_key == "frc" + DataLoader.teamNumber) {
                        if (Integer.toString(DataLoader.rankDC.data[0].rankings[i]!!.rank) != null) {
                            editor.putString("teamRank",
                                    Integer.toString(DataLoader.rankDC.data[0].rankings[i]!!.rank))
                        }
                        if (DataLoader.rankDC.data[0].rankings[i]!!.record != null) {
                            editor.putString("teamRecord", Rank.recordToString(
                                    DataLoader.rankDC.data[0].rankings[i]!!.record))
                        }
                        editor.apply()
                    }
                }
            }
            if (context != null) {
                Constants.checkNoDataScreen(DataLoader.rankDC.data, recyclerView!!, emptyView!!)
                Animations.loadAnimation(context, recyclerView, adapter, firstLoad,
                        DataLoader.rankDC.parser.isNewData)
                if (firstLoad!!) firstLoad = false
                adapter = RankAdapter(context!!, DataLoader.rankDC.data, DataLoader.teamDC.data)
                val llm = LinearLayoutManager(context)
                llm.orientation = LinearLayoutManager.VERTICAL
                recyclerView!!.adapter = adapter
                mSwipeRefreshLayout!!.isRefreshing = false
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
