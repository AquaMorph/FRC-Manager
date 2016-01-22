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
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.aquamorph.frcmanager.R;
import com.aquamorph.frcmanager.models.Events;
import com.aquamorph.frcmanager.parsers.EventParsers;

import java.util.ArrayList;

public class EventSlide extends Fragment {

	String TAG = "EventSlide";
	Spinner eventSpinnder;
	private ArrayAdapter dataAdapter;
	ArrayList<Events> eventList = new ArrayList<>();
	ArrayList<String> eventListString = new ArrayList<>();
	private String teamNumber;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.event_slide, container, false);

		eventSpinnder = (Spinner) view.findViewById(R.id.event_spinner);
		dataAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item,
				eventListString);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		eventSpinnder.setAdapter(dataAdapter);
		return view;
	}

	public void load() {
		Log.i(TAG, "load is running");
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
			Log.i(TAG, "doInBackground is running");
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
}
