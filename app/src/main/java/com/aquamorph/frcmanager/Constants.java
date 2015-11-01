package com.aquamorph.frcmanager;

public class Constants {
	public static final String LOG_TAG = "FRC Regional";
	public static final String URL = "http://www.thebluealliance.com/api/v2/";
	public static final String API_HEADER = "christian_colglazier:frc_manager:dev";
	public static final String YEAR = "2015";
	public static final String TBA_HEADER = "X-TBA-App-Id";

	public static String getEventURL(String team) {
		return URL + "team/" + team + "/" + YEAR + "/events";
	}

	public static String getEventTeamMatches(String team, String event) {
		return String.format("%steam/%s/event/%s%s/matches", URL, team, YEAR, event);
	}

	public static String getEventMatches(String event) {
		return String.format("%sevent/%s%s/matches", URL, YEAR, event);
	}

	public static String getEventStats(String event) {
		return URL + "/event/" + YEAR + event + "/stats";
	}

	public static String getEventRanks(String event) {
		return URL + "event/" + YEAR + event + "/rankings";
	}

	public static String getEventAwards(String event) {
		return URL + "event/" + YEAR + event + "/awards";
	}

	public static String getApiHeader() {
		return API_HEADER;
	}

}