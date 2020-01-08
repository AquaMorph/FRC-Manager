package com.aquamorph.frcmanager.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.LayoutInflater.from
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.models.Award
import com.aquamorph.frcmanager.utils.Constants
import java.util.*

/**
 * Populates the recyclerview with award data
 *
 * @author Christian Colglazier
 * @version 4/2/2018
 */
class AwardAdapter(context: Context, private val data: ArrayList<Award>) :
        RecyclerView.Adapter<AwardAdapter.MyViewHolder>() {

    private val inflater: LayoutInflater = from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = inflater.inflate(R.layout.award, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        var team = ""
        var awardee = ""

        var i = 0
        while (i in 0..(data[position].recipient_list.size-1)) {
            if (data[position].recipient_list[i].team_key != null) {
                if (i > 0) team += "\n"
                team += Constants.formatTeamNumber(data[position].recipient_list[i].team_key)
            }
            if (data[position].recipient_list[i].awardee != null) {
                if (i > 0) awardee += "\n"
                awardee += data[position].recipient_list[i].awardee
            }
            i++
        }
        if (awardee == "") {
            holder.details.visibility = View.GONE
        } else {
            holder.details.text = awardee
        }
        holder.teamNumber.text = team
        holder.award.text = data[position].name
    }

    override fun onViewDetachedFromWindow(holder: MyViewHolder) {
        holder.itemView.clearAnimation()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var teamNumber: TextView = itemView.findViewById(R.id.team_number)
        internal var award: TextView = itemView.findViewById(R.id.award_name)
        internal var details: TextView = itemView.findViewById(R.id.award_details)
    }
}