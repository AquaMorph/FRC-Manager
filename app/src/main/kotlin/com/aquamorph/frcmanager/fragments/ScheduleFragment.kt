package com.aquamorph.frcmanager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.activities.MainActivity
import com.aquamorph.frcmanager.adapters.ScheduleAdapter
import com.aquamorph.frcmanager.adapters.scheduleRows
import com.aquamorph.frcmanager.decoration.Divider
import com.aquamorph.frcmanager.utils.Constants

abstract class ScheduleFragment(private val layout: Int) : TabFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View =
        inflater.inflate(layout, container, false)

    protected fun showSchedule(view: View, teamOnly: Boolean = false, selectedTeam: String? = null) {
        val adapter = ScheduleAdapter(requireContext())
        val divider = if (MainActivity.appTheme == Constants.Theme.BATTERY_SAVER) {
            Divider(requireContext(), Constants.DIVIDER_WIDTH, 0)
        } else null
        setupList(view, adapter, divider)
        // Apply live changes in place rather than sliding or fading the teams around.
        view.findViewById<RecyclerView>(R.id.rv).itemAnimator = null

        observeEvent({ Triple(it.matches, it.display, it.predictions) }) { (section, display, predictions) ->
            val rows = scheduleRows(
                section.items, selectedTeam ?: display.teamNumber, teamOnly, display.matchSort,
                predictions, display.predictionPercentage
            )
            adapter.submitList(rows)
            showListStatus(rows.isEmpty(), section)
        }
    }
}
