package com.aquamorph.frcmanager.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * A Win-Loss-Tie record for a team, or an alliance.
 *
 * @author Christian Colglazier
 * @version 1/31/2020
 */
data class WLTRecord(
    @Expose
    @SerializedName("losses")
    var losses: Int,
    @Expose
    @SerializedName("wins")
    var wins: Int,
    @Expose
    @SerializedName("ties")
    var ties: Int
)
