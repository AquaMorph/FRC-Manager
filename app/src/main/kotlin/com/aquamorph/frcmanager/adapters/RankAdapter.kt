package com.aquamorph.frcmanager.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.LayoutInflater.from
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.activities.TeamSummary
import com.aquamorph.frcmanager.models.Rank
import com.aquamorph.frcmanager.models.Team
import com.aquamorph.frcmanager.utils.Constants

/**
 * Populates a RecyclerView with the ranks and team names and number for an event.
 *
 * @author Christian Colglazier
 * @version 12/30/2017
 */
class RankAdapter(
    private val context: Context,
    private val data: ArrayList<Rank>,
    private val teams: ArrayList<Team>
) :
        RecyclerView.Adapter<RankAdapter.MyViewHolder>() {

    private val inflater: LayoutInflater = from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(inflater.inflate(R.layout.rank, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.teamNumber.text = String.format("%s. %s", position + 1,
                getTeamName(data[0].rankings[position]!!.teamKey))
        holder.teamNumber.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        holder.rankNumber.text = Constants.formatTeamNumber(data[0].rankings[position]!!.teamKey)
        holder.details.visibility = View.GONE
        holder.table.removeAllViews()

        val typedValue = TypedValue()
        val theme = context.theme
        theme.resolveAttribute(R.attr.textOnBackground, typedValue, true)

        val ranks = ArrayList<RankInfo>()
        for (i in 0 until data[0].sortOrderInfo.size) {
            ranks.add(RankInfo(String.format("%s: ", data[0].sortOrderInfo[i]!!.name),
                    String.format("%." + data[0].sortOrderInfo[i]!!.precision +
                            "f", data[0].rankings[position]!!.sortOrders!![i])))
        }
        ranks.add(RankInfo("Record: ",
                Rank.recordToString(data[0].rankings[position]!!.record)))

        var i = 0
        while (i < ranks.size) {
            val column1 = TextView(context)
            val column2 = TextView(context)
            val column3 = TextView(context)
            val column4 = TextView(context)
            val rowHeader = TableRow(context)

            column1.text = ranks[i].name
            column2.text = ranks[i].value

            if (i + 1 < ranks.size) {
                column3.text = ranks[i + 1].name
                column4.text = ranks[i + 1].value
            } else {
                column3.text = ""
                column4.text = ""
            }

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
            i += 2
        }
    }

    override fun getItemCount(): Int {
        return if (data.size == 0) 0 else data[0].rankings.size
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
