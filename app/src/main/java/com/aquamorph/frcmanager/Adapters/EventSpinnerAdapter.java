package com.aquamorph.frcmanager.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.aquamorph.frcmanager.models.Events;

import java.util.ArrayList;

/**
 * <p></p>
 *
 * @author Christian Colglazier
 * @version 1/22/2016
 */
public class EventSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

	private final ArrayList<Events> eventList;
	private Activity activity;

	public EventSpinnerAdapter(ArrayList<Events> eventList, Activity activity) {
		this.eventList = eventList;
		this.activity = activity;
	}

	@Override
	public int getCount() {
		return eventList.size();
	}

	@Override
	public Object getItem(int position) {
		return eventList.get(position).name;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView text;
		if (convertView != null){
			// Re-use the recycled view here!
			text = (TextView) convertView;
		} else {
			// No recycled view, inflate the "original" from the platform:
			text = (TextView) activity.getLayoutInflater().inflate(
					android.R.layout.simple_dropdown_item_1line, parent, false
			);
		}
		text.setTextColor(Color.BLACK);
		text.setText(eventList.get(position).name);
		return text;
	}
}

