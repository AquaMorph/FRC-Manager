package com.aquamorph.frcmanager.network

import com.aquamorph.frcmanager.models.Alliance
import com.aquamorph.frcmanager.models.Award
import com.aquamorph.frcmanager.models.DistrictRank
import com.aquamorph.frcmanager.models.Event
import com.aquamorph.frcmanager.models.Match
import com.aquamorph.frcmanager.models.MatchScore2019
import com.aquamorph.frcmanager.models.Rank
import com.aquamorph.frcmanager.models.Status
import com.aquamorph.frcmanager.models.Team
import io.reactivex.Observable
import retrofit2.Call
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
            Observable<ArrayList<Event>>

    /**
     * getEvent() returns info about an event.
     *
     * @param event event identification
     */
    @GET("event/{event}")
    fun getEvent(@Path("event") event: String):
            Observable<Event>

    /**
     * getEventTeams() returns a list of teams at an event.
     *
     * @param event event identification
     */
    @GET("event/{event}/teams")
    fun getEventTeams(@Path("event") event: String):
            Observable<ArrayList<Team>>

    /**
     * getTeam() returns information about a team.
     *
     * @param team team identification "frc####"
     */
    @GET("team/{team}")
    fun getTeam(@Path("team") team: String):
            Observable<Team>

    /**
     * getEventMatches returns a list of matches of an event
     *
     * @param event event identification
     */
    @GET("event/{event}/matches")
    fun getEventMatches(@Path("event") event: String):
            Observable<ArrayList<Match>>

    /**
     * getEventRankings() returns event rankings.
     *
     * @param event event identification
     */
    @GET("event/{event}/rankings")
    fun getEventRankings(@Path("event") event: String):
            Observable<Rank>

    /**
     * getEventAwards() returns list of event awards.
     *
     * @param event event identification
     */
    @GET("event/{event}/awards")
    fun getEventAwards(@Path("event") event: String):
            Observable<ArrayList<Award>>

    /**
     * getEventAlliances() returns list of event alliances.
     *
     * @param event event identification
     */
    @GET("event/{event}/alliances")
    fun getEventAlliances(@Path("event") event: String):
            Observable<ArrayList<Alliance>>

    /**
     * getDistrictRankings() returns list of district rankings.
     *
     * @param districtKey district identification
     */
    @GET("district/{districtKey}/rankings")
    fun getDistrictRankings(@Path("districtKey") districtKey: String):
            Observable<ArrayList<DistrictRank>>

    /**
    * getDistrictTeams() returns list of teams in a district.
    *
    * @param districtKey district identification
    */
    @GET("district/{districtKey}/teams")
    fun getDistrictTeams(@Path("districtKey") districtKey: String):
            Observable<ArrayList<Team>>

    @GET("match/{matchKey}")
    fun getMatch2019(@Path("matchKey") matchKey: String):
            Call<MatchScore2019>
}
