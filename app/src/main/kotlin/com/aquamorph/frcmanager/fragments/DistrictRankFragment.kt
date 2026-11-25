package com.aquamorph.frcmanager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.adapters.DistrictRankAdapter
import com.aquamorph.frcmanager.decoration.Divider
import com.aquamorph.frcmanager.models.tba.DistrictRank
import com.aquamorph.frcmanager.models.tba.Team
import com.aquamorph.frcmanager.utils.Constants

class DistrictRankFragment : TabFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View =
        inflater.inflate(R.layout.fragment_team_schedule, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val items = ArrayList<DistrictRank>()
        val relatedItems = ArrayList<Team>()
        setupList(
            view,
            DistrictRankAdapter(requireContext(), items, relatedItems),
            Divider(requireContext(), Constants.DIVIDER_WIDTH, 72)
        )
        observeEvent({ it.districtRankings to it.districtTeams }) { (section, related) ->
            items.clear()
            items.addAll(section.items)
            relatedItems.clear()
            relatedItems.addAll(related.items)
            showList(items, section.loading || related.loading)
        }
    }
}
