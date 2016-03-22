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
	TeamScheduleFragment teamScheduleFragment = TeamScheduleFragment.newInstance();
	EventScheduleFragment eventScheduleFragment = EventScheduleFragment.newInstance();
	RankFragment rankFragment = RankFragment.newInstance();
	AwardFragment awardFragment = AwardFragment.newInstance();
	TeamEventFragment teamEventFragment = TeamEventFragment.newInstance();

	public SectionsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	public void refreshAll() {
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
				return teamScheduleFragment;
			case 1:
				return eventScheduleFragment;
			case 2:
				return rankFragment;
			case 3:
				return teamEventFragment;
			case 4:
				return awardFragment;
			default:
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