package com.aquamorph.frcmanager.models;

/**
 * Stores information about a team at an event.
 *
 * @author Christian Colglazier
 * @version 12/27/2017
 */
public class EventTeam implements Comparable {

	public String key;
	public int team_number;
	public String nickname;
	public String name;
	public String city;
	public String state_prov;
	public String country;
	public String address;
	public String postal_code;
	public String gmaps_place_id;
	public String gmaps_url;
	public String lat;
	public String lng;
	public String location_name;
	public String website;
	public int rookie_year;
	public String motto;
	public String[] home_championship;

	@Override
	public int compareTo(Object another) {
		return this.team_number - ((EventTeam) another).team_number;
	}
}