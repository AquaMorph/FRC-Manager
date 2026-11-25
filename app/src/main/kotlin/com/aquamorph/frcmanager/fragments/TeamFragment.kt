package com.aquamorph.frcmanager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.adapters.TeamAdapter
import com.aquamorph.frcmanager.decoration.Divider
import com.aquamorph.frcmanager.models.tba.Rank
import com.aquamorph.frcmanager.models.tba.Team
import com.aquamorph.frcmanager.utils.Constants

class TeamFragment : TabFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View =
        inflater.inflate(R.layout.fragment_team_schedule, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val items = ArrayList<Team>()
        val relatedItems = ArrayList<Rank>()
        setupList(
            view,
            TeamAdapter(requireContext(), items, relatedItems),
            Divider(requireContext(), Constants.DIVIDER_WIDTH, 72)
        )
        observeEvent({ it.teams to it.rankings }) { (section, related) ->
            items.clear()
            items.addAll(section.items.sorted())
            relatedItems.clear()
            relatedItems.addAll(related.items)
            showList(items, section, related)
        }
    }
}
