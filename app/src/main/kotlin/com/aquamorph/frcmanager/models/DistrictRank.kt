package com.aquamorph.frcmanager.models

/**
 * Stores rank information for a district.
 *
 * @author Christian Colglazier
 * @version 1/30/2020
 */
data class DistrictRank(
    var team_key: String,
    var rank: Int,
    var rookie_bonus: Int,
    var point_total: Int,
    var event_points: ArrayList<EventPoints>
) {
    inner class EventPoints(
        var event_key: String,
        var district_cmp: Boolean,
        var award_points: Int,
        var qual_points: Int,
        var elim_points: Int,
        var total: Int
    )
}
