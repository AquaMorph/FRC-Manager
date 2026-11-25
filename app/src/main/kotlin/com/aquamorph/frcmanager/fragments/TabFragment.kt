package com.aquamorph.frcmanager.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.activities.MainActivity
import com.aquamorph.frcmanager.decoration.Animations
import com.aquamorph.frcmanager.network.EventState
import com.aquamorph.frcmanager.network.EventViewModel
import com.aquamorph.frcmanager.utils.Constants
import com.aquamorph.frcmanager.utils.ThemeSwipeRefreshLayout
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

abstract class TabFragment : Fragment() {
    private var views: TabViews? = null
    private var firstLoad = true

    private class TabViews(view: View, val adapter: RecyclerView.Adapter<*>) {
        val recycler = view.findViewById<RecyclerView>(R.id.rv)
        val empty = view.findViewById<TextView>(R.id.empty_view)
        val swipe = view.findViewById<ThemeSwipeRefreshLayout>(R.id.swipeRefreshLayout)
    }

    protected fun setupList(view: View, adapter: RecyclerView.Adapter<*>, decor: RecyclerView.ItemDecoration? = null) {
        val list = TabViews(view, adapter)
        views = list
        firstLoad = true
        val host = activity as? MainActivity
        list.swipe.setColorSchemeResources(R.color.accent)
        list.swipe.isEnabled = host != null
        list.swipe.setOnRefreshListener { host?.refresh() }
        list.recycler.layoutManager = LinearLayoutManager(context)
        list.recycler.adapter = adapter
        if (decor != null) list.recycler.addItemDecoration(decor)
    }

    internal fun <T> observeEvent(select: (EventState) -> T, render: (T) -> Unit) {
        val owner = viewLifecycleOwner
        val host = requireActivity()
        val model = ViewModelProvider(host, EventViewModel.factory(host.applicationContext))[EventViewModel::class.java]
        owner.lifecycleScope.launch {
            owner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.state.map(select).distinctUntilChanged().collect(render)
            }
        }
    }

    protected fun showList(items: List<*>, loading: Boolean) {
        val list = checkNotNull(views)
        list.swipe.isRefreshing = loading
        Constants.checkNoDataScreen(items, list.recycler, list.empty)
        if (loading && items.isEmpty()) list.empty.visibility = View.GONE
        list.adapter.notifyDataSetChanged()
        if (firstLoad && items.isNotEmpty()) {
            Animations.loadAnimation(context, list.recycler, list.adapter, firstLoad = true, isNewData = false)
            firstLoad = false
        }
    }

    override fun onDestroyView() {
        views?.recycler?.adapter = null
        views = null
        super.onDestroyView()
    }
}
