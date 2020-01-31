package com.aquamorph.frcmanager.utils

import com.aquamorph.frcmanager.models.Match
import java.util.Collections
import kotlin.collections.ArrayList

/**
 * Custom match sorting
 *
 * @author Christian Colglazier
 * @version 1/30/2020
 */
object MatchSort {

    /**
     * sortMatches() sorts matches by desired sorting method.
     */
    fun sortMatches(matches: ArrayList<Match>, matchSort: String?) {
        when (matchSort) {
            "seriesGrouping" -> matches.sort()
            "chronological" -> matches.sortWith(compareBy(Match::time))
            "reverseChronological" -> {
                matches.sortWith(compareBy(Match::time))
                matches.reverse()
            }
            "upcoming" -> {
                matches.sortWith(compareBy(Match::post_result_time, Match::time))
                Collections.rotate(matches, -matchNext(matches))
            }
            "upcomingLast" -> {
                matches.sortWith(compareBy(Match::post_result_time, Match::time))
                Collections.rotate(matches, -matchNext(matches)+1)
            }
            else -> matches.sort()
        }
    }

    private fun matchNext(matches: ArrayList<Match>) : Int {
        var count = 0
        matches.forEach { match ->
            if (match.post_result_time <= 0) return count
            count++
        }
        return count
    }
}
