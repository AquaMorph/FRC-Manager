package com.aquamorph.frcmanager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aquamorph.frcmanager.activities.MainActivity
import com.aquamorph.frcmanager.adapters.ScheduleAdapter
import com.aquamorph.frcmanager.decoration.Divider
import com.aquamorph.frcmanager.models.tba.Match
import com.aquamorph.frcmanager.utils.Constants
import com.aquamorph.frcmanager.utils.MatchSort

abstract class ScheduleFragment(private val layout: Int) : TabFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View =
        inflater.inflate(layout, container, false)

    protected fun showSchedule(view: View, teamOnly: Boolean = false, selectedTeam: String? = null) {
        val matches = ArrayList<Match>()
        val adapter = ScheduleAdapter(requireContext(), matches)
        val divider = if (MainActivity.appTheme == Constants.Theme.BATTERY_SAVER) {
            Divider(requireContext(), Constants.DIVIDER_WIDTH, 0)
        } else null
        setupList(view, adapter, divider)

        observeEvent({ Triple(it.matches, selectedTeam ?: it.display.teamNumber, it.display.matchSort) }) { (section, team, sort) ->
            matches.clear()
            val key = "frc$team"
            matches.addAll(section.items.filter {
                !teamOnly || key in it.alliances.red.teamKeys || key in it.alliances.blue.teamKeys
            })
            MatchSort.sortMatches(matches, sort)
            adapter.highlightedTeam = team
            showList(matches, section)
        }
        observeEvent({ it.predictions to it.display.predictionPercentage }) { (predictions, percentage) ->
            adapter.updatePredictions(predictions, percentage)
        }
    }
}
