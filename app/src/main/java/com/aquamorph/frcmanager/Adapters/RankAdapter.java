package com.aquamorph.frcmanager.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
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
		holder.teamNumber.setText(data.get(position + 1)[0]);
		holder.rankNumber.setText(data.get(position + 1)[1]);
		holder.details.setVisibility(View.GONE);
		holder.table.removeAllViews();

		TypedValue typedValue = new TypedValue();
		Resources.Theme theme = context.getTheme();
		theme.resolveAttribute(R.attr.textOnBackground, typedValue, true);

		for (int i = 2; i < data.get(position).length; i+=2) {
			TextView column1 = new TextView(context);
			TextView column2 = new TextView(context);
			TextView column3 = new TextView(context);
			TextView column4 = new TextView(context);
			TableRow rowHeader = new TableRow(context);

			column1.setText(data.get(0)[i] + ": ");
			column2.setText(data.get(position + 1)[i]);

			if(i+1 < data.get(position).length) {
				column3.setText(data.get(0)[i+1] + ":");
				column4.setText(data.get(position + 1)[i+1]);
			} else {
				column3.setText("");
				column4.setText("");
			}

			column1.setTextSize(context.getResources().getDimension(R.dimen.text_size));
			column1.setTextColor(typedValue.data);
			column2.setTextSize(context.getResources().getDimension(R.dimen.text_size));
			column2.setTypeface(null, Typeface.ITALIC);
			column2.setTextColor(typedValue.data);
			column3.setTextSize(context.getResources().getDimension(R.dimen.text_size));
			column3.setTextColor(typedValue.data);
			column4.setTextSize(context.getResources().getDimension(R.dimen.text_size));
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
		return data.size() - 1;
	}

	public class MyViewHolder extends RecyclerView.ViewHolder {

		TextView teamNumber;
		TextView rankNumber;
		TextView details;
		TableLayout table;

		public MyViewHolder(View itemView) {
			super(itemView);
			table = (TableLayout) itemView.findViewById(R.id.table);
			teamNumber = (TextView) itemView.findViewById(R.id.team_number);
			rankNumber = (TextView) itemView.findViewById(R.id.rank);
			details = (TextView) itemView.findViewById(R.id.details);
		}
	}
}
