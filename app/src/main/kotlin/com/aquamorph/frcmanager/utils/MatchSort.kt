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
                matches.sortWith(compareBy{it.time})
                Collections.rotate(matches, matches.size - matchNext(matches))
            }
            "upcomingLast" -> {
                matches.sortWith(compareBy(Match::time))
                val c = matchNext(matches)
                if (c != matches.size) {
                    Collections.rotate(matches, matches.size - c + 1)
                }
            }
            else -> matches.sort()
        }
    }

    private fun matchNext(matches: ArrayList<Match>): Int {
        var count = 0
        matches.forEach { match ->
            if (match.alliances.red.score <= 0 || match.alliances.blue.score <= 0) return count
            count++
        }
        return count
    }
}
