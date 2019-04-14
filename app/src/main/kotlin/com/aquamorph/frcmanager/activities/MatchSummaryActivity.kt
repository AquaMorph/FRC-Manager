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
            try {
                redRobot1 = extras.getString("redRobot1")!!
                redRobot2 = extras.getString("redRobot2")!!
                redRobot3 = extras.getString("redRobot3")!!
                blueRobot1 = extras.getString("blueRobot1")!!
                blueRobot2 = extras.getString("blueRobot2")!!
                blueRobot3 = extras.getString("blueRobot3")!!
            } catch (e :Exception) {

            }
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
        val fouls = findViewById<View>(R.id.fouls)
        fouls.findViewById<TextView>(R.id.text).text = "Fouls"
        val adjustments = findViewById<View>(R.id.adjustments)
        adjustments.findViewById<TextView>(R.id.text).text = "Adjustments"

        val rankPoints = findViewById<View>(R.id.rankPoints)
        rankPoints.findViewById<TextView>(R.id.text).text = "Rank Points"

        val sandstormTotal = findViewById<View>(R.id.sandstormTotal)
        sandstormTotal.findViewById<TextView>(R.id.text).text = "Sandstorm Total"
        val habLineRobot1 = findViewById<View>(R.id.habLineRobot1)
        habLineRobot1.findViewById<TextView>(R.id.text).text = "HabLine Robot 1"
        val habLineRobot2 = findViewById<View>(R.id.habLineRobot2)
        habLineRobot2.findViewById<TextView>(R.id.text).text = "HabLine Robot 2"
        val habLineRobot3 = findViewById<View>(R.id.habLineRobot3)
        habLineRobot3.findViewById<TextView>(R.id.text).text = "HabLine Robot 3"

        val teleTotal = findViewById<View>(R.id.teleTotal)
        teleTotal.findViewById<TextView>(R.id.text).text = "Teleop Total"

        val hatchTotal = findViewById<View>(R.id.hatchTotal)
        hatchTotal.findViewById<TextView>(R.id.text).text = "Hatch Panel Points"

        val cargoTotal = findViewById<View>(R.id.cargoTotal)
        cargoTotal.findViewById<TextView>(R.id.text).text = "Cargo Points"

        val habTotal = findViewById<View>(R.id.habTotal)
        habTotal.findViewById<TextView>(R.id.text).text = "Hab Climb Points"
        val habRobot1 = findViewById<View>(R.id.habRobot1)
        habRobot1.findViewById<TextView>(R.id.text).text = "Robot 1 Hab Climb"
        val habRobot2 = findViewById<View>(R.id.habRobot2)
        habRobot2.findViewById<TextView>(R.id.text).text = "Robot 2 Hab Climb"
        val habRobot3 = findViewById<View>(R.id.habRobot3)
        habRobot3.findViewById<TextView>(R.id.text).text = "Robot 3 Hab Climb"

        //val teamScheduleFragment = TeamScheduleFragment.newInstance()
        //teamScheduleFragment.setTeamNumber(teamNumber)
        //supportFragmentManager.beginTransaction().replace(R.id.content_frame, teamScheduleFragment).commit()
        MainActivity.theme(this)
        val call = RetrofitInstance.getRetrofit(this).create(TbaApi::class.java).getMatch2019(matchKey)

        call.enqueue(object : Callback<MatchScore2019> {
            override fun onResponse(call: Call<MatchScore2019>, response: Response<MatchScore2019>) {
                if(response.isSuccessful) {
                    try {
                        val match = response.body()!!.score_breakdown

                        if (compLevel != "qm") {
                            rankPoints.visibility = View.GONE
                        }
                        totalScore.findViewById<TextView>(R.id.redText).text = match.red.totalPoints.toString()
                        totalScore.findViewById<TextView>(R.id.blueText).text = match.blue.totalPoints.toString()

                        fouls.findViewById<TextView>(R.id.redText).text = match.red.foulPoints.toString()
                        fouls.findViewById<TextView>(R.id.blueText).text = match.blue.foulPoints.toString()

                        if (match.red.adjustPoints != 0 || match.blue.adjustPoints != 0) {
                            adjustments.visibility = View.VISIBLE
                            adjustments.findViewById<TextView>(R.id.redText).text = match.red.adjustPoints.toString()
                            adjustments.findViewById<TextView>(R.id.blueText).text = match.blue.adjustPoints.toString()
                        }

                        rankPoints.findViewById<TextView>(R.id.redText).text = match.red.rp.toString()
                        rankPoints.findViewById<TextView>(R.id.blueText).text = match.blue.rp.toString()

                        sandstormTotal.findViewById<TextView>(R.id.redText).text = match.red.autoPoints.toString()
                        sandstormTotal.findViewById<TextView>(R.id.blueText).text = match.blue.autoPoints.toString()
                        habLineRobot1.findViewById<TextView>(R.id.redText).text =
                                habScore(match.red.preMatchLevelRobot1, match.red.habLineRobot1)
                        habLineRobot1.findViewById<TextView>(R.id.blueText).text =
                                habScore(match.blue.preMatchLevelRobot1, match.blue.habLineRobot1)
                        habLineRobot2.findViewById<TextView>(R.id.redText).text =
                                habScore(match.red.preMatchLevelRobot2, match.red.habLineRobot2)
                        habLineRobot2.findViewById<TextView>(R.id.blueText).text =
                                habScore(match.blue.preMatchLevelRobot2, match.blue.habLineRobot2)
                        habLineRobot3.findViewById<TextView>(R.id.redText).text =
                                habScore(match.red.preMatchLevelRobot3, match.red.habLineRobot3)
                        habLineRobot3.findViewById<TextView>(R.id.blueText).text =
                                habScore(match.blue.preMatchLevelRobot3, match.blue.habLineRobot3)

                        teleTotal.findViewById<TextView>(R.id.redText).text = match.red.teleopPoints.toString()
                        teleTotal.findViewById<TextView>(R.id.blueText).text = match.blue.teleopPoints.toString()

                        hatchTotal.findViewById<TextView>(R.id.redText).text = match.red.hatchPanelPoints.toString()
                        hatchTotal.findViewById<TextView>(R.id.blueText).text = match.blue.hatchPanelPoints.toString()

                        cargoTotal.findViewById<TextView>(R.id.redText).text = match.red.cargoPoints.toString()
                        cargoTotal.findViewById<TextView>(R.id.blueText).text = match.blue.cargoPoints.toString()

                        habTotal.findViewById<TextView>(R.id.redText).text = match.red.habClimbPoints.toString()
                        habTotal.findViewById<TextView>(R.id.blueText).text = match.blue.habClimbPoints.toString()
                        habRobot1.findViewById<TextView>(R.id.redText).text = habClimb(match.red.endgameRobot1)
                        habRobot1.findViewById<TextView>(R.id.blueText).text = habClimb(match.blue.endgameRobot1)
                        habRobot2.findViewById<TextView>(R.id.redText).text = habClimb(match.red.endgameRobot2)
                        habRobot2.findViewById<TextView>(R.id.blueText).text = habClimb(match.blue.endgameRobot2)
                        habRobot3.findViewById<TextView>(R.id.redText).text = habClimb(match.red.endgameRobot3)
                        habRobot3.findViewById<TextView>(R.id.blueText).text = habClimb(match.blue.endgameRobot3)
                    } catch (e : UninitializedPropertyAccessException) {
                        totalScore.visibility = View.GONE
                        fouls.visibility = View.GONE
                        sandstormTotal.visibility = View.GONE
                        habLineRobot1.visibility = View.GONE
                        habLineRobot2.visibility = View.GONE
                        habLineRobot3.visibility = View.GONE
                        teleTotal.visibility = View.GONE
                        hatchTotal.visibility = View.GONE
                        cargoTotal.visibility = View.GONE
                        habTotal.visibility = View.GONE
                        habRobot1.visibility = View.GONE
                        habRobot2.visibility = View.GONE
                        habRobot3.visibility = View.GONE
                        rankPoints.visibility = View.GONE
                    }
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

    fun habScore(preMatchLevelRobot: String, habLineRobot: String) : String {
        return if(habLineRobot == "CrossedHabLineInSandstorm") {
            if (preMatchLevelRobot == "HabLevel1") {
                "3"
            } else {
                "6"
            }
        } else {
            "0"
        }
    }

    fun habClimb(endgameRobot: String) : String {
        return when (endgameRobot) {
            "HabLevel3" -> "12"
            "HabLevel2" -> "6"
            "HabLevel1" -> "3"
            else -> "0"
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> this.finish()
        }
        return super.onOptionsItemSelected(item)
    }
}