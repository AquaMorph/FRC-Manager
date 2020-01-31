package com.aquamorph.frcmanager.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Stores information about a team at an event.
 *
 * @author Christian Colglazier
 * @version 1/31/2020
 */
data class Team(
    @Expose
    @SerializedName("key")
    var key: String,
    @Expose
    @SerializedName("team_number")
    var teamNumber: Int,
    @Expose
    @SerializedName("nickname")
    var nickname: String,
    @Expose
    @SerializedName("name")
    var name: String,
    @Expose
    @SerializedName("city")
    var city: String,
    @Expose
    @SerializedName("state_prov")
    var stateProv: String,
    @Expose
    @SerializedName("country")
    var country: String,
    @Expose
    @SerializedName("address")
    var address: String,
    @Expose
    @SerializedName("postal_code")
    var postalCode: String,
    @Expose
    @SerializedName("gmaps_place_id")
    var gmapsPlaceId: String,
    @Expose
    @SerializedName("gmaps_url")
    var gmapsUrl: String,
    @Expose
    @SerializedName("lat")
    var lat: String,
    @Expose
    @SerializedName("lng")
    var lng: String,
    @Expose
    @SerializedName("location_name")
    var locationName: String,
    @Expose
    @SerializedName("website")
    var website: String,
    @Expose
    @SerializedName("rookie_year")
    var rookieYear: Int,
    @Expose
    @SerializedName("motto")
    var motto: String
//    @Expose
//    @SerializedName("home_championship")
//    var homeChampionship: ArrayList<String>
) : Comparable<Team> {
    override operator fun compareTo(other: Team): Int {
        return this.teamNumber - other.teamNumber
    }
}
