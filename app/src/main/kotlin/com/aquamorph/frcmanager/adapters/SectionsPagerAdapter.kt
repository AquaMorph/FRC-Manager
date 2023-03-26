package com.aquamorph.frcmanager.adapters

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.aquamorph.frcmanager.fragments.RefreshFragment
import com.aquamorph.frcmanager.models.Tab
import com.aquamorph.frcmanager.network.DataLoader
import com.aquamorph.frcmanager.utils.Logging
import com.google.android.material.tabs.TabLayout

/**
 * Populates a tab layout with fragments.
 *
 * @author Christian Colglazier
 * @version 4/2/2018
 */
class SectionsPagerAdapter(
    fragmentManager: FragmentManager,
    private val viewPager: ViewPager,
    private val tabLayout: TabLayout,
    var activity: Activity
) : FragmentStatePagerAdapter(fragmentManager) {
    var tabs = ArrayList<Tab>()

    /**
     * refreshAll() reloads all dataLoader in the fragments.
     */
    fun refreshAll() {
        refreshData()
        for (i in tabs.indices) {
            (tabs[i].fragment as RefreshFragment).refresh()
        }
    }

    /**
     * refreshData() updates all event data.
     */
    private fun refreshData() {
        DataLoader.teamDC.complete = false
        DataLoader.rankDC.complete = false
        DataLoader.awardDC.complete = false
        DataLoader.allianceDC.complete = false
        DataLoader.matchDC.complete = false
        DataLoader.districtRankDC.complete = false
        DataLoader.districtTeamDC.complete = false
        DataLoader.tbaPredictionsDC.complete = false
        DataLoader.refresh(this, activity)
    }

    override fun getPageTitle(position: Int): CharSequence {
        return tabs[position].name
    }

    override fun getItem(position: Int): Fragment {
        return tabs[position].fragment
    }

    override fun getCount(): Int {
        return tabs.size
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    /**
     * addFragment() adds a fragment tab to the adapter.
     *
     * @param tab tab data holder
     */
    fun addFragment(tab: Tab) {
        try {
            if (!tabs.contains(tab) and !tab.fragment.isAdded) {
                tabs.add(tab)
                tabs.sort()
                notifyDataSetChanged()
            }
        } catch (e: Exception) {
            Logging.error(this, e.toString(), 0)
        }
    }

    /**
     * removeFragment() removes a fragment tab from the adapter
     *
     * @param position fragment tab position
     */
    fun removeFragment(position: Int) {
        destroyFragmentView(viewPager)
        removeTab(position)
        tabs.removeAt(position)
        notifyDataSetChanged()
    }

    /**
     * destroyFragmentView() kills a fragment.
     *
     * @param view fragment view
     */
    private fun destroyFragmentView(view: Any) {
        try {
            val manager = (view as Fragment).fragmentManager
            if (manager != null) {
                val trans = manager.beginTransaction()
                trans.remove(view)
                trans.commitAllowingStateLoss()
            }
        } catch (e: Exception) {
            Logging.error(this, e.toString(), 0)
        }
    }

    /**
     * removeTab() removes the tab at the given position.
     *
     * @param position position of tab to be removed
     */
    private fun removeTab(position: Int) {
        if (tabLayout.childCount > 0) {
            tabLayout.removeTabAt(position)
        }
    }

    /**
     * isTab() returns true if a tab name is in the list of tabs.
     *
     * @param name tab name
     * @return if tab exists
     */
    fun isTab(name: String): Boolean {
        for (tab in tabs) {
            if (name == tab.name) {
                return true
            }
        }
        return false
    }

    /**
     * tabPosition() returns the tab position matching the given string and returns -1 is none is
     * found.
     *
     * @param name name of tab
     * @return tab position
     */
    fun tabPosition(name: String): Int {
        for (i in tabs.indices) {
            if (name == tabs[i].name) {
                return i
            }
        }
        return -1
    }
}
