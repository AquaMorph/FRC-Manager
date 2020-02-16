package com.aquamorph.frcmanager.models

data class TBAPrediction(var matchPredictions: MatchPredictions) {
    data class MatchPredictions(
        var playoff: ArrayList<Match>,
        var qual: ArrayList<Match>
    )

    data class Match(
        var blue: Alliance,
        var prob: Double,
        var red: Alliance,
        var winningAlliance: String
    )

//    data class Alliance(
//
//    )
}
