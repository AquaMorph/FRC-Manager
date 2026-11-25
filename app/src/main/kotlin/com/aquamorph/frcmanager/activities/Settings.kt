package com.aquamorph.frcmanager.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.commitNow
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.fragments.SettingsFragment
import com.aquamorph.frcmanager.utils.applyEdgeToEdge

/**
 * Settings activity to take users input about how the app should be set up as well as
 * general information about the app.
 *
 * @author Christian Colglazier
 * @version 1/15/2020
 */
class Settings : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        MainActivity.theme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)
        applyEdgeToEdge()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        if (toolbar != null) {
            toolbar.title = "Settings"
        }
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        if (savedInstanceState == null) {
            supportFragmentManager.commitNow { replace(R.id.content_frame, SettingsFragment()) }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId != android.R.id.home) return super.onOptionsItemSelected(item)
        finishAfterTransition()
        return true
    }
}
