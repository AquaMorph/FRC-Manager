package com.aquamorph.frcmanager.fragments

import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.adapters.ScheduleAdapter
import com.aquamorph.frcmanager.decoration.Animations
import com.aquamorph.frcmanager.models.Match
import com.aquamorph.frcmanager.network.DataLoader
import com.aquamorph.frcmanager.utils.Constants
import com.aquamorph.frcmanager.utils.Logging

/**
 * Displays a list of matches at an event.
 *
 * @author Christian Colglazier
 * @version 4/14/2018
 */
class EventScheduleFragment :
        TabFragment(), SharedPreferences.OnSharedPreferenceChangeListener, RefreshFragment {

    private var matches: ArrayList<Match> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_fastscroll, container, false)
        super.onCreateView(view, DataLoader.matchDC.data)
        prefs.registerOnSharedPreferenceChangeListener(this)
        return view
    }

    override fun dataUpdate() {
        matches.clear()
        matches.addAll(DataLoader.matchDC.data)
        Logging.debug(this, matches.size.toString(),0)
    }

    override fun onResume() {
        super.onResume()
        if (matches.isEmpty())
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
                adapter = ScheduleAdapter(context!!, matches, DataLoader.teamNumber)
            }
            val llm = LinearLayoutManager(context)
            llm.orientation = LinearLayoutManager.VERTICAL
            recyclerView.adapter = adapter
            recyclerView.layoutManager = llm
            refresh(true)
        }
    }

    internal inner class LoadEventSchedule(var force: Boolean) : AsyncTask<Void?, Void?, Void?>() {

        override fun onPreExecute() {
            if (mSwipeRefreshLayout != null) mSwipeRefreshLayout.isRefreshing = true
        }

        override fun doInBackground(vararg params: Void?): Void? {
            while (!DataLoader.matchDC.complete) SystemClock.sleep(Constants.THREAD_WAIT_TIME.toLong())
            return null
        }

        override fun onPostExecute(result: Void?) {
            if (context != null) {
                dataUpdate()
                Constants.checkNoDataScreen(matches, recyclerView, emptyView)
                Animations.loadAnimation(context, recyclerView, adapter, firstLoad,
                        DataLoader.matchDC.parser.isNewData)
                if (firstLoad) firstLoad = false
                if (mSwipeRefreshLayout != null) mSwipeRefreshLayout.isRefreshing = false
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