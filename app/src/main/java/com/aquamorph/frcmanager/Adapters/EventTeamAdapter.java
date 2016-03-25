package com.aquamorph.frcmanager.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aquamorph.frcmanager.R;
import com.aquamorph.frcmanager.activities.TeamSummary;
import com.aquamorph.frcmanager.models.EventTeam;

import java.util.ArrayList;

import static android.view.LayoutInflater.from;

/**
 * <p></p>
 *
 * @author Christian Colglazier
 * @version 3/11/2016
 */
public class EventTeamAdapter extends RecyclerView.Adapter<EventTeamAdapter.MyViewHolder> {
	private String TAG = "EventTeamAdapter", info;
	private LayoutInflater inflater;
	private Context context;
	private ArrayList<EventTeam> data;

	public EventTeamAdapter(Context context, ArrayList<EventTeam> data) {
		inflater = from(context);
		this.data = data;
		this.context = context;
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.rank, parent, false);
		MyViewHolder holder = new MyViewHolder(view);
		return holder;
	}

	@Override
	public void onBindViewHolder(EventTeamAdapter.MyViewHolder holder, int position) {
		holder.rankNumber.setText(Integer.toString(data.get(position).team_number));
		holder.teamNumber.setText(data.get(position).nickname);
		holder.details.setText(data.get(position).location);
	}

	@Override
	public int getItemCount() {
		return data.size() - 1;
	}

	public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		TextView teamNumber;
		TextView rankNumber;
		TextView details;

		public MyViewHolder(View itemView) {
			super(itemView);
			itemView.setOnClickListener(this);
			teamNumber = (TextView) itemView.findViewById(R.id.team_number);
			rankNumber = (TextView) itemView.findViewById(R.id.rank);
			details = (TextView) itemView.findViewById(R.id.details);
		}

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(context, TeamSummary.class);
			intent.putExtra("teamNumber", rankNumber.getText().toString());
			context.startActivity(intent);
		}
	}
}
