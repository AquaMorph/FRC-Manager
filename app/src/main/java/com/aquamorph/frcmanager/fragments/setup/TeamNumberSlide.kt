package com.aquamorph.frcmanager.fragments.setup

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView

import com.aquamorph.frcmanager.BuildConfig
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.utils.AppConfig
import com.aquamorph.frcmanager.utils.Logging

/**
 * Fragment slide that asks the user their team number.
 *
 * @author Christian Colglazier
 * @version 3/31/2018
 */
class TeamNumberSlide : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var yearSpinnder: Spinner
    internal lateinit var teamNumber: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.team_number_slide, container, false)
        teamNumber = view.findViewById(R.id.teamNumberEditText)
        yearSpinnder = view.findViewById(R.id.year_spinner)
        yearSpinnder.onItemSelectedListener = this
        if (BuildConfig.APP_DEBUG) yearSpinnder.visibility = View.VISIBLE
        return view
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        try {
            (yearSpinnder.selectedView as TextView).setTextColor(resources.getColor(R.color.icons))
            AppConfig.setYear(parent.getItemAtPosition(position).toString(), context!!)
        } catch (e: Exception) {
            Logging.error(this, e.message!!, 0)
        }
    }

    fun getTeamNumber(): String {
        return teamNumber.text.toString()
    }

    fun setTeamNumber() {
        AppConfig.setTeamNumber(getTeamNumber(), context!!)
    }

    override fun onNothingSelected(parent: AdapterView<*>) {}
}
