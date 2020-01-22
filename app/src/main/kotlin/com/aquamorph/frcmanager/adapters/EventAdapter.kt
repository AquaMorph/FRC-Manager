package com.aquamorph.frcmanager.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.models.Event
import com.aquamorph.frcmanager.utils.AppConfig
import com.aquamorph.frcmanager.utils.Logging
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.collections.ArrayList

class EventAdapter(private val context: Context?, private var events: ArrayList<Event>) : RecyclerView.Adapter<EventAdapter.SingleViewHolder>() {
    // if checkedPosition = 0, 1st item is selected by default
    private var checkedPosition = 0

    @NonNull
    override fun onCreateViewHolder(@NonNull viewGroup: ViewGroup, i: Int): SingleViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_event, viewGroup, false)
        return SingleViewHolder(view)
    }

    override fun onBindViewHolder(@NonNull singleViewHolder: SingleViewHolder, position: Int) {
        singleViewHolder.bind(events[position])
    }

    override fun getItemCount(): Int {
        return events.size
    }

    inner class SingleViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val eventNameView: TextView = itemView.findViewById(R.id.eventName)
        private val eventDateView: TextView = itemView.findViewById(R.id.eventDate)
        private val checkView: ImageView = itemView.findViewById(R.id.check)
        fun bind(event: Event) {
            if (checkedPosition == -1) {
                checkView.visibility = View.GONE
            } else {
                if (checkedPosition == adapterPosition) {
                    checkView.visibility = View.VISIBLE
                    setEvent(event)
                } else {
                    checkView.visibility = View.GONE
                }
            }
            eventNameView.text = event.name
            eventDateView.text = eventDateToString(
                    stringToDate(event.start_date),
                    stringToDate(event.end_date))
            itemView.setOnClickListener {
                checkView.visibility = View.VISIBLE
                if (checkedPosition != adapterPosition) {
                    notifyItemChanged(checkedPosition)
                    checkedPosition = adapterPosition
                }
                setEvent(event)
            }
        }
    }

    fun setEvent(event: Event) {
        AppConfig.setEventKey(event.key, context!!)
        Logging.info(this, "Key:" + event.key, 0)
        Logging.info(this, "Short Name:" + event.short_name, 0)
        if (event.district != null) {
            AppConfig.setDistrictKey(event.district.key, context)
        } else {
            AppConfig.setDistrictKey("", context)
        }
        AppConfig.setEventShortName(event.short_name, context)
        AppConfig.setEventAddress(event.address, context)
    }

    private fun stringToDate(text: String): Date {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(text)
    }

    private fun eventDateToString(start: Date, end: Date): String {
        return if (start.month == end.month) {
            if (start.day == end.day) {
                SimpleDateFormat("MMMM d yyyy", Locale.US).format(start)
            } else {
                "%s to %s".format(SimpleDateFormat("MMMM d", Locale.US)
                        .format(start), SimpleDateFormat("d yyyy", Locale.US).format(end))
            }
        } else {
            "%s to %s".format(SimpleDateFormat("MMMM d", Locale.US)
                    .format(start), SimpleDateFormat("MMMM d yyyy", Locale.US).format(end))
        }
    }
}
