package com.aquamorph.frcmanager.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.activities.MainActivity
import com.aquamorph.frcmanager.adapters.ScheduleAdapter
import com.aquamorph.frcmanager.network.DataLoader
import com.aquamorph.frcmanager.utils.Constants

abstract class TabFragment : Fragment() {
    internal lateinit var prefs: SharedPreferences
    protected lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    protected lateinit var recyclerView: RecyclerView
    protected lateinit var emptyView: TextView
    protected var firstLoad: Boolean = true
    protected lateinit var adapter: RecyclerView.Adapter<*>
    abstract fun dataUpdate()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    fun onCreateView(view: View, data : ArrayList<*>) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context)


        recyclerView = view.findViewById(R.id.rv)
        emptyView = view.findViewById(R.id.empty_view)

        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        mSwipeRefreshLayout.setColorSchemeResources(R.color.accent)
        mSwipeRefreshLayout.setOnRefreshListener { MainActivity.refresh() }

        adapter = ScheduleAdapter(context!!, DataLoader.matchDC.data, DataLoader.teamNumber)
        val llm = LinearLayoutManager(context)
        llm.orientation = LinearLayoutManager.VERTICAL
        recyclerView.adapter = adapter
        recyclerView.layoutManager = llm

        Constants.checkNoDataScreen(data, recyclerView, emptyView)
    }
}