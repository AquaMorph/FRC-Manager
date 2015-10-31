package com.aquamorph.frcmanager.models;

public class TeamEventMatches implements Comparable {
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
		int compareMatchNumber = ((TeamEventMatches) another).match_number;
		String compareLevel = ((TeamEventMatches) another).comp_level;
		if(compareLevel.equals(this.comp_level)) {
			return this.match_number - compareMatchNumber;
		} else {
			switch(compareLevel) {
				case "qm":
					return 1;
				case "sf":
					if (this.comp_level.equals("qm")) {
						return 1;
					} else {
						return -1;
					}
				default:
					return compareLevel.compareTo(this.comp_level);
			}
		}
	}
}
