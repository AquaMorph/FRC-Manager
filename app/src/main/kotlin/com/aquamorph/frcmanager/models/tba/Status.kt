package com.aquamorph.frcmanager.models.tba

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Stores information about status of the Blue Alliance.
 *
 * @author Christian Colglazier
 * @version 1/31/2020
 */

data class Status(
    @Expose
    @SerializedName("current_season")
    var currentSeason: Int,
    @Expose
    @SerializedName("down_events")
    var downEvents: ArrayList<String>,
    @Expose
    @SerializedName("is_datafeed_down")
    var isDatafeedDown: Boolean,
    @Expose
    @SerializedName("max_season")
    var maxSeason: Int
)
