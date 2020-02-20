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
import com.aquamorph.frcmanager.models.DistrictRank
import com.aquamorph.frcmanager.models.Team
import com.aquamorph.frcmanager.utils.Constants

/**
 * Populates a RecyclerView with the ranks and team names and number for a district.
 *
 * @author Christian Colglazier
 * @version 11/3/2018
 */
class DistrictRankAdapter(
    private val context: Context,
    private val data: ArrayList<DistrictRank>,
    private val teams: ArrayList<Team>
) :
        RecyclerView.Adapter<DistrictRankAdapter.MyViewHolder>() {

    private val inflater: LayoutInflater = from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(inflater.inflate(R.layout.rank, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.teamNumber.text = String.format("%s. %s", position + 1,
                getTeamName(data[position].teamKey))
        holder.teamNumber.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        holder.rankNumber.text = Constants.formatTeamNumber(data[position].teamKey)
        holder.details.visibility = View.GONE
        holder.table.removeAllViews()

        val typedValue = TypedValue()
        val theme = context.theme
        theme.resolveAttribute(R.attr.textOnBackground, typedValue, true)

        for (i in 1..3) {
            val column1 = TextView(context)
            val column2 = TextView(context)
            val column3 = TextView(context)
            val column4 = TextView(context)
            val rowHeader = TableRow(context)

            if (i == 1) {
                column1.text = context.getString(R.string.rankPointTotal)
                column2.text = data[position].pointTotal.toString()
                column3.text = context.getString(R.string.rank_rookie_bonus)
                column4.text = data[position].rookieBonus.toString()
            }
            if (i == 2 && data[position].eventPoints.isNotEmpty()) {
                column1.text = context.getString(R.string.rankEventOne)
                column2.text = data[position].eventPoints[0].total.toString()
                if (data[position].eventPoints.size >= 2) {
                    column3.text = context.getString(R.string.rankEvent2)
                    column4.text = data[position].eventPoints[1].total.toString()
                } else {
                    column3.text = ""
                    column4.text = ""
                }
            }
            if (i == 3 && data[position].eventPoints.size > 2) {
                column1.text = context.getString(R.string.rankDistricChamp)
                column2.text = data[position].eventPoints[2].total.toString()
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
            if ((i == 1 || data[position].eventPoints.size >= 3) ||
                    (i == 2 && data[position].eventPoints.size != 1 &&
                            data[position].eventPoints.isNotEmpty()) ||
                    (i == 3 && data[position].eventPoints.size > 2)) {
                holder.table.addView(rowHeader)
            }
        }
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

    inner class MyViewHolder internal constructor(itemView: View) :
            RecyclerView.ViewHolder(itemView),
            View.OnClickListener {

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
}
