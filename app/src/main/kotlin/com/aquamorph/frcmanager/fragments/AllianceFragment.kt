package com.aquamorph.frcmanager.fragments

import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.adapters.AllianceAdapter
import com.aquamorph.frcmanager.decoration.Animations
import com.aquamorph.frcmanager.decoration.Divider
import com.aquamorph.frcmanager.models.Alliance
import com.aquamorph.frcmanager.network.DataLoader
import com.aquamorph.frcmanager.utils.Constants

/**
 * Displays a list of alliance for eliminations.
 *
 * @author Christian Colglazier
 * @version 1/23/2020
 */
class AllianceFragment : TabFragment(),
        SharedPreferences.OnSharedPreferenceChangeListener,
        RefreshFragment {

    private var alliances: ArrayList<Alliance> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_team_schedule, container, false)
        super.onCreateView(view, alliances,
                AllianceAdapter(context!!, alliances),
                Divider(context!!, Constants.DIVIDER_WIDTH, 72))
        prefs.registerOnSharedPreferenceChangeListener(this)
        return view
    }

    override fun onResume() {
        super.onResume()
        if (alliances.isEmpty()) {
            refresh()
        }
    }

    /**
     * refresh() loads dataLoader needed for this fragment.
     */
    override fun refresh() {
        task = Constants.runRefresh(task, LoadAlliances())
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == "eventKey") {
            refresh()
        }
    }

    override fun dataUpdate() {
        alliances.clear()
        alliances.addAll(DataLoader.allianceDC.data)
        adapter.notifyDataSetChanged()
        Constants.checkNoDataScreen(DataLoader.allianceDC.data, recyclerView, emptyView)
        Animations.loadAnimation(context, recyclerView, adapter,
                firstLoad, DataLoader.allianceDC.newData)
        firstLoad = false
    }

    internal inner class LoadAlliances : AsyncTask<Void?, Void?, Void?>() {

        override fun onPreExecute() {
            mSwipeRefreshLayout.isRefreshing = true
        }

        override fun doInBackground(vararg params: Void?): Void? {
            while (!DataLoader.allianceDC.complete) {
                SystemClock.sleep(Constants.THREAD_WAIT_TIME.toLong())
            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            if (context != null) {
                dataUpdate()
                mSwipeRefreshLayout.isRefreshing = false
            }
        }
    }

    companion object {

        /**
         * newInstance creates and returns a new AllianceFragment
         *
         * @return AllianceFragment
         */
        fun newInstance(): AllianceFragment {
            return AllianceFragment()
        }
    }
}
