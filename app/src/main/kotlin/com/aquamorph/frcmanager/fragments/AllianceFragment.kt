package com.aquamorph.frcmanager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.adapters.AllianceAdapter
import com.aquamorph.frcmanager.models.tba.Alliance
import kotlin.math.roundToInt

class AllianceFragment : TabFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View =
        inflater.inflate(R.layout.fragment_team_schedule, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val items = ArrayList<Alliance>()
        setupList(view, AllianceAdapter(requireContext(), items))
        val list = view.findViewById<RecyclerView>(R.id.rv)
        list.clipToPadding = false
        list.setPadding(0, 0, 0, (10 * resources.displayMetrics.density).roundToInt())
        observeEvent({ it.alliances }) { section ->
            items.clear()
            items.addAll(section.items)
            showList(items, section, emptyMessage = R.string.alliance_empty)
        }
    }
}
