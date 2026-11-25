package com.aquamorph.frcmanager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.adapters.ScheduleAdapter
import com.aquamorph.frcmanager.adapters.ScheduleRow
import com.aquamorph.frcmanager.adapters.scheduleRows
import com.aquamorph.frcmanager.models.tba.Match
import com.aquamorph.frcmanager.network.EventDisplay
import com.aquamorph.frcmanager.network.SchedulePredictions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class ScheduleFragment(private val layout: Int) : TabFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View =
        inflater.inflate(layout, container, false)

    protected fun showSchedule(view: View, teamOnly: Boolean = false, selectedTeam: String? = null) {
        val adapter = ScheduleAdapter(requireContext())
        setupList(view, adapter)
        view.findViewById<RecyclerView>(R.id.rv).apply {
            setHasFixedSize(true)
            // Apply live changes in place rather than sliding or fading the teams around.
            itemAnimator = null
        }

        var renderedInput: Triple<List<Match>, EventDisplay, SchedulePredictions>? = null
        var rows: List<ScheduleRow> = emptyList()
        observeEvent({ Triple(it.matches, it.display, it.predictions) }) { (section, display, predictions) ->
            val input = Triple(section.items, display, predictions)
            if (input != renderedInput) {
                rows = withContext(Dispatchers.Default) {
                    scheduleRows(
                        section.items, selectedTeam ?: display.teamNumber, teamOnly, display.matchSort,
                        predictions, display.predictionPercentage
                    )
                }
                renderedInput = input
                adapter.submitList(rows)
            }
            showListStatus(rows.isEmpty(), section)
        }
    }
}
