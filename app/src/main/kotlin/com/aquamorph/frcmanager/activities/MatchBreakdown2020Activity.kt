package com.aquamorph.frcmanager.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.decoration.Animations
import com.aquamorph.frcmanager.models.MatchScore2020
import com.aquamorph.frcmanager.network.RetrofitInstance
import com.aquamorph.frcmanager.network.TbaApi
import com.aquamorph.frcmanager.utils.Constants
import java.lang.Exception
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Activity with a summary of the scoring of a match.
 *
 * @author Christian Colglazier
 * @version 2/16/2020
 */
class MatchBreakdown2020Activity : AppCompatActivity() {

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

    fun lineExtendedToString(alliance: MatchScore2020.BreakDown2020): String {
        var robot = ""
        robot += if (alliance.initLineRobot1 == "Exited") {
            "✓"
        } else {
            "✖"
        }
        robot += if (alliance.initLineRobot2 == "Exited") {
            "✓"
        } else {
            "✖"
        }
        robot += if (alliance.initLineRobot3 == "Exited") {
            "✓"
        } else {
            "✖"
        }
        return "(%s) %d".format(robot, alliance.autoInitLinePoints)
    }

    fun autoPowerCellsToString(alliance: MatchScore2020.BreakDown2020): String {
        return "(▭%d⬡%d○%d) %d".format(alliance.autoCellsBottom,
                alliance.autoCellsOuter,
                alliance.autoCellsInner,
                alliance.autoCellPoints)
    }

    fun teleopPowerCellsToString(alliance: MatchScore2020.BreakDown2020): String {
        return "(▭%d⬡%d○%d) %d".format(alliance.teleopCellsBottom,
                alliance.teleopCellsOuter,
                alliance.teleopCellsInner,
                alliance.teleopCellPoints)
    }

    fun foulsToString(alliance: MatchScore2020.BreakDown2020): String {
        return "(%d/%d) %d".format(alliance.foulCount, alliance.techFoulCount, alliance.foulPoints)
    }

    fun stageActivationsToString(alliance: MatchScore2020.BreakDown2020): String {
        return when {
            alliance.stage3Activated -> "3"
            alliance.stage2Activated -> "2"
            alliance.stage1Activated -> "1"
            else -> "0"
        }
    }

    fun shieldGenToString(alliance: MatchScore2020.BreakDown2020): String {
        return when {
            alliance.shieldOperationalRankingPoint -> "✓"
            else -> "✖"
        }
    }

    fun switchLevelToString(alliance: MatchScore2020.BreakDown2020): String {
        return if (alliance.endgameRungIsLevel == "IsLevel") "✓"
        else "✖"
    }

    fun endgameToString(end: String): String {
        return when (end) {
            "Park" -> "(Park) 5"
            "Hang" -> "(Hang) 25"
            else -> "(✖) 0"
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivity.theme(this)
        setContentView(R.layout.match_2020)

        val refresh = findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
        refresh.isRefreshing = true

        val table = findViewById<TableLayout>(R.id.scoreMatch2020)
        table.visibility = View.GONE

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
            } catch (e: Exception) {
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

        // Set alliance robot numbers
        findViewById<TextView>(R.id.redAllianceOne).text = redRobot1
        findViewById<TextView>(R.id.blueAllianceOne).text = blueRobot1
        findViewById<TextView>(R.id.redAllianceTwo).text = redRobot2
        findViewById<TextView>(R.id.blueAllianceTwo).text = blueRobot2
        findViewById<TextView>(R.id.redAllianceThree).text = redRobot3
        findViewById<TextView>(R.id.blueAllianceThree).text = blueRobot3

        MainActivity.theme(this)
        val call = RetrofitInstance.getRetrofit(this)
                .create(TbaApi::class.java).getMatch2020(matchKey)

        call.enqueue(object : Callback<MatchScore2020> {
            override fun onResponse(
                call: Call<MatchScore2020>,
                response: Response<MatchScore2020>
            ) {
                if (response.isSuccessful) {
                    refresh.isRefreshing = false
                    refresh.isEnabled = false
                    try {
                        val match = response.body()!!.scoreBreakdown

                        if (compLevel != "qm") {
                            findViewById<TableRow>(R.id.rankPoints).visibility = View.GONE
                        }

                        if (match != null) {
                            // Auto
                            findViewById<TextView>(R.id.redAutoTotal).text =
                                    match.red.autoPoints.toString()
                            findViewById<TextView>(R.id.blueAutoTotal).text =
                                    match.blue.autoPoints.toString()

                            findViewById<TextView>(R.id.redLineExtended).text =
                                    lineExtendedToString(match.red)
                            findViewById<TextView>(R.id.blueLineExtended).text =
                                    lineExtendedToString(match.blue)

                            findViewById<TextView>(R.id.redAutoPowerCells).text =
                                    autoPowerCellsToString(match.red)
                            findViewById<TextView>(R.id.blueAutoPowerCells).text =
                                    autoPowerCellsToString(match.blue)

                            // Teleop
                            findViewById<TextView>(R.id.redTeleopTotal).text =
                                    match.red.teleopPoints.toString()
                            findViewById<TextView>(R.id.blueTeleopTotal).text =
                                    match.blue.teleopPoints.toString()

                            findViewById<TextView>(R.id.redTeleopPowerCells).text =
                                    teleopPowerCellsToString(match.red)
                            findViewById<TextView>(R.id.blueTeleopPowerCells).text =
                                    teleopPowerCellsToString(match.blue)

                            findViewById<TextView>(R.id.redControlPanelPoints).text =
                                    match.red.controlPanelPoints.toString()
                            findViewById<TextView>(R.id.blueControlPanelPoints).text =
                                    match.blue.controlPanelPoints.toString()

                            // Endgame
                            findViewById<TextView>(R.id.redEndgameTotal).text =
                                    match.red.endgamePoints.toString()
                            findViewById<TextView>(R.id.blueEndgameTotal).text =
                                    match.blue.endgamePoints.toString()

                            findViewById<TextView>(R.id.redEndgameOne).text =
                                    endgameToString(match.red.endgameRobot1)
                            findViewById<TextView>(R.id.blueEndgameOne).text =
                                    endgameToString(match.blue.endgameRobot1)

                            findViewById<TextView>(R.id.redEndgameTwo).text =
                                    endgameToString(match.red.endgameRobot2)
                            findViewById<TextView>(R.id.blueEndgameTwo).text =
                                    endgameToString(match.blue.endgameRobot2)

                            findViewById<TextView>(R.id.redEndgameThree).text =
                                    endgameToString(match.red.endgameRobot3)
                            findViewById<TextView>(R.id.blueEndgameThree).text =
                                    endgameToString(match.blue.endgameRobot3)

                            findViewById<TextView>(R.id.redShieldLevel).text =
                                    switchLevelToString(match.red)
                            findViewById<TextView>(R.id.blueShieldLevel).text =
                                    switchLevelToString(match.blue)

                            // Total
                            findViewById<TextView>(R.id.redTotalScore).text =
                                    match.red.totalPoints.toString()
                            findViewById<TextView>(R.id.blueTotalScore).text =
                                    match.blue.totalPoints.toString()

                            findViewById<TextView>(R.id.redStageActivations).text =
                                    stageActivationsToString(match.red)
                            findViewById<TextView>(R.id.blueStageActivations).text =
                                    stageActivationsToString(match.blue)

                            findViewById<TextView>(R.id.redShieldGenerator).text =
                                    shieldGenToString(match.red)
                            findViewById<TextView>(R.id.blueShieldGenerator).text =
                                    shieldGenToString(match.blue)

                            findViewById<TextView>(R.id.redFouls).text =
                                    foulsToString(match.red)
                            findViewById<TextView>(R.id.blueFouls).text =
                                    foulsToString(match.blue)

                            findViewById<TextView>(R.id.redRankPoints).text =
                                    match.red.rp.toString()
                            findViewById<TextView>(R.id.blueRankPoints).text =
                                    match.blue.rp.toString()

                            if (match.red.adjustPoints != 0 || match.blue.adjustPoints != 0) {
                                findViewById<TextView>(R.id.redAdjustments).text =
                                        match.red.adjustPoints.toString()
                                findViewById<TextView>(R.id.blueAdjustments).text =
                                        match.blue.adjustPoints.toString()
                            } else {
                                findViewById<TableRow>(R.id.adjustments).visibility = View.GONE
                            }
                        } else {
                            findViewById<TableLayout>(R.id.scoreBreakdown2020).visibility =
                                    View.GONE
                        }
                        table.visibility = View.VISIBLE
                        Animations.loadMatchBreakdownAnimation(baseContext, table)
                    } catch (e: UninitializedPropertyAccessException) {
                    }
                }
            }

            override fun onFailure(call: Call<MatchScore2020>, t: Throwable) {
                call.cancel()
                refresh.isRefreshing = false
                refresh.isEnabled = false
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
