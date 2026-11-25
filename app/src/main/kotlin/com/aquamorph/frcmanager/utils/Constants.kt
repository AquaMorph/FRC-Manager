package com.aquamorph.frcmanager.utils

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

    fun formatTeamNumber(team: String): String = team.filter { it in '0'..'9' }

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
