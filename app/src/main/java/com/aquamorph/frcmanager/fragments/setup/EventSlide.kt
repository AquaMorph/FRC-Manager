package com.aquamorph.frcmanager.fragments.setup

import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.os.SystemClock
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.adapters.EventSpinnerAdapter
import com.aquamorph.frcmanager.models.Event
import com.aquamorph.frcmanager.network.Parser
import com.aquamorph.frcmanager.utils.AppConfig
import com.aquamorph.frcmanager.utils.Constants
import com.aquamorph.frcmanager.utils.Logging
import com.google.gson.reflect.TypeToken
import java.util.*
import java.util.Collections.sort

/**
 * Loads events a team is signed up for and allows for the selection of that event.
 *
 * @author Christian Colglazier
 * @version 3/31/2018
 */
class EventSlide : Fragment() {

    internal lateinit var eventSpinnder: Spinner
    private lateinit var dataAdapter: EventSpinnerAdapter
    internal var eventList = ArrayList<Event>()
    private var teamNumber: String? = null
    private var year: String? = null
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.event_slide, container, false)

        eventSpinnder = view.findViewById(R.id.event_spinner)
        dataAdapter = EventSpinnerAdapter(eventList, activity!!)
        eventSpinnder.adapter = dataAdapter
        eventSpinnder.onItemSelectedListener = EventSpinnerListener()
        return view
    }

    /**
     * load() loads the team events
     */
    fun load(force: Boolean) {
        val loadTeamEvents = LoadTeamEvents(force)
        loadTeamEvents.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    internal inner class LoadTeamEvents(var force: Boolean) : AsyncTask<Void?, Void?, Void?>() {

        override fun onPreExecute() {
            teamNumber = prefs.getString("teamNumber", "")
            year = prefs.getString("year", "")
            Logging.info(this, "Team Number: " + teamNumber!!, 0)
        }

        override fun doInBackground(vararg params: Void?): Void? {
            val url = Constants.getEventURL("frc" + teamNumber!!, year!!)
            Logging.info(this, "Loading: $url", 0)
            val parser = Parser<ArrayList<Event>>("Event", url, object :
                    TypeToken<ArrayList<Event>>() {}.type, activity!!, force)
            try {
                parser.fetchJSON(false, false)
                while (parser.parsingComplete) {
                    SystemClock.sleep(Constants.THREAD_WAIT_TIME.toLong())
                }
                if (parser.data != null) {
                    eventList.clear()
                    eventList.addAll(parser.data!!)
                    sort(eventList)
                }
                Logging.info(this, "Event size: " + eventList.size, 0)
            } catch (e: Exception) {
                Logging.error(this, e.message!!, 0)
            }

            return null
        }

        override fun onPostExecute(result: Void?) {
            dataAdapter!!.notifyDataSetChanged()
        }
    }

    private inner class EventSpinnerListener : AdapterView.OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            AppConfig.setEventKey(eventList[position].key, context!!)
            Logging.info(this, "Key:" + eventList[position].key, 0)
            Logging.info(this, "Short Name:" + eventList[position].short_name, 0)
            AppConfig.setEventShortName(eventList[position].short_name, context!!)
            (eventSpinnder.selectedView as TextView).setTextColor(resources
                    .getColor(R.color.icons))
        }

        override fun onNothingSelected(parent: AdapterView<*>) {}
    }
}
