package com.aquamorph.frcmanager.models

/**
 * Stores information about status of the Blue Alliance.
 *
 * @author Christian Colglazier
 * @version 1/30/2020
 */

data class Status(
    var current_season: Int,
    var down_events: ArrayList<String>,
    var is_datafeed_down: Boolean,
    var max_season: Int
)
