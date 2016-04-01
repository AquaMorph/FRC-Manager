package com.aquamorph.frcmanager.models;

/**
 * Stores match information at an event.
 *
 * @author Christian Colglazier
 * @version 3/31/2016
 */
public class Match implements Comparable {

	public String TAG = "Match";
	public String comp_level;
	public int match_number;
	public long time;
	public String time_string;
	public String set_number;
	public String key;

	public Alliances alliances;

	public class Alliances {
		public Blue blue;
		public Red red;
	}

	public class Blue {
		public int score;
		public String[] teams;
	}

	public class Red {
		public int score;
		public String[] teams;
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
