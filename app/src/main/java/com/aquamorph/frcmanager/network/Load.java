package com.aquamorph.frcmanager.network;

import android.os.AsyncTask;
import android.os.SystemClock;

import com.aquamorph.frcmanager.models.Match;
import com.aquamorph.frcmanager.utils.Constants;

import java.util.Collection;

import static java.util.Collections.sort;

/**
 * Created by ccolglazier on 2/21/2018.
 */

class Load extends AsyncTask<Void, Void, Void> {

	DataContainer dataContainer;
	boolean isRank = false;
	boolean isSortable = false;

	Load(DataContainer dataContainer) {
		this.dataContainer = dataContainer;
	}

	Load(DataContainer dataContainer, boolean isRank, boolean isSortable) {
		this.dataContainer = dataContainer;
		this.isRank = isRank;
		this.isSortable = isSortable;
	}

	@Override
	protected void onPreExecute() {
		dataContainer.complete = false;
	}

	@Override
	protected Void doInBackground(Void... params) {
		dataContainer.parser.fetchJSON(true);
		while (dataContainer.parser.parsingComplete) {
			SystemClock.sleep(Constants.THREAD_WAIT_TIME);
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		dataContainer.data.clear();
		if (dataContainer.parser.getData() != null) {
			if(isRank) {
				dataContainer.data.add(dataContainer.parser.getData());
			} else {
				dataContainer.data.addAll((Collection) dataContainer.parser.getData());
				if (isSortable) {
					sort(dataContainer.data);
				}
			}
		}
		dataContainer.complete = true;
	}
}