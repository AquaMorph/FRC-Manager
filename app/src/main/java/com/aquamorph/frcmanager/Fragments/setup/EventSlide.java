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

import com.aquamorph.frcmanager.R;
import com.aquamorph.frcmanager.adapters.EventSpinnerAdapter;
import com.aquamorph.frcmanager.models.Events;
import com.aquamorph.frcmanager.parsers.EventParsers;

import java.util.ArrayList;
import java.util.Collections;

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
			Log.i(TAG, "Event size: " + eventList.size());
			EventParsers eventParsers = new EventParsers();
			eventParsers.fetchJSON("frc" + teamNumber, year);
			while (eventParsers.parsingComplete) ;
			eventList.clear();
			eventList.addAll(eventParsers.getEvents());
			Collections.sort(eventList);
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
		editor.commit();
	}

	/**
	 * setEventShortName set the shared variable of the events name.
	 *
	 * @param name event name
	 */
	public void setEventShortName(String name) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("eventShortName", name);
		editor.commit();
	}

	private class EventSpinnerListener implements android.widget.AdapterView.OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			Log.i(TAG, "Key:" + eventList.get(position).key);
			setEventKey(eventList.get(position).key);
			Log.i(TAG, "Short Name:" + eventList.get(position).short_name);
			setEventShortName(eventList.get(position).short_name);
			((TextView) eventSpinnder.getSelectedView()).setTextColor(getResources().getColor(R.color.icons));
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	}

}
