package com.aquamorph.frcmanager.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.aquamorph.frcmanager.fragments.AwardFragment;
import com.aquamorph.frcmanager.fragments.EventScheduleFragment;
import com.aquamorph.frcmanager.fragments.RankFragment;
import com.aquamorph.frcmanager.fragments.TeamEventFragment;
import com.aquamorph.frcmanager.fragments.TeamScheduleFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

	public String[] tabNames = {"Team Schedule", "Event Schedule", "Rankings", "Teams", "Awards"};
	TeamScheduleFragment teamScheduleFragment;
	EventScheduleFragment eventScheduleFragment;
	RankFragment rankFragment;
	AwardFragment awardFragment;
	TeamEventFragment teamEventFragment;
	FragmentManager fragmentManager;

	public SectionsPagerAdapter(FragmentManager fm) {
		super(fm);
		fragmentManager = fm;
		teamScheduleFragment = TeamScheduleFragment.newInstance();
		eventScheduleFragment = EventScheduleFragment.newInstance();
		rankFragment = RankFragment.newInstance();
		awardFragment = AwardFragment.newInstance();
		teamEventFragment = TeamEventFragment.newInstance();
	}

	public void refreshAll() {
//		teamScheduleFragment = TeamScheduleFragment.newInstance();
//		eventScheduleFragment = EventScheduleFragment.newInstance();
//		rankFragment = RankFragment.newInstance();
//		awardFragment = AwardFragment.newInstance();
//		teamEventFragment = TeamEventFragment.newInstance();
		teamScheduleFragment.refresh();
		eventScheduleFragment.refresh();
		rankFragment.refresh();
		awardFragment.refresh();
		teamEventFragment.refresh();
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
			case 0:
//				teamScheduleFragment = TeamScheduleFragment.newInstance();
				return teamScheduleFragment;
			case 1:
//				eventScheduleFragment = EventScheduleFragment.newInstance();
				return eventScheduleFragment;
			case 2:
//				rankFragment = RankFragment.newInstance();
				return rankFragment;
			case 3:
//				teamEventFragment = TeamEventFragment.newInstance();
				return teamEventFragment;
			case 4:
//				awardFragment = AwardFragment.newInstance();
				return awardFragment;
			default:
//				teamEventFragment = TeamEventFragment.newInstance();
				return teamEventFragment;
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


}