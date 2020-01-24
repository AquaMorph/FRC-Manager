package com.aquamorph.frcmanager.utils

import com.aquamorph.frcmanager.models.Match

/**
 * Custom match sorting
 *
 * @author Christian Colglazier
 * @version 1/23/2020
 */
object MatchSort {

    /**
     * sortMatches() sorts matches by desired sorting method.
     */
    fun sortMatches(matches: ArrayList<Match>, matchSort: String?) {
        if (matchSort == "linear") {
            matches.sortWith(compareBy(Match::time))
        } else {
            matches.reverse()
        }
    }
}
