package com.aquamorph.frcmanager.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Store information about an event.
 *
 * @author Christian Colglazier
 * @version 3/29/2016
 */
public class Events implements Comparable {
	public String key;
	public String website;
	public Boolean official;
	public String end_date;
	public String name;
	public String short_name;
	public String facebook_eid;
	public String event_district_string;
	public String venue_address;
	public String event_district;
	public String location;
	public String event_code;
	public String year;
	public Alliances[] alliances;
	public String event_type_string;
	public String start_date;
	public String event_type;

	public class Alliances {
		public String[] declines;
		public String[] picks;
	}

	@Override
	public int compareTo(Object another) {
		try {

			Date first = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(this.start_date);
			Date second = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
					.parse(((Events) another).start_date);
			return first.compareTo(second);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}
