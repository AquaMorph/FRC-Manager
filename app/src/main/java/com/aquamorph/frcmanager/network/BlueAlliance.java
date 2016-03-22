package com.aquamorph.frcmanager.network;

import android.content.Context;

import com.aquamorph.frcmanager.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BlueAlliance {

	HttpURLConnection conn;
	public String lastUpdated = "";
	public int status;

	public InputStream connect(String address, String updated, Context context) {

		try {
			URL url = new URL(address);
			conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(15000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.setRequestProperty(Constants.TBA_HEADER, Constants.getApiHeader(context));
			conn.setRequestProperty("If-Modified-Since", updated);
			conn.connect();
			status = conn.getResponseCode();
			if (status != 304) lastUpdated = conn.getHeaderField("last-modified");
			return conn.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public void close() {
		try {
			conn.getInputStream().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getLastUpdated() {
		return lastUpdated;
	}

	public int getStatus() {
		return status;
	}
}