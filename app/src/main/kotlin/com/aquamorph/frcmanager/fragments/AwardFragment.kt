package com.aquamorph.frcmanager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.adapters.AwardAdapter
import com.aquamorph.frcmanager.decoration.Divider
import com.aquamorph.frcmanager.models.tba.Award
import com.aquamorph.frcmanager.utils.Constants

class AwardFragment : TabFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View =
        inflater.inflate(R.layout.fragment_team_schedule, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val items = ArrayList<Award>()
        setupList(
            view,
            AwardAdapter(requireContext(), items),
            Divider(requireContext(), Constants.DIVIDER_WIDTH, 0)
        )
        observeEvent({ it.awards }) { section ->
            items.clear()
            items.addAll(section.items.filterNot { award ->
                award.recipientList.singleOrNull()?.let { it.awardee == null && it.teamKey == null } == true
            })
            showList(items, section)
        }
    }
}
