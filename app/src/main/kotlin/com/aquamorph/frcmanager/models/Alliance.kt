package com.aquamorph.frcmanager.models

/**
 * Store information about playoff alliances.
 *
 * @author Christian Colglazier
 * @version 1/30/2020
 */

data class Alliance(
    var name: String,
    var backup: Backup,
    var declines: ArrayList<String>,
    var picks: ArrayList<String>,
    var status: Status
) {
    data class Backup(
        var out: String,
        var `in`: String
    )

    data class Status(
        var current_level_record: WLTRecord,
        var level: String,
        var playoff_average: Double,
        var record: WLTRecord,
        var status: String
    )
}
