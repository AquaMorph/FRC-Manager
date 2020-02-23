package com.aquamorph.frcmanager.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Store information about playoff alliances.
 *
 * @author Christian Colglazier
 * @version 1/31/2020
 */

data class Alliance(
    @Expose
    @SerializedName("name")
    var name: String,
    @Expose
    @SerializedName("backup")
    var backup: Backup,
    @Expose
    @SerializedName("declines")
    var declines: ArrayList<String>,
    @Expose
    @SerializedName("picks")
    var picks: ArrayList<String>,
    @Expose
    @SerializedName("status")
    var status: Status
) {
    data class Backup(
        @Expose
        @SerializedName("out")
        var teamOut: String,
        @Expose
        @SerializedName("in")
        var teamIn: String
    )

    data class Status(
        @Expose
        @SerializedName("current_level_record")
        var currentLevelRecord: WLTRecord,
        @Expose
        @SerializedName("level")
        var level: String,
        @Expose
        @SerializedName("playoff_average")
        var playoffAverage: Double,
        @Expose
        @SerializedName("record")
        var record: WLTRecord,
        @Expose
        @SerializedName("status")
        var status: String
    )
}
