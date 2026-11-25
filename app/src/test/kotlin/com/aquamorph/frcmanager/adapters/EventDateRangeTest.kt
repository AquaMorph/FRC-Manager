package com.aquamorph.frcmanager.adapters

import org.junit.Assert.assertEquals
import org.junit.Test

class EventDateRangeTest {
    @Test
    fun formatsSingleDayOnce() {
        assertEquals("March 7 2026", formatEventDateRange("2026-03-07", "2026-03-07"))
    }

    @Test
    fun preservesRangeWhenDatesFallOnSameWeekday() {
        assertEquals("March 7 to 14 2026", formatEventDateRange("2026-03-07", "2026-03-14"))
    }

    @Test
    fun showsBothMonthsWhenRangeCrossesMonthBoundary() {
        assertEquals("March 31 to April 2 2026", formatEventDateRange("2026-03-31", "2026-04-02"))
    }

    @Test
    fun showsBothYearsWhenRangeCrossesYearBoundary() {
        assertEquals("December 31 2026 to January 2 2027", formatEventDateRange("2026-12-31", "2027-01-02"))
    }
}
