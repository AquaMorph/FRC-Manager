package com.aquamorph.frcmanager.fragments;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aquamorph.frcmanager.R;
import com.aquamorph.frcmanager.activities.MainActivity;
import com.aquamorph.frcmanager.models.Event;
import com.aquamorph.frcmanager.models.Match;
import com.aquamorph.frcmanager.network.Parser;
import com.aquamorph.frcmanager.utils.Constants;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import static java.util.Collections.sort;

/**
 * <p></p>
 *
 * @author Christian Colglazier
 * @version 2/27/2017
 */

public class BracketFragment extends Fragment implements
		SharedPreferences.OnSharedPreferenceChangeListener {

	private String TAG = "BracketFragment";
	private SharedPreferences prefs;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private TextView emptyView;
	private ArrayList<Match> eventMatches = new ArrayList<>();
//	private ArrayList<Event.Alliances> alliances = new ArrayList<>();
	private String teamNumber = "", eventKey = "";
	private Parser<ArrayList<Match>> parserMatch;
	private Parser<Event> parserEvents;
	private View qf18;
	private View qf27;
	private View qf36;
	private View qf45;
	private View sf1;
	private View sf2;
	private View f;

	/**
	 * newInstance creates and returns a new BracketFragment
	 *
	 * @return BracketFragment
	 */
	public static BracketFragment newInstance() {
		return new BracketFragment();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		prefs.registerOnSharedPreferenceChangeListener(BracketFragment.this);
		teamNumber = prefs.getString("teamNumber", "");
		eventKey = prefs.getString("eventKey", "");

		View view = inflater.inflate(R.layout.bracket, container, false);
		mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.accent);
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				MainActivity.refresh();
			}
		});

		qf18 = view.findViewById(R.id.qf18);
		qf27 = view.findViewById(R.id.qf27);
		qf36 = view.findViewById(R.id.qf36);
		qf45 = view.findViewById(R.id.qf45);
		sf1 = view.findViewById(R.id.sf1);
		sf2 = view.findViewById(R.id.sf2);
		f = view.findViewById(R.id.f);

		if (savedInstanceState != null) populateBracket();
//		Constants.checkNoDataScreen(eventMatches, view, emptyView);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
//		if (alliances.size() == 0)
//			refresh(false);
	}

	public void refresh(Boolean force) {
		if (!eventKey.equals("")) {
			new BracketFragment.LoadBracket(force).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}

//	private void fillBracket(View view, Event.Alliances red, int rRank, Event.Alliances blue,
//							 int bRank, int winner) {
//		TextView redRank =  view.findViewById(R.id.redRank);
//		TextView red1 = view.findViewById(R.id.redTeam1);
//		TextView red2 = view.findViewById(R.id.redTeam2);
//		TextView red3 = view.findViewById(R.id.redTeam3);
//		TextView red4 =  view.findViewById(R.id.redTeam4);
//		TextView blueRank = view.findViewById(R.id.blueRank);
//		TextView blue1 = view.findViewById(R.id.blueTeam1);
//		TextView blue2 = view.findViewById(R.id.blueTeam2);
//		TextView blue3 = view.findViewById(R.id.blueTeam3);
//		TextView blue4 = view.findViewById(R.id.blueTeam4);
//
//		if (red != null) {
//			redRank.setText(String.valueOf(rRank));
//			red1.setText(Constants.formatTeamNumber(red.picks[0]));
//			red2.setText(Constants.formatTeamNumber(red.picks[1]));
//			red3.setText(Constants.formatTeamNumber(red.picks[2]));
//			if (red.picks.length > 3)
//				red4.setText(Constants.formatTeamNumber(red.picks[3]));
//			else
//				red4.setVisibility(View.GONE);
//		}
//		if (blue != null) {
//			blueRank.setText(String.valueOf(bRank));
//			blue1.setText(Constants.formatTeamNumber(blue.picks[0]));
//			blue2.setText(Constants.formatTeamNumber(blue.picks[1]));
//			blue3.setText(Constants.formatTeamNumber(blue.picks[2]));
//			if (blue.picks.length > 3)
//				blue4.setText(Constants.formatTeamNumber(blue.picks[3]));
//			else
//				blue4.setVisibility(View.GONE);
//		}
//		setWinner(view, red, rRank, blue, bRank, winner);
//	}
//
//	public void setWinner(View view, Event.Alliances red, int rRank, Event.Alliances blue,
//						  int bRank, int winner) {
//		TextView redRank = view.findViewById(R.id.redRank);
//		TextView red1 = view.findViewById(R.id.redTeam1);
//		TextView red2 = view.findViewById(R.id.redTeam2);
//		TextView red3 = view.findViewById(R.id.redTeam3);
//		TextView red4 = view.findViewById(R.id.redTeam4);
//		TextView blueRank = view.findViewById(R.id.blueRank);
//		TextView blue1 = view.findViewById(R.id.blueTeam1);
//		TextView blue2 = view.findViewById(R.id.blueTeam2);
//		TextView blue3 = view.findViewById(R.id.blueTeam3);
//		TextView blue4 = view.findViewById(R.id.blueTeam4);
//
//		if (winner == 1) {
//			redRank.setTypeface(null, Typeface.BOLD);
//			red1.setTypeface(null, Typeface.BOLD);
//			red2.setTypeface(null, Typeface.BOLD);
//			red3.setTypeface(null, Typeface.BOLD);
//			red4.setTypeface(null, Typeface.BOLD);
//			if (((view.equals(qf18) || view.equals(qf27)) && (rRank == 1 || rRank == 2)) ||
//					((view.equals(sf1) && (rRank == 1 || rRank == 8 || rRank == 4 || rRank == 5))))
//				fillBracket(getNext(view), red, rRank, null, 0, 0);
//			else
//				fillBracket(getNext(view), null, 0, red, rRank, 0);
//		} else if (winner == 2) {
//			blueRank.setTypeface(null, Typeface.BOLD);
//			blue1.setTypeface(null, Typeface.BOLD);
//			blue2.setTypeface(null, Typeface.BOLD);
//			blue3.setTypeface(null, Typeface.BOLD);
//			blue4.setTypeface(null, Typeface.BOLD);
//			if (((view.equals(qf18) || view.equals(qf27)) && (bRank == 8 || bRank == 7) ||
//					(view.equals(sf1) && rRank == 1 || rRank == 8 || rRank == 4 || rRank == 5)))
//				fillBracket(getNext(view), blue, bRank, null, 0, 0);
//			else
//				fillBracket(getNext(view), null, 0, blue, bRank, 0);
//		}
//	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals("teamNumber") || key.equals("eventKey")) {
			teamNumber = sharedPreferences.getString("teamNumber", "");
			eventKey = sharedPreferences.getString("eventKey", "");
			refresh(true);
		}
	}

	/**
	 * Filters out qualification matches
	 */
	private void filterMatches() {
		ArrayList<Match> temp = new ArrayList<>();
		for (int i = 0; i < eventMatches.size(); i++) {
			String compLevel = eventMatches.get(i).getComp_level();
			if (compLevel.equals("qf") || compLevel.equals("sf") || compLevel.equals("f")) {
				temp.add(eventMatches.get(i));
			}
		}
		eventMatches.clear();
		eventMatches.addAll(temp);
	}

	private ArrayList<Match> filterMatches(String compLevel, String setNumber) {
		ArrayList<Match> temp = new ArrayList<>();
//		for (int i = 0; i < eventMatches.size(); i++) {
//			if (eventMatches.get(i).comp_level.equals(compLevel) && eventMatches.get(i)
//					.set_number.equals(setNumber)) {
//				temp.add(eventMatches.get(i));
//			}
//		}
//		sort(temp);
		return temp;
	}

	private int getWinner(ArrayList<Match> matches) {
		sort(matches);
		if (matches.size() < 2)
			return 0;
		else if (matches.size() == 3) {
			if (matches.get(2).getAlliances().getRed().getScore() >
					matches.get(2).getAlliances().getBlue().getScore())
				return 1;
			else
				return 2;
		} else {
			if (matches.get(0).getAlliances().getRed().getScore() >
					matches.get(0).getAlliances().getBlue().getScore() &&
					matches.get(1).getAlliances().getRed().getScore() >
							matches.get(1).getAlliances().getBlue().getScore())
				return 1;
			else if (matches.get(0).getAlliances().getRed().getScore() <
					matches.get(0).getAlliances().getBlue().getScore() &&
					matches.get(1).getAlliances().getRed().getScore() <
							matches.get(1).getAlliances().getBlue().getScore())
				return 2;
			else
				return 0;
		}
	}

	public View getNext(View view) {
		if (view.equals(qf18))
			return sf1;
		else if (view.equals(qf27))
			return sf2;
		else if (view.equals(qf36))
			return sf2;
		else if (view.equals(qf45))
			return sf1;
		else
			return f;
	}

	class LoadBracket extends AsyncTask<Void, Void, Void> {

		boolean force;

		public LoadBracket(boolean force) {
			this.force = force;
		}

		@Override
		protected void onPreExecute() {
			mSwipeRefreshLayout.setRefreshing(true);
			parserMatch = new Parser<>("eventMatches", Constants.getEventMatches(eventKey),
					new TypeToken<ArrayList<Match>>() {
					}.getType(), getActivity(), force);
			parserEvents = new Parser<>("Event",
					Constants.getEvent(eventKey), new
					TypeToken<Event>() {
					}.getType(), getActivity(), force);
		}

		@Override
		protected Void doInBackground(Void... params) {
			parserMatch.fetchJSON(true);
			while (parserMatch.parsingComplete) ;
			parserEvents.fetchJSON(true);
			while (parserEvents.parsingComplete) ;
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			eventMatches.clear();
			eventMatches.addAll(parserMatch.getData());
//			alliances.clear();
//			alliances.addAll(new ArrayList<>(Arrays.asList(parserEvents.getData().alliances)));
//			Constants.checkNoDataScreen(eventMatches, recyclerView, emptyView);
			filterMatches();
			mSwipeRefreshLayout.setRefreshing(false);
			populateBracket();

		}
	}

	public void populateBracket() {
		int resultQF18;
		int resultQF27;
		int resultQF36;
		int resultQF45;
		int resultSF1 = 0;
		int resultSF2 = 0;

//		if (alliances.size() > 0) {
//			resultQF18 = getWinner(filterMatches("qf", "1"));
//			resultQF27 = getWinner(filterMatches("qf", "3"));
//			resultQF36 = getWinner(filterMatches("qf", "4"));
//			resultQF45 = getWinner(filterMatches("qf", "2"));
//			fillBracket(qf18, alliances.get(0), 1, alliances.get(7), 8, resultQF18);
//			fillBracket(qf27, alliances.get(1), 2, alliances.get(6), 7, resultQF27);
//			fillBracket(qf36, alliances.get(2), 3, alliances.get(5), 6, resultQF36);
//			fillBracket(qf45, alliances.get(3), 4, alliances.get(4), 5, resultQF45);
//
//			if (resultQF18 != 0 && resultQF45 != 0) {
//				int redSF;
//				if (resultQF18 == 1)
//					redSF = 0;
//				else
//					redSF = 7;
//				int blueSF;
//				if (resultQF45 == 1)
//					blueSF = 3;
//				else
//					blueSF = 4;
//				resultSF1 = getWinner(filterMatches("sf", "1"));
//				fillBracket(sf1, alliances.get(redSF), redSF + 1, alliances.get(blueSF),
//						blueSF + 1, resultSF1);
//			}
//			if (resultQF27 != 0 && resultQF36 != 0) {
//				int redSF;
//				if (resultQF27 == 1)
//					redSF = 1;
//				else
//					redSF = 6;
//				int blueSF;
//				if (resultQF36 == 1)
//					blueSF = 2;
//				else
//					blueSF = 5;
//				resultSF2 = getWinner(filterMatches("sf", "2"));
//				fillBracket(sf2, alliances.get(redSF), redSF + 1, alliances.get(blueSF),
//						blueSF + 1, resultSF2);
//			}
//			if (resultSF1 != 0 && resultSF2 != 0) {
//				setWinner(f, null, -1, null, -1, getWinner(filterMatches("f", "1")));
//			}
//		}
	}
}
