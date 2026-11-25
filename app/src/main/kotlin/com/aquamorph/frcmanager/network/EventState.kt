package com.aquamorph.frcmanager.network

import android.content.SharedPreferences
import com.aquamorph.frcmanager.models.tba.Alliance
import com.aquamorph.frcmanager.models.tba.Award
import com.aquamorph.frcmanager.models.tba.DistrictRank
import com.aquamorph.frcmanager.models.tba.Match
import com.aquamorph.frcmanager.models.tba.Rank
import com.aquamorph.frcmanager.models.tba.Team

internal data class EventSelection(
    val eventKey: String = "",
    val districtKey: String = "",
    val predictionMode: String = "none"
)

internal data class EventDisplay(
    val teamNumber: String = "",
    val matchSort: String = "seriesGrouping",
    val predictionPercentage: Boolean = false
)

internal fun SharedPreferences.eventSelection() = EventSelection(
    getString("eventKey", "").orEmpty(),
    getString("districtKey", "").orEmpty(),
    getString("predictions", "none").orEmpty()
)

internal fun SharedPreferences.eventDisplay() = EventDisplay(
    getString("teamNumber", "").orEmpty(),
    getString("matchSort", "seriesGrouping").orEmpty(),
    getString("predictionDisplay", "words") != "words"
)

internal data class EventList<T>(val items: List<T> = emptyList(), val loading: Boolean = false) {
    fun finish(items: List<T>?) = EventList(items ?: this.items)
}

internal enum class FeedNotice { FIRST_DOWN, EVENT_DOWN }

internal data class EventState(
    val selection: EventSelection = EventSelection(),
    val display: EventDisplay = EventDisplay(),
    val matches: EventList<Match> = EventList(),
    val teams: EventList<Team> = EventList(),
    val rankings: EventList<Rank> = EventList(),
    val alliances: EventList<Alliance> = EventList(),
    val awards: EventList<Award> = EventList(),
    val districtRankings: EventList<DistrictRank> = EventList(),
    val districtTeams: EventList<Team> = EventList(),
    val predictions: SchedulePredictions = SchedulePredictions(),
    val feedNotice: FeedNotice? = null
) {
    fun endRefresh() = copy(
        matches = matches.finish(null),
        teams = teams.finish(null),
        rankings = rankings.finish(null),
        alliances = alliances.finish(null),
        awards = awards.finish(null),
        districtRankings = districtRankings.finish(null),
        districtTeams = districtTeams.finish(null)
    )

    fun beginRefresh(selection: EventSelection): EventState {
        val current = if (this.selection.eventKey == selection.eventKey) this else EventState(display = display)
        if (selection.eventKey.isEmpty()) return EventState(selection, display)
        val keepDistrict = current.selection.districtKey == selection.districtKey && selection.districtKey.isNotEmpty()
        return current.copy(
            selection = selection,
            matches = current.matches.copy(loading = true),
            teams = current.teams.copy(loading = true),
            rankings = current.rankings.copy(loading = true),
            alliances = current.alliances.copy(loading = true),
            awards = current.awards.copy(loading = true),
            districtRankings = EventList(
                if (keepDistrict) current.districtRankings.items else emptyList(),
                selection.districtKey.isNotEmpty()
            ),
            districtTeams = EventList(
                if (keepDistrict) current.districtTeams.items else emptyList(),
                selection.districtKey.isNotEmpty()
            ),
            predictions = SchedulePredictions(),
            feedNotice = null
        )
    }
}
