package com.aquamorph.frcmanager.fragments

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
import com.aquamorph.frcmanager.utils.Logging

/**
 * Displays a list of alliance for eliminations.
 *
 * @author Christian Colglazier
 * @version 4/14/2018
 */
class AllianceFragment : TabFragment(), RefreshFragment {

    private var alliances: ArrayList<Alliance> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_team_schedule, container, false)
        super.onCreateView(view, alliances,
                AllianceAdapter(context!!, alliances),
                Divider(context!!, 2f, 72))
        return view
    }

    override fun onResume() {
        super.onResume()
        if (alliances.isEmpty()) {
            refresh(false)
        }
    }

    /**
     * refresh() loads dataLoader needed for this fragment.
     */
    override fun refresh(force: Boolean) {
        LoadAlliances(force).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    override fun dataUpdate() {
        alliances.clear()
        alliances.addAll(DataLoader.allianceDC.data)
        Logging.debug(this, "Data Loader: ${DataLoader.allianceDC.data.size} Alliance: ${alliances.size}", 0)
    }

    internal inner class LoadAlliances(var force: Boolean) : AsyncTask<Void?, Void?, Void?>() {

        override fun onPreExecute() {
            if (mSwipeRefreshLayout != null) mSwipeRefreshLayout.isRefreshing = true
        }

        override fun doInBackground(vararg params: Void?): Void? {
            while (!DataLoader.allianceDC.complete) SystemClock.sleep(Constants.THREAD_WAIT_TIME.toLong())
            return null
        }

        override fun onPostExecute(result: Void?) {
            if (context != null) {
                dataUpdate()
                Constants.checkNoDataScreen(DataLoader.allianceDC.data, recyclerView, emptyView)
                Animations.loadAnimation(context, recyclerView, adapter, firstLoad,
                        DataLoader.allianceDC.parser.isNewData)
                if (firstLoad) firstLoad = false
                if (mSwipeRefreshLayout != null) mSwipeRefreshLayout.isRefreshing = false
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
