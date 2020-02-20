package com.aquamorph.frcmanager.models

import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TBAPrediction(
    @Expose
    @SerializedName("match_predictions")
    var matchPredictions: MatchPredictions
) {
    data class MatchPredictions(
        var playoff: JsonObject,
        var qual: JsonObject
    )

    data class PredMatch(
        var matchKey: String,
        var prob: Double,
        var winningAlliance: String
    )
}
