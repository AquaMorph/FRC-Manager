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

internal enum class EventLoad { READY, LOADING, FAILED }

internal data class EventList<T>(val items: List<T> = emptyList(), val load: EventLoad = EventLoad.READY) {
    val loading get() = load == EventLoad.LOADING

    fun begin() = copy(load = EventLoad.LOADING)
    fun finish(items: List<T>) = EventList(items)
    fun fail() = copy(load = EventLoad.FAILED)
    fun failIfLoading() = if (loading) fail() else this
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
        matches = matches.failIfLoading(),
        teams = teams.failIfLoading(),
        rankings = rankings.failIfLoading(),
        alliances = alliances.failIfLoading(),
        awards = awards.failIfLoading(),
        districtRankings = districtRankings.failIfLoading(),
        districtTeams = districtTeams.failIfLoading()
    )

    fun beginRefresh(selection: EventSelection): EventState {
        val current = if (this.selection.eventKey == selection.eventKey) this else EventState(display = display)
        if (selection.eventKey.isEmpty()) return EventState(selection, display)
        val keepDistrict = current.selection.districtKey == selection.districtKey && selection.districtKey.isNotEmpty()
        return current.copy(
            selection = selection,
            matches = current.matches.begin(),
            teams = current.teams.begin(),
            rankings = current.rankings.begin(),
            alliances = current.alliances.begin(),
            awards = current.awards.begin(),
            districtRankings = districtSection(current.districtRankings, selection, keepDistrict),
            districtTeams = districtSection(current.districtTeams, selection, keepDistrict),
            predictions = if (current.selection.predictionMode == selection.predictionMode) {
                current.predictions
            } else SchedulePredictions(),
            feedNotice = null
        )
    }

    private fun <T> districtSection(section: EventList<T>, selection: EventSelection, keepDistrict: Boolean): EventList<T> =
        when {
            selection.districtKey.isEmpty() -> EventList()
            keepDistrict -> section.begin()
            else -> EventList(load = EventLoad.LOADING)
        }
}
