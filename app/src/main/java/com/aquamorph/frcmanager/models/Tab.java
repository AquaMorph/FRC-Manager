package com.aquamorph.frcmanager.models;


import android.support.v4.app.Fragment;

/**
 * Created by aquam on 2/22/2018.
 */

public class Tab {
	public String name = "";
	public Fragment fragment;

	public Tab(String name, Fragment fragment) {
		this.name = name;
		this.fragment = fragment;
	}
}
