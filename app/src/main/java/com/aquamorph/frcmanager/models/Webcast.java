package com.aquamorph.frcmanager.models;

/**
 * Stores information about live feeds.
 *
 * @author Christian Colglazier
 * @version 2/19/2018
 */

public class Webcast {
	public Type type;
	public String channel;
	String file;

	public enum Type {
		youtube, twitch, ustream, iframe, html5, rtmp, livestream
	}

}
