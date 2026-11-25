package com.aquamorph.frcmanager.adapters

import com.aquamorph.frcmanager.network.EventSelection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EventTabTest {
    @Test
    fun `event tabs are available before any data arrives regardless of prediction provider`() {
        val expected = listOf(
            EventTab.TEAM_SCHEDULE,
            EventTab.EVENT_SCHEDULE,
            EventTab.RANKINGS,
            EventTab.TEAMS,
            EventTab.ALLIANCES,
            EventTab.AWARDS
        )
        for (mode in listOf("none", "tba", "statbotics")) {
            assertEquals(expected, EventTab.forSelection(EventSelection(eventKey = "event", predictionMode = mode)))
        }
    }

    @Test
    fun `district tab requires a district event`() {
        assertTrue(EventTab.forSelection(EventSelection(districtKey = "district")).isEmpty())
        assertEquals(EventTab.entries, EventTab.forSelection(EventSelection("event", "district")))
    }
}
