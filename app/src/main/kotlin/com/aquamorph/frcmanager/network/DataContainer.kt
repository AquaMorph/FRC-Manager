package com.aquamorph.frcmanager.network

import java.util.ArrayList

/**
 * DataLoader structure for parser and parsed dataLoader.
 *
 * @author Christian Colglazier
 * @version 3/31/2018
 */

class DataContainer<T> internal constructor() {
    var data = ArrayList<T>()
    var complete: Boolean = false
    var newData = false
}
