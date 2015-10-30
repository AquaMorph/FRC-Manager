package com.aquamorph.frcmanager.network;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class ConnectBlueAlliance {

	String TAG = "ConnectBlueAlliance";
	String TBA_CACHE_HEADER = "X-TBA-Cache";
	final String TBA_CACHE_WEB = "web";
	final String TBA_CACHE_LOCAL = "local";

	OkHttpClient client = new OkHttpClient();

	String run(String url) throws IOException {
		Request request = new Request.Builder()
				.url(url)
				.build();

		Response response = null;
		try {
			response = client.newCall(request).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response.body().string();
	}
}