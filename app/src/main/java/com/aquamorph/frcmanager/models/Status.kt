package com.aquamorph.frcmanager.models

/**
 * Stores information about status of the Blue Alliance.
 *
 * @author Christian Colglazier
 * @version 3/30/2018
 */

abstract class Status {
    abstract var current_season: Int?
    abstract var down_events: Array<String>?
    abstract var is_datafeed_down: Boolean?
    abstract var max_season: Int?
}