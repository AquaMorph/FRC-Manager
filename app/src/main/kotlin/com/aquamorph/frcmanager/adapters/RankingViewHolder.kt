package com.aquamorph.frcmanager.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.activities.TeamSummary
import com.aquamorph.frcmanager.utils.Constants
import com.aquamorph.frcmanager.utils.NavigationMotion
import kotlin.math.roundToInt

internal data class RankingMetric(val label: String, val value: String)

class RankingViewHolder private constructor(view: View) : RecyclerView.ViewHolder(view) {
    private val context = view.context
    private val inflater = LayoutInflater.from(context)
    private val rank = view.findViewById<TextView>(R.id.rank)
    private val teamName = view.findViewById<TextView>(R.id.ranking_team_name)
    private val teamNumber = view.findViewById<TextView>(R.id.team_number)
    private val metrics = view.findViewById<LinearLayout>(R.id.ranking_metrics)
    private val columnCount = if (context.resources.configuration.fontScale >= 1.5f) 1 else 2
    private val columnGap = (16 * context.resources.displayMetrics.density).roundToInt()
    private val cells = mutableListOf<MetricCell>()
    private var number = ""

    init {
        view.setOnClickListener {
            NavigationMotion.openDetail(it, Intent(context, TeamSummary::class.java).putExtra("teamNumber", number))
        }
    }

    internal fun bind(position: Int, teamKey: String, nickname: String?, stats: List<RankingMetric>, highlighted: Boolean) {
        number = Constants.formatTeamNumber(teamKey)
        itemView.transitionName = "ranking:$teamKey"
        rank.text = context.getString(R.string.rankingPosition, position)
        rank.contentDescription = context.getString(R.string.rankingPositionDescription, position)
        rank.isActivated = highlighted
        val hasNickname = !nickname.isNullOrBlank()
        teamName.text = if (hasNickname) nickname else context.getString(R.string.rankingTeamDescription, number)
        teamNumber.text = number
        teamNumber.contentDescription = context.getString(R.string.rankingTeamDescription, number)
        teamNumber.isVisible = hasNickname

        while (cells.size < stats.size) repeat(columnCount) { addMetricCell() }
        for ((index, cell) in cells.withIndex()) {
            val stat = stats.getOrNull(index)
            // Reserve space for the last cell of an odd row without keeping its old text or height.
            cell.label.isVisible = stat != null
            cell.value.isVisible = stat != null
            if (index % columnCount == 0) (cell.view.parent as View).isVisible = stat != null
            if (stat != null) {
                cell.label.text = stat.label
                cell.value.text = stat.value
            }
        }
        metrics.isVisible = stats.isNotEmpty()
    }

    private fun addMetricCell() {
        val column = cells.size % columnCount
        val row = if (column == 0) {
            LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                metrics.addView(this, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }
        } else {
            metrics.getChildAt(metrics.childCount - 1) as LinearLayout
        }
        val cell = inflater.inflate(R.layout.ranking_metric, row, false)
        if (columnCount > 1) {
            (cell.layoutParams as LinearLayout.LayoutParams).apply {
                if (column == 0) marginEnd = columnGap / 2 else marginStart = columnGap / 2
            }
        }
        row.addView(cell)
        cells += MetricCell(cell, cell.findViewById(R.id.ranking_metric_label), cell.findViewById(R.id.ranking_metric_value))
    }

    private class MetricCell(val view: View, val label: TextView, val value: TextView)

    companion object {
        internal fun create(parent: ViewGroup) = RankingViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.ranking_row, parent, false)
        )
    }
}
