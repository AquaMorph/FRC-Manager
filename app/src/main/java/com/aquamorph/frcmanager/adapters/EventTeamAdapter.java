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
import com.aquamorph.frcmanager.models.EventTeam;

import java.util.ArrayList;

import static android.view.LayoutInflater.from;

/**
 * Populates a RecyclerView with teams at an event.
 *
 * @author Christian Colglazier
 * @version 3/19/2016
 */
public class EventTeamAdapter extends RecyclerView.Adapter<EventTeamAdapter.MyViewHolder> {

	private String TAG = "EventTeamAdapter", info;
	private LayoutInflater inflater;
	private Context context;
	private ArrayList<EventTeam> data;
	private ArrayList<String[]> ranks;

	public EventTeamAdapter(Context context, ArrayList<EventTeam> data, ArrayList<String[]> ranks) {
		inflater = from(context);
		this.data = data;
		this.context = context;
		this.ranks = ranks;
	}

	private String getTeamRank(String teamNumber, ArrayList<String[]> ranks) {
		for (int i = 1; i < ranks.size(); i++) {
			if (ranks.get(i)[1].equals(teamNumber)) return " Ranked #" + ranks.get(i)[0];
		}
		return "";
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.rank, parent, false);
		return new MyViewHolder(view);
	}

	@Override
	public void onBindViewHolder(EventTeamAdapter.MyViewHolder holder, int position) {
		SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
		SpannableString teamName = new SpannableString(data.get(position).nickname);
		teamName.setSpan(new StyleSpan(Typeface.BOLD), 0, teamName.length(), 0);
		spannableStringBuilder.append(teamName);
		SpannableString rank = new SpannableString(getTeamRank(String.valueOf(data.get(position)
				.team_number), ranks));
		rank.setSpan(new RelativeSizeSpan(0.75f), 0, rank.length(), 0);
		rank.setSpan(new StyleSpan(Typeface.ITALIC), 0, rank.length(), 0);
		spannableStringBuilder.append(rank);

		holder.rankNumber.setText(String.valueOf(data.get(position).team_number));
		holder.teamNumber.setText(spannableStringBuilder, TextView.BufferType.SPANNABLE);
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