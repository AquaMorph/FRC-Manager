package com.aquamorph.frcmanager.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.util.DisplayMetrics
import android.view.View
import com.aquamorph.frcmanager.BuildConfig
import java.util.*

/**
 * A collection of constants needed to interact with the Blue Alliance.
 *
 * @author Christian Colglazier
 * @version 3/30/2018
 */
object Constants {

    private const val URL = "https://www.thebluealliance.com/api/v3"
    const val TBA_HEADER = "X-TBA-Auth-Key"
    const val FORCE_DATA_RELOAD = false
    val TRACING_LEVEL = 3
    const val MAX_EVENT_TITLE_LENGTH = 20
    const val THREAD_WAIT_TIME = 10

    /**
     * getStatusURL() returns the url for the status of FIRST's server.
     *
     * @return url to server status
     */
    val statusURL: String
        get() = "$URL/status"

    /**
     * getEventURL() returns the url for a list of events a team is registered.
     *
     * @param team team identification "frc####"
     * @return url to team events
     */
    fun getEventURL(team: String, year: String): String {
        return String.format("%s/team/%s/events/%s", URL, team, year)
    }

    /**
     * getEvent() returns the url for an event.
     *
     * @param event event tag
     * @return url to event
     */
    fun getEvent(event: String): String {
        return String.format("%s/event/%s", URL, event)
    }

    /**
     * getEventURL() returns the url for a list of events a team is registered.
     *
     * @param event vent identification number
     * @return url to list of teams at an event
     */
    fun getEventTeams(event: String): String {
        return String.format("%s/event/%s/teams", URL, event)
    }

    /**
     * getEventTeamMatches() returns the url for a team's matches at a specific event.
     *
     * @param team  team identification "frc####"
     * @param event event identification number
     * @return url to team matches for an event
     */
    fun getEventTeamMatches(team: String, event: String): String {
        return String.format("%s/team/%s/event/%s/matches", URL, team, event)
    }

    /**
     * getEventMatches() returns the url for all matches at an event.
     *
     * @param event event identification number
     * @return url to matches at en event
     */
    fun getEventMatches(event: String): String {
        return String.format("%s/event/%s/matches", URL, event)
    }

    /**
     * getEventStats() returns the url for event statistics
     *
     * @param event event identification number
     * @return url to event statistics
     */
    fun getEventStats(event: String, year: String): String {
        return "$URL/event/$year$event/stats"
    }

    /**
     * getEventRanks() returns the url for event team rankings.
     *
     * @param event event identification number
     * @return url to event rankings
     */
    fun getEventRanks(event: String): String {
        return String.format("%s/event/%s/rankings", URL, event)
    }

    /**
     * getEventAwards() returns the url awards given at.
     *
     * @param event event identification number
     * @return url to event awards
     */
    fun getEventAwards(event: String): String {
        return String.format("%s/event/%s/awards", URL, event)
    }

    /**
     * getAlliancesURL() returns the url for alliances at an event.
     * @param event event key
     * @return url to alliances at an event
     */
    fun getAlliancesURL(event: String): String {
        return String.format("%s/event/%s/alliances", URL, event)
    }

    /**
     * getApiHeader() returns the header needed to get access to the Blue Alliance.
     *
     * @return the Blue Alliance header
     */
    fun getApiHeader(context: Context): String {
        return BuildConfig.TBA_KEY
    }

    /**
     * isNetworkAvailable() returns if a connection to the internet is available.
     *
     * @param context fragment or activity the request takes place from
     * @return is internet available
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    /**
     * formatTeamNumber() removes all text from the team string and adds padding to create a string
     * of length 4.
     *
     * @param team string
     * @return formatted string
     */
    fun formatTeamNumber(team: String): String {
        return String.format("%4s", team.replace("\\D+".toRegex(), ""))
    }

    /**
     * underlineText() returns a string with formatting in HTML that is underlined.
     *
     * @param text team number
     * @return returns formatted team number
     */
    fun underlineText(text: String): String {
        var text = text
        text = text.replace(" ", "")
        val spaces = 4 - text.length
        val spacesText = StringBuilder()
        for (i in 0 until spaces) {
            spacesText.append("&nbsp;")
        }
        return String.format("<pre>%s<u>%s</u></pre>", spacesText.toString(), text)
    }

    /**
     * checkNoDataScreen() displays a no dataLoader screen if there is not any dataLoader to display. Else
     * displays dataLoader.
     */
    fun checkNoDataScreen(data: ArrayList<*>, recyclerView: View, emptyView: View) {
        if (data.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
        }
    }

    /**
     * isLargeScreen returns if the screen is large or not
     *
     * @param context app context
     * @return screen size
     */
    fun isLargeScreen(context: Context): Boolean {
        val display = (context as Activity).windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)

        val density = context.getResources().displayMetrics.density
        val dpWidth = outMetrics.widthPixels / density
        return dpWidth >= 600
    }
}