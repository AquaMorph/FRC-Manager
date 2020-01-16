package com.aquamorph.frcmanager.utils

import android.os.Build
import android.text.Html
import android.text.Spanned
import android.view.View
import com.aquamorph.frcmanager.BuildConfig
import com.aquamorph.frcmanager.network.DataLoader

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

    /**
     * getTeamName() returns the name of a team.
     *
     * @param number of the team
     * @return name of the team
     */
    fun getTeamName(number: String): String {
        if (isDistrict()) {
            for (i in DataLoader.districtTeamDC.data.indices) {
                if (number == DataLoader.districtTeamDC.data[i].key) {
                    return DataLoader.districtTeamDC.data[i].nickname
                }
            }
        } else {
            for (i in DataLoader.teamDC.data.indices) {
                if (number == DataLoader.teamDC.data[i].key) {
                    return DataLoader.teamDC.data[i].nickname
                }
            }
        }
        return ""
    }

    /**
     * getTeamRecord() returns the record of a team.
     *
     * @param number of the team
     * @return record of the team
     */
    fun getTeamRecord(number: String): String {
        if (DataLoader.rankDC.data != null && DataLoader.rankDC.data.isNotEmpty()) {
            if (DataLoader.rankDC.data[0].rankings != null && DataLoader.rankDC.data[0].rankings.isNotEmpty()) {
                for (i in DataLoader.rankDC.data[0].rankings.indices) {
                    if (number == DataLoader.rankDC.data[0].rankings[i]!!.team_key) {
                        val record = DataLoader.rankDC.data[0].rankings[i]!!.record
                        return "(" + record.wins + "-" + record.losses + "-" + record.ties + ")"
                    }
                }
            }
        }
        return ""
    }

    /**
     * getTeamRecord() returns the rank of a team.
     *
     * @param number of the team
     * @return rank of the team
     */
    fun getTeamRank(number: String): String {
        if (DataLoader.rankDC.data.isNotEmpty()) {
            for (i in DataLoader.rankDC.data[0].rankings.indices) {
                if (number == DataLoader.rankDC.data[0].rankings[i]!!.team_key) {
                    val rank = DataLoader.rankDC.data[0].rankings[i]!!.rank
                    return "Rank #$rank"
                }
            }
        }
        return ""
    }

    /**
     * isDistrict() returns if the event is a district.
     */
    fun isDistrict(): Boolean {
        return DataLoader.districtKey != ""
    }

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

    fun matchString(compLevel: String, setNumber: Int, matchNumber: Int): String {
        return if (compLevel == "qm") {
            String.format("%S-%s", compLevel, matchNumber)
        } else {
            String.format("%S-%s-%s", compLevel, setNumber, matchNumber)
        }
    }

    enum class Theme {
        LIGHT, DARK, BATTERY_SAVER
    }
}
