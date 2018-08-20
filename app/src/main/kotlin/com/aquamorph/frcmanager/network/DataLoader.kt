package com.aquamorph.frcmanager.network

import android.app.Activity
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

class DataLoader() {

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
                    adapter: SectionsPagerAdapter, observer: Int) {


            dataContainer.complete = false
            var t = arrayOf(RetrofitInstance.getRetrofit().create(TbaApi::class.java).getEventMatches(DataLoader.eventKey),
                    RetrofitInstance.getRetrofit().create(TbaApi::class.java).getEventTeams(DataLoader.eventKey),
                    RetrofitInstance.getRetrofit().create(TbaApi::class.java).getEventRankings(DataLoader.eventKey),
                    RetrofitInstance.getRetrofit().create(TbaApi::class.java).getEventAwards(DataLoader.eventKey),
                    RetrofitInstance.getRetrofit().create(TbaApi::class.java).getEventAlliances(DataLoader.eventKey))
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

        fun refresh(adapter: SectionsPagerAdapter) {
            if (eventKey != "") {
                getData(matchDC, false, true, matchTabs, adapter, 0)
                getData(teamDC, false, true, teamTabs, adapter, 1)
                getData(rankDC, true, false, rankTabs, adapter, 2)
                getData(awardDC, false, false, awardTabs, adapter, 3)
                getData(allianceDC, false, false, allianceTabs, adapter, 4)
            }
        }
    }
}
