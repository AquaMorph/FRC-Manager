package com.aquamorph.frcmanager.models;

public class TeamEventMatches {
	public String comp_level;
	public String match_number;
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
}
