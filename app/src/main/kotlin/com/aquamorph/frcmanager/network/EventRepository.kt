package com.aquamorph.frcmanager.network

import android.content.Context
import com.aquamorph.frcmanager.models.tba.districtPointsSpots
import com.aquamorph.frcmanager.utils.Constants
import com.aquamorph.frcmanager.utils.Logging
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import retrofit2.Response

internal typealias EventUpdate = (EventState) -> EventState

internal class EventRepository(
    context: Context,
    private val io: Scheduler = Schedulers.io()
) {
    private val appContext = context.applicationContext
    private val tba by lazy { TBARetrofitInstance.getRetrofit(appContext).create(TbaApi::class.java) }
    private val statbotics by lazy {
        StatboticsRetrofitInstance.getRetrofit(appContext).create(StatboticsAPI::class.java)
    }

    fun load(selection: EventSelection): Observable<EventUpdate> {
        val event = selection.eventKey
        val district = selection.districtKey
        val requests = mutableListOf(
            listSection(tba.getEventMatches(event)) { state, update -> state.copy(matches = update(state.matches)) },
            listSection(tba.getEventTeams(event)) { state, update -> state.copy(teams = update(state.teams)) },
            section(tba.getEventRankings(event), { rank ->
                if (rank == null || rank.rankings.isNullOrEmpty()) emptyList() else listOf(rank)
            }) { state, update ->
                state.copy(rankings = update(state.rankings))
            },
            listSection(tba.getEventAlliances(event)) { state, update ->
                state.copy(alliances = update(state.alliances))
            },
            listSection(tba.getEventAwards(event)) { state, update -> state.copy(awards = update(state.awards)) },
            tba.getStatus().map<EventUpdate> { status ->
                val notice = when {
                    status.isDatafeedDown -> FeedNotice.FIRST_DOWN
                    event in status.downEvents -> FeedNotice.EVENT_DOWN
                    else -> null
                }
                { state -> state.copy(feedNotice = notice) }
            }.doOnError(::logError).onErrorResumeNext(Observable.empty()).subscribeOn(io)
        )

        if (district.isNotEmpty()) {
            requests += listSection(tba.getDistrictRankings(district)) { state, update ->
                state.copy(districtRankings = update(state.districtRankings))
            }
            requests += listSection(tba.getDistrictTeams(district)) { state, update ->
                state.copy(districtTeams = update(state.districtTeams))
            }
            district.take(4).toIntOrNull()?.let { year ->
                requests += districtCutoff(district, year)
            }
        }

        val predictions = when (selection.predictionMode) {
            "tba" -> tba.getEventPredictions(event).optionalPredictions(
                { SchedulePredictions(tba = Constants.tbaPredictionToArray(it)) },
                ::logError
            )
            "statbotics" -> statbotics.getEventMatches(event).optionalPredictions(
                { SchedulePredictions(statbotics = it) },
                ::logError
            )
            else -> Observable.empty()
        }
        requests += predictions.map<EventUpdate> { result -> { state -> state.copy(predictions = result) } }
            .subscribeOn(io)
        return Observable.merge(requests)
    }

    private fun districtCutoff(district: String, year: Int): Observable<EventUpdate> = Observable.zip(
        tba.getDistrictAllocations(year).subscribeOn(io),
        tba.getDistrictAdvancement(district).subscribeOn(io),
        BiFunction { allocations, advancement ->
            if (!allocations.isSuccessful) throw HttpException(allocations)
            if (!advancement.isSuccessful) throw HttpException(advancement)
            val slots = allocations.body()?.firstOrNull { it.key == district }?.counts?.cmp
            val cutoff = districtPointsSpots(slots, advancement.body())
            val update: EventUpdate = { it.copy(districtHighlightCutoff = cutoff) }
            update
        }
    ).doOnError(::logError).onErrorResumeNext(Observable.empty())

    private fun <T> listSection(
        request: Observable<Response<ArrayList<T>>>,
        update: (EventState, (EventList<T>) -> EventList<T>) -> EventState
    ): Observable<EventUpdate> = section(request, { it.orEmpty() }, update)

    private fun <T : Any, Item> section(
        request: Observable<Response<T>>,
        items: (T?) -> List<Item>,
        update: (EventState, (EventList<Item>) -> EventList<Item>) -> EventState
    ): Observable<EventUpdate> {
        val failed: EventUpdate = { state -> update(state, EventList<Item>::fail) }
        return request.map<EventUpdate> { response ->
            if (!response.isSuccessful) throw HttpException(response)
            val result = items(response.body())
            return@map { state -> update(state) { section -> section.finish(result) } }
        }.doOnError(::logError)
            .onErrorReturnItem(failed)
            .defaultIfEmpty(failed)
            .subscribeOn(io)
    }

    private fun logError(error: Throwable) = Logging.error(this, error.toString(), 0)
}
