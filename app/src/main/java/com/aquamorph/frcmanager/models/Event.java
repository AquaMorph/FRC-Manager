package com.aquamorph.frcmanager.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Stores information about an event.
 *
 * @author Christian Colglazier
 * @version 3/29/2016
 */
public class Event implements Comparable {

	public String key;
	public String name;
//	public String event_code;
//	public int event_type;
//	public DistrictList district;
//	public String city;
//	public String state_prov;
//	public String country;
	public String start_date;
//	public String end_date;
	public int year;
	public String short_name;
//	public String event_type_string;
//	public int week;
//	public String address;
//	public String postal_code;
//	public String gmaps_place_id;
//	public String gmaps_url;
//	public double lat;
//	public double lng;
//	public String location_name;
//	public String timezone;
//	public String website;
//	public String first_event_id;
//	public String first_event_code;
//	public Webcast[] webcasts;
//	public String[] division_keys;
//	public int playoff_type;
//	public String playoff_type_string;

	@Override
	public int compareTo(Object another) {
		try {

			Date first = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(this.start_date);
			Date second = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(((Event) another).start_date);
			return first.compareTo(second);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}
