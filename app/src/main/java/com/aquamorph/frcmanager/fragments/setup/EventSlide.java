package com.aquamorph.frcmanager.fragments.setup;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.aquamorph.frcmanager.Constants;
import com.aquamorph.frcmanager.R;
import com.aquamorph.frcmanager.adapters.EventSpinnerAdapter;
import com.aquamorph.frcmanager.models.Events;
import com.aquamorph.frcmanager.network.Parser;
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

	String TAG = "EventSlide";
	Spinner eventSpinnder;
	private EventSpinnerAdapter dataAdapter;
	ArrayList<Events> eventList = new ArrayList<>();
	private String teamNumber, year;
	private SharedPreferences prefs;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.event_slide, container, false);

		eventSpinnder = (Spinner) view.findViewById(R.id.event_spinner);
		dataAdapter = new EventSpinnerAdapter(eventList, getActivity());
		eventSpinnder.setAdapter(dataAdapter);
		eventSpinnder.setOnItemSelectedListener(new EventSpinnerListener());
		return view;
	}

	/**
	 * load() loads the team events
	 */
	public void load() {
		final LoadTeamEvents loadTeamEvents = new LoadTeamEvents();
		loadTeamEvents.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	class LoadTeamEvents extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			teamNumber = prefs.getString("teamNumber", "");
			year = prefs.getString("year", "");
			Log.i(TAG, "Team Number: " + teamNumber);
		}

		@Override
		protected Void doInBackground(Void... params) {
			Parser<ArrayList<Events>> parser = new Parser<>("Events",
					Constants.getEventURL("frc" + teamNumber, year), new
					TypeToken<ArrayList<Events>>() {
					}.getType(), getActivity());
			parser.fetchJSON(false);
			while (parser.parsingComplete) ;
			if (parser.getData() != null) {
				eventList.clear();
				eventList.addAll(parser.getData());
				sort(eventList);
				Log.i(TAG, "Event size: " + eventList.size() + parser.getData().toString());
			}
			Log.i(TAG, "Event size: " + eventList.size());
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			dataAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * setEventKey set the shared variable of the event key.
	 *
	 * @param key identification key of an event
	 */
	public void setEventKey(String key) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("eventKey", key);
		editor.apply();
	}

	/**
	 * setEventShortName() set the shared variable of the events name.
	 *
	 * @param name event name
	 */
	public void setEventShortName(String name) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("eventShortName", name);
		editor.apply();
	}

	private class EventSpinnerListener implements AdapterView.OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			setEventKey(eventList.get(position).key);
			if (Constants.TRACING_LEVEL > 0) {
				Log.i(TAG, "Key:" + eventList.get(position).key);
				Log.i(TAG, "Short Name:" + eventList.get(position).short_name);
			}
			setEventShortName(eventList.get(position).short_name);
			((TextView) eventSpinnder.getSelectedView()).setTextColor(getResources()
					.getColor(R.color.icons));
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}
	}
}