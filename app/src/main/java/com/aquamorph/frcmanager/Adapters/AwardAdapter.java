package com.aquamorph.frcmanager.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aquamorph.frcmanager.R;
import com.aquamorph.frcmanager.models.Award;

import java.util.ArrayList;

import static android.view.LayoutInflater.from;

/**
 * Populates the recyclerview with award data
 *
 * @author Christian Colglazier
 * @version 1/26/2016
 */
public class AwardAdapter extends RecyclerView.Adapter<AwardAdapter.MyViewHolder> {

	private String TAG = "AwardAdapter";
	private LayoutInflater inflater;
	private ArrayList<Award> data;
	private String team = "";

	public AwardAdapter(Context context, ArrayList<Award> data) {
		inflater = from(context);
		this.data = data;
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.award, parent, false);
		MyViewHolder holder = new MyViewHolder(view);
		return holder;
	}

	@Override
	public void onBindViewHolder(MyViewHolder holder, int position) {
		team = "";
		for(int i = 0; data.get(position).recipient_list.length > i; i++) {
			if(i > 0) team += "\n";
			team += data.get(position).recipient_list[i].team_number;
		}
		holder.teamNumber.setText(team);
		holder.award.setText(data.get(position).name);
	}

	@Override
	public int getItemCount() {
		return data.size();
	}

	public class MyViewHolder extends RecyclerView.ViewHolder {

		TextView teamNumber;
		TextView award;

		public MyViewHolder(View itemView) {
			super(itemView);
			teamNumber = (TextView) itemView.findViewById(R.id.team_number);
			award = (TextView) itemView.findViewById(R.id.award_name);
		}
	}
}