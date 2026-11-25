package com.aquamorph.frcmanager.utils

import com.aquamorph.frcmanager.models.tba.Match
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MatchSortTest {
    @Test
    fun `upcoming first skips completed zero score matches`() {
        val tie = match(1, 0, 0)
        val shutout = match(2, 20, 0)
        val unplayed = match(3, -1, -1)
        val matches = arrayListOf(unplayed, shutout, tie)

        MatchSort.sortMatches(matches, "upcoming")

        assertEquals(listOf(unplayed, tie, shutout), matches)
    }

    @Test
    fun `upcoming plus last starts with the most recent completed match even at zero`() {
        val earlier = match(1, 20, 0)
        val lastPlayed = match(2, 0, 0)
        val next = match(3, -1, -1)
        val matches = arrayListOf(next, lastPlayed, earlier)

        MatchSort.sortMatches(matches, "upcomingLast")

        assertEquals(listOf(lastPlayed, next, earlier), matches)
    }

    @Test
    fun `both alliance scores are required for a result`() {
        assertTrue(match(1, 0, 0).hasResult)
        assertTrue(match(2, 0, 20).hasResult)
        assertFalse(match(3, -1, 0).hasResult)
        assertFalse(match(4, 0, -1).hasResult)
        assertFalse(match(5, -1, -1).hasResult)
    }

    private fun match(number: Int, red: Int, blue: Int): Match {
        fun alliance(score: Int) = Match.MatchAlliance(score, arrayListOf(), arrayListOf(), arrayListOf())
        return Match(
            "event_qm$number", "qm", 1, number, Match.Alliances(alliance(blue), alliance(red)),
            "", "event", number.toLong(), 0, 0, 0, null
        )
    }
}
