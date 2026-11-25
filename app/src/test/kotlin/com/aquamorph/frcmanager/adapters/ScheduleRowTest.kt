package com.aquamorph.frcmanager.adapters

import com.aquamorph.frcmanager.models.tba.Match
import com.aquamorph.frcmanager.models.tba.MatchScore
import com.aquamorph.frcmanager.models.tba.TBAPrediction
import com.aquamorph.frcmanager.network.EventSelection
import com.aquamorph.frcmanager.network.EventState
import com.aquamorph.frcmanager.network.SchedulePredictions
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ScheduleRowTest {
    private val match = Match(
        "event_qm1", "qm", 1, 1,
        Match.Alliances(
            Match.MatchAlliance(-1, arrayListOf("frc8"), arrayListOf(), arrayListOf()),
            Match.MatchAlliance(-1, arrayListOf("frc254", "frc1678", "frc10023"), arrayListOf(), arrayListOf())
        ),
        "", "event", 10, 0, 0, 0, null
    )
    private val predictions = SchedulePredictions(tba = listOf(TBAPrediction.PredMatch(match.key, 0.6, "red")))

    @Test
    fun `five-digit team filters and highlights without padding or truncation`() {
        val row = rows(match).single()
        assertEquals(listOf("254", "1678", "10023"), row.red.teams)
        assertEquals("10023", row.red.highlightedTeam)
        assertEquals(listOf("8"), row.blue.teams)
        assertNull(row.blue.highlightedTeam)
        assertTrue(scheduleRows(listOf(match), "1002", true, "seriesGrouping", predictions, false).isEmpty())
    }

    @Test
    fun `posting a result changes the existing row without changing its lineup`() {
        val before = rows(match).single()
        val after = rows(match.copy(alliances = match.alliances.copy(
            red = match.alliances.red.copy(score = 120), blue = match.alliances.blue.copy(score = 0)
        ))).single()
        assertTrue(ScheduleRowDiff.areItemsTheSame(before, after))
        assertFalse(ScheduleRowDiff.areContentsTheSame(before, after))
        assertTrue(ScheduleRowDiff.areContentsTheSame(before, rows(match.copy(actualTime = 11)).single()))
        assertEquals(before.red.teams, after.red.teams)
        assertNull(after.prediction)
        assertTrue(after.red.emphasized)
        assertFalse(after.blue.emphasized)
        assertEquals(0, after.blue.score)
    }

    @Test
    fun `modern winner and loser each show three dots when they earn three bonus points`() {
        val row = rows(playedMatch("2026event", "red", 6, 3)).single()
        assertEquals(3, row.red.bonusRp)
        assertEquals(3, row.blue.bonusRp)
        assertEquals("\u2022\u2022\u2022", bonusRpDots(row.red.bonusRp))
    }

    @Test
    fun `win points alone show no dots starting in 2025`() {
        val row = rows(playedMatch("2025event", "blue", 0, 3)).single()
        assertEquals("", bonusRpDots(row.blue.bonusRp))
    }

    @Test
    fun `older wins subtract two points and ties subtract one from both alliances`() {
        val win = rows(playedMatch("2024event", "red", 4, 1)).single()
        assertEquals(2, win.red.bonusRp)
        assertEquals(1, win.blue.bonusRp)

        val tie = rows(playedMatch("2026event", "", 4, 1)).single()
        assertEquals(3, tie.red.bonusRp)
        assertEquals(0, tie.blue.bonusRp)
    }

    @Test
    fun `matches without earned qualification RP show no dots`() {
        val played = playedMatch("2026event", "red", 0, 0)
        assertEquals(0, rows(played).single().red.bonusRp)
        assertEquals(0, rows(played.copy(scoreBreakDown = null)).single().red.bonusRp)

        val withBonus = playedMatch("2026event", "red", 6, 3)
        assertEquals(0, rows(withBonus.copy(compLevel = "sf")).single().red.bonusRp)
        assertEquals(0, rows(withBonus.copy(alliances = match.alliances)).single().red.bonusRp)
    }

    @Test
    fun `refresh retains predictions only for the same event and source`() {
        val selection = EventSelection(eventKey = "event", predictionMode = "tba")
        val state = EventState(selection = selection, predictions = predictions)
        assertEquals(predictions, state.beginRefresh(selection).predictions)
        assertEquals(SchedulePredictions(), state.beginRefresh(selection.copy(eventKey = "other")).predictions)
        assertEquals(SchedulePredictions(), state.beginRefresh(selection.copy(predictionMode = "none")).predictions)
    }

    private fun playedMatch(eventKey: String, winner: String, redRp: Int, blueRp: Int) = match.copy(
        eventKey = eventKey,
        winningAlliance = winner,
        alliances = match.alliances.copy(
            red = match.alliances.red.copy(score = if (winner == "blue") 100 else 120),
            blue = match.alliances.blue.copy(score = if (winner == "red") 100 else 120)
        ),
        scoreBreakDown = Gson().fromJson(
            """{"red":{"rp":$redRp},"blue":{"rp":$blueRp}}""", MatchScore.ScoreBreakDown::class.java
        )
    )

    private fun rows(match: Match) = scheduleRows(listOf(match), "10023", true, "seriesGrouping", predictions, true)
}
