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
        @SerializedName("foulCount")
        var foulCount: Int,
        @Expose
        @SerializedName("foulPoints")
        var foulPoints: Int,
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
        @SerializedName("totalPoints")
        var totalPoints: Int
    )
}
