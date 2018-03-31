package com.aquamorph.frcmanager.models

/**
 * Contains information about awards given at an event
 *
 * @author Christian Colglazier
 * @version 3/30/2018
 */
abstract class Award {
    abstract var name: String?
    abstract var award_type: Int
    abstract var event_key: String?
    abstract var recipient_list: Array<RecipientList>?
    abstract var year: String?

    abstract inner class RecipientList {
        abstract var team_key: String?
        abstract var awardee: String?
    }
}
