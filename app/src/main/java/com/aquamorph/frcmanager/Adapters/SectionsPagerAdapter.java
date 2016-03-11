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

	public String[] tabNames = {"Team Schedule", "Event Schedule", "Rankings", "Awards", "Teams"};//, "OPR", "DPR"};

	public SectionsPagerAdapter(FragmentManager fm) {
		super(fm);
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
				return AwardFragment.newInstance();
			case 4:
				return TeamEventFragment.newInstance();
			default:
				return AwardFragment.newInstance();
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