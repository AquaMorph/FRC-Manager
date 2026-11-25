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
        // Feeds can return equal-time matches in a different order on each refresh.
        val series = naturalOrder<Match>().thenBy { it.key }
        val chronological = compareBy(Match::time).then(series)
        when (matchSort) {
            "chronological" -> matches.sortWith(chronological)
            "reverseChronological" -> matches.sortWith(chronological.reversed())
            "upcoming" -> {
                matches.sortWith(chronological)
                Collections.rotate(matches, matches.size - matchNext(matches))
            }
            "upcomingLast" -> {
                matches.sortWith(chronological)
                val c = matchNext(matches)
                if (c != matches.size && c != 0) {
                    Collections.rotate(matches, matches.size - c + 1)
                }
            }
            else -> matches.sortWith(series)
        }
    }

    /**
     *  matchNext() returns the number of the next upcoming match.
     *
     *  @param matches list of matches
     *  @return next match position in list
     */
    private fun matchNext(matches: List<Match>): Int =
        matches.indexOfFirst { !it.hasResult }.takeIf { it >= 0 } ?: matches.size
}
