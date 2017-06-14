package com.aquamorph.frcmanager.models

/**
 * Contains information about awards given at an event

 * @author Christian Colglazier
 * *
 * @version 1/26/2016
 */
class Award {

    var event_key: String? = null
    var award_type: Int = 0
    var name: String? = null
    var recipient_list: Array<RecipientList>? = null
    var year: String? = null

    inner class RecipientList {
        var team_number: String? = null
        var awardee: String? = null
    }
}
