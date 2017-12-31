package com.aquamorph.frcmanager.models;

/**
 * Stores rank information at an event.
 *
 * @author Christian Colglazier
 * @version 12/27/2017
 */
public class Rank {
	public Rankings[] rankings = new Rankings[0];
	public ExtraStatsInfo[] extra_stats_info = new ExtraStatsInfo[0];
	public SortOrderInfo[] sort_order_info = new SortOrderInfo[0];

	public class Rankings {
		public int dq;
		public int matches_played;
		public double qual_average;
		public int rank;
		public WLTRecord record;
		public double[] extra_stats;
		public double[] sort_orders;
		public String team_key;
	}

	public class WLTRecord {
		public int losses;
		public int wins;
		public int ties;
	}

	public class ExtraStatsInfo {
		public String name;
		public int precision;
	}

	public class SortOrderInfo {
		public String name;
		public int precision;
	}
}
