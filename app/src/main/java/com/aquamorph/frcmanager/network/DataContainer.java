package com.aquamorph.frcmanager.network;

import android.app.Activity;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * DataLoader structure for parser and parsed dataLoader.
 *
 * @author Christian Colglazier
 * @version 2/21/2018
 */

public class DataContainer<T> {
	public boolean force;
	public Parser parser;
	public ArrayList<T> data = new ArrayList<>();
	public boolean complete;

	DataContainer(boolean force, Activity activity, Type type, String url, String name) {
		this.force = force;
		this.parser = new Parser<>(name, url, type, activity, force);
		complete = false;
	}

	public void setForce(boolean force) {
		this.force = force;
	}
}
