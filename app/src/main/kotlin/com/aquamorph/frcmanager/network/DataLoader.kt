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

        private fun isRankEmpty(dataContainer: DataContainer<*>): Boolean {
            return dataContainer.data[0] is Rank &&
                    (dataContainer.data[0] as Rank).rankings.isEmpty()
        }

        private fun getData(
            dataContainer: DataContainer<*>,
            isRank: Boolean,
            isSortable: Boolean,
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
                        updateData(dataContainer, isRank, isSortable,
                                tabs, adapter, result.body() as Any) },
                                { error ->
                                    Logging.error(this, error.toString(), 0)
                                removeTab(tabs, adapter)
                                dataContainer.complete = true })
        }

        private fun updateData(
            dataContainer: DataContainer<*>,
            isRank: Boolean,
            isSortable: Boolean,
            tabs: ArrayList<Tab>,
            adapter: SectionsPagerAdapter,
            result: Any
        ) {
            dataContainer.data.clear()
                if (isRank) {
                    if (result is TBAPrediction) {
                        (dataContainer.data as MutableList<Any?>)
                                .addAll(Constants.tbaPredToArray(result))
                    } else {
                        (dataContainer.data as MutableList<Any?>).add(result)
                    }
                } else {
                    dataContainer.data.addAll(result as Collection<Nothing>)
                    if (isSortable) {
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

        private fun removeTab(tabs: ArrayList<Tab>, adapter: SectionsPagerAdapter) {
            for (tab in tabs) {
                if (adapter.isTab(tab.name)!!) {
                    adapter.removeFrag(adapter.tabPosition(tab.name))
                }
            }
        }

        private fun addTab(tabs: ArrayList<Tab>, adapter: SectionsPagerAdapter) {
            for (tab in tabs) {
                if ((!adapter.isTab(tab.name)!!)) {
                    adapter.addFrag(tab)
                }
            }
        }

        /**
         * clearData()
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

        fun refresh(adapter: SectionsPagerAdapter, activity: Activity) {
            if (eventKey != "") {
                // Checks for internet connections
                if (!NetworkCheck.hasNetwork(activity)) {
                    Snackbar.make(activity.findViewById<View>(R.id.myCoordinatorLayout),
                            R.string.no_connection_message,
                            Snackbar.LENGTH_LONG).show()
                }
                getData(matchDC, false, true,
                        matchTabs, adapter, 0, activity)
                getData(teamDC, false, true,
                        teamTabs, adapter, 1, activity)
                getData(rankDC, true, false,
                        rankTabs, adapter, 2, activity)
                getData(awardDC, false, false,
                        awardTabs, adapter, 3, activity)
                getData(allianceDC, false, false,
                        allianceTabs, adapter, 4, activity)
                if (isDistrict()) {
                    getData(districtRankDC, false, false,
                            districtRankTabs, adapter, 5, activity)
                    getData(districtTeamDC, false, true,
                            districtRankTabs, adapter, 6, activity)
                    addTab(districtRankTabs, adapter)
                } else {
                    removeTab(districtRankTabs, adapter)
                }
                if (MainActivity.predEnabled) {
                    getData(tbaPredictionsDC, true, false,
                            matchTabs, adapter, 7, activity)
                }
                // Check if FIRST or event feed is down
                RetrofitInstance.getRetrofit(activity!!).create(TbaApi::class.java)
                        .getStatus().subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ result -> if (result != null) {
                            if (result.isDatafeedDown) {
                                Snackbar.make(activity.findViewById<View>(R.id.myCoordinatorLayout),
                                        R.string.first_server_down,
                                        Snackbar.LENGTH_LONG).show()
                            } else if (result.downEvents.contains(eventKey)) {
                                Snackbar.make(activity.findViewById<View>(R.id.myCoordinatorLayout),
                                        R.string.event_server_down,
                                        Snackbar.LENGTH_LONG).show()
                            }
                        } },
                    { error -> Logging.error(this, error.toString(), 0) })
            }
        }
    }
}
