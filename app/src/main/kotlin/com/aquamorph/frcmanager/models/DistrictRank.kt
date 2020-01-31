package com.aquamorph.frcmanager.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Stores rank information for a district.
 *
 * @author Christian Colglazier
 * @version 1/31/2020
 */
data class DistrictRank(
    @Expose
    @SerializedName("team_key")
    var teamKey: String,
    @Expose
    @SerializedName("rank")
    var rank: Int,
    @Expose
    @SerializedName("rookie_bonus")
    var rookieBonus: Int,
    @Expose
    @SerializedName("point_total")
    var pointTotal: Int,
    @Expose
    @SerializedName("event_points")
    var eventPoints: ArrayList<EventPoints>
) {
    inner class EventPoints(
        @Expose
        @SerializedName("event_key")
        var eventKey: String,
        @Expose
        @SerializedName("district_cmp")
        var districtCmp: Boolean,
        @Expose
        @SerializedName("award_points")
        var awardPoints: Int,
        @Expose
        @SerializedName("qual_points")
        var qual_points: Int,
        @Expose
        @SerializedName("elim_points")
        var elimPoints: Int,
        @Expose
        @SerializedName("total")
        var total: Int
    )
}
