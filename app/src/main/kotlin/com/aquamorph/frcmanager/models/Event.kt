package com.aquamorph.frcmanager.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Stores information about an event.
 *
 * @author Christian Colglazier
 * @version 1/31/2020
 */
data class Event(
    @Expose
    @SerializedName("key")
    var key: String,
    @Expose
    @SerializedName("name")
    var name: String,
    @Expose
    @SerializedName("event_code")
    var eventCode: String,
    @Expose
    @SerializedName("event_type")
    var eventType: Int,
    @Expose
    @SerializedName("district")
    var district: DistrictList,
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
    @SerializedName("start_date")
    var startDate: String,
    @Expose
    @SerializedName("end_date")
    var endDate: String,
    @Expose
    @SerializedName("year")
    var year: Int,
    @Expose
    @SerializedName("short_name")
    var shortName: String,
    @Expose
    @SerializedName("event_type_string")
    var eventTypeString: String,
    @Expose
    @SerializedName("week")
    var week: Int,
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
    var lat: Double,
    @Expose
    @SerializedName("lng")
    var lng: Double,
    @Expose
    @SerializedName("location_name")
    var locationName: String,
    @Expose
    @SerializedName("timezone")
    var timezone: String,
    @Expose
    @SerializedName("website")
    var website: String,
    @Expose
    @SerializedName("first_event_id")
    var firstEventId: String,
    @Expose
    @SerializedName("first_event_code")
    var firstEventCode: String,
//    @Expose
//    @SerializedName("webcasts")
//    var webcasts: Array<WebCast>?
    @Expose
    @SerializedName("division_keys")
    var divisionKeys: ArrayList<String>,
    @Expose
    @SerializedName("playoff_type")
    var playoffType: Int,
    @Expose
    @SerializedName("playoff_type_string")
    var playoffTypeString: String
) : Comparable<Event> {
    override operator fun compareTo(other: Event): Int {
        try {
            return SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(this.startDate)!!
                    .compareTo(SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(other.startDate))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }
}
