package com.aquamorph.frcmanager.fragments

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import com.aquamorph.frcmanager.R

class TeamScheduleFragment : ScheduleFragment(R.layout.fragment_team_schedule) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showSchedule(view, teamOnly = true, selectedTeam = arguments?.getString(TEAM_NUMBER))
    }

    companion object {
        private const val TEAM_NUMBER = "teamNumber"

        fun forTeam(teamNumber: String) = TeamScheduleFragment().apply {
            arguments = bundleOf(TEAM_NUMBER to teamNumber)
        }
    }
}
