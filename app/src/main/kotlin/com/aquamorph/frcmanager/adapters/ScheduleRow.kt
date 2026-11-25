package com.aquamorph.frcmanager.adapters

import com.aquamorph.frcmanager.models.tba.Match
import com.aquamorph.frcmanager.network.SchedulePredictions
import com.aquamorph.frcmanager.utils.Constants
import com.aquamorph.frcmanager.utils.MatchSort

internal data class ScheduleRow(
    val key: String,
    val compLevel: String,
    val setNumber: Int,
    val matchNumber: Int,
    val red: ScheduleAlliance,
    val blue: ScheduleAlliance,
    val time: Long?,
    val prediction: SchedulePrediction?,
    val winningAlliance: String? = null
)

internal data class ScheduleAlliance(
    val teams: List<String>,
    val highlightedTeam: String?,
    val score: Int?,
    val bonusRp: Int,
    val emphasized: Boolean
)

internal data class SchedulePrediction(val probability: Double, val winner: String, val percentage: Boolean)

internal fun scheduleRows(
    matches: List<Match>, team: String, teamOnly: Boolean, sort: String,
    predictions: SchedulePredictions, predictionPercentage: Boolean
): List<ScheduleRow> {
    val key = "frc$team"
    val sorted = matches.filterTo(ArrayList()) {
        !teamOnly || key in it.alliances.red.teamKeys || key in it.alliances.blue.teamKeys
    }
    MatchSort.sortMatches(sorted, sort)
    val tba = predictions.tba.associateBy { it.matchKey }
    val statbotics = predictions.statbotics.associateBy { it.key }
    return sorted.map { match ->
        val season = match.eventKey.take(4).toIntOrNull()
        val winRp = if (season != null && season >= 2025) 3 else 2
        fun alliance(data: Match.MatchAlliance, otherScore: Int, color: String, rp: Int?): ScheduleAlliance {
            val teams = data.teamKeys.take(3).map(Constants::formatTeamNumber)
            val resultRp = when (match.winningAlliance) {
                color -> winRp
                "red", "blue" -> 0
                else -> 1
            }
            val bonusRp = if (match.hasResult && match.compLevel == "qm" && rp != null) {
                (rp - resultRp).coerceAtLeast(0)
            } else 0
            return ScheduleAlliance(
                teams, team.takeIf { it in teams }, data.score.takeIf { match.hasResult },
                bonusRp,
                match.hasResult && data.score >= otherScore
            )
        }
        val (blue, red) = match.alliances
        val prediction = if (match.hasResult) null else if (tba.isNotEmpty()) {
            tba[match.key]?.let { SchedulePrediction(it.prob, it.winningAlliance, predictionPercentage) }
        } else {
            statbotics[match.key]?.let { SchedulePrediction(it.winnerProb(), it.pred.winner, predictionPercentage) }
        }
        ScheduleRow(
            match.key, match.compLevel, match.setNumber, match.matchNumber,
            alliance(red, blue.score, "red", match.scoreBreakDown?.red?.rp),
            alliance(blue, red.score, "blue", match.scoreBreakDown?.blue?.rp),
            match.time.takeUnless { match.hasResult }, prediction,
            match.winningAlliance.takeIf { match.hasResult && (it == "red" || it == "blue") }
        )
    }
}
