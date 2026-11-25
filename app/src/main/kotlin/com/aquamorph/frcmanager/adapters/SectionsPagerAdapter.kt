package com.aquamorph.frcmanager.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.aquamorph.frcmanager.fragments.AllianceFragment
import com.aquamorph.frcmanager.fragments.AwardFragment
import com.aquamorph.frcmanager.fragments.DistrictRankFragment
import com.aquamorph.frcmanager.fragments.EventScheduleFragment
import com.aquamorph.frcmanager.fragments.RankFragment
import com.aquamorph.frcmanager.fragments.TeamFragment
import com.aquamorph.frcmanager.fragments.TeamScheduleFragment
import com.aquamorph.frcmanager.network.EventState

internal class SectionsPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    private enum class Tab(val title: String, val create: () -> Fragment) {
        TEAM_SCHEDULE("Team Schedule", { TeamScheduleFragment() }),
        EVENT_SCHEDULE("Event Schedule", { EventScheduleFragment() }),
        RANKINGS("Rankings", { RankFragment() }),
        TEAMS("Teams", { TeamFragment() }),
        ALLIANCES("Alliances", { AllianceFragment() }),
        AWARDS("Awards", { AwardFragment() }),
        DISTRICT_RANKINGS("District Rankings", { DistrictRankFragment() })
    }

    private var tabs = emptyList<Tab>()

    fun updateTabs(state: EventState) {
        val visible = buildList {
            if (state.matches.items.isNotEmpty()) {
                add(Tab.TEAM_SCHEDULE)
                add(Tab.EVENT_SCHEDULE)
            }
            if (state.rankings.items.isNotEmpty()) add(Tab.RANKINGS)
            if (state.teams.items.isNotEmpty()) add(Tab.TEAMS)
            if (state.alliances.items.isNotEmpty()) add(Tab.ALLIANCES)
            if (state.awards.items.isNotEmpty()) add(Tab.AWARDS)
            if (state.selection.eventKey.isNotEmpty() && state.selection.districtKey.isNotEmpty()) {
                add(Tab.DISTRICT_RANKINGS)
            }
        }
        if (tabs == visible) return
        tabs = visible
        notifyDataSetChanged()
    }

    fun getPageTitle(position: Int) = tabs[position].title
    override fun getItemCount() = tabs.size
    override fun getItemId(position: Int) = tabs[position].ordinal.toLong()
    override fun containsItem(itemId: Long) = tabs.any { it.ordinal.toLong() == itemId }
    override fun createFragment(position: Int) = tabs[position].create()
}
