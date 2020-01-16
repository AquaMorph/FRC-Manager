package com.aquamorph.frcmanager.fragments

import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.activities.MainActivity
import com.aquamorph.frcmanager.adapters.ScheduleAdapter
import com.aquamorph.frcmanager.decoration.Divider
import com.aquamorph.frcmanager.models.Match
import com.aquamorph.frcmanager.network.DataLoader
import com.aquamorph.frcmanager.utils.Constants

/**
 * Displays a list of matches at an event.
 *
 * @author Christian Colglazier
 * @version 4/14/2018
 */
class EventScheduleFragment :
        TabFragment(), SharedPreferences.OnSharedPreferenceChangeListener, RefreshFragment {

    private var matches: ArrayList<Match> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_fastscroll, container, false)
        if (MainActivity.appTheme == Constants.Theme.BATTERY_SAVER) {
            super.onCreateView(view, matches,
                    ScheduleAdapter(context!!, matches, DataLoader.teamNumber),
                    Divider(context!!, Constants.DIVIDER_WIDTH, 0))
        } else {
            super.onCreateView(view, matches,
                    ScheduleAdapter(context!!, matches, DataLoader.teamNumber))
        }
        prefs.registerOnSharedPreferenceChangeListener(this)

        return view
    }

    override fun dataUpdate() {
        matches.clear()
        matches.addAll(DataLoader.matchDC.data)
        prefs.edit().putString("nextMatch", "%s".format(nextMatch(matches))).apply()
        adapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        if (matches.isEmpty())
            refresh()
    }

    /**
     * refresh reloads the event schedule and repopulates the listview
     */
    override fun refresh() {
        if (DataLoader.eventKey != "") {
            LoadEventSchedule().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == "teamNumber" || key == "eventKey") {
            refresh()
        }
    }

    /**
     * nextMatch() returns the next match to be played in the event.
     */
    private fun nextMatch(matches: ArrayList<Match>): String {
        for (match in matches) {
            if (match.post_result_time <= 0) {
                return "Playing %S-%s".format(match.comp_level, match.match_number)
            }
        }
        return ""
    }

    internal inner class LoadEventSchedule : AsyncTask<Void?, Void?, Void?>() {

        override fun onPreExecute() {
            mSwipeRefreshLayout.isRefreshing = true
        }

        override fun doInBackground(vararg params: Void?): Void? {
            while (!DataLoader.matchDC.complete) SystemClock.sleep(Constants.THREAD_WAIT_TIME.toLong())
            return null
        }

        override fun onPostExecute(result: Void?) {
            if (context != null) {
                dataUpdate()
                Constants.checkNoDataScreen(matches, recyclerView, emptyView)
                mSwipeRefreshLayout.isRefreshing = false
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
