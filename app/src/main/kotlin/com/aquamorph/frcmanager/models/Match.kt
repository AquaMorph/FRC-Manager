package com.aquamorph.frcmanager.models

/**
 * Stores match information at an event.
 *
 * @author Christian Colglazier
 * @version 1/30/2020
 */
class Match(
    var key: String,
    var comp_level: String,
    var set_number: Int,
    var match_number: Int,
    var alliances: Alliances,
    var winning_alliance: String,
    var event_key: String,
    var time: Long,
    var actual_time: Long,
    var predicted_time: Long,
    var post_result_time: Long
) : Comparable<Match> {

    data class Alliances(
        var blue: MatchAlliance,
        var red: MatchAlliance
    )

    data class MatchAlliance(
        var score: Int,
        var team_keys: ArrayList<String>,
        var surrogate_team_keys: ArrayList<String>,
        var dq_team_keys: ArrayList<String>
    )

    override operator fun compareTo(other: Match): Int {
        val compareMatchNumber = other.match_number
        val compareLevel = other.comp_level
        return if (getCompLevelValue(compareLevel) == getCompLevelValue(this.comp_level)) {
            this.match_number - compareMatchNumber
        } else {
            getCompLevelValue(this.comp_level) - getCompLevelValue(compareLevel)
        }
    }

    private fun getCompLevelValue(comp_level: String?): Int {
        return when (comp_level) {
            "qm" -> 1
            "ef" -> 2
            "qf" -> 3
            "sf" -> 4
            "f" -> 5
            else -> 6
        }
    }
}
