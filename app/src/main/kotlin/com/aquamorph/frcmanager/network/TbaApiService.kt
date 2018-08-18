package com.aquamorph.frcmanager.network

import com.aquamorph.frcmanager.models.Match
import com.aquamorph.frcmanager.models.Status
import com.aquamorph.frcmanager.models.Team
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path


interface TbaApiService {
    @GET("status")
    fun getStatus(): Observable<Status>

    @GET("team/{team_key}")
    fun getTeam(@Path("team_key") teamKey: String): Observable<Team>

    @GET("event/{event}/teams")
    fun getEventTeams(@Path("event") event: String): Observable<ArrayList<Team>>

    @GET("event/{event}/matches")
    fun getEventMatches(@Path("event") event: String): Observable<ArrayList<Match>>
}