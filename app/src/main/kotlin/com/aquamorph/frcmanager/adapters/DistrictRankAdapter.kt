package com.aquamorph.frcmanager.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.support.v4.widget.TextViewCompat
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.LayoutInflater.from
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.activities.TeamSummary
import com.aquamorph.frcmanager.models.DistrictRank
import com.aquamorph.frcmanager.models.Team
import com.aquamorph.frcmanager.utils.Constants
import java.util.*

/**
 * Populates a RecyclerView with the ranks and team names and number for an event.
 *
 * @author Christian Colglazier
 * @version 12/30/2017
 */
class DistrictRankAdapter(private val context: Context, private val data: ArrayList<DistrictRank>,
                          private val teams: ArrayList<Team>) :
        RecyclerView.Adapter<DistrictRankAdapter.MyViewHolder>() {

    private val inflater: LayoutInflater = from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(inflater.inflate(R.layout.rank, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.teamNumber.text = String.format("%s. %s", position + 1,
                getTeamName(data[position].team_key))
        holder.teamNumber.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        holder.rankNumber.text = Constants.formatTeamNumber(data[position].team_key)
        holder.details.visibility = View.GONE
        holder.table.removeAllViews()

        val typedValue = TypedValue()
        val theme = context.theme
        theme.resolveAttribute(R.attr.textOnBackground, typedValue, true)


            val column1 = TextView(context)
            val column2 = TextView(context)
            val column3 = TextView(context)
            val column4 = TextView(context)
            val rowHeader = TableRow(context)

            column1.text = "Point Total: "
            column2.text = data[position].point_total.toString()
            column3.text = "Rookie Bonus: "
            column4.text = data[position].rookie_bonus.toString()


            if (Build.VERSION.SDK_INT >= 26) {
                TextViewCompat.setAutoSizeTextTypeWithDefaults(column1,
                        TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM)
                TextViewCompat.setAutoSizeTextTypeWithDefaults(column2,
                        TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM)
                TextViewCompat.setAutoSizeTextTypeWithDefaults(column3,
                        TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM)
                TextViewCompat.setAutoSizeTextTypeWithDefaults(column4,
                        TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM)
            }

            column1.setTextColor(typedValue.data)
            column2.setTypeface(null, Typeface.ITALIC)
            column2.setTextColor(typedValue.data)
            column3.setTextColor(typedValue.data)
            column4.setTypeface(null, Typeface.ITALIC)
            column4.setTextColor(typedValue.data)
            rowHeader.addView(column1)
            rowHeader.addView(column2)
            rowHeader.addView(column3)
            rowHeader.addView(column4)
            holder.table.addView(rowHeader)

    }

    override fun getItemCount(): Int {
        return if (data.size == 0) 0 else data.size
    }

    /**
     * getTeamName() returns the name of a team.
     *
     * @param number of the team
     * @return name of the team
     */
    private fun getTeamName(number: String): String {
        for (i in teams.indices) {
            if (number == teams[i].key) {
                return teams[i].nickname
            }
        }
        return ""
    }

    inner class MyViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        internal var teamNumber: TextView
        internal var rankNumber: TextView
        internal var details: TextView
        internal var table: TableLayout

        init {
            itemView.setOnClickListener(this)
            table = itemView.findViewById(R.id.table)
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

    inner class RankInfo internal constructor(internal var name: String, internal var value: String)
}
