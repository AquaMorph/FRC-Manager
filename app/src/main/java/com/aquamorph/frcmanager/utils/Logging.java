package com.aquamorph.frcmanager.utils;

import android.util.Log;

/**
 * Parses date of type T.
 *
 * @author Christian Colglazier
 * @version 2/15/2018
 */

public class Logging {

    /**
     * debug() prints to debug at a tracing level
     * @param object
     * @param message
     * @param level
     */
    public static void debug(Object object, String message, int level) {
        if (Constants.TRACING_LEVEL >= level ) {
            Log.d(object.getClass().getSimpleName(), message);
        }
    }

    /**
     * error() prints to error at a tracing level
     * @param object
     * @param message
     * @param level
     */
    public static void error(Object object, String message, int level) {
        if (Constants.TRACING_LEVEL >= level ) {
            Log.e(object.getClass().getSimpleName(), message);
        }
    }
}
