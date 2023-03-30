package com.aquamorph.frcmanager.models.tba

import androidx.fragment.app.Fragment

/**
 * Store information about tabs.
 *
 * @author Christian Colglazier
 * @version 3/30/2018
 */

class Tab(name: String, var fragment: Fragment) : Comparable<Tab> {
    var name = ""

    init {
        this.name = name
    }

    override operator fun compareTo(other: Tab): Int {
        return getTabOrder(this.name) - getTabOrder(other.name)
    }

    private fun getTabOrder(name: String): Int {
        return when (name) {
            "Team Schedule" -> 0
            "Event Schedule" -> 1
            "Rankings" -> 2
            "Teams" -> 3
            "Alliances" -> 4
            "Awards" -> 5
            else -> 10
        }
    }
}
