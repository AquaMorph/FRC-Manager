package com.aquamorph.frcmanager.network;

import android.content.Context;
import android.util.Log;

import com.aquamorph.frcmanager.utils.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Established a connection to the Blue Alliance and fetches JSON data.
 *
 * @author Christian Colglazier
 * @version 3/29/2016
 */
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
			Log.i("BA Status", Integer.toString(status));
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

	/**
	 * getLastUpdated() returns when the request was last updated from the server.
	 *
	 * @return last updated
	 */
	public String getLastUpdated() {
		return lastUpdated;
	}

	/**
	 * getStatus() returns the status of the connection to the Blue Alliance.
	 *
	 * @return connection status
	 */
	public int getStatus() {
		return status;
	}
}