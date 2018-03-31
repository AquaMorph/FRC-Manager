package com.aquamorph.frcmanager.fragments

import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.os.SystemClock
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.activities.MainActivity
import com.aquamorph.frcmanager.adapters.ScheduleAdapter
import com.aquamorph.frcmanager.decoration.Animations
import com.aquamorph.frcmanager.network.DataLoader
import com.aquamorph.frcmanager.utils.Constants

/**
 * Displays a list of matches at an event.
 *
 * @author Christian Colglazier
 * @version 3/31/2018
 */
class EventScheduleFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener, RefreshFragment {

    internal lateinit var prefs: SharedPreferences
    private var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    private var recyclerView: RecyclerView? = null
    private var emptyView: TextView? = null
    private var adapter: RecyclerView.Adapter<*>? = null
    private var firstLoad: Boolean? = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.registerOnSharedPreferenceChangeListener(this@EventScheduleFragment)

        val view = inflater.inflate(R.layout.fragment_fastscroll, container, false)
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        mSwipeRefreshLayout!!.setColorSchemeResources(R.color.accent)
        mSwipeRefreshLayout!!.setOnRefreshListener { MainActivity.refresh() }

        recyclerView = view.findViewById(R.id.rv)
        emptyView = view.findViewById(R.id.empty_view)
        adapter = ScheduleAdapter(context!!, DataLoader.matchDC.data, DataLoader.teamNumber)
        val llm = LinearLayoutManager(context)
        llm.orientation = LinearLayoutManager.VERTICAL
        recyclerView!!.adapter = adapter
        recyclerView!!.layoutManager = llm

        Constants.checkNoDataScreen(DataLoader.matchDC.data, recyclerView!!, emptyView!!)
        return view
    }

    override fun onResume() {
        super.onResume()
        if (DataLoader.matchDC.data.size == 0)
            refresh(false)
    }

    /**
     * refresh reloads the event schedule and repopulates the listview
     */
    override fun refresh(force: Boolean) {
        if (DataLoader.eventKey != "") {
            LoadEventSchedule(force).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == "teamNumber" || key == "eventKey") {
            if (context != null) {
                adapter = ScheduleAdapter(context!!, DataLoader.matchDC.data,
                        DataLoader.teamNumber)
            }
            val llm = LinearLayoutManager(context)
            llm.orientation = LinearLayoutManager.VERTICAL
            recyclerView!!.adapter = adapter
            recyclerView!!.layoutManager = llm
            refresh(true)
        }
    }

    internal inner class LoadEventSchedule(var force: Boolean) : AsyncTask<Void?, Void?, Void?>() {

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
                Constants.checkNoDataScreen(DataLoader.matchDC.data, recyclerView!!, emptyView!!)
                Animations.loadAnimation(context, recyclerView, adapter, firstLoad,
                        DataLoader.matchDC.parser.isNewData)
                if (firstLoad!!) firstLoad = false
                adapter = ScheduleAdapter(context!!, DataLoader.matchDC.data, DataLoader.teamNumber)
                recyclerView!!.adapter = adapter
                mSwipeRefreshLayout!!.isRefreshing = false
            }
        }
    }

    companion object {

        /**
         * newInstance creates and returns a new EventScheduleFragment
         *
         * @return EventScheduleFragment
         */
        fun newInstance(): EventScheduleFragment {
            return EventScheduleFragment()
        }
    }
}