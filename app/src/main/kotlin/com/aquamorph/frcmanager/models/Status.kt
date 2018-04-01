package com.aquamorph.frcmanager.models

/**
 * Stores information about status of the Blue Alliance.
 *
 * @author Christian Colglazier
 * @version 3/30/2018
 */

class Status {
    var current_season: Int? = null
    var down_events: Array<String>? = emptyArray()
    var is_datafeed_down: Boolean = false
    var max_season: Int? = 0
}