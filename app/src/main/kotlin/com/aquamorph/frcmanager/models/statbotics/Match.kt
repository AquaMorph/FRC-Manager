package com.aquamorph.frcmanager.models.statbotics

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Match(
    @Expose
    @SerializedName("key")
    var key: String,
    @Expose
    @SerializedName("pred")
    var pred: Pred
) {
    fun winnerProb(): Double {
        return if (pred.red_win_prob > 0.5) {
            pred.red_win_prob
        } else {
            1 - pred.red_win_prob
        }
    }

    data class Pred(
        @Expose
        @SerializedName("winner")
        var winner: String,
        @Expose
        @SerializedName("red_win_prob")
        var red_win_prob: Double
    )
}
