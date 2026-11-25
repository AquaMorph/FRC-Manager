package com.aquamorph.frcmanager.network

import android.content.Context
import com.aquamorph.frcmanager.utils.Constants
import com.aquamorph.frcmanager.utils.Logging
import io.reactivex.Observable
import io.reactivex.Scheduler
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
            section(tba.getEventMatches(event)) { state, matches -> state.copy(matches = state.matches.finish(matches)) },
            section(tba.getEventTeams(event)) { state, teams -> state.copy(teams = state.teams.finish(teams)) },
            section(tba.getEventRankings(event)) { state, rank ->
                val rankings = rank?.let { if (it.rankings.isNullOrEmpty()) emptyList() else listOf(it) }
                state.copy(rankings = state.rankings.finish(rankings))
            },
            section(tba.getEventAlliances(event)) { state, alliances ->
                state.copy(alliances = state.alliances.finish(alliances))
            },
            section(tba.getEventAwards(event)) { state, awards -> state.copy(awards = state.awards.finish(awards)) },
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
            requests += section(tba.getDistrictRankings(district)) { state, ranks ->
                state.copy(districtRankings = state.districtRankings.finish(ranks))
            }
            requests += section(tba.getDistrictTeams(district)) { state, teams ->
                state.copy(districtTeams = state.districtTeams.finish(teams))
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

    private fun <T : Any> section(
        request: Observable<Response<T>>,
        update: (EventState, T?) -> EventState
    ): Observable<EventUpdate> {
        return request.map<EventUpdate> { response ->
            if (!response.isSuccessful) throw HttpException(response)
            val body = response.body()
            return@map { state -> update(state, body) }
        }.doOnError(::logError)
            .onErrorReturnItem { state -> update(state, null) }
            .defaultIfEmpty { state -> update(state, null) }
            .subscribeOn(io)
    }

    private fun logError(error: Throwable) = Logging.error(this, error.toString(), 0)
}
