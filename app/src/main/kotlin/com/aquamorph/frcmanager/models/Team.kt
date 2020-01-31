package com.aquamorph.frcmanager.models

/**
 * Stores information about a team at an event.
 *
 * @author Christian Colglazier
 * @version 1/30/2020
 */
data class Team(
    var key: String,
    var team_number: Int,
    var nickname: String,
    var name: String,
    var city: String,
    var state_prov: String
//    var country: String,
//    var address: String,
//    var postal_code: String,
//    var gmaps_place_id: String,
//    var gmaps_url: String,
//    var lat: String,
//    var lng: String,
//    var location_name: String,
//    var website: String,
//    var rookie_year: Int,
//    var motto: String,
//    var home_championship: ArrayList<String>
) : Comparable<Team> {

    override operator fun compareTo(other: Team): Int {
        return this.team_number - other.team_number
    }
}
