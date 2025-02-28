package com.aquamorph.frcmanager.network

import com.aquamorph.frcmanager.models.statbotics.Match
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface StatboticsAPI {
    @GET("matches")
    fun getEventMatches(@Query("event") event: String):
            Observable<Response<ArrayList<Match>>>
}
