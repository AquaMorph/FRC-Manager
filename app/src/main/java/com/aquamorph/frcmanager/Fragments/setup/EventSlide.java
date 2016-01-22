package com.aquamorph.frcmanager.fragments.setup;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.aquamorph.frcmanager.R;
import com.aquamorph.frcmanager.adapters.EventSpinnerAdapter;
import com.aquamorph.frcmanager.models.Events;
import com.aquamorph.frcmanager.parsers.EventParsers;

import java.util.ArrayList;

public class EventSlide extends Fragment {

	String TAG = "EventSlide";
	Spinner eventSpinnder;
	private EventSpinnerAdapter dataAdapter;
	ArrayList<Events> eventList = new ArrayList<>();
	ArrayList<String> eventListString = new ArrayList<>();
	private String teamNumber;


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
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
			teamNumber = prefs.getString("teamNumber", "0000");
		}

		@Override
		protected Void doInBackground(Void... params) {
			EventParsers eventParsers = new EventParsers();
			eventParsers.fetchJSON("frc" + teamNumber);
			while (eventParsers.parsingComplete) ;
			eventList.clear();
			eventList.addAll(eventParsers.getEvents());
			eventListString.clear();
			for (Events eventlist : eventList) {
				if (eventlist.short_name != null) {
					eventListString.add(eventlist.short_name);
				}
			}
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
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
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
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
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
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	}

}
