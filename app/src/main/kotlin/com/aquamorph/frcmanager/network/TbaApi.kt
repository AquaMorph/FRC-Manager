package com.aquamorph.frcmanager.network

import com.aquamorph.frcmanager.models.tba.Alliance
import com.aquamorph.frcmanager.models.tba.Award
import com.aquamorph.frcmanager.models.tba.DistrictRank
import com.aquamorph.frcmanager.models.tba.Event
import com.aquamorph.frcmanager.models.tba.Match
import com.aquamorph.frcmanager.models.tba.MatchScore2019
import com.aquamorph.frcmanager.models.tba.MatchScore2020
import com.aquamorph.frcmanager.models.tba.Rank
import com.aquamorph.frcmanager.models.tba.Status
import com.aquamorph.frcmanager.models.tba.TBAPrediction
import com.aquamorph.frcmanager.models.tba.Team
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * API requests for the Blue Alliance.
 *
 * @author Christian Colglazier
 * #version 8/18/18
 */
interface TbaApi {

    /**
     * getStatus() gets status information from of the Blue Alliance.
     */
    @GET("status")
    fun getStatus():
            Observable<Status>

    /**
     * getTeamEvents() returns a list of events
     *
     * @param team team identification "frc####"
     */
    @GET("team/{team}/events/{year}")
    fun getTeamEvents(@Path("team") team: String, @Path("year") event: String):
            Observable<Response<ArrayList<Event>>>

    /**
     * getEvent() returns info about an event.
     *
     * @param event event identification
     */
    @GET("event/{event}")
    fun getEvent(@Path("event") event: String):
            Observable<Response<Event>>

    /**
     * getEventTeams() returns a list of teams at an event.
     *
     * @param event event identification
     */
    @GET("event/{event}/teams")
    fun getEventTeams(@Path("event") event: String):
            Observable<Response<ArrayList<Team>>>

    /**
     * getTeam() returns information about a team.
     *
     * @param team team identification "frc####"
     */
    @GET("team/{team}")
    fun getTeam(@Path("team") team: String):
            Observable<Response<Team>>

    /**
     * getEventMatches returns a list of matches of an event
     *
     * @param event event identification
     */
    @GET("event/{event}/matches")
    fun getEventMatches(@Path("event") event: String):
            Observable<Response<ArrayList<Match>>>

    /**
     * getEventRankings() returns event rankings.
     *
     * @param event event identification
     */
    @GET("event/{event}/rankings")
    fun getEventRankings(@Path("event") event: String):
            Observable<Response<Rank>>

    /**
     * getEventAwards() returns list of event awards.
     *
     * @param event event identification
     */
    @GET("event/{event}/awards")
    fun getEventAwards(@Path("event") event: String):
            Observable<Response<ArrayList<Award>>>

    /**
     * getEventAlliances() returns list of event alliances.
     *
     * @param event event identification
     */
    @GET("event/{event}/alliances")
    fun getEventAlliances(@Path("event") event: String):
            Observable<Response<ArrayList<Alliance>>>

    /**
     * getDistrictRankings() returns list of district rankings.
     *
     * @param districtKey district identification
     */
    @GET("district/{districtKey}/rankings")
    fun getDistrictRankings(@Path("districtKey") districtKey: String):
            Observable<Response<ArrayList<DistrictRank>>>

    /**
    * getDistrictTeams() returns list of teams in a district.
    *
    * @param districtKey district identification
    */
    @GET("district/{districtKey}/teams")
    fun getDistrictTeams(@Path("districtKey") districtKey: String):
            Observable<Response<ArrayList<Team>>>

    /**
     * getMatch2019() returns match breakdown for 2019 matches.
     *
     * @param matchKey district identification
     */
    @GET("match/{matchKey}")
    fun getMatch2019(@Path("matchKey") matchKey: String):
            Call<MatchScore2019>

    /**
     * getMatch2020() returns match breakdown for 2020 matches.
     *
     * @param matchKey district identification
     */
    @GET("match/{matchKey}")
    fun getMatch2020(@Path("matchKey") matchKey: String):
            Call<MatchScore2020>

    /**
     * getEventPredictions() returns event predictions.
     *
     * @param eventKey event identification
     */
    @GET("event/{eventKey}/predictions")
    fun getEventPredictions(@Path("eventKey") eventKey: String):
            Observable<Response<TBAPrediction>>
}
