package com.aquamorph.frcmanager.network

import com.aquamorph.frcmanager.models.statbotics.Match
import com.aquamorph.frcmanager.models.tba.TBAPrediction
import io.reactivex.Observable
import retrofit2.HttpException
import retrofit2.Response

internal data class SchedulePredictions(
    val tba: List<TBAPrediction.PredMatch> = emptyList(),
    val statbotics: List<Match> = emptyList()
)

internal fun <T : Any> Observable<Response<T>>.optionalPredictions(
    transform: (T) -> SchedulePredictions,
    onError: (Throwable) -> Unit
): Observable<SchedulePredictions> {
    val unavailable = SchedulePredictions()
    return map { response ->
        if (!response.isSuccessful) throw HttpException(response)
        response.body()?.let(transform) ?: unavailable
    }.doOnError { onError(it) }
        .onErrorReturnItem(unavailable)
        .defaultIfEmpty(unavailable)
}
