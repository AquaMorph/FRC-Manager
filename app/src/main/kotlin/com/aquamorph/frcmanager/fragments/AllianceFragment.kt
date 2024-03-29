package com.aquamorph.frcmanager.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.adapters.AllianceAdapter
import com.aquamorph.frcmanager.decoration.Animations
import com.aquamorph.frcmanager.decoration.Divider
import com.aquamorph.frcmanager.models.tba.Alliance
import com.aquamorph.frcmanager.network.DataLoader
import com.aquamorph.frcmanager.utils.Constants

/**
 * Displays a list of alliance for eliminations.
 *
 * @author Christian Colglazier
 * @version 3/25/2023
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
                AllianceAdapter(requireContext(), alliances),
                Divider(requireContext(), Constants.DIVIDER_WIDTH, 72))
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
        mSwipeRefreshLayout.isRefreshing = true
        executor.execute {
            while (!DataLoader.allianceDC.complete) {
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

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == "eventKey") {
            refresh()
        }
    }

    override fun dataUpdate() {
        alliances.clear()
        alliances.addAll(DataLoader.allianceDC.data)
        Constants.checkNoDataScreen(DataLoader.allianceDC.data, recyclerView, emptyView)
        Animations.loadAnimation(context, recyclerView, adapter,
                firstLoad, DataLoader.allianceDC.newData)
        firstLoad = false
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
