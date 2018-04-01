package com.aquamorph.frcmanager.models

import java.text.SimpleDateFormat
import java.util.*

/**
 * Stores information about an event.
 *
 * @author Christian Colglazier
 * @version 3/30/2018
 */
class Event : Comparable<Event> {
    var key: String = ""
    var name: String = ""
    var event_code: String = ""
    var event_type: Int = 0
    var district: DistrictList = DistrictList()
    var city: String = ""
    var state_prov: String = ""
    var country: String = ""
    var start_date: String = ""
    var end_date: String = ""
    var year: Int = 0
    var short_name: String  = ""
    var event_type_string: String = ""
    var week: Int = 0
    var address: String = ""
    var postal_code: String = ""
    var gmaps_place_id: String = ""
    var gmaps_url: String = ""
    var lat: Double = 0.0
    var lng: Double =0.0
    var location_name: String = ""
    var timezone: String = ""
    var website: String = ""
    var first_event_id: String = ""
    var first_event_code: String = ""
//    var webcasts: Array<WebCast>?
    var division_keys: Array<String> = emptyArray()
    var playoff_type: Int = 0
    var playoff_type_string: String = ""

    override operator fun compareTo(other: Event): Int {
        try {
            val first = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(this.start_date)
            val second = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(other.start_date)
            return first.compareTo(second)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }
}
