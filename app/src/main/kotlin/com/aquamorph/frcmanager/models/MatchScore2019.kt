package com.aquamorph.frcmanager.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Stores match data for 2019 game.
 *
 * @author Christian Colglazier
 * @version 1/31/2020
 */
class MatchScore2019(
    @Expose
    @SerializedName("score_breakdown")
    var scoreBreakdown: ScoreBreakDown
) {

    data class ScoreBreakDown(
        @Expose
        @SerializedName("comp_level")
        var compLevel: String,
        @Expose
        @SerializedName("match_number")
        var match_number: Int,
        @Expose
        @SerializedName("blue")
        var blue: BreakDown2019,
        @Expose
        @SerializedName("red")
        var red: BreakDown2019
    )

    data class BreakDown2019(
        @Expose
        @SerializedName("adjustPoints")
        var adjustPoints: Int,
        @Expose
        @SerializedName("autoPoints")
        var autoPoints: Int,
        @Expose
        @SerializedName("bay1")
        var bay1: String,
        @Expose
        @SerializedName("bay2")
        var bay2: String,
        @Expose
        @SerializedName("bay3")
        var bay3: String,
        @Expose
        @SerializedName("bay4")
        var bay4: String,
        @Expose
        @SerializedName("bay5")
        var bay5: String,
        @Expose
        @SerializedName("bay6")
        var bay6: String,
        @Expose
        @SerializedName("bay7")
        var bay7: String,
        @Expose
        @SerializedName("bay8")
        var bay8: String,
        @Expose
        @SerializedName("cargoPoints")
        var cargoPoints: Int,
        @Expose
        @SerializedName("completeRocketRankingPoint")
        var completeRocketRankingPoint: Boolean,
        @Expose
        @SerializedName("completedRocketFar")
        var completedRocketFar: Boolean,
        @Expose
        @SerializedName("completedRocketNear")
        var completedRocketNear: Boolean,
        @Expose
        @SerializedName("endgameRobot1")
        var endgameRobot1: String,
        @Expose
        @SerializedName("endgameRobot2")
        var endgameRobot2: String,
        @Expose
        @SerializedName("endgameRobot3")
        var endgameRobot3: String,
        @Expose
        @SerializedName("foulCount")
        var foulCount: Int,
        @Expose
        @SerializedName("foulPoints")
        var foulPoints: Int,
        @Expose
        @SerializedName("habClimbPoints")
        var habClimbPoints: Int,
        @Expose
        @SerializedName("habDockingRankingPoint")
        var habDockingRankingPoint: Boolean,
        @Expose
        @SerializedName("habLineRobot1")
        var habLineRobot1: String,
        @Expose
        @SerializedName("habLineRobot2")
        var habLineRobot2: String,
        @Expose
        @SerializedName("habLineRobot3")
        var habLineRobot3: String,
        @Expose
        @SerializedName("hatchPanelPoints")
        var hatchPanelPoints: Int,
        @Expose
        @SerializedName("lowLeftRocketFar")
        var lowLeftRocketFar: String,
        @Expose
        @SerializedName("lowLeftRocketNear")
        var lowLeftRocketNear: String,
        @Expose
        @SerializedName("lowRightRocketFar")
        var lowRightRocketFar: String,
        @Expose
        @SerializedName("lowRightRocketNear")
        var lowRightRocketNear: String,
        @Expose
        @SerializedName("midLeftRocketFar")
        var midLeftRocketFar: String,
        @Expose
        @SerializedName("midLeftRocketNear")
        var midLeftRocketNear: String,
        @Expose
        @SerializedName("midRightRocketFar")
        var midRightRocketFar: String,
        @Expose
        @SerializedName("midRightRocketNear")
        var midRightRocketNear: String,
        @Expose
        @SerializedName("preMatchBay1")
        var preMatchBay1: String,
        @Expose
        @SerializedName("preMatchBay2")
        var preMatchBay2: String,
        @Expose
        @SerializedName("preMatchBay3")
        var preMatchBay3: String,
        @Expose
        @SerializedName("preMatchBay6")
        var preMatchBay6: String,
        @Expose
        @SerializedName("preMatchBay7")
        var preMatchBay7: String,
        @Expose
        @SerializedName("preMatchBay8")
        var preMatchBay8: String,
        @Expose
        @SerializedName("preMatchLevelRobot1")
        var preMatchLevelRobot1: String,
        @Expose
        @SerializedName("preMatchLevelRobot2")
        var preMatchLevelRobot2: String,
        @Expose
        @SerializedName("preMatchLevelRobot3")
        var preMatchLevelRobot3: String,
        @Expose
        @SerializedName("rp")
        var rp: Int,
        @Expose
        @SerializedName("sandStormBonusPoints")
        var sandStormBonusPoints: Int,
        @Expose
        @SerializedName("techFoulCount")
        var techFoulCount: Int,
        @Expose
        @SerializedName("teleopPoints")
        var teleopPoints: Int,
        @Expose
        @SerializedName("topLeftRocketFar")
        var topLeftRocketFar: String,
        @Expose
        @SerializedName("topLeftRocketNear")
        var topLeftRocketNear: String,
        @Expose
        @SerializedName("topRightRocketFar")
        var topRightRocketFar: String,
        @Expose
        @SerializedName("topRightRocketNear")
        var topRightRocketNear: String,
        @Expose
        @SerializedName("totalPoints")
        var totalPoints: Int
    )
}
