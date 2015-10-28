package com.aquamorph.frcmanager.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aquamorph.frcmanager.R;

import static android.view.LayoutInflater.from;

public class TeamScheduleAdapter extends RecyclerView.Adapter<TeamScheduleAdapter.MyViewHolder> {

	private LayoutInflater inflater;
	private Context context;
	private String[] data;

	public TeamScheduleAdapter(Context context, String[] data) {
		inflater = from(context);
		this.data = data;
		this.context = context;
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.team_schedule, parent, false);
		MyViewHolder holder = new MyViewHolder(view);
		return holder;
	}

	@Override
	public void onBindViewHolder(MyViewHolder holder, int position) {
		holder.matchNumber.setText(data[position]);
	}

	@Override
	public int getItemCount() {
		return data.length;
	}

	public class MyViewHolder extends RecyclerView.ViewHolder {

		protected TextView matchNumber;

		public MyViewHolder(View itemView) {
			super(itemView);
			matchNumber = (TextView) itemView.findViewById(R.id.match_number);
		}
	}
}
