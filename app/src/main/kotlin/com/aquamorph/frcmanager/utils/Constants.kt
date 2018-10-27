package com.aquamorph.frcmanager.utils

import android.os.Build
import android.text.Html
import android.text.Spanned
import android.view.View
import com.aquamorph.frcmanager.BuildConfig
import java.util.*

/**
 * A collection of constants needed to interact with the Blue Alliance.
 *
 * @author Christian Colglazier
 * @version 10/27/2018
 */
object Constants {

    const val URL = "https://www.thebluealliance.com/api/v3/"
    const val TBA_HEADER = "X-TBA-Auth-Key"
    const val TRACING_LEVEL = 3
    const val THREAD_WAIT_TIME = 100
    const val MAX_NUMBER_OF_TABS = 7
    const val CACHE_SIZE = (10 * 1024 * 1024).toLong()

    /**
     * getApiHeader() returns the header needed to get access to the Blue Alliance.
     *
     * @return the Blue Alliance header
     */
    fun getApiHeader(): String {
        return BuildConfig.TBA_KEY
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
     * fromHtml() converts HTML code to a string.
     */
    @SuppressWarnings("deprecation")
    fun fromHtml(html: String): Spanned? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(html)
        }
    }
}