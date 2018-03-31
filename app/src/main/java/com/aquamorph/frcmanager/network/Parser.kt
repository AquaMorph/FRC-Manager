package com.aquamorph.frcmanager.network

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.view.View
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.models.Status
import com.aquamorph.frcmanager.utils.Constants
import com.aquamorph.frcmanager.utils.Logging
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException
import java.lang.reflect.Type
import java.util.*

/**
 * Parses date of type T.
 *
 * @author Christian Colglazier
 * @version 3/30/2018
 */

class Parser<T>
/**
 * Initializes Parser.
 *
 * @param name     The name of date being collected
 * @param url      The url where the date will be parsed
 * @param type     The dataLoader type of the dataLoader to be collected
 * @param activity The current state of the application
 * @param force       Force reload of dataLoader
 */
(private val name: String, private val url: String, private val type: Type, private val activity: Activity, force: Boolean) {

    @Volatile
    var parsingComplete = true
    /**
     * Returns a list of parsed dataLoader.
     *
     * @return dataLoader
     */
    var data: T? = null
        private set
    private val gson = Gson()
    private val context: Context = activity.applicationContext
    private val prefs: SharedPreferences
    /**
     * isNewData() returns if there has been new dataLoader loaded.
     *
     * @return did the parser return new dataLoader
     */
    var isNewData = false
        private set

    /**
     * Gets the last modified date.
     *
     * @return last modified
     */
    private val lastModified: String
        get() = prefs.getString(name + "Last", "")

    /**
     * getData() returns dataLoader from a stored json string.
     *
     * @return dataLoader
     */
    private val storedData: T?
        get() {
            Logging.debug(this, "Loading dataLoader from a save", 0)
            val json = prefs.getString(name, "")
            return gson.fromJson<T>(json, type)
        }

    init {
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        if (force) {
            Logging.debug(this, "$name force reloading", 1)
            storeData("")
            storeLastModified("")
        }
    }

    /**
     * Updates dataLoader. Checks if dataLoader has already been collected and if it
     * had loads from saved dataLoader.
     *
     * @param storeData Determines if dataLoader should be stored in memory
     */
    @JvmOverloads
    fun fetchJSON(storeData: Boolean?, checkAPI: Boolean? = true) {

        Logging.debug(this, "Loading $name", 0)
        val online = Constants.isNetworkAvailable(context)

        // Displays message saying there is no connection
        if (!online) {
            Snackbar.make(activity.findViewById<View>(R.id.myCoordinatorLayout),
                    R.string.no_connection_message, Snackbar.LENGTH_LONG).show()
        }

        if (checkAPI!!) {
            // Checks FIRST sever status
            val statusBlueAlliance = BlueAlliance()
            val statusStream = statusBlueAlliance.connect(Constants.statusURL, "",
                    context)

            if (statusBlueAlliance.status == 200) {
                var reader: BufferedReader? = null
                try {
                    reader = BufferedReader(InputStreamReader(statusStream!!, "UTF-8"))
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }

                val status = gson.fromJson(reader!!, Status::class.java)
                // Displays error message when the FIRST server is down
                if (status.is_datafeed_down) {
                    Snackbar.make(activity.findViewById<View>(R.id.myCoordinatorLayout),
                            R.string.first_server_down, Snackbar.LENGTH_LONG).show()
                }
                val eventKey = prefs.getString("eventKey", "")
                // Displays error message when the event server is down
                if (Arrays.asList(*status.down_events!!).contains(eventKey)) {
                    Snackbar.make(activity.findViewById<View>(R.id.myCoordinatorLayout),
                            R.string.event_server_down, Snackbar.LENGTH_LONG).show()
                }
            }
            statusBlueAlliance.close()
        }

        // Checks for internet connection
        if (online) {
            Logging.debug(this, "Online", 0)
            val blueAlliance = BlueAlliance()
            val stream: InputStream?
            if (storeData!!) {
                stream = blueAlliance.connect(url, lastModified, context)
            } else {
                stream = blueAlliance.connect(url, "", context)
            }

            // Checks for change in dataLoader
            if (blueAlliance.status == 200 || storedData == null
                    || Constants.FORCE_DATA_RELOAD || !storeData) {
                try {
                    Logging.debug(this, "Loading new dataLoader for $name", 0)
                    val reader = BufferedReader(InputStreamReader(stream!!))
                    data = gson.fromJson<T>(reader, type)
                } catch (e: Exception) {
                    Logging.debug(this, "Trace: " + e.message, 0)
                }

                if (storeData) {
                    storeLastModified(blueAlliance.lastUpdated)
                    storeData(gson.toJson(data))
                }
                blueAlliance.close()
            } else {
                data = storedData
            }// Date Not Changed
        } else {
            data = storedData
        }// Not Online
        parsingComplete = false
    }

    /**
     * Stores the last modified date.
     *
     * @param date last modified date
     */
    private fun storeLastModified(date: String) {
        val editor = prefs.edit()
        editor.putString(name + "Last", date)
        editor.apply()
    }

    /**
     * Sets the date to a json string.
     *
     * @param data parsed values
     */
    fun storeData(data: String) {
        isNewData = true
        Logging.debug(this, "Storing DataLoader", 0)
        val editor = prefs.edit()
        editor.putString(name, data)
        editor.apply()
    }
}
