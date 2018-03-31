package com.aquamorph.frcmanager.models

/**
 * Store information about playoff alliances.
 *
 * @author Christian Colglazier
 * @version 3/30/2018
 */

abstract class Alliance {
    abstract var name: String?
    abstract var backup: Backup?
    abstract var declines: Array<String>?
    abstract var picks: Array<String>?
    abstract var status: Status?

    abstract inner class Backup {
        abstract var out: String?
        abstract var `in`: String?
    }

    abstract inner class Status {
        abstract var current_level_record: WLTRecord?
        abstract var level: String?
        abstract var playoff_average: Double
        abstract var record: WLTRecord?
        abstract var status: String?
    }

    abstract inner class WLTRecord {
        abstract var losses: Int?
        abstract var wins: Int?
        abstract var ties: Int?
    }
}
