package com.aquamorph.frcmanager.adapters

import android.app.Activity
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.ViewGroup
import com.aquamorph.frcmanager.fragments.RefreshFragment
import com.aquamorph.frcmanager.models.Tab
import com.aquamorph.frcmanager.network.DataLoader
import com.aquamorph.frcmanager.utils.Logging
import java.util.*

/**
 * Populates a tab layout with fragments.
 *
 * @author Christian Colglazier
 * @version 4/2/2018
 */
class SectionsPagerAdapter(fragmentManager: FragmentManager, private val viewPager: ViewPager,
                           private val tabLayout: TabLayout, activity: Activity) :
        FragmentStatePagerAdapter(fragmentManager) {
    var act = activity
    var tabs = ArrayList<Tab>()
    val isDataLoading: Boolean
        get() = (!DataLoader.awardDC.complete || !DataLoader.teamDC.complete || !DataLoader.rankDC.complete
                || !DataLoader.matchDC.complete || !DataLoader.allianceDC.complete)

    /**
     * refreshAll() reloads all dataLoader in the fragments.
     */
    fun refreshAll(force: Boolean) {
        refreshData(force)
        for (i in tabs.indices) {
            (tabs[i].fragment as RefreshFragment).refresh(force)
        }
    }

    fun refreshData(force: Boolean) {
        DataLoader.teamDC.complete = false
        DataLoader.rankDC.complete = false
        DataLoader.awardDC.complete = false
        DataLoader.allianceDC.complete = false
        DataLoader.matchDC.complete = false
        DataLoader.refresh(force, this, act)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return tabs[position].name
    }

    override fun getItem(position: Int): Fragment {
        return tabs[position].fragment
    }

    override fun getCount(): Int {
        return tabs.size
    }

    fun addFrag(tab: Tab) {
        try {
            tabs.add(tab)
            tabs.sort()
            notifyDataSetChanged()
        } catch (e : IllegalStateException) {
            Logging.error(this, e.toString(), 0)
        }
    }

    fun removeFrag(position: Int) {
        val fragment = tabs[position].fragment
        destroyFragmentView(viewPager, position, fragment)
        removeTab(position)
        tabs.removeAt(position)
        notifyDataSetChanged()
    }

    private fun destroyFragmentView(container: ViewGroup, position: Int, `object`: Any) {
        val manager = (`object` as Fragment).fragmentManager
        if (manager != null) {
            val trans = manager.beginTransaction()
            trans.remove(`object`)
            trans.commitAllowingStateLoss()
        }
    }

    private fun removeTab(position: Int) {
        if (tabLayout.childCount > 0) {
            tabLayout.removeTabAt(position)
        }
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    fun isTab(name: String): Boolean? {
        for (tab in tabs) {
            if (name == tab.name) {
                return true
            }
        }
        return false
    }

    fun tabPosition(name: String): Int {
        for (i in tabs.indices) {
            if (name == tabs[i].name) {
                return i
            }
        }
        return -1
    }
}