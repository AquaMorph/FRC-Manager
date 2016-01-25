package com.aquamorph.frcmanager.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aquamorph.frcmanager.R;

import java.util.ArrayList;

import static android.view.LayoutInflater.from;

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.MyViewHolder> {

	private String TAG = "RankAdapter";
	private LayoutInflater inflater;
	private Context context;
	private ArrayList<String[]> data;

	/**
	 * RankAdapter constructor
	 *
	 * @param context context of the fragment
	 * @param data ranking data in an array
	 */
	public RankAdapter(Context context, ArrayList<String[]> data) {
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
	public void onBindViewHolder(MyViewHolder holder, int position) {
		holder.teamNumber.setText(data.get(position)[0]);
		holder.teamName.setText("Team Robobotics");
		holder.rankNumber.setText(data.get(position)[1]);
		holder.details.setText(data.get(position)[2]);
	}

	@Override
	public int getItemCount() {
		return data.size();
	}

	public class MyViewHolder extends RecyclerView.ViewHolder {

		TextView teamNumber;
		TextView teamName;
		TextView rankNumber;
		TextView details;

		public MyViewHolder(View itemView) {
			super(itemView);
			teamNumber = (TextView) itemView.findViewById(R.id.team_number);
			teamName = (TextView) itemView.findViewById(R.id.team_name);
			rankNumber = (TextView) itemView.findViewById(R.id.rank);
			details = (TextView) itemView.findViewById(R.id.details);
		}
	}
}
