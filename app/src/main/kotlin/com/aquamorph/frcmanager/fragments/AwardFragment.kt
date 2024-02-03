package com.aquamorph.frcmanager.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.adapters.AwardAdapter
import com.aquamorph.frcmanager.decoration.Animations
import com.aquamorph.frcmanager.decoration.Divider
import com.aquamorph.frcmanager.models.tba.Award
import com.aquamorph.frcmanager.network.DataLoader
import com.aquamorph.frcmanager.utils.Constants

/**
 * Displays a list of awards at a event
 *
 * @author Christian Colglazier
 * @version 3/25/2023
 */
class AwardFragment :
        TabFragment(), SharedPreferences.OnSharedPreferenceChangeListener, RefreshFragment {

    private var awards: ArrayList<Award> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_team_schedule, container, false)
        super.onCreateView(view, awards,
                AwardAdapter(requireContext(), awards),
                Divider(requireContext(), Constants.DIVIDER_WIDTH, 0))
        prefs.registerOnSharedPreferenceChangeListener(this)
        return view
    }

    override fun dataUpdate() {
        awards.clear()
        awards.addAll(DataLoader.awardDC.data)
        filterAwards()
        Constants.checkNoDataScreen(DataLoader.awardDC.data, recyclerView, emptyView)
        Animations.loadAnimation(context, recyclerView, adapter,
                firstLoad, DataLoader.awardDC.newData)
        firstLoad = false
    }

    /**
     * filterAwards() removes awards that were not given out.
     */
    private fun filterAwards() {
        var i = 0
        while (i < awards.size) {
            if (awards[i].recipientList.size == 1 &&
                    awards[i].recipientList[0].awardee == null &&
                    awards[i].recipientList[0].teamKey == null) {
                awards.remove(awards[i])
            } else {
                i++
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (awards.isEmpty())
            refresh()
    }

    /**
     * refresh() loads dataLoader needed for this fragment.
     */
    override fun refresh() {
        if (DataLoader.eventKey != "") {
            mSwipeRefreshLayout.isRefreshing = true
            executor.execute {
                while (!DataLoader.awardDC.complete) {
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
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        if (key == "eventKey") {
            refresh()
        }
    }

    companion object {

        /**
         * newInstance creates and returns a new AwardFragment
         *
         * @return AwardFragment
         */
        fun newInstance(): AwardFragment {
            return AwardFragment()
        }
    }
}
