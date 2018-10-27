package com.aquamorph.frcmanager.network

import android.app.Activity
import android.content.Context
import android.support.design.widget.Snackbar
import android.view.View
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.adapters.SectionsPagerAdapter
import com.aquamorph.frcmanager.fragments.*
import com.aquamorph.frcmanager.models.*
import com.aquamorph.frcmanager.utils.Logging
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.Collections.sort
import kotlin.collections.ArrayList

/**
 * Loads needed dataLoader.
 *
 * @author Christian Colglazier
 * @version 8/19/2018
 */

class DataLoader {

    init {
        matchTabs.add(Tab("Team Schedule", TeamScheduleFragment.newInstance()))
        matchTabs.add(Tab("Event Schedule", EventScheduleFragment.newInstance()))
        rankTabs.add(Tab("Rankings", RankFragment.newInstance()))
        teamTabs.add(Tab("Teams", TeamFragment.newInstance()))
        allianceTabs.add(Tab("Alliances", AllianceFragment.newInstance()))
        awardTabs.add(Tab("Awards", AwardFragment.newInstance()))
    }

    companion object {
        var eventKey = ""
        var teamNumber = ""
        val teamDC = DataContainer<Team>("eventTeams")
        val rankDC = DataContainer<Rank>("eventRankings")
        val awardDC = DataContainer<Award>("eventAwards")
        val matchDC = DataContainer<Match>("eventMatches")
        val allianceDC = DataContainer<Alliance>("eventAlliances")
        private val teamTabs = ArrayList<Tab>()
        private val rankTabs = ArrayList<Tab>()
        private val awardTabs = ArrayList<Tab>()
        private val matchTabs = ArrayList<Tab>()
        private val allianceTabs = ArrayList<Tab>()


        private var disposable: Disposable? = null

        fun isRankEmpty(dataContainer: DataContainer<*>): Boolean {
            return dataContainer.data.get(0) is Rank &&
                    (dataContainer.data.get(0) as Rank).rankings.isEmpty()
        }

        fun getData(dataContainer: DataContainer<*>,
                    isRank: Boolean, isSortable: Boolean, tabs: ArrayList<Tab>,
                    adapter: SectionsPagerAdapter, observer: Int,
                    activity: Activity) {
            dataContainer.complete = false
            val t = arrayOf(RetrofitInstance.getRetrofit(activity).create(TbaApi::class.java).getEventMatches(DataLoader.eventKey),
                    RetrofitInstance.getRetrofit(activity).create(TbaApi::class.java).getEventTeams(DataLoader.eventKey),
                    RetrofitInstance.getRetrofit(activity).create(TbaApi::class.java).getEventRankings(DataLoader.eventKey),
                    RetrofitInstance.getRetrofit(activity).create(TbaApi::class.java).getEventAwards(DataLoader.eventKey),
                    RetrofitInstance.getRetrofit(activity).create(TbaApi::class.java).getEventAlliances(DataLoader.eventKey))
            disposable = t[observer].subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe( { result -> updateData(dataContainer, isRank, isSortable, tabs, adapter, result as Any) },
                                { error -> Logging.error(this, error.toString(), 0) })
        }

        fun updateData(dataContainer: DataContainer<*>, isRank: Boolean, isSortable: Boolean, tabs: ArrayList<Tab>,
                       adapter: SectionsPagerAdapter,
                       result: Any) {
            dataContainer.data.clear()
                if (isRank) {
                    (dataContainer.data as MutableList<Any?>).add(result)
                } else {
                    dataContainer.data.addAll(result as Collection<Nothing>)
                    if (isSortable) {
                        sort(dataContainer.data as List<Nothing>)
                    }
                }
            if (dataContainer.data.isEmpty() || isRankEmpty(dataContainer)) {
                for (tab in tabs) {
                    if (adapter.isTab(tab.name)!!) {
                        adapter.removeFrag(adapter.tabPosition(tab.name))
                    }
                }
            } else {
                for (tab in tabs) {
                    if ((!adapter.isTab(tab.name)!!)) {
                        adapter.addFrag(tab)
                    }
                }
            }
            dataContainer.complete = true
        }

        fun refresh(adapter: SectionsPagerAdapter, activity: Activity) {
            if (eventKey != "") {
                // Checks for internet connections
                if(!NetworkCheck.hasNetwork(activity)) {
                    Snackbar.make(activity.findViewById<View>(R.id.myCoordinatorLayout),
                            R.string.no_connection_message, Snackbar.LENGTH_LONG).show()
                }
                getData(matchDC, false, true, matchTabs, adapter, 0, activity)
                getData(teamDC, false, true, teamTabs, adapter, 1, activity)
                getData(rankDC, true, false, rankTabs, adapter, 2, activity)
                getData(awardDC, false, false, awardTabs, adapter, 3, activity)
                getData(allianceDC, false, false, allianceTabs, adapter, 4, activity)
            }
        }
    }
}
