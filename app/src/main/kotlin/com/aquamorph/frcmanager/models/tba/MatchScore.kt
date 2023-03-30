package com.aquamorph.frcmanager.models.tba

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Stores match data for a generic game.
 *
 * @author Christian Colglazier
 * @version 2/1/2020
 */
data class MatchScore(
    @Expose
    @SerializedName("score_breakdown")
    var scoreBreakdown: ScoreBreakDown
) {

    class ScoreBreakDown(
        @Expose
        @SerializedName("comp_level")
        var compLevel: String,
        @Expose
        @SerializedName("match_number")
        var match_number: Int,
        @Expose
        @SerializedName("blue")
        var blue: MatchBreakdown,
        @Expose
        @SerializedName("red")
        var red: MatchBreakdown
    )
}
