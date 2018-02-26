package com.aquamorph.frcmanager.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aquamorph.frcmanager.R;
import com.aquamorph.frcmanager.activities.TeamSummary;
import com.aquamorph.frcmanager.models.Rank;
import com.aquamorph.frcmanager.models.Team;

import java.util.ArrayList;

import static android.view.LayoutInflater.from;

/**
 * Populates a RecyclerView with teams at an event.
 *
 * @author Christian Colglazier
 * @version 3/19/2016
 */
public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.MyViewHolder> {

	private LayoutInflater inflater;
	private Context context;
	private ArrayList<Team> data;
	private ArrayList<Rank> ranks;

	public TeamAdapter(Context context, ArrayList<Team> data, ArrayList<Rank> ranks) {
		inflater = from(context);
		this.data = data;
		this.context = context;
		this.ranks = ranks;
	}

	private String getTeamRank(String teamNumber, ArrayList<Rank> ranks) {
		if(ranks != null && ranks.size() > 0 && ranks.get(0).rankings != null) {
			for (int i = 0; i < ranks.get(0).rankings.length; i++) {
				if (ranks.get(0).rankings[i].team_key.equals(teamNumber)) return " Ranked #" +
						ranks.get(0).rankings[i].rank;
			}
		}
		return "";
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.rank, parent, false);
		return new MyViewHolder(view);
	}

	@Override
	public void onBindViewHolder(TeamAdapter.MyViewHolder holder, int position) {
		SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
		SpannableString teamName = new SpannableString(data.get(position).nickname);
		teamName.setSpan(new StyleSpan(Typeface.BOLD), 0, teamName.length(), 0);
		spannableStringBuilder.append(teamName);
		SpannableString rank = new SpannableString(getTeamRank(String.valueOf(data.get(position)
				.key), ranks));
		rank.setSpan(new RelativeSizeSpan(0.75f), 0, rank.length(), 0);
		rank.setSpan(new StyleSpan(Typeface.ITALIC), 0, rank.length(), 0);
		spannableStringBuilder.append(rank);


		holder.rankNumber.setText(String.valueOf(data.get(position).team_number));
		holder.teamNumber.setText(spannableStringBuilder, TextView.BufferType.SPANNABLE);
		holder.details.setText(data.get(position).city + ", " + data.get(position).state_prov);
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
			teamNumber = itemView.findViewById(R.id.team_number);
			rankNumber = itemView.findViewById(R.id.rank);
			details = itemView.findViewById(R.id.details);
		}

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(context, TeamSummary.class);
			intent.putExtra("teamNumber", rankNumber.getText().toString());
			context.startActivity(intent);
		}
	}
}
