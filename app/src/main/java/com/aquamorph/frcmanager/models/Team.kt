package com.aquamorph.frcmanager.models

/**
 * Stores information about a team at an event.
 *
 * @author Christian Colglazier
 * @version 12/27/2017
 */
class Team : Comparable<Team> {

    var key: String? = null
    var team_number: Int = 0
    var nickname: String? = null
    var name: String? = null
    var city: String? = null
    var state_prov: String? = null
    var country: String? = null
    var address: String? = null
    var postal_code: String? = null
    var gmaps_place_id: String? = null
    var gmaps_url: String? = null
    var lat: String? = null
    var lng: String? = null
    var location_name: String? = null
    var website: String? = null
    var rookie_year: Int? = null
    var motto: String? = null
    var home_championship: Array<String>? = null

    override operator fun compareTo(other: Team): Int {
        return this.team_number - other.team_number
    }
}