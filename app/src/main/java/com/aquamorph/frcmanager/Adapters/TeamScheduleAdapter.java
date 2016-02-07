package com.aquamorph.frcmanager.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.aquamorph.frcmanager.R;
import com.aquamorph.frcmanager.models.Match;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.view.LayoutInflater.from;

public class TeamScheduleAdapter extends RecyclerView.Adapter<TeamScheduleAdapter.MyViewHolder> {

	private String TAG = "TeamScheduleAdapter";
	private LayoutInflater inflater;
	private Context context;
	private ArrayList<Match> data;

	public TeamScheduleAdapter(Context context, ArrayList<Match> data) {
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
		holder.setIsRecyclable(false);
		holder.matchNumber.setText(String.format("%S-%s", data.get(position).comp_level, data
				.get(position).match_number));
		holder.redTeam1.setText(ParseTeamNumber(true, 0, position));
		holder.redTeam2.setText(ParseTeamNumber(true, 1, position));
		holder.redTeam3.setText(ParseTeamNumber(true, 2, position));
		holder.blueTeam1.setText(ParseTeamNumber(false, 0, position));
		holder.blueTeam2.setText(ParseTeamNumber(false, 1, position));
		holder.blueTeam3.setText(ParseTeamNumber(false, 2, position));

		if (data.get(position).alliances.red.score == data.get(position).alliances.blue.score) {
			holder.redScore.setTypeface(null, Typeface.BOLD);
			holder.blueScore.setTypeface(null, Typeface.BOLD);
		} else if (data.get(position).alliances.red.score > data.get(position).alliances.blue.score) {
			holder.redScore.setTypeface(null, Typeface.BOLD);
		} else {
			holder.blueScore.setTypeface(null, Typeface.BOLD);
		}

		if (data.get(position).alliances.red.score != -1) {
			holder.matchTime.setVisibility(View.GONE);
			holder.scoreTable.setVisibility(View.VISIBLE);
			holder.redScore.setText(String.valueOf(data.get(position).alliances.red.score));
			holder.blueScore.setText(String.valueOf(data.get(position).alliances.blue.score));
		} else {
			holder.matchTime.setVisibility(View.VISIBLE);
			holder.scoreTable.setVisibility(View.GONE);
			Date time = new Date();
			DateFormat df = new SimpleDateFormat("hh:mm aa");
			time.setTime(data.get(position).time * 1000);
			holder.matchTime.setText(df.format(time));
		}
	}

	@Override
	public int getItemCount() {
		return data.size();
	}

	public class MyViewHolder extends RecyclerView.ViewHolder {

		protected TextView matchNumber;
		protected TextView redTeam1;
		protected TextView redTeam2;
		protected TextView redTeam3;
		protected TextView blueTeam1;
		protected TextView blueTeam2;
		protected TextView blueTeam3;
		protected TextView matchTime;
		protected TextView redScore;
		protected TextView blueScore;
		protected TableLayout scoreTable;

		public MyViewHolder(View itemView) {
			super(itemView);
			matchNumber = (TextView) itemView.findViewById(R.id.match_number);
			redTeam1 = (TextView) itemView.findViewById(R.id.red_team_1);
			redTeam2 = (TextView) itemView.findViewById(R.id.red_team_2);
			redTeam3 = (TextView) itemView.findViewById(R.id.red_team_3);
			blueTeam1 = (TextView) itemView.findViewById(R.id.blue_team_1);
			blueTeam2 = (TextView) itemView.findViewById(R.id.blue_team_2);
			blueTeam3 = (TextView) itemView.findViewById(R.id.blue_team_3);
			redScore = (TextView) itemView.findViewById(R.id.red_score);
			blueScore = (TextView) itemView.findViewById(R.id.blue_score);
			matchTime = (TextView) itemView.findViewById(R.id.match_time);
			scoreTable = (TableLayout) itemView.findViewById(R.id.score_table);
		}
	}

	private String ParseTeamNumber(Boolean red, int robot, int position) {
		if (red) {
			return data.get(position).alliances.red.teams[robot].replaceAll("\\D+", "");
		} else {
			return data.get(position).alliances.blue.teams[robot].replaceAll("\\D+", "");
		}

	}
}
