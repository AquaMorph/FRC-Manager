package com.aquamorph.frcmanager.network

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EventListTest {
    @Test
    fun `successful empty response clears old items without marking an error`() {
        val section = EventList(listOf("previous")).begin()
        val result = section.finish(emptyList())

        assertTrue(section.loading)
        assertTrue(result.items.isEmpty())
        assertEquals(EventLoad.READY, result.load)
    }

    @Test
    fun `failed refresh keeps existing items and stops loading`() {
        val result = EventList(listOf("previous")).begin().fail()

        assertEquals(listOf("previous"), result.items)
        assertEquals(EventLoad.FAILED, result.load)
        assertFalse(result.loading)
    }

    @Test
    fun `ending an incomplete refresh flags only unfinished sections`() {
        val loading = EventState().beginRefresh(EventSelection(eventKey = "event"))
        val result = loading.copy(matches = loading.matches.finish(emptyList())).endRefresh()

        assertEquals(EventLoad.READY, result.matches.load)
        assertEquals(EventLoad.FAILED, result.awards.load)
        assertEquals(EventLoad.READY, result.districtRankings.load)
    }
}
