package com.aquamorph.frcmanager.models;

/**
 * Contains information about awards given at an event
 *
 * @author Christian Colglazier
 * @version 12/27/2017
 */
public class Award {
	public String name;
//	public int award_type;
//	public String event_key;
	public RecipientList[] recipient_list;
//	public String year;

	public class RecipientList {
		public String team_key;
		public String awardee;
	}
}
