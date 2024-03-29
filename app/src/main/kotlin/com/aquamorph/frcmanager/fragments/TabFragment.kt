package com.aquamorph.frcmanager.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.activities.MainActivity
import com.aquamorph.frcmanager.utils.Constants
import com.aquamorph.frcmanager.utils.ThemeSwipeRefreshLayout
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

abstract class TabFragment : Fragment() {
    internal lateinit var prefs: SharedPreferences
    protected lateinit var mSwipeRefreshLayout: ThemeSwipeRefreshLayout
    protected lateinit var recyclerView: RecyclerView
    protected lateinit var emptyView: TextView
    protected lateinit var adapter: Adapter<*>
    protected var firstLoad = true
    protected val executor: ExecutorService = Executors.newSingleThreadExecutor()
    protected val handler = Handler(Looper.getMainLooper())

    abstract fun dataUpdate()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    fun onCreateView(
        view: View,
        data: ArrayList<*>,
        adp: Adapter<*>,
        decor: RecyclerView.ItemDecoration
    ) {
        onCreateView(view, data, adp)
        recyclerView.addItemDecoration(decor)
    }

    fun onCreateView(view: View, data: ArrayList<*>, adp: Adapter<*>) {
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())

        recyclerView = view.findViewById(R.id.rv)
        emptyView = view.findViewById(R.id.empty_view)

        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        mSwipeRefreshLayout.setColorSchemeResources(R.color.accent)
        mSwipeRefreshLayout.setOnRefreshListener { MainActivity.refresh() }

        adapter = adp
        val llm = LinearLayoutManager(context)
        llm.orientation = LinearLayoutManager.VERTICAL
        recyclerView.adapter = adapter
        recyclerView.layoutManager = llm

        Constants.checkNoDataScreen(data, recyclerView, emptyView)
    }
}
