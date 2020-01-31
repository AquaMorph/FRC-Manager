package com.aquamorph.frcmanager.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Stores information about a district.
 *
 * @author Christian Colglazier
 * @version 1/31/2020
 */

data class DistrictList(
    @Expose
    @SerializedName("abbreviation")
    var abbreviation: String,
    @Expose
    @SerializedName("display_name")
    var displayName: String,
    @Expose
    @SerializedName("key")
    var key: String,
    @Expose
    @SerializedName("year")
    var year: Int
)
