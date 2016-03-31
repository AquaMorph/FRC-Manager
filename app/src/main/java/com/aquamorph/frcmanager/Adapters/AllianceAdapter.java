package com.aquamorph.frcmanager.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aquamorph.frcmanager.Constants;
import com.aquamorph.frcmanager.R;
import com.aquamorph.frcmanager.models.Events;

import java.util.ArrayList;

import static android.view.LayoutInflater.from;

/**
 * Populated a view with alliance data.
 *
 * @author Christian Colglazier
 * @version 3/31/2016
 */
public class AllianceAdapter extends RecyclerView.Adapter<AllianceAdapter.MyViewHolder> {

	private String TAG = "AllianceAdapter";
	private LayoutInflater inflater;
	private ArrayList<Events.Alliances> data;

	public AllianceAdapter(Context context, ArrayList<Events.Alliances> data) {
		inflater = from(context);
		this.data = data;
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.alliance, parent, false);
		return new MyViewHolder(view);
	}

	@Override
	public void onBindViewHolder(MyViewHolder holder, int position) {
		holder.allianceNumber.setText(Integer.toString(position + 1));
		holder.team1.setText(Constants.formatTeamNumber(data.get(position).picks[0]));
		holder.team2.setText(Constants.formatTeamNumber(data.get(position).picks[1]));
		holder.team3.setText(Constants.formatTeamNumber(data.get(position).picks[2]));
		if(data.get(position).picks.length > 3) {
			holder.team4.setText(Constants.formatTeamNumber(data.get(position).picks[3]));
		} else {
			holder.team4.setVisibility(View.GONE);
		}
	}

	@Override
	public int getItemCount() {
		return data.size();
	}

	public class MyViewHolder extends RecyclerView.ViewHolder {

		TextView team1;
		TextView team2;
		TextView team3;
		TextView team4;
		TextView allianceNumber;

		public MyViewHolder(View itemView) {
			super(itemView);
			team1 = (TextView) itemView.findViewById(R.id.team_1);
			team2 = (TextView) itemView.findViewById(R.id.team_2);
			team3 = (TextView) itemView.findViewById(R.id.team_3);
			team4 = (TextView) itemView.findViewById(R.id.team_4);
			allianceNumber = (TextView) itemView.findViewById(R.id.alliance_number);
		}
	}
}
