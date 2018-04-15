package com.aquamorph.frcmanager.models

import java.util.*

/**
 * Stores rank information at an event.
 *
 * @author Christian Colglazier
 * @version 3/30/2017
 */
class Rank {
    var rankings = arrayOfNulls<Rankings>(0)
    var extra_stats_info = arrayOfNulls<ExtraStatsInfo>(0)
    var sort_order_info = arrayOfNulls<SortOrderInfo>(0)

    inner class Rankings {
        var dq: Int = 0
        var matches_played: Int = 0
        var qual_average: Double = 0.0
        var rank: Int = 0
        var record: WLTRecord = WLTRecord()
        var extra_stats: DoubleArray? = null
        var sort_orders: DoubleArray? = null
        var team_key: String = ""
    }

    inner class WLTRecord {
        var losses: Int = 0
        var wins: Int = 0
        var ties: Int = 0
    }

    inner class ExtraStatsInfo {
        var name: String  = ""
        var precision: Int = 0
    }

    inner class SortOrderInfo {
        var name: String = ""
        var precision: Int = 0
    }

    companion object {
        fun recordToString(record: WLTRecord): String {
            return String.format(Locale.ENGLISH, "%d-%d-%d",
                    record.wins, record.losses, record.ties)
        }
    }
}
