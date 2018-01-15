package com.aquamorph.frcmanager.models;

/**
 * Stores information about a team at an event.
 *
 * @author Christian Colglazier
 * @version 3/11/2016
 */
public class EventTeam implements Comparable {

	public String website;
	public String name;
	public String locality;
	public int rookie_year;
	public String region;
	public int team_number;
	public String location;
	public String key;
	public String country_name;
	public String motto;
	public String nickname;

	@Override
	public int compareTo(Object another) {
		return this.team_number - ((EventTeam) another).team_number;
	}
}