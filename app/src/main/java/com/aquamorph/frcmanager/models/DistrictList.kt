package com.aquamorph.frcmanager.models

/**
 * Stores information about a district.
 *
 * @author Christian Colglazier
 * @version 3/30/2018
 */

abstract class DistrictList {
    abstract var abbreviation: String?
    abstract var display_name: String?
    abstract var key: String?
    abstract var year: Int?
}
