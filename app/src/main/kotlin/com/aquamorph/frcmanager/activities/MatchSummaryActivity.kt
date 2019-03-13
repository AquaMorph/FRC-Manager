package com.aquamorph.frcmanager.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.models.MatchScore2019
import com.aquamorph.frcmanager.network.RetrofitInstance
import com.aquamorph.frcmanager.network.TbaApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Activity with a summary of the scoring of a match.
 *
 * @author Christian Colglazier
 * @version 3/13/2019
 */
class MatchSummaryActivity : AppCompatActivity() {

    private var matchKey = ""

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivity.theme(this)
        setContentView(R.layout.match_2019)

        val extras = intent.extras
        if (extras != null) {
            matchKey = extras.getString("matchKey")!!
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        if (toolbar != null) {
            toolbar.title = "Match Summary $matchKey"
        }
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        val totalScore = findViewById<View>(R.id.totalScore)
        totalScore.findViewById<TextView>(R.id.text).text = "Total Score"
        val rankPoints = findViewById<View>(R.id.rankPoints)
        rankPoints.findViewById<TextView>(R.id.text).text = "Rank Points"

        val sandstormTotal = findViewById<View>(R.id.sandstormTotal)
        sandstormTotal.findViewById<TextView>(R.id.text).text = "Sandstorm Total"

        val hatchTotal = findViewById<View>(R.id.hatchTotal)
        hatchTotal.findViewById<TextView>(R.id.text).text = "Hatch Panel Points"

        val cargoTotal = findViewById<View>(R.id.cargoTotal)
        cargoTotal.findViewById<TextView>(R.id.text).text = "Cargo Points"

        val habTotal = findViewById<View>(R.id.habTotal)
        habTotal.findViewById<TextView>(R.id.text).text = "Hab Climb Points"

        //val teamScheduleFragment = TeamScheduleFragment.newInstance()
        //teamScheduleFragment.setTeamNumber(teamNumber)
        //supportFragmentManager.beginTransaction().replace(R.id.content_frame, teamScheduleFragment).commit()
        MainActivity.theme(this)
        val call = RetrofitInstance.getRetrofit(this).create(TbaApi::class.java).getMatch2019(matchKey)

        call.enqueue(object : Callback<MatchScore2019> {
            override fun onResponse(call: Call<MatchScore2019>, response: Response<MatchScore2019>) {
                if(response.isSuccessful) {
                    val match = response.body()!!.score_breakdown

                    totalScore.findViewById<TextView>(R.id.redText).text = match.red.totalPoints.toString()
                    totalScore.findViewById<TextView>(R.id.blueText).text = match.blue.totalPoints.toString()

                    rankPoints.findViewById<TextView>(R.id.redText).text = match.red.rp.toString()
                    rankPoints.findViewById<TextView>(R.id.blueText).text = match.blue.rp.toString()

                    sandstormTotal.findViewById<TextView>(R.id.redText).text = match.red.autoPoints.toString()
                    sandstormTotal.findViewById<TextView>(R.id.blueText).text = match.blue.autoPoints.toString()

                    hatchTotal.findViewById<TextView>(R.id.redText).text = match.red.hatchPanelPoints.toString()
                    hatchTotal.findViewById<TextView>(R.id.blueText).text = match.blue.hatchPanelPoints.toString()

                    cargoTotal.findViewById<TextView>(R.id.redText).text = match.red.cargoPoints.toString()
                    cargoTotal.findViewById<TextView>(R.id.blueText).text = match.blue.cargoPoints.toString()

                    habTotal.findViewById<TextView>(R.id.redText).text = match.red.habClimbPoints.toString()
                    habTotal.findViewById<TextView>(R.id.blueText).text = match.blue.habClimbPoints.toString()
                }
            }

            override fun onFailure(call: Call<MatchScore2019>, t: Throwable) {
                call.cancel()
            }
        })
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> this.finish()
        }
        return super.onOptionsItemSelected(item)
    }
}