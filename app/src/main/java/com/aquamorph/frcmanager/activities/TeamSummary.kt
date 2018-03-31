package com.aquamorph.frcmanager.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem

import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.fragments.TeamScheduleFragment

/**
 * Activiy with a summary of the schedule for a given team number.
 *
 * @author Christian Colglazier
 * @version 3/31/2018
 */
class TeamSummary : AppCompatActivity() {

    private var teamNumber = ""

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        val extras = intent.extras
        if (extras != null) {
            teamNumber = extras.getString("teamNumber")!!.replace("[^\\d.]".toRegex(), "")
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        if (toolbar != null) {
            toolbar.title = teamNumber
        }
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
        val teamScheduleFragment = TeamScheduleFragment.newInstance()
        teamScheduleFragment.setTeamNumber(teamNumber)
        supportFragmentManager.beginTransaction().replace(R.id.content_frame, teamScheduleFragment).commit()
        MainActivity.theme(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> this.finish()
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }
}