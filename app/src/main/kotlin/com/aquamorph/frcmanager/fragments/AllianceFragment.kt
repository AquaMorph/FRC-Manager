package com.aquamorph.frcmanager.fragments

import android.os.AsyncTask
import android.os.Bundle
import android.os.SystemClock
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
import com.aquamorph.frcmanager.adapters.AllianceAdapter
import com.aquamorph.frcmanager.decoration.Animations
import com.aquamorph.frcmanager.decoration.Divider
import com.aquamorph.frcmanager.network.DataLoader
import com.aquamorph.frcmanager.utils.Constants

/**
 * Displays a list of alliance for eliminations.
 *
 * @author Christian Colglazier
 * @version 4/2/2018
 */
class AllianceFragment : Fragment(), RefreshFragment {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
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
        swipeRefreshLayout.setColorSchemeResources(R.color.accent)
        swipeRefreshLayout.setOnRefreshListener { MainActivity.refresh() }

        recyclerView = view.findViewById(R.id.rv)
        recyclerView.addItemDecoration(Divider(context!!, 2f, 72))
        emptyView = view.findViewById(R.id.empty_view)
        adapter = AllianceAdapter(context!!, DataLoader.allianceDC.data)
        val llm = LinearLayoutManager(context)
        llm.orientation = LinearLayoutManager.VERTICAL
        recyclerView.adapter = adapter
        recyclerView.layoutManager = llm
        Constants.checkNoDataScreen(DataLoader.allianceDC.data, recyclerView, emptyView)
        return view
    }

    override fun onResume() {
        super.onResume()
        if (DataLoader.allianceDC.data.size == 0) {
            refresh(false)
        }
    }

    /**
     * refresh() loads dataLoader needed for this fragment.
     */
    override fun refresh(force: Boolean) {
        LoadAlliances(force).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    internal inner class LoadAlliances(var force: Boolean) : AsyncTask<Void?, Void?, Void?>() {

        override fun onPreExecute() {
            swipeRefreshLayout.isRefreshing = true
        }

        override fun doInBackground(vararg params: Void?): Void? {
            while (!DataLoader.allianceDC.complete) SystemClock.sleep(Constants.THREAD_WAIT_TIME.toLong())
            return null
        }

        override fun onPostExecute(result: Void?) {
            if (context != null) {
                Constants.checkNoDataScreen(DataLoader.allianceDC.data, recyclerView, emptyView)
                Animations.loadAnimation(context, recyclerView, adapter, firstLoad,
                        DataLoader.allianceDC.parser.isNewData)
                if (firstLoad!!) firstLoad = false
                adapter = AllianceAdapter(context!!, DataLoader.allianceDC.data)
                recyclerView.adapter = adapter
                swipeRefreshLayout.isRefreshing = false
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
