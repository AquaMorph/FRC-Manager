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

class EventAdapter(private val context: Context?, private var events: ArrayList<Event>) : RecyclerView.Adapter<EventAdapter.SingleViewHolder>() {
    // if checkedPosition = 0, 1st item is selected by default
    private var checkedPosition = 0

    @NonNull
    override fun onCreateViewHolder(@NonNull viewGroup: ViewGroup, i: Int): SingleViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_employee, viewGroup, false)
        return SingleViewHolder(view)
    }

    override fun onBindViewHolder(@NonNull singleViewHolder: SingleViewHolder, position: Int) {
        singleViewHolder.bind(events[position])
    }

    override fun getItemCount(): Int {
        return events.size
    }

    inner class SingleViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.textView)
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        fun bind(event: Event) {
            if (checkedPosition == -1) {
                imageView.visibility = View.GONE
            } else {
                if (checkedPosition == adapterPosition) {
                    imageView.visibility = View.VISIBLE
                    setEvent(event)
                } else {
                    imageView.visibility = View.GONE
                }
            }
            textView.text = event.name
            itemView.setOnClickListener {
                imageView.visibility = View.VISIBLE
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
    }
}
