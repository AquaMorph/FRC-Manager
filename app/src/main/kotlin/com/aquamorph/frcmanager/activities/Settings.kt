package com.aquamorph.frcmanager.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.fragments.SettingsFragment

/**
 * Settings activity to take users input about how the app should be set up as well as
 * general information about the app.
 *
 * @author Christian Colglazier
 * @version 3/31/2018
 */
class Settings : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivity.theme(this)
        setContentView(R.layout.settings)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        if (toolbar != null) {
            toolbar.title = "Settings"
        }
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val fragment = supportFragmentManager.findFragmentById(R.id.content_frame)
        if (fragment == null) {
            supportFragmentManager.beginTransaction().replace(R.id.content_frame,
                    SettingsFragment()).commit()
        } else {
            supportFragmentManager.beginTransaction().remove(fragment).commit()
            supportFragmentManager.beginTransaction().replace(R.id.content_frame,
                    SettingsFragment()).commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> this.finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
