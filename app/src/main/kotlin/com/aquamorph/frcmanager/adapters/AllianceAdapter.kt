package com.aquamorph.frcmanager.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.activities.TeamSummary
import com.aquamorph.frcmanager.models.tba.Alliance
import com.aquamorph.frcmanager.utils.Constants
import com.aquamorph.frcmanager.utils.NavigationMotion

/**
 * Populated a view with alliance dataLoader.
 *
 * @author Christian Colglazier
 * @version 4/2/2018
 */
class AllianceAdapter(private val context: Context, private val data: ArrayList<Alliance>) :
        RecyclerView.Adapter<AllianceAdapter.MyViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = inflater.inflate(R.layout.alliance, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(position + 1, data[position].picks)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title = itemView.findViewById<TextView>(R.id.alliance_number)
        private val teams = listOf(R.id.team_1, R.id.team_2, R.id.team_3, R.id.team_4)
            .map { TeamTile(itemView.findViewById(it)) }

        init {
            ViewCompat.setAccessibilityHeading(title, true)
            val context = itemView.context
            teams.forEachIndexed { index, tile ->
                if (index == 0) tile.view.setBackgroundResource(R.drawable.bg_alliance_captain)
                tile.role.text = if (index == 0) context.getString(R.string.alliance_captain)
                    else context.getString(R.string.alliance_pick, index)
                tile.view.setOnClickListener {
                    val number = tile.number.text.toString()
                    if (number.isNotEmpty()) {
                        val intent = Intent(context, TeamSummary::class.java)
                        intent.putExtra("teamNumber", number)
                        NavigationMotion.openDetail(tile.view, intent)
                    }
                }
            }
        }

        internal fun bind(seed: Int, picks: List<String>) {
            val context = itemView.context
            title.text = context.getString(R.string.alliance_title, seed)
            teams.forEachIndexed { index, tile ->
                val number = picks.getOrNull(index)?.let(Constants::formatTeamNumber).orEmpty()
                tile.number.text = number
                tile.view.transitionName = if (number.isEmpty()) null else "alliance:$seed:$index:$number"
                tile.view.isVisible = number.isNotEmpty()
                tile.view.contentDescription = if (number.isEmpty()) null
                    else context.getString(R.string.alliance_team_description, tile.role.text, number)
            }
        }

        private class TeamTile(val view: View) {
            val role: TextView = view.findViewById(R.id.alliance_team_role)
            val number: TextView = view.findViewById(R.id.alliance_team_number)
        }
    }
}
