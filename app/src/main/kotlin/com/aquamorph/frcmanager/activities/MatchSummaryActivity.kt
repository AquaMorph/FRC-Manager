package com.aquamorph.frcmanager.activities

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.fragments.TeamScheduleFragment
import com.aquamorph.frcmanager.models.MatchScore2019
import com.aquamorph.frcmanager.network.RetrofitInstance
import com.aquamorph.frcmanager.network.TbaApi
import com.aquamorph.frcmanager.utils.Constants
import com.aquamorph.frcmanager.utils.Logging
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * Activity with a summary of the scoring of a match.
 *
 * @author Christian Colglazier
 * @version 3/13/2019
 */
class MatchSummaryActivity : AppCompatActivity() {

    private var matchKey = ""
    private lateinit var matchData: MatchScore2019

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

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
        //val teamScheduleFragment = TeamScheduleFragment.newInstance()
        //teamScheduleFragment.setTeamNumber(teamNumber)
        //supportFragmentManager.beginTransaction().replace(R.id.content_frame, teamScheduleFragment).commit()
        MainActivity.theme(this)
        load(this)
    }

    /**
     * load() loads the team events
     */
    @SuppressLint("CheckResult")
    fun load(context: Context) {
        RetrofitInstance.getRetrofit(context).create(TbaApi::class.java)
                .getMatch2019(matchKey).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result -> if (result != null) {
                    matchData = result
                }}, { error -> Logging.error(this, error.toString(), 0) })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> this.finish()
        }
        return super.onOptionsItemSelected(item)
    }
}