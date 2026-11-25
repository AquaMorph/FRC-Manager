package com.aquamorph.frcmanager.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.models.tba.Event
import com.aquamorph.frcmanager.utils.AppConfig
import com.aquamorph.frcmanager.utils.Logging
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.collections.ArrayList

class EventAdapter(private val context: Context?, private var events: ArrayList<Event>) :
        RecyclerView.Adapter<EventAdapter.SingleViewHolder>() {
    // if checkedPosition = 0, 1st item is selected by default
    private var checkedPosition = 0

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): SingleViewHolder {
        val view: View = LayoutInflater.from(context)
                .inflate(R.layout.item_event, viewGroup, false)
        return SingleViewHolder(view)
    }

    override fun onBindViewHolder(singleViewHolder: SingleViewHolder, position: Int) {
        singleViewHolder.bind(events[position])
    }

    override fun getItemCount(): Int {
        return events.size
    }

    inner class SingleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val eventNameView: TextView = itemView.findViewById(R.id.eventName)
        private val eventDateView: TextView = itemView.findViewById(R.id.eventDate)
        private val checkView: ImageView = itemView.findViewById(R.id.check)
        fun bind(event: Event) {
            if (checkedPosition == -1) {
                checkView.visibility = View.GONE
            } else {
                if (checkedPosition == absoluteAdapterPosition) {
                    checkView.visibility = View.VISIBLE
                    setEvent(event)
                } else {
                    checkView.visibility = View.GONE
                }
            }
            eventNameView.text = event.name
            eventDateView.text = formatEventDateRange(event.startDate, event.endDate)
            itemView.setOnClickListener {
                checkView.visibility = View.VISIBLE
                if (checkedPosition != absoluteAdapterPosition) {
                    notifyItemChanged(checkedPosition)
                    checkedPosition = absoluteAdapterPosition
                }
                setEvent(event)
            }
        }
    }

    /**
     * setEvent() sets event data in shared data store.
     *
     * @param event event to be tracked
     */
    fun setEvent(event: Event) {
        AppConfig.setEventKey(event.key, context!!)
        Logging.info(this, "Key:" + event.key, 0)
        Logging.info(this, "Short Name:" + event.shortName, 0)
        if (event.district != null) {
            AppConfig.setDistrictKey(event.district!!.key, context)
        } else {
            AppConfig.setDistrictKey("", context)
        }
        if (event.address != null) {
            AppConfig.setEventShortName(event.shortName, context)
        }
        if (event.address != null) {
            AppConfig.setEventAddress(event.address, context)
        }
    }
}

internal fun formatEventDateRange(startDate: String, endDate: String): String {
    // Event dates have no time zone; use UTC so the device's time zone cannot shift them.
    val utc = TimeZone.getTimeZone("UTC")
    fun formatter(pattern: String) = SimpleDateFormat(pattern, Locale.US).apply { timeZone = utc }

    val parser = formatter("yyyy-MM-dd")
    val start = parser.parse(startDate)!!
    val end = parser.parse(endDate)!!
    val startCalendar = Calendar.getInstance(utc, Locale.US).apply { time = start }
    val endCalendar = Calendar.getInstance(utc, Locale.US).apply { time = end }
    val fullDate = formatter("MMMM d yyyy")

    return when {
        start == end -> fullDate.format(start)
        startCalendar.get(Calendar.YEAR) != endCalendar.get(Calendar.YEAR) ->
            "${fullDate.format(start)} to ${fullDate.format(end)}"
        startCalendar.get(Calendar.MONTH) == endCalendar.get(Calendar.MONTH) ->
            "${formatter("MMMM d").format(start)} to ${formatter("d yyyy").format(end)}"
        else -> "${formatter("MMMM d").format(start)} to ${fullDate.format(end)}"
    }
}
