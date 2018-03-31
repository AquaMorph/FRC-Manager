package com.aquamorph.frcmanager.models

import java.text.SimpleDateFormat
import java.util.*

/**
 * Stores information about an event.
 *
 * @author Christian Colglazier
 * @version 3/30/2018
 */
abstract class Event : Comparable<Event> {
    abstract var key: String?
    abstract var name: String?
    abstract var event_code: String?
    abstract var event_type: Int?
    abstract var district: DistrictList?
    abstract var city: String?
    abstract var state_prov: String?
    abstract var country: String?
    abstract var start_date: String?
    abstract var end_date: String?
    abstract var year: Int?
    abstract var short_name: String?
    abstract var event_type_string: String?
    abstract var week: Int?
    abstract var address: String?
    abstract var postal_code: String?
    abstract var gmaps_place_id: String?
    abstract var gmaps_url: String?
    abstract var lat: Double
    abstract var lng: Double
    abstract var location_name: String?
    abstract var timezone: String?
    abstract var website: String?
    abstract var first_event_id: String?
    abstract var first_event_code: String?
//    abstract var webcasts: Array<WebCast>?
    abstract var division_keys: Array<String>?
    abstract var playoff_type: Int?
    abstract var playoff_type_string: String?

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
