package com.aquamorph.frcmanager.models.tba

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DistrictAdvancementTest {
    @Test
    fun subtractsOnlyInvitationsThatUseAnAllocatedChampionshipSpot() {
        val gson = Gson()
        val district = gson.fromJson(
            """{"key":"2026ne","official_advancement_counts":{"dcmp":96,"cmp":30}}""",
            DistrictAllocation::class.java
        )
        val advancement = gson.fromJson(
            """{"teams":null,"cutoffs":{"cmp_qualification":{
                "frc1":"dcmp_impact", "frc2":"dcmp_impact", "frc3":"dcmp_winner",
                "frc4":"dcmp_engineering_inspiration", "frc5":"dcmp_rookie_all_star",
                "frc6":"district_points", "frc7":"hall_of_fame", "frc8":"waitlist",
                "frc9":"late_regional_impact", "frc10":"regional_impact"
            }}}""",
            DistrictAdvancement::class.java
        )

        assertEquals(24, districtPointsSpots(district.counts?.cmp, advancement))
        assertEquals(0, districtPointsSpots(2, advancement))
    }

    @Test
    fun leavesTheCutoffUnknownWhenDataIsMissing() {
        assertNull(districtPointsSpots(30, DistrictAdvancement(null)))
        assertNull(districtPointsSpots(30, DistrictAdvancement(DistrictAdvancement.Cutoffs(null))))
        assertNull(districtPointsSpots(null, DistrictAdvancement(DistrictAdvancement.Cutoffs(emptyMap()))))
        assertEquals(30, districtPointsSpots(30, DistrictAdvancement(DistrictAdvancement.Cutoffs(emptyMap()))))
    }
}
