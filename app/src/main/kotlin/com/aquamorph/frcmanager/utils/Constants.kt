package com.aquamorph.frcmanager.utils

import android.os.Build
import android.text.Html
import android.text.Spanned
import android.view.View
import com.aquamorph.frcmanager.BuildConfig
import com.aquamorph.frcmanager.models.tba.TBAPrediction
import com.google.gson.JsonObject

/**
 * A collection of constants needed to interact with the Blue Alliance.
 *
 * @author Christian Colglazier
 * @version 3/3/2020
 */
object Constants {

    const val TBA_API_URL = "https://www.thebluealliance.com/api/v3/"
    const val TBA_HEADER = "X-TBA-Auth-Key"
    const val STATBOTICS_API_URL = "https://api.statbotics.io/v3/"
    const val TRACING_LEVEL = 3
    const val CACHE_SIZE = (10 * 1024 * 1024).toLong()
    const val DIVIDER_WIDTH = 4f

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
        val tmp = text.replace(" ", "")
        val spaces = 4 - tmp.length
        val spacesText = StringBuilder()
        for (i in 0 until spaces) {
            spacesText.append("&nbsp;")
        }
        return String.format("<pre>%s<u>%s</u></pre>", spacesText.toString(), tmp)
    }

    /**
     * checkNoDataScreen() displays a no dataLoader screen if there is not any dataLoader to
     * display. Else displays dataLoader.
     *
     * @param data data to be shown
     * @param recyclerView data view
     * @param emptyView view for no data
     */
    fun checkNoDataScreen(data: List<*>, recyclerView: View, emptyView: View) {
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
     *
     * @param html html code
     */
    @SuppressWarnings("deprecation")
    fun fromHtml(html: String): Spanned? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(html)
        }
    }

    /**
     * matchTypeToString() converts match type to english.
     *
     * @param compLevel match type
     * @return competition level in English
     */
    fun matchTypeToString(compLevel: String): String {
        return when (compLevel) {
            "qm" -> "Qualification"
            "ef" -> "Octofinal"
            "qf" -> "Quarterfinal"
            "sf" -> "Semifinal"
            "f" -> "Final"
            else -> ""
        }
    }

    /**
     * matchString() converts match information to a formatted string.
     *
     * @param compLevel match competition level
     * @param setNumber match set number
     * @param matchNumber match number
     * return formatted match shortcode
     */
    fun matchString(compLevel: String, setNumber: Int, matchNumber: Int): String {
        return if (compLevel == "qm") {
            String.format("%S-%s", compLevel, matchNumber)
        } else {
            String.format("%S-%s-%s", compLevel, setNumber, matchNumber)
        }
    }

    /**
     * tbaPredictionToObject() converts json object to prediction and adds prediction to an array.
     *
     * @param qual json object
     * @param predictions array of match predictions
     */
    private fun tbaPredictionToObject(
        qual: JsonObject,
        predictions: ArrayList<TBAPrediction.PredMatch>
    ) {
        for (q in qual.keySet()) {
            val matchData = qual.get(q).asJsonObject
            predictions.add(
                TBAPrediction.PredMatch(q,
                    matchData.get("prob").asDouble,
                    matchData.get("winning_alliance").asString))
        }
    }

    /**
     * tbaPredictionToArray() converts TBA API return to array of match predictions.
     *
     * @param matchPredictions match prediction json blob
     * @return list of match predictions
     */
    fun tbaPredictionToArray(matchPredictions: TBAPrediction):
            ArrayList<TBAPrediction.PredMatch> {
        val predictions: ArrayList<TBAPrediction.PredMatch> = ArrayList()
        val qual = matchPredictions.matchPredictions.qual
        val playoffs = matchPredictions.matchPredictions.playoff
        tbaPredictionToObject(qual, predictions)
        tbaPredictionToObject(playoffs, predictions)
        return predictions
    }

    /**
     * Theme manages app theme states.
     */
    enum class Theme {
        LIGHT, DARK, BATTERY_SAVER
    }
}
