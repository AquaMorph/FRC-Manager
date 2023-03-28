package com.aquamorph.frcmanager.utils

import com.aquamorph.frcmanager.models.tba.Match
import java.util.Collections

/**
 * Custom match sorting
 *
 * @author Christian Colglazier
 * @version 1/30/2020
 */
object MatchSort {

    /**
     * sortMatches() sorts matches by desired sorting method.
     *
     * @param matches list of matches
     * @param matchSort matching sorting method
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
                matches.sortWith(compareBy { it.time })
                Collections.rotate(matches, matches.size - matchNext(matches))
            }
            "upcomingLast" -> {
                matches.sortWith(compareBy(Match::time))
                val c = matchNext(matches)
                if (c != matches.size && c != 0) {
                    Collections.rotate(matches, matches.size - c + 1)
                }
            }
            else -> matches.sort()
        }
    }

    /**
     *  matchNext() returns the number of the next upcoming match.
     *
     *  @param matches list of matches
     *  @return next match position in list
     */
    private fun matchNext(matches: ArrayList<Match>): Int {
        var count = 0
        matches.forEach { match ->
            if (match.alliances.red.score <= 0 || match.alliances.blue.score <= 0) return count
            count++
        }
        return count
    }
}
