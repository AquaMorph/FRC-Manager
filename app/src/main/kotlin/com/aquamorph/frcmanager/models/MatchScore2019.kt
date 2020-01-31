package com.aquamorph.frcmanager.models

class MatchScore2019(
    var score_breakdown: ScoreBreakDown
) {

    data class ScoreBreakDown(
        var comp_level: String,
        var match_number: Int,
        var blue: BreakDown2019,
        var red: BreakDown2019
    )

    data class BreakDown2019(
        var adjustPoints: Int,
        var autoPoints: Int,
        var bay1: String,
        var bay2: String,
        var bay3: String,
        var bay4: String,
        var bay5: String,
        var bay6: String,
        var bay7: String,
        var bay8: String,
        var cargoPoints: Int,
        var completeRocketRankingPoint: Boolean,
        var completedRocketFar: Boolean,
        var completedRocketNear: Boolean,
        var endgameRobot1: String,
        var endgameRobot2: String,
        var endgameRobot3: String,
        var foulCount: Int,
        var foulPoints: Int,
        var habClimbPoints: Int,
        var habDockingRankingPoint: Boolean,
        var habLineRobot1: String,
        var habLineRobot2: String,
        var habLineRobot3: String,
        var hatchPanelPoints: Int,
        var lowLeftRocketFar: String,
        var lowLeftRocketNear: String,
        var lowRightRocketFar: String,
        var lowRightRocketNear: String,
        var midLeftRocketFar: String,
        var midLeftRocketNear: String,
        var midRightRocketFar: String,
        var midRightRocketNear: String,
        var preMatchBay1: String,
        var preMatchBay2: String,
        var preMatchBay3: String,
        var preMatchBay6: String,
        var preMatchBay7: String,
        var preMatchBay8: String,
        var preMatchLevelRobot1: String,
        var preMatchLevelRobot2: String,
        var preMatchLevelRobot3: String,
        var rp: Int,
        var sandStormBonusPoints: Int,
        var techFoulCount: Int,
        var teleopPoints: Int,
        var topLeftRocketFar: String,
        var topLeftRocketNear: String,
        var topRightRocketFar: String,
        var topRightRocketNear: String,
        var totalPoints: Int
    )
}
