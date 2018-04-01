package com.aquamorph.frcmanager.models

/**
 * Store information about playoff alliances.
 *
 * @author Christian Colglazier
 * @version 3/30/2018
 */

class Alliance {
    var name: String = ""
    var backup: Backup? = null
    var declines: Array<String> = emptyArray()
    var picks: Array<String> = emptyArray()
    var status: Status? = null

    inner class Backup {
        var out: String = ""
        var `in`: String = ""
    }

    inner class Status {
        var current_level_record: WLTRecord = WLTRecord()
        var level: String = ""
        var playoff_average: Double = 0.0
        var record: WLTRecord = WLTRecord()
        var status: String = ""
    }

    inner class WLTRecord {
        var losses: Int = 0
        var wins: Int = 0
        var ties: Int = 0
    }
}
