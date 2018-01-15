package com.aquamorph.frcmanager.models;

/**
 * Stores match information at an event.
 *
 * @author Christian Colglazier
 * @version 12/27/2017
 */
public class Match implements Comparable {

	public String TAG = "Match";
	public String key;
	public String comp_level;
	public int set_number;
	public int match_number;
	public Alliances alliances;
	public String winning_alliance;
	public String event_key;
	public long time;
	public long actual_time;
	public long predicted_time;
	public long post_result_time;

	public class Alliances {
		public MatchAlliance blue;
		public MatchAlliance red;
	}

	public class MatchAlliance {
		public int score;
		public String[] team_keys;
		public String[] surrogate_team_keys;
		public String[] dq_team_keys;
	}

	@Override
	public int compareTo(Object another) {
		int compareMatchNumber = ((Match) another).match_number;
		String compareLevel = ((Match) another).comp_level;
		if (getCompLevelValue(compareLevel) == getCompLevelValue(this.comp_level)) {
			return this.match_number - compareMatchNumber;
		} else {
			return getCompLevelValue(this.comp_level) - getCompLevelValue(compareLevel);
		}
	}

	public int getCompLevelValue(String comp_level) {
		switch (comp_level) {
			case "qm":
				return 1;
			case "ef":
				return 2;
			case "qf":
				return 3;
			case "sf":
				return 4;
			case "f":
				return 5;
			default:
				return 6;
		}
	}
}
