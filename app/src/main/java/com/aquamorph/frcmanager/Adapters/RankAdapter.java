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

	private String TAG = "RankAdapter", info;
	private LayoutInflater inflater;
	private Context context;
	private ArrayList<String[]> data;

	/**
	 * RankAdapter constructor
	 *
	 * @param context context of the fragment
	 * @param data    ranking data in an array
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
		info = "";
		for (int i = 2; i < data.get(position).length; i++) {
			info += data.get(0)[i] + ": " + data.get(position + 1)[i] + " ";
		}
		holder.teamNumber.setText(data.get(position + 1)[0]);
		holder.rankNumber.setText(data.get(position + 1)[1]);
		holder.details.setText(info);
	}

	@Override
	public int getItemCount() {
		return data.size() - 1;
	}

	public class MyViewHolder extends RecyclerView.ViewHolder {

		TextView teamNumber;
		TextView rankNumber;
		TextView details;

		public MyViewHolder(View itemView) {
			super(itemView);
			teamNumber = (TextView) itemView.findViewById(R.id.team_number);
			rankNumber = (TextView) itemView.findViewById(R.id.rank);
			details = (TextView) itemView.findViewById(R.id.details);
		}
	}
}
