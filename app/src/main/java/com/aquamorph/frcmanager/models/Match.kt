package com.aquamorph.frcmanager.models

/**
 * Stores match information at an event.
 *
 * @author Christian Colglazier
 * @version 3/30/2018
 */
abstract class Match : Comparable<Match> {
    abstract var key: String?
    abstract var comp_level: String?
    abstract var set_number: Int?
    abstract var match_number: Int?
    abstract var alliances: Alliances?
    abstract var winning_alliance: String?
    abstract var event_key: String?
    abstract var time: Long?
    abstract var actual_time: Long?
    abstract var predicted_time: Long?
    abstract var post_result_time: Long?

    abstract inner class Alliances {
        abstract var blue: MatchAlliance?
        abstract var red: MatchAlliance?
    }

    abstract inner class MatchAlliance {
        abstract var score: Int?
        abstract var team_keys: Array<String>?
        abstract var surrogate_team_keys: Array<String>?
        abstract var dq_team_keys: Array<String>?
    }

    override operator fun compareTo(other: Match): Int {
        val compareMatchNumber = other.match_number
        val compareLevel = other.comp_level
        return if (getCompLevelValue(compareLevel) == getCompLevelValue(this.comp_level)) {
            this.match_number!! - compareMatchNumber!!
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
