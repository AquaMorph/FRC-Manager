package com.aquamorph.frcmanager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.adapters.AwardAdapter
import com.aquamorph.frcmanager.models.tba.Award

class AwardFragment : TabFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View =
        inflater.inflate(R.layout.fragment_awards, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val items = ArrayList<Award>()
        val teamNames = mutableMapOf<String, String>()
        setupList(view, AwardAdapter(items, teamNames))
        observeEvent({ Triple(it.awards, it.teams, it.districtTeams.items) }) { (section, teams, districtTeams) ->
            items.clear()
            items.addAll(section.items.filterNot { award ->
                award.recipientList.singleOrNull()?.let { it.awardee == null && it.teamKey == null } == true
            })
            val roster = districtTeams + teams.items
            teamNames.clear()
            for (team in roster) {
                val name = team.nickname?.takeIf { it.isNotBlank() } ?: team.name
                if (!name.isNullOrBlank()) teamNames[team.key] = name
            }
            showList(items, section, teams.copy(items = roster), emptyMessage = R.string.award_empty)
        }
    }
}
