package com.aquamorph.frcmanager.fragments

import android.os.Bundle
import android.view.View
import com.aquamorph.frcmanager.R

class EventScheduleFragment : ScheduleFragment(R.layout.fragment_fastscroll) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showSchedule(view)
    }
}
