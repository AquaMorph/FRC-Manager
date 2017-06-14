package com.aquamorph.frcmanager.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.LayoutInflater.from
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.models.Award
import java.util.*

/**
 * Populates the recyclerview with award data

 * @author Christian Colglazier
 * *
 * @version 3/29/2016
 */
class AwardAdapter(private val context: Context, private val data: ArrayList<Award>) : RecyclerView.Adapter<AwardAdapter.MyViewHolder>() {

    private val TAG = "AwardAdapter"
    private val inflater: LayoutInflater
    private var team = ""
    private var awardee = ""
    private var view: View? = null

    init {
        inflater = from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        view = inflater.inflate(R.layout.award, parent, false)
        return MyViewHolder((view as View?)!!)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        team = ""
        awardee = ""

        var i = 0
        while (data[position].recipient_list!!.size > i) {
            if (data[position].recipient_list!![i].team_number != null) {
                if (i > 0) team += "\n"
                team += data[position].recipient_list!![i].team_number
            }
            if (data[position].recipient_list!![i].awardee != null) {
                if (i > 0) awardee += "\n"
                awardee += data[position].recipient_list!![i].awardee
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


    override fun onViewDetachedFromWindow(holder: MyViewHolder?) {
        holder!!.itemView.clearAnimation()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var teamNumber: TextView
        internal var award: TextView
        internal var details: TextView

        init {
            teamNumber = itemView.findViewById(R.id.team_number) as TextView
            award = itemView.findViewById(R.id.award_name) as TextView
            details = itemView.findViewById(R.id.award_details) as TextView
        }
    }
}
