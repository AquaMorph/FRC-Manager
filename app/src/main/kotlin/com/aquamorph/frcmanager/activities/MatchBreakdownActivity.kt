package com.aquamorph.frcmanager.activities

import android.annotation.SuppressLint
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
import com.aquamorph.frcmanager.network.DataLoader
import com.aquamorph.frcmanager.network.TBARetrofitInstance
import com.aquamorph.frcmanager.network.TbaApi
import com.aquamorph.frcmanager.utils.Constants
import com.aquamorph.frcmanager.utils.Logging
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

/**
 * Activity with a summary of the scoring of a match.
 *
 * @author Christian Colglazier
 * @version 3/28/2023
 */
class MatchBreakdownActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

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
        setContentView(R.layout.match_breakdown)

        val refresh = findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
        refresh.setOnRefreshListener(this)
        refresh.isRefreshing = true

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
            } catch (_: Exception) {
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

        refresh()
    }

    @SuppressLint("CheckResult")
    fun refresh() {
        val refresh = findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
        refresh.isRefreshing = true

        val table = findViewById<TableLayout>(R.id.scoreMatchTeams)
        val scoreBreakdown = findViewById<TableLayout>(R.id.scoreBreakdown)
        scoreBreakdown.visibility = View.GONE

        TBARetrofitInstance.getRetrofit(this).create(TbaApi::class.java)
            .getMatch(matchKey).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result -> if (result?.scoreBreakdown != null) {
                val red = result.scoreBreakdown.red
                val blue = result.scoreBreakdown.blue

                // Auto Score
                findViewById<TextView>(R.id.redAutoTotal).text = red.autoPoints.toString()
                findViewById<TextView>(R.id.blueAutoTotal).text = blue.autoPoints.toString()

                // Teleop Score
                findViewById<TextView>(R.id.redTeleopTotal).text = red.teleopPoints.toString()
                findViewById<TextView>(R.id.blueTeleopTotal).text = blue.teleopPoints.toString()

                // End Game
                findViewById<TextView>(R.id.redEndgameTotal).text =
                    red.endGameParkPoints.toString()
                findViewById<TextView>(R.id.blueEndgameTotal).text =
                    blue.endGameParkPoints.toString()

                // Total Score
                findViewById<TextView>(R.id.redTotalScore).text = red.totalPoints.toString()
                findViewById<TextView>(R.id.blueTotalScore).text = blue.totalPoints.toString()
                findViewById<TextView>(R.id.redFouls).text = red.foulPoints.toString()
                findViewById<TextView>(R.id.blueFouls).text = blue.foulPoints.toString()
                if (red.adjustPoints != 0 || blue.adjustPoints != 0) {
                    findViewById<TextView>(R.id.redAdjustments).text = red.adjustPoints.toString()
                    findViewById<TextView>(R.id.blueAdjustments).text = blue.adjustPoints.toString()
                } else {
                    findViewById<TableRow>(R.id.adjustments).visibility = View.GONE
                }
                if (compLevel != "qm") {
                    findViewById<TableRow>(R.id.rankPoints).visibility = View.GONE
                }

                // Rank Points
                findViewById<TextView>(R.id.redRankPoints).text = red.rp.toString()
                findViewById<TextView>(R.id.blueRankPoints).text = blue.rp.toString()

                if (DataLoader.year == "2023") {
                    findViewById<TextView>(R.id.autoTotal).text = "Auto Mobility"
                    findViewById<TextView>(R.id.redAutoTotal).text =
                        red.autoMobilityPoints.toString()
                    findViewById<TextView>(R.id.blueAutoTotal).text =
                        blue.autoMobilityPoints.toString()

                    findViewById<TextView>(R.id.teleopTotal).text = "Grid"
                    findViewById<TextView>(R.id.redTeleopTotal).text =
                        (red.linkPoints + red.teleopGamePiecePoints + red.autoGamePiecePoints)
                            .toString()
                    findViewById<TextView>(R.id.blueTeleopTotal).text =
                        (blue.linkPoints + blue.teleopGamePiecePoints + blue.autoGamePiecePoints)
                            .toString()

                    findViewById<TableRow>(R.id.teleopExtraTable1).visibility = View.VISIBLE
                    findViewById<TextView>(R.id.teleopExtra1).text = "Charge Station"
                    findViewById<TextView>(R.id.redTeleopExtra1).text =
                        red.totalChargeStationPoints.toString()
                    findViewById<TextView>(R.id.blueTeleopExtra1).text =
                        blue.totalChargeStationPoints.toString()

                    findViewById<TextView>(R.id.endgameTotal).text = "Endgame Park"
                    findViewById<TextView>(R.id.redEndgameTotal).text =
                        red.endGameParkPoints.toString()
                    findViewById<TextView>(R.id.blueEndgameTotal).text =
                        blue.endGameParkPoints.toString()
                }

                scoreBreakdown.visibility = View.VISIBLE
                refresh.isRefreshing = false
                refresh.isEnabled = false
            } else {
                refresh.isRefreshing = false
                refresh.isEnabled = true
            }
            }, {
                    error -> Logging.error(this, error.toString(), 0)
                refresh.isRefreshing = false
                refresh.isEnabled = false
            })
    }

    /**
     * setToString() adds set number to match string for playoff matches.
     *
     * @return set
     */
    private fun setToString(): String {
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

    override fun onRefresh() {
        refresh()
    }
}
