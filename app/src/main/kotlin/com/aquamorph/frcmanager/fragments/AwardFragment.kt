package com.aquamorph.frcmanager.fragments

import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.adapters.AwardAdapter
import com.aquamorph.frcmanager.decoration.Animations
import com.aquamorph.frcmanager.decoration.Divider
import com.aquamorph.frcmanager.models.Award
import com.aquamorph.frcmanager.network.DataLoader
import com.aquamorph.frcmanager.utils.Constants

/**
 * Displays a list of awards at a event
 *
 * @author Christian Colglazier
 * @version 4/14/2018
 */
class AwardFragment :
        TabFragment(), SharedPreferences.OnSharedPreferenceChangeListener, RefreshFragment {

    private var awards: ArrayList<Award> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_team_schedule, container, false)
        super.onCreateView(view, awards,
                AwardAdapter(context!!, awards),
                Divider(context!!, 2f, 0))
        prefs.registerOnSharedPreferenceChangeListener(this)
        return view
    }

    override fun dataUpdate() {
        awards.clear()
        awards.addAll(DataLoader.awardDC.data)
    }

    override fun onResume() {
        super.onResume()
        if (awards.isEmpty())
            refresh(false)
    }

    /**
     * refresh() loads dataLoader needed for this fragment
     *
     * @param force force reload dataLoader
     */
    override fun refresh(force: Boolean) {
        if (DataLoader.eventKey != "") {
            LoadAwards(force).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == "eventKey") {
            refresh(true)
        }
    }

    internal inner class LoadAwards(var force: Boolean) : AsyncTask<Void?, Void?, Void?>() {

        override fun onPreExecute() {
            if (mSwipeRefreshLayout != null) mSwipeRefreshLayout.isRefreshing = true
        }

        override fun doInBackground(vararg p0: Void?): Void? {
            while (!DataLoader.awardDC.complete) SystemClock.sleep(Constants.THREAD_WAIT_TIME.toLong())
            return null
        }

        override fun onPostExecute(result: Void?) {
            if (context != null) {
                dataUpdate()
                Constants.checkNoDataScreen(DataLoader.awardDC.data, recyclerView, emptyView)
//                Animations.loadAnimation(context, recyclerView, adapter, firstLoad,
//                        DataLoader.awardDC.parser.isNewData)
                if (firstLoad) firstLoad = false
                if (mSwipeRefreshLayout != null) mSwipeRefreshLayout.isRefreshing = false
            }
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
