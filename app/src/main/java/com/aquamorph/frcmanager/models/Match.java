package com.aquamorph.frcmanager.models;

public class Match implements Comparable {

	public String TAG = "Match";
	public String comp_level;
	public int match_number;
	public String time_string;
	public String set_number;
	public String key;
	public Alliances alliances;

	public class Alliances {
		public Blue blue;
		public Red red;
	}

	public class Blue {
		public double score;
		public String[] teams;
	}

	public class Red {
		public double score;
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
			case "qf":
				return 2;
			case "sf":
				return 3;
			case "f":
				return 4;
			default:
				return 5;
		}
	}
}
