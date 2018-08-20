package com.aquamorph.frcmanager.network

import android.app.Activity
import io.reactivex.Observable
import java.lang.reflect.Type
import java.util.*

/**
 * DataLoader structure for parser and parsed dataLoader.
 *
 * @author Christian Colglazier
 * @version 3/31/2018
 */

class DataContainer<T> internal constructor(name: String) {
    var data = ArrayList<T>()
    var complete: Boolean = false
}
