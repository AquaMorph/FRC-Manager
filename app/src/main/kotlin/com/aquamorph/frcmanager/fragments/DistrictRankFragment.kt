package com.aquamorph.frcmanager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.adapters.DistrictRankAdapter
import com.aquamorph.frcmanager.models.tba.DistrictRank
import com.aquamorph.frcmanager.models.tba.Team

class DistrictRankFragment : TabFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View =
        inflater.inflate(R.layout.fragment_team_schedule, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val items = ArrayList<DistrictRank>()
        val relatedItems = ArrayList<Team>()
        val adapter = DistrictRankAdapter(items, relatedItems)
        setupList(view, adapter)
        observeEvent({ Triple(it.districtRankings, it.districtTeams, it.districtHighlightCutoff) }) { (section, related, cutoff) ->
            val cutoffChanged = adapter.highlightCutoff != cutoff
            val rowsChanged = items != section.items || relatedItems != related.items
            adapter.highlightCutoff = cutoff
            items.clear()
            items.addAll(section.items)
            relatedItems.clear()
            relatedItems.addAll(related.items)
            showList(items, section, related)
            if (cutoffChanged && !rowsChanged) adapter.notifyItemRangeChanged(0, adapter.itemCount)
        }
    }
}
