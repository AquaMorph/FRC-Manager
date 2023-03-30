package com.aquamorph.frcmanager.models.tba

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.Locale

/**
 * Stores rank information at an event.
 *
 * @author Christian Colglazier
 * @version 1/31/2020
 */
data class Rank(
    @Expose
    @SerializedName("rankings")
    var rankings: ArrayList<Rankings>?,
    @Expose
    @SerializedName("extra_stats_info")
    var extraStatsInfo: ArrayList<ExtraStatsInfo>,
    @Expose
    @SerializedName("sort_order_info")
    var sortOrderInfo: ArrayList<SortOrderInfo>
) {
    inner class Rankings(
        @Expose
        @SerializedName("dq")
        var dq: Int,
        @Expose
        @SerializedName("matches_played")
        var matchesPlayed: Int,
        @Expose
        @SerializedName("qual_average")
        var qualAverage: Double,
        @Expose
        @SerializedName("rank")
        var rank: Int,
        @Expose
        @SerializedName("record")
        var record: WLTRecord,
        @Expose
        @SerializedName("extra_stats")
        var extraStats: DoubleArray,
        @Expose
        @SerializedName("sort_orders")
        var sortOrders: DoubleArray,
        @Expose
        @SerializedName("team_key")
        var teamKey: String
    )

    data class WLTRecord(
        @Expose
        @SerializedName("losses")
        var losses: Int,
        @Expose
        @SerializedName("wins")
        var wins: Int,
        @Expose
        @SerializedName("ties")
        var ties: Int
    )

    data class ExtraStatsInfo(
        @Expose
        @SerializedName("name")
        var name: String,
        @Expose
        @SerializedName("precision")
        var precision: Int
    )

    data class SortOrderInfo(
        @Expose
        @SerializedName("name")
        var name: String,
        @Expose
        @SerializedName("precision")
        var precision: Int
    )
    companion object {

        /**
         * recordToString() converts a teams win loss record to a readable format.
         *
         * @param record team win loss record
         * @return readable format
         */
        fun recordToString(record: WLTRecord): String {
            return String.format(Locale.ENGLISH, "%d-%d-%d",
                    record.wins, record.losses, record.ties)
        }
    }
}
