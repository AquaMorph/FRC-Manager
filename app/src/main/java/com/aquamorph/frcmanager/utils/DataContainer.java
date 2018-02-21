package com.aquamorph.frcmanager.utils;

import android.app.Activity;

import com.aquamorph.frcmanager.network.Parser;


import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by ccolglazier on 2/21/2018.
 */

public class DataContainer<T> {
	public boolean force;
	public Parser parser;
	public ArrayList<T> data = new ArrayList<>();
	public boolean complete;

	DataContainer(boolean force, Activity activity, Type type, String url) {
		this.force = force;
		this.parser = new Parser<>("eventTeams", url, type, activity, force);
		complete = false;
	}

	public void setForce(boolean force) {
		this.force = force;
	}
}
