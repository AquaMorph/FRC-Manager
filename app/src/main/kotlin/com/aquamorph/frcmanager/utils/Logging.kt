package com.aquamorph.frcmanager.utils

import android.util.Log

/**
 * Parses date of type T.
 *
 * @author Christian Colglazier
 * @version 3/30/2018
 */

object Logging {

    /**
     * debug() prints to debug at a tracing level
     * @param object
     * @param message
     * @param level
     */
    fun debug(`object`: Any, message: String, level: Int) {
        if (Constants.TRACING_LEVEL >= level) {
            Log.d(`object`.javaClass.simpleName, message)
        }
    }

    /**
     * info() prints to info at a tracing level
     * @param object
     * @param message
     * @param level
     */
    fun info(`object`: Any, message: String, level: Int) {
        if (Constants.TRACING_LEVEL >= level) {
            Log.i(`object`.javaClass.simpleName, message)
        }
    }

    /**
     * error() prints to error at a tracing level
     * @param object
     * @param message
     * @param level
     */
    fun error(`object`: Any, message: String, level: Int) {
        if (Constants.TRACING_LEVEL >= level) {
            Log.e(`object`.javaClass.simpleName, message)
        }
    }
}
