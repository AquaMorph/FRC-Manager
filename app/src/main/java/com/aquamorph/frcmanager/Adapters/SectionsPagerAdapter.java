package com.aquamorph.frcmanager.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.aquamorph.frcmanager.fragments.AllianceFragment;
import com.aquamorph.frcmanager.fragments.AwardFragment;
import com.aquamorph.frcmanager.fragments.EventScheduleFragment;
import com.aquamorph.frcmanager.fragments.RankFragment;
import com.aquamorph.frcmanager.fragments.TeamEventFragment;
import com.aquamorph.frcmanager.fragments.TeamScheduleFragment;

/**
 * Populates a tab layout with fragments.
 *
 * @author Christian Colglazier
 * @version 3/29/2016
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

	public String[] tabNames = {"Team Schedule", "Event Schedule", "Rankings", "Teams", "Alliances", "Awards"};
	FragmentManager fragmentManager;
	ViewPager viewPager;

	public SectionsPagerAdapter(FragmentManager fm, ViewPager mViewPager) {
		super(fm);
		fragmentManager = fm;
		viewPager = mViewPager;
	}

	/**
	 * refreshAll() reloads all data in the fragments.
	 */
	public void refreshAll() {
		final TeamScheduleFragment tab1 = (TeamScheduleFragment) fragmentManager
				.findFragmentByTag(makeFragmentName(viewPager.getId(), 0));
		tab1.refresh();
		final EventScheduleFragment tab2 = (EventScheduleFragment) fragmentManager
				.findFragmentByTag(makeFragmentName(viewPager.getId(), 1));
		tab2.refresh();
		final RankFragment tab3 = (RankFragment) fragmentManager
				.findFragmentByTag(makeFragmentName(viewPager.getId(), 2));
		tab3.refresh();
		final TeamEventFragment tab4 = (TeamEventFragment) fragmentManager
				.findFragmentByTag(makeFragmentName(viewPager.getId(), 3));
		tab4.refresh();
		final AllianceFragment tab5 = (AllianceFragment) fragmentManager
				.findFragmentByTag(makeFragmentName(viewPager.getId(), 4));
		tab5.refresh();
		final AwardFragment tab6 = (AwardFragment) fragmentManager
				.findFragmentByTag(makeFragmentName(viewPager.getId(), 5));
		tab6.refresh();
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
				return AllianceFragment.newInstance();
			case 5:
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

	/**
	 * makeFragmentName() returns the tag for a desired fragment.
	 *
	 * @param viewPagerId pager id
	 * @param index fragment number
	 * @return fragment tag
	 */
	private static String makeFragmentName(int viewPagerId, int index) {
		return "android:switcher:" + viewPagerId + ":" + index;
	}
}