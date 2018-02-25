package com.aquamorph.frcmanager.adapters;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import com.aquamorph.frcmanager.fragments.AllianceFragment;
import com.aquamorph.frcmanager.fragments.AwardFragment;
import com.aquamorph.frcmanager.fragments.EventScheduleFragment;
import com.aquamorph.frcmanager.fragments.RankFragment;
import com.aquamorph.frcmanager.fragments.RefreshFragment;
import com.aquamorph.frcmanager.fragments.TeamFragment;
import com.aquamorph.frcmanager.fragments.TeamScheduleFragment;
import com.aquamorph.frcmanager.models.Tab;
import com.aquamorph.frcmanager.network.DataLoader;

import java.util.ArrayList;

/**
 * Populates a tab layout with fragments.
 *
 * @author Christian Colglazier
 * @version 2/25/2018
 */
public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

	private TabLayout tabLayout;
	private ViewPager viewPager;
	public ArrayList<Tab> tabs = new ArrayList<>();

	public SectionsPagerAdapter(FragmentManager fragmentManager, ViewPager viewPager,
								TabLayout tabLayout) {
		super(fragmentManager);
		this.tabLayout = tabLayout;
		this.viewPager = viewPager;
		addFrag("Team Schedule", TeamScheduleFragment.newInstance());
		addFrag("Event Schedule", EventScheduleFragment.newInstance());
		addFrag("Rankings", RankFragment.newInstance());
		addFrag("Teams", TeamFragment.newInstance());
		addFrag("Alliances", AllianceFragment.newInstance());
		addFrag("Awards", AwardFragment.newInstance());
	}

	public boolean isDataLoading() {
		return !DataLoader.awardDC.complete || !DataLoader.teamDC.complete || !DataLoader.rankDC.complete
				|| !DataLoader.matchDC.complete || !DataLoader.allianceDC.complete;
	}

	/**
	 * refreshAll() reloads all dataLoader in the fragments.
	 */
	public void refreshAll(boolean force) {
		refrestData(force);
		for (int i = 0; i < tabs.size(); i++) {
			((RefreshFragment) tabs.get(i).fragment).refresh(force);
		}
	}

	public void refrestData(boolean force) {
		DataLoader.teamDC.complete = false;
		DataLoader.rankDC.complete = false;
		DataLoader.awardDC.complete = false;
		DataLoader.allianceDC.complete = false;
		DataLoader.matchDC.complete= false;
		DataLoader.refresh(force);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return tabs.get(position).name;
	}

	@Override
	public Fragment getItem(int position) {
		return tabs.get(position).fragment;
	}

	@Override
	public boolean isViewFromObject(android.view.View view, Object object) {
		return ((Fragment) object).getView() == view;
	}

	@Override
	public int getCount() {
		return tabs.size();
	}

	public void addFrag(String title, Fragment fragment) {
		tabs.add(new Tab(title, fragment));
	}

	public void removeFrag(int position) {
		removeTab(position);
		Fragment fragment = tabs.get(position).fragment;
		tabs.remove(position);
		destroyFragmentView(viewPager, position, fragment);
		notifyDataSetChanged();
	}

	public void destroyFragmentView(ViewGroup container, int position, Object object) {
		FragmentManager manager = ((Fragment) object).getFragmentManager();
		FragmentTransaction trans = manager.beginTransaction();
		trans.remove((Fragment) object);
		trans.commit();
	}

	public void removeTab(int position) {
		if (tabLayout.getChildCount() > 0) {
			tabLayout.removeTabAt(position);
		}
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}
}