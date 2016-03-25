package com.aquamorph.frcmanager.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.aquamorph.frcmanager.fragments.AwardFragment;
import com.aquamorph.frcmanager.fragments.EventScheduleFragment;
import com.aquamorph.frcmanager.fragments.RankFragment;
import com.aquamorph.frcmanager.fragments.TeamEventFragment;
import com.aquamorph.frcmanager.fragments.TeamScheduleFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

	public String[] tabNames = {"Team Schedule", "Event Schedule", "Rankings", "Teams", "Awards"};
	FragmentManager fragmentManager;
	ViewPager viewPager;

	public SectionsPagerAdapter(FragmentManager fm, ViewPager mViewPager) {
		super(fm);
		fragmentManager = fm;
		viewPager = mViewPager;
	}

	public void refreshAll() {
		final TeamScheduleFragment tab1 = (TeamScheduleFragment) fragmentManager.findFragmentByTag(makeFragmentName(viewPager.getId(), 0));
		tab1.refresh();
		final EventScheduleFragment tab2 = (EventScheduleFragment) fragmentManager.findFragmentByTag(makeFragmentName(viewPager.getId(), 1));
		tab2.refresh();
		final RankFragment tab3 = (RankFragment) fragmentManager.findFragmentByTag(makeFragmentName(viewPager.getId(), 2));
		tab3.refresh();
		final TeamEventFragment tab4 = (TeamEventFragment) fragmentManager.findFragmentByTag(makeFragmentName(viewPager.getId(), 3));
		tab4.refresh();
		final AwardFragment tab5 = (AwardFragment) fragmentManager.findFragmentByTag(makeFragmentName(viewPager.getId(), 4));
		tab5.refresh();
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
			case 0:
				return TeamScheduleFragment.newInstance();
			case 1:
				return EventScheduleFragment.newInstance();
			case 2:
				return RankFragment.newInstance();
			case 3:
				return TeamEventFragment.newInstance();
			case 4:
				return AwardFragment.newInstance();
			default:
				return TeamEventFragment.newInstance();
		}
	}

	@Override
	public int getCount() {
		return tabNames.length;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return tabNames[position];
	}

	private static String makeFragmentName(int viewPagerId, int index) {
		return "android:switcher:" + viewPagerId + ":" + index;
	}
}