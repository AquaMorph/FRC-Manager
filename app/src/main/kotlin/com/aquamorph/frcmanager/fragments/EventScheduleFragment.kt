package com.aquamorph.frcmanager.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.activities.MainActivity
import com.aquamorph.frcmanager.adapters.ScheduleAdapter
import com.aquamorph.frcmanager.decoration.Animations
import com.aquamorph.frcmanager.decoration.Divider
import com.aquamorph.frcmanager.models.tba.Match
import com.aquamorph.frcmanager.network.DataLoader
import com.aquamorph.frcmanager.utils.Constants
import com.aquamorph.frcmanager.utils.MatchSort

/**
 * Displays a list of matches at an event.
 *
 * @author Christian Colglazier
 * @version 3/25/2023
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
                    ScheduleAdapter(requireContext(), matches, DataLoader.teamNumber),
                    Divider(requireContext(), Constants.DIVIDER_WIDTH, 0))
        } else {
            super.onCreateView(view, matches,
                    ScheduleAdapter(requireContext(), matches, DataLoader.teamNumber))
        }
        prefs.registerOnSharedPreferenceChangeListener(this)

        return view
    }

    override fun dataUpdate() {
        matches.clear()
        matches.addAll(DataLoader.matchDC.data)
        prefs.edit().putString("nextMatch", "%s".format(nextMatch(matches))).apply()
        MatchSort.sortMatches(matches, prefs.getString("matchSort", ""))
        Constants.checkNoDataScreen(matches, recyclerView, emptyView)
        if (firstLoad || !DataLoader.matchDC.newData) adapter.notifyDataSetChanged()
        Animations.loadAnimation(context, recyclerView, adapter, firstLoad, DataLoader.matchDC.newData)
        firstLoad = false
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
        val scheduleView = view
        if (DataLoader.eventKey != "" && scheduleView != null) {
            mSwipeRefreshLayout.isRefreshing = true
            executor.execute {
                while (!DataLoader.matchDC.complete) {
                    SystemClock.sleep(Constants.THREAD_WAIT_TIME.toLong())
                }
                handler.post {
                    if (view === scheduleView) {
                        dataUpdate()
                        mSwipeRefreshLayout.isRefreshing = false
                    }
                }
            }
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == "teamNumber" || key == "eventKey" || key == "matchSort") {
            refresh()
        }
    }

    /**
     * nextMatch() returns the next match to be played in the event.
     *
     * @return match currently being played
     */
    private fun nextMatch(matches: ArrayList<Match>): String {
        matches.sort()
        for (match in matches) {
            if (match.postResultTime <= 0) {
                return "Playing %S-%s".format(match.compLevel, match.matchNumber)
            }
        }
        return ""
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
