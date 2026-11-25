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
import com.aquamorph.frcmanager.network.EventSelection

internal enum class EventTab(val title: String, val create: () -> Fragment) {
    TEAM_SCHEDULE("Team Schedule", { TeamScheduleFragment() }),
    EVENT_SCHEDULE("Event Schedule", { EventScheduleFragment() }),
    RANKINGS("Rankings", { RankFragment() }),
    TEAMS("Teams", { TeamFragment() }),
    ALLIANCES("Alliances", { AllianceFragment() }),
    AWARDS("Awards", { AwardFragment() }),
    DISTRICT_RANKINGS("District Rankings", { DistrictRankFragment() });

    companion object {
        fun forSelection(selection: EventSelection): List<EventTab> = when {
            selection.eventKey.isEmpty() -> emptyList()
            selection.districtKey.isEmpty() -> entries.filterNot { it == DISTRICT_RANKINGS }
            else -> entries
        }
    }
}

internal class SectionsPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    private var tabs = emptyList<EventTab>()

    fun updateTabs(selection: EventSelection) {
        val visible = EventTab.forSelection(selection)
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
