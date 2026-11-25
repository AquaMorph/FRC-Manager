package com.aquamorph.frcmanager.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.models.tba.DistrictRank
import com.aquamorph.frcmanager.models.tba.Team

/** Season standings for a district. */
class DistrictRankAdapter(
    private val data: List<DistrictRank>,
    private val teams: List<Team>
) : RecyclerView.Adapter<RankingViewHolder>() {

    var highlightCutoff: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RankingViewHolder.create(parent)

    override fun onBindViewHolder(holder: RankingViewHolder, position: Int) {
        val entry = data[position]
        val context = holder.itemView.context
        val metrics = buildList {
            add(RankingMetric(context.getString(R.string.rankPointTotal), entry.pointTotal.toString()))
            add(RankingMetric(context.getString(R.string.rankRookieBonus), entry.rookieBonus.toString()))
            var eventNumber = 0
            for (event in entry.eventPoints) {
                val label = if (event.districtCmp) {
                    context.getString(R.string.rankDistrictChamp)
                } else {
                    context.getString(R.string.rankEvent, ++eventNumber)
                }
                add(RankingMetric(label, event.total.toString()))
            }
        }
        val highlighted = entry.rank in 1..(highlightCutoff ?: 0)
        holder.bind(entry.rank, entry.teamKey, teams.firstOrNull { it.key == entry.teamKey }?.nickname, metrics, highlighted)
    }

    override fun getItemCount() = data.size
}
