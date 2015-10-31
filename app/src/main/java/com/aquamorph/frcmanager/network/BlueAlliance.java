package com.aquamorph.frcmanager.network;

import com.aquamorph.frcmanager.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BlueAlliance {

	HttpURLConnection conn;

	public InputStream connect(String address) {

		try {
			URL url = new URL(address);
			conn = (HttpURLConnection) url.openConnection();

			conn.setReadTimeout(10000);
			conn.setConnectTimeout(15000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.setRequestProperty(Constants.TBA_HEADER, Constants.API_HEADER);
			conn.connect();


			return conn.getInputStream();
		} catch (IOException e1) {
			e1.printStackTrace();
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
}