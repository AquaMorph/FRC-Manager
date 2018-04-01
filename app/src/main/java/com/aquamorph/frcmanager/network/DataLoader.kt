package com.aquamorph.frcmanager.network

import android.app.Activity
import android.os.AsyncTask
import android.os.SystemClock
import com.aquamorph.frcmanager.adapters.SectionsPagerAdapter
import com.aquamorph.frcmanager.fragments.*
import com.aquamorph.frcmanager.models.*
import com.aquamorph.frcmanager.utils.Constants
import com.aquamorph.frcmanager.utils.Logging
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.util.*
import java.util.Collections.sort

/**
 * Loads needed dataLoader.
 *
 * @author Christian Colglazier
 * @version 2/21/2018
 */

class DataLoader(activity: Activity, adapter: SectionsPagerAdapter) {

    init {
        matchTabs.add(Tab("Team Schedule", TeamScheduleFragment.newInstance()))
        matchTabs.add(Tab("Event Schedule", EventScheduleFragment.newInstance()))
        rankTabs.add(Tab("Rankings", RankFragment.newInstance()))
        teamTabs.add(Tab("Teams", TeamFragment.newInstance()))
        allianceTabs.add(Tab("Alliances", AllianceFragment.newInstance()))
        awardTabs.add(Tab("Awards", AwardFragment.newInstance()))
        setDataContainers(false, activity)
    }

    internal class Load : AsyncTask<Void?, Void?, Void?> {

        private var dataContainer: DataContainer<*>? = null
        private var tabs: ArrayList<Tab>? = null
        private var adapter: SectionsPagerAdapter? = null
        private var isRank = false
        private var isSortable = false

        constructor(dataContainer: DataContainer<*>, tabs: ArrayList<Tab>, adapter: SectionsPagerAdapter) {
            this.dataContainer = dataContainer
            this.tabs = tabs
            this.adapter = adapter
        }

        constructor(dataContainer: DataContainer<*>, isRank: Boolean, isSortable: Boolean, tabs: ArrayList<Tab>,
                    adapter: SectionsPagerAdapter) {
            this.dataContainer = dataContainer
            this.isRank = isRank
            this.isSortable = isSortable
            this.tabs = tabs
            this.adapter = adapter
        }

        override fun onPreExecute() {
            dataContainer!!.complete = false
        }

        override fun doInBackground(vararg params: Void?): Void? {
            try {
                dataContainer!!.parser.fetchJSON(true)
            } catch (exception: JsonSyntaxException) {
                Logging.error(this, "JSON Parsing Error", 0)
                Logging.error(this, exception.message!!, 0)
            }

            while (dataContainer!!.parser.parsingComplete) {
                SystemClock.sleep(Constants.THREAD_WAIT_TIME.toLong())
            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            dataContainer!!.data.clear()
            if (dataContainer!!.parser.data != null) {
                if (isRank) {
                    (dataContainer!!.data as MutableList<Any?>).add(dataContainer!!.parser.data)
                } else {
                    dataContainer!!.data.addAll(dataContainer!!.parser.data as Collection<Nothing>)
                    if (isSortable) {
                        sort(dataContainer!!.data as List<Nothing>)
                    }
                }
            }
            if (dataContainer!!.data.isEmpty()) {
                for (tab in tabs!!) {
                    if (adapter!!.isTab(tab.name)!!) {
                        adapter!!.removeFrag(adapter!!.tabPosition(tab.name))
                    }
                }
            } else {
                for (tab in tabs!!) {
                    if ((!adapter!!.isTab(tab.name)!!)) {
                        adapter!!.addFrag(tab)
                    }
                }
            }
            dataContainer!!.complete = true
        }
    }

    companion object {
        var eventKey = ""
        var teamNumber = ""
        lateinit var teamDC: DataContainer<Team>
        lateinit var rankDC: DataContainer<Rank>
        lateinit var awardDC: DataContainer<Award>
        lateinit var matchDC: DataContainer<Match>
        lateinit var allianceDC: DataContainer<Alliance>
        private val teamTabs = ArrayList<Tab>()
        private val rankTabs = ArrayList<Tab>()
        private val awardTabs = ArrayList<Tab>()
        private val matchTabs = ArrayList<Tab>()
        private val allianceTabs = ArrayList<Tab>()

        fun refresh(force: Boolean, adapter: SectionsPagerAdapter, activity: Activity) {
            if (eventKey != "") {
                setDataContainers(force, activity)
                Load(matchDC, false, true, matchTabs, adapter).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                Load(teamDC, false, true, teamTabs, adapter).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                Load(rankDC, true, false, rankTabs, adapter).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                Load(awardDC, awardTabs, adapter).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                Load(allianceDC, allianceTabs, adapter).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
            }
        }

        private fun setDataContainers(force: Boolean, activity: Activity) {
            teamDC = DataContainer(force, activity,
                    object : TypeToken<ArrayList<Team>>() {

                    }.type, Constants.getEventTeams(eventKey),
                    "eventTeams")
            rankDC = DataContainer(force, activity,
                    object : TypeToken<Rank>() {

                    }.type, Constants.getEventRanks(eventKey),
                    "eventRank")
            awardDC = DataContainer(force, activity,
                    object : TypeToken<ArrayList<Award>>() {

                    }.type, Constants.getEventAwards(eventKey),
                    "eventAwards")
            matchDC = DataContainer(force, activity,
                    object : TypeToken<ArrayList<Match>>() {

                    }.type, Constants.getEventMatches(eventKey),
                    "eventMatches")
            allianceDC = DataContainer(force, activity,
                    object : TypeToken<ArrayList<Alliance>>() {

                    }.type, Constants.getAlliancesURL(eventKey),
                    "Alliance")
        }
    }
}
