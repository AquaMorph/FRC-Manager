package com.aquamorph.frcmanager.fragments

import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.activities.MainActivity
import com.aquamorph.frcmanager.models.Event
import com.aquamorph.frcmanager.models.Match
import com.aquamorph.frcmanager.network.Parser
import com.aquamorph.frcmanager.utils.Constants
import com.google.gson.reflect.TypeToken
import java.util.*
import java.util.Collections.sort

/**
 *
 *
 *
 * @author Christian Colglazier
 * @version 2/27/2017
 */

class BracketFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var prefs: SharedPreferences
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private val eventMatches = ArrayList<Match>()
    private var teamNumber: String = ""
    private var eventKey: String = ""
    private lateinit var parserMatch: Parser<ArrayList<Match>>
    private lateinit var parserEvents: Parser<Event>
    private lateinit var qf18: View
    private lateinit var qf27: View
    private lateinit var qf36: View
    private lateinit var qf45: View
    private lateinit var sf1: View
    private lateinit var sf2: View
    private lateinit var f: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.registerOnSharedPreferenceChangeListener(this@BracketFragment)
        teamNumber = prefs.getString("teamNumber", "")
        eventKey = prefs.getString("eventKey", "")

        val view = inflater.inflate(R.layout.bracket, container, false)
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        mSwipeRefreshLayout.setColorSchemeResources(R.color.accent)
        mSwipeRefreshLayout.setOnRefreshListener { MainActivity.refresh() }

        qf18 = view.findViewById(R.id.qf18)
        qf27 = view.findViewById(R.id.qf27)
        qf36 = view.findViewById(R.id.qf36)
        qf45 = view.findViewById(R.id.qf45)
        sf1 = view.findViewById(R.id.sf1)
        sf2 = view.findViewById(R.id.sf2)
        f = view.findViewById(R.id.f)

        if (savedInstanceState != null) populateBracket()
        //		Constants.checkNoDataScreen(eventMatches, view, emptyView);
        return view
    }

    override fun onResume() {
        super.onResume()
        //		if (alliances.size() == 0)
        //			refresh(false);
    }

    fun refresh(force: Boolean?) {
        if (eventKey != "") {
            LoadBracket(force!!).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
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

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == "teamNumber" || key == "eventKey") {
            teamNumber = sharedPreferences.getString("teamNumber", "")
            eventKey = sharedPreferences.getString("eventKey", "")
            refresh(true)
        }
    }

    /**
     * Filters out qualification matches
     */
    private fun filterMatches() {
        val temp = ArrayList<Match>()
        for (i in eventMatches.indices) {
            val compLevel = eventMatches[i].comp_level
            if (compLevel == "qf" || compLevel == "sf" || compLevel == "f") {
                temp.add(eventMatches[i])
            }
        }
        eventMatches.clear()
        eventMatches.addAll(temp)
    }

    private fun filterMatches(compLevel: String, setNumber: String): ArrayList<Match> {
//		for (int i = 0; i < eventMatches.size(); i++) {
        //			if (eventMatches.get(i).comp_level.equals(compLevel) && eventMatches.get(i)
        //					.set_number.equals(setNumber)) {
        //				temp.add(eventMatches.get(i));
        //			}
        //		}
        //		sort(temp);
        return ArrayList()
    }

    private fun getWinner(matches: ArrayList<Match>): Int {
        sort(matches)
        return if (matches.size < 2)
            0
        else if (matches.size == 3) {
            if (matches[2].alliances.red.score > matches[2].alliances.blue.score)
                1
            else
                2
        } else {
            if (matches[0].alliances.red.score > matches[0].alliances.blue.score && matches[1].alliances.red.score > matches[1].alliances.blue.score)
                1
            else if (matches[0].alliances.red.score < matches[0].alliances.blue.score && matches[1].alliances.red.score < matches[1].alliances.blue.score)
                2
            else
                0
        }
    }

    fun getNext(view: View): View? {
        return if (view == qf18)
            sf1
        else if (view == qf27)
            sf2
        else if (view == qf36)
            sf2
        else if (view == qf45)
            sf1
        else
            f
    }

    internal inner class LoadBracket(var force: Boolean) : AsyncTask<Void?, Void?, Void?>() {

        override fun onPreExecute() {
            mSwipeRefreshLayout.isRefreshing = true
            parserMatch = Parser("eventMatches", Constants.getEventMatches(eventKey!!),
                    object : TypeToken<ArrayList<Match>>() {}.type, activity!!, force)
            parserEvents = Parser("Event", Constants.getEvent(eventKey!!),
                    object : TypeToken<Event>() {}.type, activity!!, force)
        }

        override fun doInBackground(vararg params: Void?): Void? {
            parserMatch.fetchJSON(true)
            while (parserMatch.parsingComplete);
            parserEvents.fetchJSON(true)
            while (parserEvents.parsingComplete);
            return null
        }

        override fun onPostExecute(result: Void?) {
            eventMatches.clear()
            eventMatches.addAll(parserMatch.data!!)
            //			alliances.clear();
            //			alliances.addAll(new ArrayList<>(Arrays.asList(parserEvents.getData().alliances)));
            //			Constants.checkNoDataScreen(eventMatches, recyclerView, emptyView);
            filterMatches()
            mSwipeRefreshLayout.isRefreshing = false
            populateBracket()

        }
    }

    fun populateBracket() {
        val resultQF18: Int
        val resultQF27: Int
        val resultQF36: Int
        val resultQF45: Int
        val resultSF1 = 0
        val resultSF2 = 0

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

    companion object {

        /**
         * newInstance creates and returns a new BracketFragment
         *
         * @return BracketFragment
         */
        fun newInstance(): BracketFragment {
            return BracketFragment()
        }
    }
}
