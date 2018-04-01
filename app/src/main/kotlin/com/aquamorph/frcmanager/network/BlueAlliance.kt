package com.aquamorph.frcmanager.network

import android.content.Context
import android.util.Log

import com.aquamorph.frcmanager.utils.Constants

import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Established a connection to the Blue Alliance and fetches JSON dataLoader.
 *
 * @author Christian Colglazier
 * @version 3/30/2018
 */
internal class BlueAlliance {

    var conn: HttpURLConnection? = null
    /**
     * getLastUpdated() returns when the request was last updated from the server.
     *
     * @return last updated
     */
    var lastUpdated = ""
        private set
    /**
     * getStatus() returns the status of the connection to the Blue Alliance.
     *
     * @return connection status
     */
    var status: Int = 0
        private set

    fun connect(address: String, updated: String, context: Context): InputStream? {

        try {
            val url = URL(address)
            conn = url.openConnection() as HttpURLConnection
            conn!!.readTimeout = 10000
            conn!!.connectTimeout = 15000
            conn!!.requestMethod = "GET"
            conn!!.doInput = true
            conn!!.setRequestProperty(Constants.TBA_HEADER, Constants.getApiHeader(context))
            conn!!.setRequestProperty("If-Modified-Since", updated)
            conn!!.connect()
            status = conn!!.responseCode
            Log.i("BA Status", Integer.toString(status))
            if (status != 304) lastUpdated = conn!!.getHeaderField("last-modified")
            return conn!!.inputStream
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    fun close() {
        try {
            if (conn != null) {
                conn!!.inputStream.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
}