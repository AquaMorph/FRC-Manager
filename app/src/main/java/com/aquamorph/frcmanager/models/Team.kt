package com.aquamorph.frcmanager.models

/**
 * Stores information about a team at an event.
 *
 * @author Christian Colglazier
 * @version 3/30/2018
 */
class Team : Comparable<Team> {
    var key: String = ""
    var team_number: Int = 0
    var nickname: String = ""
    var name: String = ""
    var city: String = ""
    var state_prov: String = ""
    var country: String = ""
    var address: String = ""
    var postal_code: String = ""
    var gmaps_place_id: String = ""
    var gmaps_url: String = ""
    var lat: String = ""
    var lng: String = ""
    var location_name: String = ""
    var website: String = ""
    var rookie_year: Int = 0
    var motto: String = ""
    var home_championship: Array<String> = emptyArray()

    override operator fun compareTo(other: Team): Int {
        return this.team_number - other.team_number
    }
}