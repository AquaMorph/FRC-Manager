package com.aquamorph.frcmanager.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.activities.MainActivity
import com.aquamorph.frcmanager.network.EventList
import com.aquamorph.frcmanager.network.EventLoad
import com.aquamorph.frcmanager.network.EventState
import com.aquamorph.frcmanager.network.EventViewModel
import com.aquamorph.frcmanager.utils.ThemeSwipeRefreshLayout
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

abstract class TabFragment : Fragment() {
    private var views: TabViews? = null

    private class TabViews(view: View) {
        val recycler = view.findViewById<RecyclerView>(R.id.rv)
        val empty = view.findViewById<TextView>(R.id.empty_view)
        val swipe = view.findViewById<ThemeSwipeRefreshLayout>(R.id.swipeRefreshLayout)
        var displayedItems: Pair<List<*>, List<*>?>? = null
    }

    protected fun setupList(view: View, adapter: RecyclerView.Adapter<*>, decor: RecyclerView.ItemDecoration? = null) {
        val list = TabViews(view)
        views = list
        val host = activity as? MainActivity
        list.swipe.setColorSchemeResources(R.color.accent)
        list.swipe.isEnabled = host != null
        list.swipe.setOnRefreshListener { host?.refresh() }
        list.recycler.layoutManager = LinearLayoutManager(context)
        list.recycler.adapter = adapter
        if (decor != null) list.recycler.addItemDecoration(decor)
    }

    internal fun <T> observeEvent(select: (EventState) -> T, render: suspend (T) -> Unit) {
        val owner = viewLifecycleOwner
        val host = requireActivity()
        val model = ViewModelProvider(host, EventViewModel.factory(host.applicationContext))[EventViewModel::class.java]
        owner.lifecycleScope.launch {
            owner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.state.map(select).distinctUntilChanged().collectLatest(render)
            }
        }
    }

    internal fun showList(
        items: List<*>,
        section: EventList<*>,
        related: EventList<*>? = null,
        @StringRes emptyMessage: Int = R.string.noData
    ) {
        showListStatus(items.isEmpty(), section, related, emptyMessage)
        val list = checkNotNull(views)
        val data = items to related?.items
        if (list.displayedItems != data) {
            list.displayedItems = items.toList() to related?.items
            list.recycler.adapter?.notifyDataSetChanged()
        }
    }

    internal fun showListStatus(
        isEmpty: Boolean,
        section: EventList<*>,
        related: EventList<*>? = null,
        @StringRes emptyMessage: Int = R.string.noData
    ) {
        val list = checkNotNull(views)
        list.swipe.isRefreshing = section.loading || (!isEmpty && related?.loading == true)
        list.empty.setText(if (section.load == EventLoad.FAILED) R.string.eventDataLoadError else emptyMessage)
        list.empty.visibility = if (isEmpty && !section.loading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        views?.recycler?.adapter = null
        views = null
        super.onDestroyView()
    }
}
