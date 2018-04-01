package com.aquamorph.frcmanager.models

/**
 * Contains information about awards given at an event
 *
 * @author Christian Colglazier
 * @version 3/30/2018
 */
class Award {
    var name: String = ""
    var award_type: Int = 0
    var event_key: String = ""
    var recipient_list: Array<RecipientList> = emptyArray()
    var year: String = ""

    inner class RecipientList {
        var team_key: String = ""
        var awardee: String = ""
    }
}
