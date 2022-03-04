package com.aquamorph.frcmanager.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Stores match data for 2020 game.
 *
 * @author Christian Colglazier
 * @version 1/31/2020
 */
data class MatchScore2020(
    @Expose
    @SerializedName("score_breakdown")
    var scoreBreakdown: ScoreBreakDown?
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
        var blue: BreakDown2020,
        @Expose
        @SerializedName("red")
        var red: BreakDown2020
    )

    data class BreakDown2020(
        @Expose
        @SerializedName("initLineRobot1")
        var initLineRobot1: String,
        @Expose
        @SerializedName("endgameRobot1")
        var endgameRobot1: String,
        @Expose
        @SerializedName("initLineRobot2")
        var initLineRobot2: String,
        @Expose
        @SerializedName("endgameRobot2")
        var endgameRobot2: String,
        @Expose
        @SerializedName("initLineRobot3")
        var initLineRobot3: String,
        @Expose
        @SerializedName("endgameRobot3")
        var endgameRobot3: String,
        @Expose
        @SerializedName("autoCellsBottom")
        var autoCellsBottom: Int,
        @Expose
        @SerializedName("autoCellsOuter")
        var autoCellsOuter: Int,
        @Expose
        @SerializedName("autoCellsInner")
        var autoCellsInner: Int,
        @Expose
        @SerializedName("teleopCellsBottom")
        var teleopCellsBottom: Int,
        @Expose
        @SerializedName("teleopCellsOuter")
        var teleopCellsOuter: Int,
        @Expose
        @SerializedName("teleopCellsInner")
        var teleopCellsInner: Int,
        @Expose
        @SerializedName("stage1Activated")
        var stage1Activated: Boolean,
        @Expose
        @SerializedName("stage2Activated")
        var stage2Activated: Boolean,
        @Expose
        @SerializedName("stage3Activated")
        var stage3Activated: Boolean,
        @Expose
        @SerializedName("stage3TargetColor")
        var stage3TargetColor: String,
        @Expose
        @SerializedName("endgameRungIsLevel")
        var endgameRungIsLevel: String,
        @Expose
        @SerializedName("autoInitLinePoints")
        var autoInitLinePoints: Int,
        @Expose
        @SerializedName("autoCellPoints")
        var autoCellPoints: Int,
        @Expose
        @SerializedName("autoPoints")
        var autoPoints: Int,
        @Expose
        @SerializedName("teleopCellPoints")
        var teleopCellPoints: Int,
        @Expose
        @SerializedName("controlPanelPoints")
        var controlPanelPoints: Int,
        @Expose
        @SerializedName("endgamePoints")
        var endgamePoints: Int,
        @Expose
        @SerializedName("teleopPoints")
        var teleopPoints: Int,
        @Expose
        @SerializedName("shieldOperationalRankingPoint")
        var shieldOperationalRankingPoint: Boolean,
        @Expose
        @SerializedName("shieldEnergizedRankingPoint")
        var shieldEnergizedRankingPoint: Boolean,
        @Expose
        @SerializedName("foulCount")
        var foulCount: Int,
        @Expose
        @SerializedName("techFoulCount")
        var techFoulCount: Int,
        @Expose
        @SerializedName("adjustPoints")
        var adjustPoints: Int,
        @Expose
        @SerializedName("foulPoints")
        var foulPoints: Int,
        @Expose
        @SerializedName("rp")
        var rp: Int,
        @Expose
        @SerializedName("totalPoints")
        var totalPoints: Int
    )
}
