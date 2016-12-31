package com.aquamorph.frcmanager;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * A collection of constants needed to interact with the Blue Alliance.
 *
 * @author Christian Colglazier
 * @version 3/31/16
 */
public class Constants {

	public static final String TAG = "FRC Regional";
	private static final String URL = "https://www.thebluealliance.com/api/v2/";
	public static final String TBA_HEADER = "X-TBA-App-Id";
	public static final Boolean FORCE_DATA_RELOAD = false;
	public static int TRACING_LEVEL = 3;
	public static int MAX_EVENT_TITLE_LENGTH = 20;

	/**
	 * getEventURL() returns the url for a list of events a team is registered.
	 *
	 * @param team team identification "frc####"
	 * @return url to team events
	 */
	public static String getEventURL(String team, String year) {
		return String.format("%steam/%s/%s/events", URL, team, year);
	}

	/**
	 * getEvent() returns the url for an event.
	 *
	 * @param event event tag
	 * @return url to event
	 */
	public static String getEvent(String event) {
		return String.format("%sevent/%s", URL, event);
	}

	/**
	 * getEventURL() returns the url for a list of events a team is registered.
	 *
	 * @param event vent identification number
	 * @return url to list of teams at an event
	 */
	public static String getEventTeams(String event) {
		return String.format("%sevent/%s/teams", URL, event);
	}

	/**
	 * getEventTeamMatches() returns the url for a team's matches at a specific event.
	 *
	 * @param team  team identification "frc####"
	 * @param event event identification number
	 * @return url to team matches for an event
	 */
	public static String getEventTeamMatches(String team, String event) {
		return String.format("%steam/%s/event/%s/matches", URL, team, event);
	}

	/**
	 * getEventMatches() returns the url for all matches at an event.
	 *
	 * @param event event identification number
	 * @return url to matches at en event
	 */
	public static String getEventMatches(String event) {
		return String.format("%sevent/%s/matches", URL, event);
	}

	/**
	 * getEventStats() returns the url for event statistics
	 *
	 * @param event event identification number
	 * @return url to event statistics
	 */
	public static String getEventStats(String event, String year) {
		return URL + "/event/" + year + event + "/stats";
	}

	/**
	 * getEventRanks() returns the url for event team rankings.
	 *
	 * @param event event identification number
	 * @return url to event rankings
	 */
	public static String getEventRanks(String event) {
		return String.format("%sevent/%s/rankings", URL, event);
	}

	/**
	 * getEventAwards() returns the url awards given at.
	 *
	 * @param event event identification number
	 * @return url to event awards
	 */
	public static String getEventAwards(String event) {
		return String.format("%sevent/%s/awards", URL, event);
	}

	/**
	 * getApiHeader() returns the header needed to get access to the Blue Alliance.
	 *
	 * @return the Blue Alliance header
	 */
	public static String getApiHeader(Context context) {
		return "christian_colglazier:frc_manager:" + context.getResources().getString(R.string.version);
	}

	/**
	 * isNetworkAvailable() returns if a connection to the internet is available.
	 *
	 * @param context fragment or activity the request takes place from
	 * @return is internet available
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager
				= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	/**
	 * formatTeamNumber() removes all text from the team string and adds padding to create a string
	 * of length 4.
	 *
	 * @param team string
	 * @return formatted string
	 */
	public static String formatTeamNumber(String team) {
		return String.format("%4s", team.replaceAll("\\D+", ""));
	}

	/**
	 * underlineText() returns a string with formatting in HTML that is underlined.
	 *
	 * @param text team number
	 * @return returns formatted team number
	 */
	public static String underlineText(String text) {
		text = text.replace(" ", "");
		int spaces = 4 - text.length();
		String spacesText = "";
		for (int i = 0; i < spaces; i++) {
			spacesText += "&nbsp;";
		}
		return String.format("<pre>%s<u>%s</u></pre>", spacesText, text);
	}

	/**
	 * isLargeScreen returns if the screen is large or not
	 *
	 * @param context
	 * @return screen size
	 */
	public static boolean isLargeScreen(Context context) {
		Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);

		float density = context.getResources().getDisplayMetrics().density;
		float dpWidth = outMetrics.widthPixels / density;
		return dpWidth >= 600;
	}

	public static void loadAnimation(Context context, View view) {
		Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
		view.startAnimation(animation);
	}
}