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

    private fun refreshData() {
        DataLoader.teamDC.complete = false
        DataLoader.rankDC.complete = false
        DataLoader.awardDC.complete = false
        DataLoader.allianceDC.complete = false
        DataLoader.matchDC.complete = false
        DataLoader.districtRankDC.complete = false
        DataLoader.districtTeamDC.complete = false
        DataLoader.refresh(this, activity)
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
            if (!tabs.contains(tab)) {
                tabs.add(tab)
                tabs.sort()
                notifyDataSetChanged()
            }
        } catch (e: Exception) {
            Logging.error(this, e.toString(), 0)
        }
    }

    fun removeFrag(position: Int) {
        destroyFragmentView(viewPager)
        removeTab(position)
        tabs.removeAt(position)
        notifyDataSetChanged()
    }

    private fun destroyFragmentView(`object`: Any) {
        try {
            val manager = (`object` as Fragment).fragmentManager
            if (manager != null) {
                val trans = manager.beginTransaction()
                trans.remove(`object`)
                trans.commitAllowingStateLoss()
            }
        } catch (e: Exception) {
            Logging.error(this, e.toString(), 0)
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
