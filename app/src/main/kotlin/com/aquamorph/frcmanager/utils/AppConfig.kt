package com.aquamorph.frcmanager.utils

import android.content.Context
import android.preference.PreferenceManager

/**
 * Handles storing and receiving keys.
 *
 * @author Christian Colglazier
 * @version 3/30/2018
 */

object AppConfig {

    /**
     * Stores the year so SharedPreferences
     *
     * @param year year of event being tracked
     * @param context app context
     */
    fun setYear(year: String, context: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = prefs.edit()
        editor.putString("year", year)
        Logging.info(context, "Year set to: $year", 0)
        editor.apply()
    }

    /**
     * setTeamNumber() saves the team number to shared preferences.
     *
     * @param team number
     * @param context app context
     */
    fun setTeamNumber(team: String, context: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = prefs.edit()
        editor.putString("teamNumber", team)
        editor.apply()
    }

    /**
     * setEventKey set the shared variable of the event key.
     *
     * @param key identification key of an event
     * @param context app context
     */
    fun setEventKey(key: String, context: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = prefs.edit()
        editor.putString("eventKey", key)
        editor.apply()
    }

    /**
     * setEventShortName() set the shared variable of the events name.
     *
     * @param name event name
     * @param context app context
     */
    fun setEventShortName(name: String, context: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = prefs.edit()
        editor.putString("eventShortName", name)
        editor.apply()
    }

    /**
     * setDistictKey() set the shared variable of the district key.
     *
     * @param key district key
     * @param context app context
     */
    fun setDistictKey(key: String, context: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = prefs.edit()
        editor.putString("districtKey", key)
        editor.apply()
    }
}
