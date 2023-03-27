package com.aquamorph.frcmanager.network

import com.aquamorph.frcmanager.models.statbotics.Match
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface StatboticsAPI {
    @GET("matches/event/{event}")
    fun getEventMatches(@Path("event") event: String):
            Observable<Response<ArrayList<Match>>>
}