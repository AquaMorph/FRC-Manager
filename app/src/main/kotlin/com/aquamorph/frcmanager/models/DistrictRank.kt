package com.aquamorph.frcmanager.models

/**
 * Stores rank information for a district.
 *
 * @author Christian Colglazier
 * @version 10/28/2018
 */
class DistrictRank {
    var team_key = ""
    var rank = 0
    var rookie_bonus = 0
    var point_total = 0
    var event_points = arrayOfNulls<EventPoints>(0)

    inner class EventPoints {
        var event_key = ""
        var district_cmp = false
        var award_points = 0
        var qual_points = 0
        var elim_points = 0
        var total = 0
    }
}
