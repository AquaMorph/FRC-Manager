package com.aquamorph.frcmanager.models;


import android.support.v4.app.Fragment;

/**
 * Created by aquam on 2/22/2018.
 */

public class Tab implements Comparable {
	public String name = "";
	public Fragment fragment;

	public Tab(String name, Fragment fragment) {
		this.name = name;
		this.fragment = fragment;
	}


	@Override
	public int compareTo(Object another) {
		return getTabOrder(this.name) - getTabOrder(((Tab) another).name);
	}

	private int getTabOrder(String name) {
		switch (name) {
			case "Team Schedule":
				return 0;
			case "Event Schedule":
				return 1;
			case "Rankings":
				return 2;
			case "Teams":
				return 3;
			case "Alliances":
				return 4;
			case "Awards":
				return 5;
			default:
				return 10;
		}
	}
}
