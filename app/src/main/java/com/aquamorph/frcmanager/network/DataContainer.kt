package com.aquamorph.frcmanager.network

import android.app.Activity
import java.lang.reflect.Type
import java.util.*

/**
 * DataLoader structure for parser and parsed dataLoader.
 *
 * @author Christian Colglazier
 * @version 3/31/2018
 */

class DataContainer<T> internal constructor(var force: Boolean, activity: Activity, type: Type, url: String, name: String) {
    var parser: Parser<Any> = Parser(name, url, type, activity, force)
    var data = ArrayList<T>()
    var complete: Boolean = false
}
