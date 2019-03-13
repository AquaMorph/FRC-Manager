package com.aquamorph.frcmanager.models

class MatchScore2019 {
    
    lateinit var score_breakdown: ScoreBreakDown

    inner class ScoreBreakDown {
        lateinit var blue: BreakDown2019
        lateinit var red: BreakDown2019
    }

    inner class BreakDown2019 {
        var adjustPoints: Int = 0
        var autoPoints: Int = 0
        var bay1: String = ""
        var bay2: String = ""
        var bay3: String = ""
        var bay4: String = ""
        var bay5: String = ""
        var bay6: String = ""
        var bay7: String = ""
        var bay8: String = ""
        var cargoPoints: Int = 0
        var completeRocketRankingPoint:	Boolean = false
        var completedRocketFar: Boolean = false
        var completedRocketNear: Boolean = false
        var endgameRobot1: String = ""
        var endgameRobot2: String = ""
        var endgameRobot3: String = ""
        var foulCount: Int = 0
        var foulPoints: Int = 0
        var habClimbPoints: Int = 0
        var habDockingRankingPoint:	Boolean = false
        var habLineRobot1: String = ""
        var habLineRobot2: String = ""
        var habLineRobot3: String = ""
        var hatchPanelPoints	: Int = 0
        var lowLeftRocketFar: String = ""
        var lowLeftRocketNear: String = ""
        var lowRightRocketFar: String = ""
        var lowRightRocketNear: String = ""
        var midLeftRocketFar: String = ""
        var midLeftRocketNear: String = ""
        var midRightRocketFar: String = ""
        var midRightRocketNear: String = ""
        var preMatchBay1: String = ""
        var preMatchBay2: String = ""
        var preMatchBay3: String = ""
        var preMatchBay6: String = ""
        var preMatchBay7: String = ""
        var preMatchBay8: String = ""
        var preMatchLevelRobot1: String = ""
        var preMatchLevelRobot2: String = ""
        var preMatchLevelRobot3: String = ""
        var rp	: Int = 0
        var sandStormBonusPoints	: Int = 0
        var techFoulCount	: Int = 0
        var teleopPoints	: Int = 0
        var topLeftRocketFar: String = ""
        var topLeftRocketNear: String = ""
        var topRightRocketFar: String = ""
        var topRightRocketNear: String = ""
        var totalPoints	: Int = 0
    }
}