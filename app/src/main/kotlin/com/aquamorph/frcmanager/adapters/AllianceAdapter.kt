package com.aquamorph.frcmanager.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.LayoutInflater.from
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.activities.TeamSummary
import com.aquamorph.frcmanager.models.tba.Alliance
import com.aquamorph.frcmanager.utils.Constants

/**
 * Populated a view with alliance dataLoader.
 *
 * @author Christian Colglazier
 * @version 4/2/2018
 */
class AllianceAdapter(private val context: Context, private val data: ArrayList<Alliance>) :
        RecyclerView.Adapter<AllianceAdapter.MyViewHolder>() {

    private val inflater: LayoutInflater = from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = inflater.inflate(R.layout.alliance, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.allianceNumber.text = (position + 1).toString()
        holder.teams.forEachIndexed { index, slot ->
            val number = data[position].picks.getOrNull(index)?.let(Constants::formatTeamNumber).orEmpty()
            slot.text = number
            slot.visibility = if (number.isEmpty()) View.INVISIBLE else View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal val teams = listOf(R.id.team_1, R.id.team_2, R.id.team_3, R.id.team_4)
            .map { itemView.findViewById<TextView>(it) }
        internal val allianceNumber: TextView = itemView.findViewById(R.id.alliance_number)

        init {
            teams.forEach { slot ->
                slot.setOnClickListener {
                    val intent = Intent(context, TeamSummary::class.java)
                    intent.putExtra("teamNumber", slot.text.toString())
                    context.startActivity(intent)
                }
            }
        }
    }
}
