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
import com.aquamorph.frcmanager.adapters.AwardAdapter
import com.aquamorph.frcmanager.decoration.Animations
import com.aquamorph.frcmanager.decoration.Divider
import com.aquamorph.frcmanager.network.DataLoader
import com.aquamorph.frcmanager.utils.Constants

/**
 * Displays a list of awards at a event
 *
 * @author Christian Colglazier
 * @version 4/2/2018
 */
class AwardFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener, RefreshFragment {

    internal lateinit var prefs: SharedPreferences
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
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
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout!!.setColorSchemeResources(R.color.accent)
        swipeRefreshLayout!!.setOnRefreshListener { MainActivity.refresh() }

        recyclerView = view.findViewById(R.id.rv)
        emptyView = view.findViewById(R.id.empty_view)
        adapter = AwardAdapter(context!!, DataLoader.awardDC.data)
        val llm = LinearLayoutManager(context)
        llm.orientation = LinearLayoutManager.VERTICAL
        recyclerView.adapter = adapter
        recyclerView.layoutManager = llm
        recyclerView.addItemDecoration(Divider(context!!, 2f, 0))

        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.registerOnSharedPreferenceChangeListener(this@AwardFragment)

        if (savedInstanceState == null) refresh(false)
        Constants.checkNoDataScreen(DataLoader.awardDC.data, recyclerView, emptyView)
        return view
    }

    override fun onResume() {
        super.onResume()
        if (DataLoader.awardDC.data.size == 0)
            refresh(false)
    }

    /**
     * refrest() loads dataLoader needed for this fragment
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
            if (swipeRefreshLayout != null) swipeRefreshLayout!!.isRefreshing = true
        }

        override fun doInBackground(vararg p0: Void?): Void? {
            while (!DataLoader.awardDC.complete) SystemClock.sleep(Constants.THREAD_WAIT_TIME.toLong())
            return null
        }

        override fun onPostExecute(result: Void?) {
            if (context != null) {
                Constants.checkNoDataScreen(DataLoader.awardDC.data, recyclerView, emptyView)
                Animations.loadAnimation(context, recyclerView, adapter, firstLoad,
                        DataLoader.awardDC.parser.isNewData)
                if (firstLoad!!) firstLoad = false
                adapter = AwardAdapter(context!!, DataLoader.awardDC.data)
                recyclerView.adapter = adapter
                if (swipeRefreshLayout != null) swipeRefreshLayout!!.isRefreshing = false
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
