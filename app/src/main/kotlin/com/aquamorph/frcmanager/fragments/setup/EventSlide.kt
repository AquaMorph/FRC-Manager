package com.aquamorph.frcmanager.fragments.setup

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.adapters.EventSpinnerAdapter
import com.aquamorph.frcmanager.models.Event
import com.aquamorph.frcmanager.network.RetrofitInstance
import com.aquamorph.frcmanager.network.TbaApi
import com.aquamorph.frcmanager.utils.AppConfig
import com.aquamorph.frcmanager.utils.Logging
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.Collections.sort

/**
 * Loads events a team is signed up for and allows for the selection of that event.
 *
 * @author Christian Colglazier
 * @version 8/20/2018
 */
class EventSlide : Fragment() {

    internal lateinit var eventSpinnder: Spinner
    private lateinit var dataAdapter: EventSpinnerAdapter
    internal var eventList = ArrayList<Event>()
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.event_slide, container, false)

        eventSpinnder = view.findViewById(R.id.event_spinner)
        dataAdapter = EventSpinnerAdapter(eventList, activity as Activity)
        eventSpinnder.adapter = dataAdapter
        eventSpinnder.onItemSelectedListener = EventSpinnerListener()
        return view
    }

    /**
     * load() loads the team events
     */
    fun load() {
        RetrofitInstance.getRetrofit(context!!).create(TbaApi::class.java)
                .getTeamEvents("frc${prefs.getString("teamNumber", "")}",
                        prefs.getString("year", "")!!).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result -> if (result != null) {
                    eventList.clear()
                    eventList.addAll(result)
                    sort(eventList)
                    dataAdapter.notifyDataSetChanged()
                }},
                        { error -> Logging.error(this, error.toString(), 0) })
    }

    private inner class EventSpinnerListener : AdapterView.OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            AppConfig.setEventKey(eventList[position].key, context!!)
            Logging.info(this, "Key:" + eventList[position].key, 0)
            Logging.info(this, "Short Name:" + eventList[position].short_name, 0)
            if (eventList[position].district != null) {
                AppConfig.setDistictKey(eventList[position].district.key, context!!)
            } else {
                AppConfig.setDistictKey("", context!!)
            }
            AppConfig.setEventShortName(eventList[position].short_name, context!!)
            (eventSpinnder.selectedView as TextView).setTextColor(resources
                    .getColor(R.color.icons))
        }

        override fun onNothingSelected(parent: AdapterView<*>) {}
    }
}
