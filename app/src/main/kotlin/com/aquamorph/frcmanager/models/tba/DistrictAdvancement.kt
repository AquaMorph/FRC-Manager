package com.aquamorph.frcmanager.models.tba

import com.google.gson.annotations.SerializedName

data class DistrictAllocation(
    val key: String,
    @SerializedName("official_advancement_counts") val counts: Counts?
) {
    data class Counts(val cmp: Int?)
}

data class DistrictAdvancement(val cutoffs: Cutoffs?) {
    data class Cutoffs(@SerializedName("cmp_qualification") val qualificationByTeam: Map<String, String>?)
}

internal fun districtPointsSpots(allocation: Int?, advancement: DistrictAdvancement?): Int? {
    val slots = allocation ?: return null
    val qualifications = advancement?.cutoffs?.qualificationByTeam ?: return null
    return (slots - qualifications.values.count { it in slotConsumingAwards }).coerceAtLeast(0)
}

// These TBA qualification methods use a district's FIRST Championship allocation.
private val slotConsumingAwards = setOf(
    "dcmp_impact", "dcmp_engineering_inspiration", "dcmp_rookie_all_star", "dcmp_winner",
    "regional_impact", "regional_engineering_inspiration", "regional_winner", "regional_wildcard"
)
