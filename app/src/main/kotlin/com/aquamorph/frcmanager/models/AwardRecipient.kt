package com.aquamorph.frcmanager.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Contains information about awards.
 *
 * @author Christian Colglazier
 * @version 1/31/2020
 */
data class AwardRecipient(
    @Expose
    @SerializedName("team_key")
    var teamKey: String,
    @Expose
    @SerializedName("awardee")
    var awardee: String
)
