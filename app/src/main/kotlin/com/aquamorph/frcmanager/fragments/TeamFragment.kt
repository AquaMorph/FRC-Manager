package com.aquamorph.frcmanager.fragments

import android.os.AsyncTask
import android.os.Bundle
import android.os.SystemClock
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
import com.aquamorph.frcmanager.adapters.TeamAdapter
import com.aquamorph.frcmanager.decoration.Animations
import com.aquamorph.frcmanager.decoration.Divider
import com.aquamorph.frcmanager.network.DataLoader
import com.aquamorph.frcmanager.utils.Constants

/**
 * Displays a list of teams at an event.
 *
 * @author Christian Colglazier
 * @version 4/2/2018
 */
class TeamFragment : Fragment(), RefreshFragment {

    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView
    private lateinit var adapter: RecyclerView.Adapter<*>
    private var firstLoad: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_team_schedule, container, false)
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        mSwipeRefreshLayout.setColorSchemeResources(R.color.accent)
        mSwipeRefreshLayout.setOnRefreshListener { MainActivity.refresh() }

        recyclerView = view.findViewById(R.id.rv)
        emptyView = view.findViewById(R.id.empty_view)
        adapter = TeamAdapter(context!!, DataLoader.teamDC.data, DataLoader.rankDC.data)
        val llm = LinearLayoutManager(context)
        llm.orientation = LinearLayoutManager.VERTICAL
        recyclerView.addItemDecoration(Divider(context!!, 2f, 72))
        recyclerView.adapter = adapter
        if (Constants.isLargeScreen(context!!)) {
            recyclerView.layoutManager = GridLayoutManager(context, 2)
        } else {
            recyclerView.layoutManager = GridLayoutManager(context, 1)
        }

        //		if (savedInstanceState == null) refresh(false);
        Constants.checkNoDataScreen(DataLoader.teamDC.data, recyclerView, emptyView)
        return view
    }

    override fun onResume() {
        super.onResume()
        if (DataLoader.teamDC.data.size == 0)
            refresh(false)
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
                Constants.checkNoDataScreen(DataLoader.teamDC.data, recyclerView, emptyView)
                Animations.loadAnimation(context, recyclerView, adapter, firstLoad,
                        DataLoader.teamDC.parser.isNewData)
                if (firstLoad) firstLoad = false
                adapter = TeamAdapter(context!!, DataLoader.teamDC.data, DataLoader.rankDC.data)
                recyclerView.adapter = adapter
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
