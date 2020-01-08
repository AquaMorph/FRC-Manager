package com.aquamorph.frcmanager.adapters

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.LayoutInflater.from
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.activities.TeamSummary
import com.aquamorph.frcmanager.models.Alliance
import com.aquamorph.frcmanager.utils.Constants
import java.util.*

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
        holder.allianceNumber.text = String.format((position + 1).toString())
        holder.team1.text = Constants.formatTeamNumber(data[position].picks[0])
        holder.team2.text = Constants.formatTeamNumber(data[position].picks[1])
        holder.team3.text = Constants.formatTeamNumber(data[position].picks[2])
        if (data[position].picks.size > 3) {
            holder.team4.text = Constants.formatTeamNumber(data[position].picks[3])
        } else {
            holder.team4.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var team1: TextView = itemView.findViewById(R.id.team_1)
        internal var team2: TextView = itemView.findViewById(R.id.team_2)
        internal var team3: TextView = itemView.findViewById(R.id.team_3)
        internal var team4: TextView = itemView.findViewById(R.id.team_4)
        internal var allianceNumber: TextView = itemView.findViewById(R.id.alliance_number)

        init {
            team1.setOnClickListener {
                val intent = Intent(context, TeamSummary::class.java)
                intent.putExtra("teamNumber", team1.text.toString())
                context.startActivity(intent)
            }
            team2.setOnClickListener {
                val intent = Intent(context, TeamSummary::class.java)
                intent.putExtra("teamNumber", team2.text.toString())
                context.startActivity(intent)
            }
            team3.setOnClickListener {
                val intent = Intent(context, TeamSummary::class.java)
                intent.putExtra("teamNumber", team3.text.toString())
                context.startActivity(intent)
            }
            team4.setOnClickListener {
                val intent = Intent(context, TeamSummary::class.java)
                intent.putExtra("teamNumber", team4.text.toString())
                context.startActivity(intent)
            }
        }
    }
}
