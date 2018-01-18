package com.aquamorph.frcmanager.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.aquamorph.frcmanager.R;
import com.aquamorph.frcmanager.activities.TeamSummary;
import com.aquamorph.frcmanager.models.Team;
import com.aquamorph.frcmanager.models.Rank;
import com.aquamorph.frcmanager.utils.Constants;

import java.util.ArrayList;

import static android.view.LayoutInflater.from;

/**
 * Populates a RecyclerView with the ranks and team names and number for an event.
 *
 * @author Christian Colglazier
 * @version 12/30/2017
 */
public class RankAdapter extends RecyclerView.Adapter<RankAdapter.MyViewHolder> {

	private String TAG = "RankAdapter";
	private LayoutInflater inflater;
	private Context context;
	private ArrayList<Rank> data;
	private ArrayList<Team> teams;

	public RankAdapter(Context context, ArrayList<Rank> data, ArrayList<Team> teams) {
		inflater = from(context);
		this.data = data;
		this.context = context;
		this.teams = teams;
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.rank, parent, false);
		return new MyViewHolder(view);
	}

	@Override
	public void onBindViewHolder(MyViewHolder holder, int position) {
		holder.teamNumber.setText(String.format("%s. %s", position + 1,
				getTeamName(data.get(0).rankings[position].team_key)));
		holder.teamNumber.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		holder.rankNumber.setText(Constants.formatTeamNumber(data.get(0).rankings[position].team_key));
		holder.details.setVisibility(View.GONE);
		holder.table.removeAllViews();

		TypedValue typedValue = new TypedValue();
		Resources.Theme theme = context.getTheme();
		theme.resolveAttribute(R.attr.textOnBackground, typedValue, true);

		for (int i = 0; i < data.get(0).sort_order_info.length; i += 2) {
			TextView column1 = new TextView(context);
			TextView column2 = new TextView(context);
			TextView column3 = new TextView(context);
			TextView column4 = new TextView(context);
			TableRow rowHeader = new TableRow(context);

			column1.setText(data.get(0).sort_order_info[i].name + ": ");
			column2.setText(String.format("%." + data.get(0).sort_order_info[i].precision +
					"f", data.get(0).rankings[position].sort_orders[i]));

			if (i + 1 < data.get(0).sort_order_info.length) {
				column3.setText(data.get(0).sort_order_info[i + 1].name + ": ");
				column4.setText(String.format("%." + data.get(0).sort_order_info[i + 1].precision +
						"f", data.get(0).rankings[position].sort_orders[i + 1]));
			} else {
				column3.setText("");
				column4.setText("");
			}

			TextViewCompat.setAutoSizeTextTypeWithDefaults(column1,
					TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
			TextViewCompat.setAutoSizeTextTypeWithDefaults(column2,
					TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
			TextViewCompat.setAutoSizeTextTypeWithDefaults(column3,
					TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
			TextViewCompat.setAutoSizeTextTypeWithDefaults(column4,
					TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);

			column1.setTextColor(typedValue.data);
			column2.setTypeface(null, Typeface.ITALIC);
			column2.setTextColor(typedValue.data);
			column3.setTextColor(typedValue.data);
			column4.setTypeface(null, Typeface.ITALIC);
			column4.setTextColor(typedValue.data);
			rowHeader.addView(column1);
			rowHeader.addView(column2);
			rowHeader.addView(column3);
			rowHeader.addView(column4);
			holder.table.addView(rowHeader);
		}
	}

	@Override
	public int getItemCount() {
		if(data.size() == 0) return 0;
		return data.get(0).rankings.length;
	}

	/**
	 * getTeamName() returns the name of a team.
	 *
	 * @param number of the team
	 * @return name of the team
	 */
	public String getTeamName(String number) {
		for (int i = 0; i < teams.size(); i++) {
			if (number.equals(teams.get(i).key)) {
				return teams.get(i).nickname;
			}
		}
		return "";
	}

	public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		TextView teamNumber;
		TextView rankNumber;
		TextView details;
		TableLayout table;

		public MyViewHolder(View itemView) {
			super(itemView);
			itemView.setOnClickListener(this);
			table = itemView.findViewById(R.id.table);
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
