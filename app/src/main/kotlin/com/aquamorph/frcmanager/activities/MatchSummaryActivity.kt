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
import com.aquamorph.frcmanager.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

/**
 * Activity with a summary of the scoring of a match.
 *
 * @author Christian Colglazier
 * @version 3/13/2019
 */
class MatchSummaryActivity : AppCompatActivity() {

    private var matchKey = ""
    private var compLevel = ""
    private var setNumber = 0
    private var matchNumber = 0
    private var redRobot1 = ""
    private var redRobot2 = ""
    private var redRobot3 = ""
    private var blueRobot1 = ""
    private var blueRobot2 = ""
    private var blueRobot3 = ""

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivity.theme(this)
        setContentView(R.layout.match_2019)

        val extras = intent.extras
        if (extras != null) {
            matchKey = extras.getString("matchKey")!!
            compLevel = extras.getString("compLevel")!!
            setNumber = extras.getInt("setNumber")
            matchNumber = extras.getInt("matchNumber")
            redRobot1 = extras.getString("redRobot1")!!
            redRobot2 = extras.getString("redRobot2")!!
            redRobot3 = extras.getString("redRobot3")!!
            blueRobot1 = extras.getString("blueRobot1")!!
            blueRobot2 = extras.getString("blueRobot2")!!
            blueRobot3 = extras.getString("blueRobot3")!!
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        if (toolbar != null) {
            toolbar.title = Constants.matchTypeToString(compLevel) +
                    setToString() + " Match $matchNumber"
        }
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
        val team1 = findViewById<View>(R.id.team1)
        team1.findViewById<TextView>(R.id.text).text = "Team 1"
        val team2 = findViewById<View>(R.id.team2)
        team2.findViewById<TextView>(R.id.text).text = "Team 2"
        val team3 = findViewById<View>(R.id.team3)
        team3.findViewById<TextView>(R.id.text).text = "Team 3"

        team1.findViewById<TextView>(R.id.redText).text = redRobot1
        team1.findViewById<TextView>(R.id.blueText).text = blueRobot1
        team2.findViewById<TextView>(R.id.redText).text = redRobot2
        team2.findViewById<TextView>(R.id.blueText).text = blueRobot2
        team3.findViewById<TextView>(R.id.redText).text = redRobot3
        team3.findViewById<TextView>(R.id.blueText).text = blueRobot3

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

    fun setToString(): String {
        return if (compLevel != "qm") {
            " $setNumber"
        } else {
            ""
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> this.finish()
        }
        return super.onOptionsItemSelected(item)
    }
}