package com.aquamorph.frcmanager.adapters

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.SpinnerAdapter
import android.widget.TextView
import com.aquamorph.frcmanager.models.Event
import java.util.*

/**
 * Populates the spinner for the setup screen with a list of events a team is registered for.
 *
 * @author Christian Colglazier
 * @version 3/31/2018
 */
class EventSpinnerAdapter(private val eventList: ArrayList<Event>, private val activity: Activity) : BaseAdapter(), SpinnerAdapter {

    override fun getCount(): Int {
        return eventList.size
    }

    override fun getItem(position: Int): Any {
        return eventList[position].short_name
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val text: TextView
        if (convertView != null) {
            // Re-use the recycled view here!
            text = (convertView as TextView?)!!
        } else {
            // No recycled view, inflate the "original" from the platform:
            text = activity.layoutInflater.inflate(
                    android.R.layout.simple_dropdown_item_1line, parent, false
            ) as TextView
        }
        text.text = eventList[position].name
        return text
    }


}

