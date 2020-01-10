package com.aquamorph.frcmanager.models

/**
 * Stores match information at an event.
 *
 * @author Christian Colglazier
 * @version 3/30/2018
 */
class Match : Comparable<Match> {
    var key: String = ""
    var comp_level: String = ""
    var set_number: Int = 0
    var match_number: Int = 0
    var alliances: Alliances = Alliances()
    var winning_alliance: String = ""
    var event_key: String = ""
    var time: Long = 0L
    var actual_time: Long = 0L
    var predicted_time: Long = 0L
    var post_result_time: Long = 0L

    inner class Alliances {
        var blue: MatchAlliance = MatchAlliance()
        var red: MatchAlliance = MatchAlliance()
    }

    inner class MatchAlliance {
        var score: Int = 0
        var team_keys: Array<String> = emptyArray()
        var surrogate_team_keys: Array<String> = emptyArray()
        var dq_team_keys: Array<String> = emptyArray()
    }

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
