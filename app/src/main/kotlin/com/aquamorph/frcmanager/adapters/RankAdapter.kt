package com.aquamorph.frcmanager.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.models.tba.Rank
import com.aquamorph.frcmanager.models.tba.Team
import java.util.Locale

/** Qualifying standings for an event. */
class RankAdapter(
    private val data: List<Rank>,
    private val teams: List<Team>
) : RecyclerView.Adapter<RankingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RankingViewHolder.create(parent)

    override fun onBindViewHolder(holder: RankingViewHolder, position: Int) {
        val event = data.first()
        val entry = checkNotNull(event.rankings)[position]
        val metrics = buildList {
            event.sortOrderInfo.orEmpty().forEachIndexed { index, info ->
                entry.sortOrders.getOrNull(index)?.let { score ->
                    add(RankingMetric(info.name, String.format(Locale.getDefault(), "%.${info.precision}f", score)))
                }
            }
            add(RankingMetric(holder.itemView.context.getString(R.string.rankingRecord), Rank.recordToString(entry.record)))
        }
        holder.bind(entry.rank, entry.teamKey, teams.firstOrNull { it.key == entry.teamKey }?.nickname, metrics, entry.rank in 1..8)
    }

    override fun getItemCount() = data.firstOrNull()?.rankings?.size ?: 0
}
