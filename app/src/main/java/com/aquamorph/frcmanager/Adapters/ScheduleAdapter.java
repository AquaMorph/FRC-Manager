package com.aquamorph.frcmanager.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.aquamorph.frcmanager.Constants;
import com.aquamorph.frcmanager.MyRecyclerView;
import com.aquamorph.frcmanager.R;
import com.aquamorph.frcmanager.activities.TeamSummary;
import com.aquamorph.frcmanager.models.Match;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.view.LayoutInflater.from;

/**
 * Populates a RecyclerView with the schedule for a team.
 *
 * @author Christian Colglazier
 * @version 4/3/2016
 */
public class ScheduleAdapter extends MyRecyclerView.Adapter<ScheduleAdapter.MyViewHolder> {

	private String TAG = "ScheduleAdapter";
	private LayoutInflater inflater;
	private Context context;
	private ArrayList<Match> data;
	private String team;

	public ScheduleAdapter(Context context, ArrayList<Match> data, String team) {
		inflater = from(context);
		this.data = data;
		this.context = context;
		this.team = team;
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.match, parent, false);
		MyViewHolder holder = new MyViewHolder(view);
		return holder;
	}

	@Override
	public void onBindViewHolder(MyViewHolder holder, int position) {
		holder.matchNumber.setText(String.format("%S-%s", data.get(position).comp_level, data
				.get(position).match_number));
		holder.redTeam1.setText(parseTeamNumber(true, 0, position));
		holder.redTeam2.setText(parseTeamNumber(true, 1, position));
		holder.redTeam3.setText(parseTeamNumber(true, 2, position));
		holder.blueTeam1.setText(parseTeamNumber(false, 0, position));
		holder.blueTeam2.setText(parseTeamNumber(false, 1, position));
		holder.blueTeam3.setText(parseTeamNumber(false, 2, position));

		// Underlines team number
		team = String.format("%4s", team);
		if (parseTeamNumber(true, 0, position).equals(team)) {
			holder.redTeam1.setText(Html.fromHtml(Constants.underlineText(team)));
		} else if (parseTeamNumber(true, 1, position).equals(team)) {
			holder.redTeam2.setText(Html.fromHtml(Constants.underlineText(team)));
		} else if (parseTeamNumber(true, 2, position).equals(team)) {
			holder.redTeam3.setText(Html.fromHtml(Constants.underlineText(team)));
		} else if (parseTeamNumber(false, 0, position).equals(team)) {
			holder.blueTeam1.setText(Html.fromHtml(Constants.underlineText(team)));
		} else if (parseTeamNumber(false, 1, position).equals(team)) {
			holder.blueTeam2.setText(Html.fromHtml(Constants.underlineText(team)));
		} else if (parseTeamNumber(false, 2, position).equals(team)) {
			holder.blueTeam3.setText(Html.fromHtml(Constants.underlineText(team)));
		}

		//Bolds winning score
		if (data.get(position).alliances.red.score == data.get(position).alliances.blue.score) {
			holder.redScore.setTypeface(null, Typeface.BOLD);
			holder.blueScore.setTypeface(null, Typeface.BOLD);
		} else if (data.get(position).alliances.red.score > data.get(position).alliances.blue.score) {
			holder.redScore.setTypeface(null, Typeface.BOLD);
			holder.blueScore.setTypeface(null, Typeface.NORMAL);
		} else {
			holder.redScore.setTypeface(null, Typeface.NORMAL);
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

		holder.redTeam1.setGravity(Gravity.CENTER);
		holder.redTeam2.setGravity(Gravity.CENTER);
		holder.redTeam3.setGravity(Gravity.CENTER);
		holder.blueTeam1.setGravity(Gravity.CENTER);
		holder.blueTeam2.setGravity(Gravity.CENTER);
		holder.blueTeam3.setGravity(Gravity.CENTER);
	}

	@Override
	public int getItemCount() {
		return data.size();
	}

	/**
	 * parseTeamNumber returns a formatted team number.
	 *
	 * @param red      is the robot red or blue
	 * @param robot    position of the team
	 * @param position data position
	 * @return team number
	 */
	private String parseTeamNumber(Boolean red, int robot, int position) {
		if (red) {
			return Constants.formatTeamNumber(data.get(position).alliances.red.teams[robot]);
		} else {
			return Constants.formatTeamNumber(data.get(position).alliances.blue.teams[robot]);
		}
	}

	public class MyViewHolder extends MyRecyclerView.ViewHolder {

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

			redTeam1.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, TeamSummary.class);
					intent.putExtra("teamNumber", redTeam1.getText().toString());
					context.startActivity(intent);
				}
			});
			redTeam2.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, TeamSummary.class);
					intent.putExtra("teamNumber", redTeam2.getText().toString());
					context.startActivity(intent);
				}
			});
			redTeam3.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, TeamSummary.class);
					intent.putExtra("teamNumber", redTeam3.getText().toString());
					context.startActivity(intent);
				}
			});
			blueTeam1.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, TeamSummary.class);
					intent.putExtra("teamNumber", blueTeam1.getText().toString());
					context.startActivity(intent);
				}
			});
			blueTeam2.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, TeamSummary.class);
					intent.putExtra("teamNumber", blueTeam2.getText().toString());
					context.startActivity(intent);
				}
			});
			blueTeam3.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, TeamSummary.class);
					intent.putExtra("teamNumber", blueTeam3.getText().toString());
					context.startActivity(intent);
				}
			});
		}


	}
}
