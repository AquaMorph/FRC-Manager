package com.aquamorph.frcmanager.models

/**
 * Stores information about a district.
 *
 * @author Christian Colglazier
 * @version 1/30/2020
 */

data class DistrictList(
    var abbreviation: String,
    var display_name: String,
    var key: String,
    var year: Int
)
