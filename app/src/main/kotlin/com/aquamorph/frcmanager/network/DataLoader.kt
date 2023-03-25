package com.aquamorph.frcmanager.network

import android.app.Activity
import android.view.View
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.activities.MainActivity
import com.aquamorph.frcmanager.adapters.SectionsPagerAdapter
import com.aquamorph.frcmanager.fragments.AllianceFragment
import com.aquamorph.frcmanager.fragments.AwardFragment
import com.aquamorph.frcmanager.fragments.DistrictRankFragment
import com.aquamorph.frcmanager.fragments.EventScheduleFragment
import com.aquamorph.frcmanager.fragments.RankFragment
import com.aquamorph.frcmanager.fragments.TeamFragment
import com.aquamorph.frcmanager.fragments.TeamScheduleFragment
import com.aquamorph.frcmanager.models.Alliance
import com.aquamorph.frcmanager.models.Award
import com.aquamorph.frcmanager.models.DistrictRank
import com.aquamorph.frcmanager.models.Match
import com.aquamorph.frcmanager.models.Rank
import com.aquamorph.frcmanager.models.TBAPrediction
import com.aquamorph.frcmanager.models.Tab
import com.aquamorph.frcmanager.models.Team
import com.aquamorph.frcmanager.utils.Constants
import com.aquamorph.frcmanager.utils.Constants.isDistrict
import com.aquamorph.frcmanager.utils.Logging
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.Collections.sort
import kotlin.collections.ArrayList
import retrofit2.Response

/**
 * Loads needed dataLoader.
 *
 * @author Christian Colglazier
 * @version 2/20/2020
 */

class DataLoader {

    init {
        matchTabs.add(Tab("Team Schedule", TeamScheduleFragment.newInstance()))
        matchTabs.add(Tab("Event Schedule", EventScheduleFragment.newInstance()))
        rankTabs.add(Tab("Rankings", RankFragment.newInstance()))
        teamTabs.add(Tab("Teams", TeamFragment.newInstance()))
        allianceTabs.add(Tab("Alliances", AllianceFragment.newInstance()))
        awardTabs.add(Tab("Awards", AwardFragment.newInstance()))
        districtRankTabs.add(Tab("District Rankings", DistrictRankFragment.newInstance()))
    }

    companion object {
        var eventKey = ""
        var teamNumber = ""
        var districtKey = ""
        var year = ""
        val teamDC = DataContainer<Team>()
        val rankDC = DataContainer<Rank>()
        val awardDC = DataContainer<Award>()
        val matchDC = DataContainer<Match>()
        val allianceDC = DataContainer<Alliance>()
        val districtRankDC = DataContainer<DistrictRank>()
        val districtTeamDC = DataContainer<Team>()
        val tbaPredictionsDC = DataContainer<TBAPrediction.PredMatch>()
        private val teamTabs = ArrayList<Tab>()
        private val rankTabs = ArrayList<Tab>()
        private val awardTabs = ArrayList<Tab>()
        private val matchTabs = ArrayList<Tab>()
        private val allianceTabs = ArrayList<Tab>()
        private val districtRankTabs = ArrayList<Tab>()

        private var disposable: Disposable? = null

        /**
         * isRankEmpty() returns if there is ranking data.
         *
         * @param dataContainer rank data container
         * @return rank data state
         */
        private fun isRankEmpty(dataContainer: DataContainer<*>): Boolean {
            return dataContainer.data[0] is Rank &&
                    (dataContainer.data[0] as Rank).rankings!!.isEmpty()
        }

        /**
         * getData() starts retrofit data request.
         *
         * @param dataContainer data container
         * @param tabs list of tabs
         * @param adapter tab adapter
         * @param observer count of observer
         * @param activity main activity
         */
        private fun getData(
            dataContainer: DataContainer<*>,
            tabs: ArrayList<Tab>,
            adapter: SectionsPagerAdapter,
            observer: Int,
            activity: Activity
        ) {
            dataContainer.complete = false
            val retrofit: ArrayList<Observable<out Any>> =
                    arrayListOf(RetrofitInstance.getRetrofit(activity)
                            .create(TbaApi::class.java).getEventMatches(eventKey))
            retrofit.add(RetrofitInstance.getRetrofit(activity)
                    .create(TbaApi::class.java).getEventTeams(eventKey))
            retrofit.add(RetrofitInstance.getRetrofit(activity)
                    .create(TbaApi::class.java).getEventRankings(eventKey))
            retrofit.add(RetrofitInstance.getRetrofit(activity)
                    .create(TbaApi::class.java).getEventAwards(eventKey))
            retrofit.add(RetrofitInstance.getRetrofit(activity)
                    .create(TbaApi::class.java).getEventAlliances(eventKey))
            retrofit.add(RetrofitInstance.getRetrofit(activity)
                    .create(TbaApi::class.java).getDistrictRankings(districtKey))
            retrofit.add(RetrofitInstance.getRetrofit(activity)
                    .create(TbaApi::class.java).getDistrictTeams(districtKey))
            retrofit.add(RetrofitInstance.getRetrofit(activity)
                    .create(TbaApi::class.java).getEventPredictions(eventKey))
            disposable = retrofit[observer]
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ result ->
                                    dataContainer.newData = (result as Response<*>).raw()!!
                                        .networkResponse() != null
                                    updateData(dataContainer, tabs, adapter,
                                            result.body() as Any) },
                                { error ->
                                    Logging.error(this, error.toString(), 0)
                                    if (observer != 7) {
                                        removeTab(tabs, adapter)
                                    }
                                    dataContainer.complete = true })
        }

        /**
         * updateData() adds data to the container.
         *
         * @param dataContainer data container
         * @param tabs list of tabs
         * @param adapter tab adapter
         * @param result data
         */
        private fun updateData(
            dataContainer: DataContainer<*>,
            tabs: ArrayList<Tab>,
            adapter: SectionsPagerAdapter,
            result: Any
        ) {
            dataContainer.data.clear()
                if (result is Rank) {
                    (dataContainer.data as MutableList<Any?>).add(result)
                } else if (result is TBAPrediction) {
                        (dataContainer.data as MutableList<Any?>)
                                .addAll(Constants.tbaPredictionToArray(result))
                } else {
                    dataContainer.data.addAll(result as Collection<Nothing>)
                    if (result is Comparable<*>) {
                        sort(dataContainer.data as List<Nothing>)
                    }
                }
            if (dataContainer.data.isEmpty() || isRankEmpty(dataContainer)) {
                removeTab(tabs, adapter)
            } else {
                addTab(tabs, adapter)
            }
            dataContainer.complete = true
        }

        /**
         * removeTab() removes tab from app.
         *
         * @param tabs list of tabs
         * @param adapter tab adapter
         */
        private fun removeTab(tabs: ArrayList<Tab>, adapter: SectionsPagerAdapter) {
            for (tab in tabs) {
                if (adapter.isTab(tab.name)!!) {
                    adapter.removeFragment(adapter.tabPosition(tab.name))
                }
            }
        }

        /**
         * addTab() adds tab to app.
         *
         * @param tabs list of tabs
         * @param adapter tab adapter
         */
        private fun addTab(tabs: ArrayList<Tab>, adapter: SectionsPagerAdapter) {
            for (tab in tabs) {
                if ((!adapter.isTab(tab.name))) {
                    adapter.addFragment(tab)
                }
            }
        }

        /**
         * clearData() removes all app data.
         */
        fun clearData() {
            teamDC.data.clear()
            rankDC.data.clear()
            awardDC.data.clear()
            matchDC.data.clear()
            allianceDC.data.clear()
            districtRankDC.data.clear()
            districtTeamDC.data.clear()
            tbaPredictionsDC.data.clear()
        }

        /**
         * refresh() updates all event data.
         *
         * @param adapter tab adapter
         * @param activity main activity
         */
        fun refresh(adapter: SectionsPagerAdapter, activity: Activity) {
            if (eventKey != "") {
                // Checks for internet connections
                if (!NetworkCheck.hasNetwork(activity)) {
                    Snackbar.make(activity.findViewById<View>(R.id.myCoordinatorLayout),
                            R.string.noConnectionMessage,
                            Snackbar.LENGTH_LONG).show()
                }
                getData(matchDC, matchTabs, adapter, 0, activity)
                getData(teamDC, teamTabs, adapter, 1, activity)
                getData(rankDC, rankTabs, adapter, 2, activity)
                getData(awardDC, awardTabs, adapter, 3, activity)
                getData(allianceDC, allianceTabs, adapter, 4, activity)
                if (isDistrict()) {
                    getData(districtRankDC, districtRankTabs, adapter, 5, activity)
                    getData(districtTeamDC, districtRankTabs, adapter, 6, activity)
                    addTab(districtRankTabs, adapter)
                } else {
                    removeTab(districtRankTabs, adapter)
                }
                if (MainActivity.predEnabled) {
                    getData(tbaPredictionsDC, matchTabs, adapter, 7, activity)
                }
                // Check if FIRST or event feed is down
                RetrofitInstance.getRetrofit(activity!!).create(TbaApi::class.java)
                        .getStatus().subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ result -> if (result != null) {
                            if (result.isDatafeedDown) {
                                Snackbar.make(activity.findViewById<View>(R.id.myCoordinatorLayout),
                                        R.string.firstServerDown,
                                        Snackbar.LENGTH_LONG).show()
                            } else if (result.downEvents.contains(eventKey)) {
                                Snackbar.make(activity.findViewById<View>(R.id.myCoordinatorLayout),
                                        R.string.eventServerDown,
                                        Snackbar.LENGTH_LONG).show()
                            }
                        } },
                    { error -> Logging.error(this, error.toString(), 0) })
            }
        }
    }
}
