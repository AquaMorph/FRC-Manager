package com.aquamorph.frcmanager.fragments.setup

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.adapters.EventAdapter
import com.aquamorph.frcmanager.models.Event
import com.aquamorph.frcmanager.network.RetrofitInstance
import com.aquamorph.frcmanager.network.TbaApi
import com.aquamorph.frcmanager.utils.Logging
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.Collections.sort

/**
 * Loads events a team is signed up for and allows for the selection of that event.
 *
 * @author Christian Colglazier
 * @version 1/19/2020
 */
class EventSlide : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapter
    private var eventList = ArrayList<Event>()
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.event_slide, container, false)
        recyclerView = view.findViewById(R.id.eventRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        eventAdapter = EventAdapter(context, eventList)
        recyclerView.adapter = eventAdapter
        return view
    }

    /**
     * load() loads the team events
     */
    @SuppressLint("CheckResult")
    fun load() {
        RetrofitInstance.getRetrofit(context!!).create(TbaApi::class.java)
                .getTeamEvents("frc${prefs.getString("teamNumber", "")}",
                        prefs.getString("year", "")!!).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result -> if (result != null) {
                    eventList.clear()
                    eventList.addAll(result)
                    sort(eventList)
                    eventAdapter.notifyDataSetChanged()
                } },
                        { error -> Logging.error(this, error.toString(), 0) })
    }
}
