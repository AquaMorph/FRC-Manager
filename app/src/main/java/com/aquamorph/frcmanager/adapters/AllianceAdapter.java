package com.aquamorph.frcmanager.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aquamorph.frcmanager.models.Alliance;
import com.aquamorph.frcmanager.utils.Constants;
import com.aquamorph.frcmanager.R;
import com.aquamorph.frcmanager.activities.TeamSummary;

import java.util.ArrayList;

import static android.view.LayoutInflater.from;

/**
 * Populated a view with alliance data.
 *
 * @author Christian Colglazier
 * @version 3/31/2016
 */
public class AllianceAdapter extends RecyclerView.Adapter<AllianceAdapter.MyViewHolder> {

	private LayoutInflater inflater;
	private ArrayList<Alliance> data;
	private Context context;

	public AllianceAdapter(Context context, ArrayList<Alliance> data) {
		inflater = from(context);
		this.data = data;
		this.context = context;
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
		if (data.get(position).picks.length > 3) {
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
			team1 = itemView.findViewById(R.id.team_1);
			team2 = itemView.findViewById(R.id.team_2);
			team3 = itemView.findViewById(R.id.team_3);
			team4 = itemView.findViewById(R.id.team_4);
			allianceNumber = itemView.findViewById(R.id.alliance_number);

			team1.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, TeamSummary.class);
					intent.putExtra("teamNumber", team1.getText().toString());
					context.startActivity(intent);
				}
			});
			team2.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, TeamSummary.class);
					intent.putExtra("teamNumber", team2.getText().toString());
					context.startActivity(intent);
				}
			});
			team3.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, TeamSummary.class);
					intent.putExtra("teamNumber", team3.getText().toString());
					context.startActivity(intent);
				}
			});
			team4.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, TeamSummary.class);
					intent.putExtra("teamNumber", team4.getText().toString());
					context.startActivity(intent);
				}
			});
		}
	}
}
