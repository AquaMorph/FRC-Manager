package com.aquamorph.frcmanager.models

import java.util.*

/**
 * Stores rank information at an event.
 *
 * @author Christian Colglazier
 * @version 3/30/2017
 */
abstract class Rank {
    var rankings = arrayOfNulls<Rankings>(0)
    var extra_stats_info = arrayOfNulls<ExtraStatsInfo>(0)
    var sort_order_info = arrayOfNulls<SortOrderInfo>(0)

    abstract inner class Rankings {
        abstract var dq: Int?
        abstract var matches_played: Int?
        abstract var qual_average: Double?
        abstract var rank: Int?
        abstract var record: WLTRecord?
        abstract var extra_stats: DoubleArray?
        abstract var sort_orders: DoubleArray?
        abstract var team_key: String?
    }

    abstract inner class WLTRecord {
        abstract var losses: Int?
        abstract var wins: Int?
        abstract var ties: Int?
    }

    abstract inner class ExtraStatsInfo {
        abstract var name: String?
        abstract var precision: Int?
    }

    abstract inner class SortOrderInfo {
        abstract var name: String?
        abstract var precision: Int?
    }

    companion object {
        fun recordToString(record: WLTRecord): String {
            return String.format(Locale.ENGLISH, "(%d-%d-%d)",
                    record.wins, record.losses, record.ties)
        }
    }
}
