package com.aquamorph.frcmanager.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.LayoutInflater.from
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.activities.TeamSummary
import com.aquamorph.frcmanager.models.tba.Rank
import com.aquamorph.frcmanager.models.tba.Team

/**
 * Populates a RecyclerView with teams at an event.
 *
 * @author Christian Colglazier
 * @version 3/31/2018
 */
class TeamAdapter(
    private val context: Context,
    private val data: ArrayList<Team>,
    private val ranks: ArrayList<Rank>
) :
        RecyclerView.Adapter<TeamAdapter.MyViewHolder>() {

    private val inflater: LayoutInflater = from(context)

    /**
     * getTeamRank() returns team rank as a readable text.
     *
     * @param teamNumber team key
     * @param ranks list of team rankings
     * @return team tank text
     */
    private fun getTeamRank(teamNumber: String, ranks: ArrayList<Rank>?): String {
        if (ranks != null && ranks.size > 0) {
            if (ranks[0].rankings != null && ranks[0].rankings!!.isNotEmpty()) {
                for (i in 0 until ranks[0].rankings!!.size) {
                    if (ranks[0].rankings!![i].teamKey == teamNumber)
                        return " Ranked #" + ranks[0].rankings!![i].rank
                }
            }
        }
        return ""
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = inflater.inflate(R.layout.rank, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val spannableStringBuilder = SpannableStringBuilder()
        val teamName = SpannableString(data[position].nickname)
        teamName.setSpan(StyleSpan(Typeface.BOLD), 0, teamName.length, 0)
        spannableStringBuilder.append(teamName)
        val rank = SpannableString(getTeamRank(data[position].key, ranks))
        rank.setSpan(RelativeSizeSpan(0.75f), 0, rank.length, 0)
        rank.setSpan(StyleSpan(Typeface.ITALIC), 0, rank.length, 0)
        spannableStringBuilder.append(rank)

        holder.rankNumber.text = data[position].teamNumber.toString()
        holder.teamNumber.setText(spannableStringBuilder, TextView.BufferType.SPANNABLE)
        holder.details.text = String.format("%s, %s", data[position].city, data[position].stateProv)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class MyViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView),
            View.OnClickListener {

        internal var teamNumber: TextView
        internal var rankNumber: TextView
        internal var details: TextView

        init {
            itemView.setOnClickListener(this)
            teamNumber = itemView.findViewById(R.id.team_number)
            rankNumber = itemView.findViewById(R.id.rank)
            details = itemView.findViewById(R.id.details)
        }

        override fun onClick(v: View) {
            val intent = Intent(context, TeamSummary::class.java)
            intent.putExtra("teamNumber", rankNumber.text.toString())
            context.startActivity(intent)
        }
    }
}
