package com.aquamorph.frcmanager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * A collection of constants needed to interact with the Blue Alliance.
 *
 * @author Christian Colglazier
 * @version 3-21-16
 */
public class Constants {
	public static final String TAG = "FRC Regional";
	public static final String URL = "http://www.thebluealliance.com/api/v2/";
	public static final String TBA_HEADER = "X-TBA-App-Id";
	public static final String NOT_ONLINE_MESSAGE = "No Connection";
	public static final Boolean FORCE_DATA_RELOAD = false;
	public static int TRACTING_LEVEL = 3;

	/**
	 * getEventURL returns the url for a list of events a team is registered.
	 *
	 * @param team team identification "frc####"
	 * @return url to team events
	 */
	public static String getEventURL(String team, String year) {
		return String.format("%steam/%s/%s/events", URL, team, year);
	}

	/**
	 * getEventURL returns the url for a list of events a team is registered.
	 *
	 * @param event vent identification number
	 * @return url to list of teams at an event
	 */
	public static String getEventTeams(String event) {
		return String.format("%sevent/%s/teams", URL, event);
	}

	/**
	 * getEventTeamMatches returns the url for a team's matches at a specific event.
	 *
	 * @param team team identification "frc####"
	 * @param event event identification number
	 * @return url to team matches for an event
	 */
	public static String getEventTeamMatches(String team, String event) {
		return String.format("%steam/%s/event/%s/matches", URL, team, event);
	}

	/**
	 * getEventMatches returns the url for all matches at an event
	 *
	 * @param event event identification number
	 * @return url to matches at en event
	 */
	public static String getEventMatches(String event) {
		return String.format("%sevent/%s/matches", URL, event);
	}

	/**
	 * getEventStats returns the url for event statistics
	 *
	 * @param event event identification number
	 * @return url to event statistics
	 */
	public static String getEventStats(String event, String year) {
		return URL + "/event/" + year + event + "/stats";
	}

	/**
	 * getEventRanks returns the url for event team rankings
	 * @param event event identification number
	 * @return url to event rankings
	 */
	public static String getEventRanks(String event) {
		return String.format("%sevent/%s/rankings", URL, event);
	}

	/**
	 * getEventAwards returns the url awards given at
	 * @param event event identification number
	 * @return url to event awards
	 */
	public static String getEventAwards(String event) {
		return String.format("%sevent/%s/awards", URL, event);
	}

	/**
	 * getApiHeader returns the header needed to get access to the Blue Alliance
	 *
	 * @return the Blue Alliance header
	 */
	public static String getApiHeader(Context context) {
		return "christian_colglazier:frc_manager:" + context.getResources().getString(R.string.version);
	}

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager
				= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}