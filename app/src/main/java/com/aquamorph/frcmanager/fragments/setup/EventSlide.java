package com.aquamorph.frcmanager.fragments.setup;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.aquamorph.frcmanager.R;
import com.aquamorph.frcmanager.adapters.EventSpinnerAdapter;
import com.aquamorph.frcmanager.models.Event;
import com.aquamorph.frcmanager.network.Parser;
import com.aquamorph.frcmanager.utils.AppConfig;
import com.aquamorph.frcmanager.utils.Constants;
import com.aquamorph.frcmanager.utils.Logging;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import static java.util.Collections.sort;

/**
 * Loads events a team is signed up for and allows for the selection of that event.
 *
 * @author Christian Colglazier
 * @version 12/29/2016
 */
public class EventSlide extends Fragment {

	Spinner eventSpinnder;
	private EventSpinnerAdapter dataAdapter;
	ArrayList<Event> eventList = new ArrayList<>();
	private String teamNumber, year;
	private SharedPreferences prefs;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.event_slide, container, false);

		eventSpinnder = view.findViewById(R.id.event_spinner);
		dataAdapter = new EventSpinnerAdapter(eventList, getActivity());
		eventSpinnder.setAdapter(dataAdapter);
		eventSpinnder.setOnItemSelectedListener(new EventSpinnerListener());
		return view;
	}

	/**
	 * load() loads the team events
	 */
	public void load(Boolean force) {
		final LoadTeamEvents loadTeamEvents = new LoadTeamEvents(force);
		loadTeamEvents.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	class LoadTeamEvents extends AsyncTask<Void, Void, Void> {

		boolean force;

		public LoadTeamEvents(boolean force) {
			this.force = force;
		}

		@Override
		protected void onPreExecute() {
			teamNumber = prefs.getString("teamNumber", "");
			year = prefs.getString("year", "");
			Logging.info(this, "Team Number: " + teamNumber,0);
		}

		@Override
		protected Void doInBackground(Void... params) {
			String url = Constants.getEventURL("frc" + teamNumber, year);
			Logging.info(this, "Loading: " + url, 0);
			Parser<ArrayList<Event>> parser = new Parser<>("Event", url, new
					TypeToken<ArrayList<Event>>(){}.getType(), getActivity(), force);
			try {
				parser.fetchJSON(false, false);
				while (parser.parsingComplete) {
					SystemClock.sleep(Constants.THREAD_WAIT_TIME);
				}
				if (parser.getData() != null) {
					eventList.clear();
					eventList.addAll(parser.getData());
					sort(eventList);
				}
				Logging.info(this, "Event size: " + eventList.size(), 0);
			} catch (Exception e) {
				Logging.error(this, e.getMessage(), 0);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			dataAdapter.notifyDataSetChanged();
		}
	}

	private class EventSpinnerListener implements AdapterView.OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			AppConfig.setEventKey(eventList.get(position).getKey(), getContext());
			Logging.info(this, "Key:" + eventList.get(position).getKey(), 0);
			Logging.info(this, "Short Name:" + eventList.get(position).getShort_name(),0);
			AppConfig.setEventShortName(eventList.get(position).getShort_name(), getContext());
			((TextView) eventSpinnder.getSelectedView()).setTextColor(getResources()
					.getColor(R.color.icons));
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}
	}
}
