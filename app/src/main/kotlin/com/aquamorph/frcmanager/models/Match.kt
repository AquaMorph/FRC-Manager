package com.aquamorph.frcmanager.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Stores match information at an event.
 *
 * @author Christian Colglazier
 * @version 1/31/2020
 */
data class Match(
    @Expose
    @SerializedName("key")
    var key: String,
    @Expose
    @SerializedName("comp_level")
    var compLevel: String,
    @Expose
    @SerializedName("set_number")
    var setNumber: Int,
    @Expose
    @SerializedName("match_number")
    var matchNumber: Int,
    @Expose
    @SerializedName("alliances")
    var alliances: Alliances,
    @Expose
    @SerializedName("winning_alliance")
    var winningAlliance: String,
    @Expose
    @SerializedName("event_key")
    var eventKey: String,
    @Expose
    @SerializedName("time")
    var time: Long,
    @Expose
    @SerializedName("actual_time")
    var actualTime: Long,
    @Expose
    @SerializedName("predicted_time")
    var predictedTime: Long,
    @Expose
    @SerializedName("post_result_time")
    var postResultTime: Long,
    @Expose
    @SerializedName("score_breakdown")
    var scoreBreakDown: MatchScore.ScoreBreakDown
) : Comparable<Match> {

    data class Alliances(
        @Expose
        @SerializedName("blue")
        var blue: MatchAlliance,
        @Expose
        @SerializedName("red")
        var red: MatchAlliance
    )

    data class MatchAlliance(
        @Expose
        @SerializedName("score")
        var score: Int,
        @Expose
        @SerializedName("team_keys")
        var teamKeys: ArrayList<String>,
        @Expose
        @SerializedName("surrogate_team_keys")
        var surrogateTeamKeys: ArrayList<String>,
        @Expose
        @SerializedName("dq_team_keys")
        var dqTeamKeys: ArrayList<String>
    )

    override operator fun compareTo(other: Match): Int {
        val compareMatchNumber = other.matchNumber
        val compareLevel = other.compLevel
        return if (getCompLevelValue(compareLevel) == getCompLevelValue(this.compLevel)) {
            this.matchNumber - compareMatchNumber
        } else {
            getCompLevelValue(this.compLevel) - getCompLevelValue(compareLevel)
        }
    }

    fun getCompLevelValue(comp_level: String?): Int {
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
