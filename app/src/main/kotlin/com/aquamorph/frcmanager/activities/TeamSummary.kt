package com.aquamorph.frcmanager.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.preference.PreferenceManager
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.fragments.TeamScheduleFragment
import com.aquamorph.frcmanager.models.tba.Rank
import com.aquamorph.frcmanager.network.EventViewModel
import com.aquamorph.frcmanager.network.eventDisplay
import com.aquamorph.frcmanager.network.eventSelection
import kotlinx.coroutines.launch

class TeamSummary : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        MainActivity.theme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        val team = intent.getStringExtra("teamNumber").orEmpty().filter { it in '0'..'9' }
        setSupportActionBar(findViewById<Toolbar>(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = team

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val model = ViewModelProvider(this, EventViewModel.factory(applicationContext))[EventViewModel::class.java]
        model.configure(prefs.eventSelection(), prefs.eventDisplay())

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.state.collect { state ->
                    val key = "frc$team"
                    val nickname = state.teams.items.firstOrNull { it.key == key }?.nickname
                        ?: state.districtTeams.items.firstOrNull { it.key == key }?.nickname
                    val rank = state.rankings.items.firstOrNull()?.rankings?.firstOrNull { it.teamKey == key }
                    supportActionBar?.title = if (nickname.isNullOrBlank()) team else "$team - $nickname"
                    supportActionBar?.subtitle = rank?.let { "Rank #${it.rank} (${Rank.recordToString(it.record)})" }
                }
            }
        }

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.content_frame, TeamScheduleFragment.forTeam(team))
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId != android.R.id.home) return super.onOptionsItemSelected(item)
        finish()
        return true
    }
}
