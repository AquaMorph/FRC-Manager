package com.aquamorph.frcmanager.models

/**
 * Stores information about a team at an event.
 *
 * @author Christian Colglazier
 * @version 3/30/2018
 */
abstract class Team : Comparable<Team> {
    abstract var key: String
    abstract var team_number: Int
    abstract var nickname: String
    abstract var name: String
    abstract var city: String
    abstract var state_prov: String
    abstract var country: String
    abstract var address: String
    abstract var postal_code: String
    abstract var gmaps_place_id: String
    abstract var gmaps_url: String
    abstract var lat: String
    abstract var lng: String
    abstract var location_name: String
    abstract var website: String
    abstract var rookie_year: Int
    abstract var motto: String
    abstract var home_championship: Array<String>

    override operator fun compareTo(other: Team): Int {
        return this.team_number - other.team_number
    }
}