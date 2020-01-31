package com.aquamorph.frcmanager.models

import java.util.Locale

/**
 * Stores rank information at an event.
 *
 * @author Christian Colglazier
 * @version 1/30/2020
 */
data class Rank(
    var rankings: ArrayList<Rankings>,
    var extra_stats_info: ArrayList<ExtraStatsInfo>,
    var sort_order_info: ArrayList<SortOrderInfo>
) {
    inner class Rankings(
        var dq: Int,
        var matches_played: Int,
        var qual_average: Double,
        var rank: Int,
        var record: WLTRecord,
        var extra_stats: DoubleArray,
        var sort_orders: DoubleArray,
        var team_key: String
    )

    data class WLTRecord(
        var losses: Int,
        var wins: Int,
        var ties: Int
    )

    data class ExtraStatsInfo(
        var name: String,
        var precision: Int
    )

    data class SortOrderInfo(
        var name: String,
        var precision: Int
    )

    companion object {
        fun recordToString(record: WLTRecord): String {
            return String.format(Locale.ENGLISH, "%d-%d-%d",
                    record.wins, record.losses, record.ties)
        }
    }
}
