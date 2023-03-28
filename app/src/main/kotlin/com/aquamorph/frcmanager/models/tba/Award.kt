package com.aquamorph.frcmanager.models.tba

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Contains information about awards given at an event
 *
 * @author Christian Colglazier
 * @version 1/31/2020
 */
data class Award(
    @Expose
    @SerializedName("name")
    var name: String,
    @Expose
    @SerializedName("award_type")
    var awardType: Int,
    @Expose
    @SerializedName("event_key")
    var eventKey: String,
    @Expose
    @SerializedName("recipient_list")
    var recipientList: ArrayList<AwardRecipient>,
    @Expose
    @SerializedName("year")
    var year: String
)
