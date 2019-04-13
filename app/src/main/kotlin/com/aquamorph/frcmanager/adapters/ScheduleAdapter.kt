package com.aquamorph.frcmanager.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.Gravity
import android.view.LayoutInflater
import android.view.LayoutInflater.from
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TextView
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.activities.MatchSummaryActivity
import com.aquamorph.frcmanager.activities.TeamSummary
import com.aquamorph.frcmanager.models.Match
import com.aquamorph.frcmanager.utils.Constants
import java.text.SimpleDateFormat
import java.util.*

/**
 * Populates a RecyclerView with the schedule for a team.
 *
 * @author Christian Colglazier
 * @version 3/31/2018
 */
class ScheduleAdapter(private val context: Context, private val data: ArrayList<Match>,
                      private var team: String?) :
        RecyclerView.Adapter<ScheduleAdapter.MyViewHolder>() {

    private val inflater: LayoutInflater = from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = inflater.inflate(R.layout.match, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.matchKey = data[position].key
        holder.compLevel = data[position].comp_level
        holder.setNumber = data[position].set_number
        holder.matchNumberKey = data[position].match_number

        holder.matchNumber.text = Constants.matchString(data[position].comp_level,
                data[position].set_number, data[position].match_number)

        holder.redTeam1.text = parseTeamNumber(true, 0, position)
        holder.redTeam2.text = parseTeamNumber(true, 1, position)
        holder.redTeam3.text = parseTeamNumber(true, 2, position)
        holder.blueTeam1.text = parseTeamNumber(false, 0, position)
        holder.blueTeam2.text = parseTeamNumber(false, 1, position)
        holder.blueTeam3.text = parseTeamNumber(false, 2, position)

        // Underlines team number
        team = String.format("%4s", team)
        when (team) {
            parseTeamNumber(true, 0, position) ->
                holder.redTeam1.text = Constants.fromHtml(Constants.underlineText(team!!))
            parseTeamNumber(true, 1, position) ->
                holder.redTeam2.text = Constants.fromHtml(Constants.underlineText(team!!))
            parseTeamNumber(true, 2, position) ->
                holder.redTeam3.text = Constants.fromHtml(Constants.underlineText(team!!))
            parseTeamNumber(false, 0, position) ->
                holder.blueTeam1.text = Constants.fromHtml(Constants.underlineText(team!!))
            parseTeamNumber(false, 1, position) ->
                holder.blueTeam2.text = Constants.fromHtml(Constants.underlineText(team!!))
            parseTeamNumber(false, 2, position) ->
                holder.blueTeam3.text = Constants.fromHtml(Constants.underlineText(team!!))
        }

        //Bolds winning score
        when {
            data[position].alliances.red.score == data[position].alliances.blue.score &&
                    data[position].alliances.red.score != -1 &&
                    data[position].alliances.blue.score != -1-> {
                holder.redTeam1.setTypeface(null, Typeface.BOLD)
                holder.redTeam2.setTypeface(null, Typeface.BOLD)
                holder.redTeam3.setTypeface(null, Typeface.BOLD)
                holder.redScore.setTypeface(null, Typeface.BOLD)
                holder.blueTeam1.setTypeface(null, Typeface.BOLD)
                holder.blueTeam2.setTypeface(null, Typeface.BOLD)
                holder.blueTeam3.setTypeface(null, Typeface.BOLD)
                holder.blueScore.setTypeface(null, Typeface.BOLD)
            }
            data[position].alliances.red.score > data[position].alliances.blue.score -> {
                holder.redTeam1.setTypeface(null, Typeface.BOLD)
                holder.redTeam2.setTypeface(null, Typeface.BOLD)
                holder.redTeam3.setTypeface(null, Typeface.BOLD)
                holder.redScore.setTypeface(null, Typeface.BOLD)
                holder.blueTeam1.setTypeface(null, Typeface.NORMAL)
                holder.blueTeam2.setTypeface(null, Typeface.NORMAL)
                holder.blueTeam3.setTypeface(null, Typeface.NORMAL)
                holder.blueScore.setTypeface(null, Typeface.NORMAL)
            }
            data[position].alliances.red.score < data[position].alliances.blue.score -> {
                holder.redTeam1.setTypeface(null, Typeface.NORMAL)
                holder.redTeam2.setTypeface(null, Typeface.NORMAL)
                holder.redTeam3.setTypeface(null, Typeface.NORMAL)
                holder.redScore.setTypeface(null, Typeface.NORMAL)
                holder.blueTeam1.setTypeface(null, Typeface.BOLD)
                holder.blueTeam2.setTypeface(null, Typeface.BOLD)
                holder.blueTeam3.setTypeface(null, Typeface.BOLD)
                holder.blueScore.setTypeface(null, Typeface.BOLD)
            }
            else -> {
                holder.redTeam1.setTypeface(null, Typeface.NORMAL)
                holder.redTeam2.setTypeface(null, Typeface.NORMAL)
                holder.redTeam3.setTypeface(null, Typeface.NORMAL)
                holder.redScore.setTypeface(null, Typeface.NORMAL)
                holder.blueTeam1.setTypeface(null, Typeface.NORMAL)
                holder.blueTeam2.setTypeface(null, Typeface.NORMAL)
                holder.blueTeam3.setTypeface(null, Typeface.NORMAL)
                holder.blueScore.setTypeface(null, Typeface.NORMAL)
            }
        }

        if (data[position].alliances.red.score != -1) {
            holder.matchTime.visibility = View.GONE
            holder.scoreTable.visibility = View.VISIBLE
            holder.redScore.text = data[position].alliances.red.score.toString()
            holder.blueScore.text = data[position].alliances.blue.score.toString()
        } else {
            holder.matchTime.visibility = View.VISIBLE
            holder.scoreTable.visibility = View.GONE
            val time = Date()
            val df = SimpleDateFormat("hh:mm aa", Locale.ENGLISH)
            time.time = data[position].time * 1000
            holder.matchTime.text = df.format(time)
        }

        holder.redTeam1.gravity = Gravity.CENTER
        holder.redTeam2.gravity = Gravity.CENTER
        holder.redTeam3.gravity = Gravity.CENTER
        holder.blueTeam1.gravity = Gravity.CENTER
        holder.blueTeam2.gravity = Gravity.CENTER
        holder.blueTeam3.gravity = Gravity.CENTER
    }

    override fun getItemCount(): Int {
        return data.size
    }

    /**
     * parseTeamNumber returns a formatted team number.
     *
     * @param red      is the robot red or blue
     * @param robot    position of the team
     * @param position dataLoader position
     * @return team number
     */
    private fun parseTeamNumber(red: Boolean, robot: Int, position: Int): String {
        return if (red) {
            Constants.formatTeamNumber(data[position].alliances.red.team_keys[robot])
        } else {
            Constants.formatTeamNumber(data[position].alliances.blue.team_keys[robot])
        }
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal val matchNumber: TextView = itemView.findViewById(R.id.match_number)
        internal val redTeam1: TextView = itemView.findViewById(R.id.red_team_1)
        internal val redTeam2: TextView = itemView.findViewById(R.id.red_team_2)
        internal val redTeam3: TextView = itemView.findViewById(R.id.red_team_3)
        internal val blueTeam1: TextView = itemView.findViewById(R.id.blue_team_1)
        internal val blueTeam2: TextView = itemView.findViewById(R.id.blue_team_2)
        internal val blueTeam3: TextView = itemView.findViewById(R.id.blue_team_3)
        internal val matchTime: TextView = itemView.findViewById(R.id.match_time)
        internal val redScore: TextView = itemView.findViewById(R.id.red_score)
        internal val blueScore: TextView = itemView.findViewById(R.id.blue_score)
        internal val scoreTable: TableLayout = itemView.findViewById(R.id.score_table)
        var matchKey = ""
        var compLevel = ""
        var setNumber = 0
        var matchNumberKey = 0

        init {
            redTeam1.setOnClickListener {
                val intent = Intent(context, TeamSummary::class.java)
                intent.putExtra("teamNumber", redTeam1.text.toString())
                context.startActivity(intent)
            }
            redTeam2.setOnClickListener {
                val intent = Intent(context, TeamSummary::class.java)
                intent.putExtra("teamNumber", redTeam2.text.toString())
                context.startActivity(intent)
            }
            redTeam3.setOnClickListener {
                val intent = Intent(context, TeamSummary::class.java)
                intent.putExtra("teamNumber", redTeam3.text.toString())
                context.startActivity(intent)
            }
            blueTeam1.setOnClickListener {
                val intent = Intent(context, TeamSummary::class.java)
                intent.putExtra("teamNumber", blueTeam1.text.toString())
                context.startActivity(intent)
            }
            blueTeam2.setOnClickListener {
                val intent = Intent(context, TeamSummary::class.java)
                intent.putExtra("teamNumber", blueTeam2.text.toString())
                context.startActivity(intent)
            }
            blueTeam3.setOnClickListener {
                val intent = Intent(context, TeamSummary::class.java)
                intent.putExtra("teamNumber", blueTeam3.text.toString())
                context.startActivity(intent)
            }
            matchNumber.setOnClickListener {
                val intent = Intent(context, MatchSummaryActivity::class.java)
                intent.putExtra("matchKey", matchKey)
                intent.putExtra("compLevel", compLevel)
                intent.putExtra("setNumber", setNumber)
                intent.putExtra("matchNumber", matchNumberKey)
                context.startActivity(intent)
            }
        }
    }
}
