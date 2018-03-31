package com.aquamorph.frcmanager.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aquamorph.frcmanager.R;
import com.aquamorph.frcmanager.models.Award;
import com.aquamorph.frcmanager.utils.Constants;

import java.util.ArrayList;

import static android.view.LayoutInflater.from;

/**
 * Populates the recyclerview with award data
 *
 * @author Christian Colglazier
 * @version 3/29/2016
 */
public class AwardAdapter extends RecyclerView.Adapter<AwardAdapter.MyViewHolder> {

	private LayoutInflater inflater;
	private ArrayList<Award> data;

	public AwardAdapter(Context context, ArrayList<Award> data) {
		inflater = from(context);
		this.data = data;
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.award, parent, false);
		return new MyViewHolder(view);
	}

	@Override
	public void onBindViewHolder(MyViewHolder holder, int position) {
		holder.setIsRecyclable(false);
		String team = "";
		String awardee = "";

		for (int i = 0; data.get(position).getRecipient_list().length > i; i++) {
			if (data.get(position).getRecipient_list()[i].getTeam_key() != null) {
				if (i > 0) team += "\n";
				team += Constants.INSTANCE.formatTeamNumber(data.get(position).getRecipient_list()[i].getTeam_key());
			}
			if (data.get(position).getRecipient_list()[i].getAwardee() != null) {
				if (i > 0) awardee += "\n";
				awardee += data.get(position).getRecipient_list()[i].getAwardee();
			}
		}
		if (awardee.equals("")) {
			holder.details.setVisibility(View.GONE);
		} else {
			holder.details.setText(awardee);
		}
		holder.teamNumber.setText(team);
		holder.award.setText(data.get(position).getName());
	}

	@Override
	public void onViewDetachedFromWindow(MyViewHolder holder) {
		holder.itemView.clearAnimation();
	}

	@Override
	public int getItemCount() {
		return data.size();
	}

	public class MyViewHolder extends RecyclerView.ViewHolder {

		TextView teamNumber;
		TextView award;
		TextView details;

		public MyViewHolder(View itemView) {
			super(itemView);
			teamNumber = itemView.findViewById(R.id.team_number);
			award = itemView.findViewById(R.id.award_name);
			details = itemView.findViewById(R.id.award_details);
		}
	}
}
