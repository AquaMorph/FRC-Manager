package com.aquamorph.frcmanager.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.aquamorph.frcmanager.fragments.EventScheduleFragment;
import com.aquamorph.frcmanager.fragments.RankFragment;
import com.aquamorph.frcmanager.fragments.TeamScheduleFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

	public SectionsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		// getItem is called to instantiate the fragment for the given page.
		// Return a EventScheduleFragment (defined as a static inner class below).
		Log.i("TAG", "Position" + position);
//		if(position == 0) return TeamScheduleFragment.newInstance();
//		else return EventScheduleFragment.newInstance(position + 1);
		switch (position) {
			case 0:
				return TeamScheduleFragment.newInstance();
			case 1:
				return EventScheduleFragment.newInstance();
			case 2:
				return TeamScheduleFragment.newInstance();
			case 3:
				return TeamScheduleFragment.newInstance();
			case 4:
				return TeamScheduleFragment.newInstance();
			default:
				return TeamScheduleFragment.newInstance();
		}
	}

	@Override
	public int getCount() {
		// Show 3 total pages.
		return 6;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
			case 0:
				return "Team Schedule";
			case 1:
				return "Event Schedule";
			case 2:
				return "Rankings";
			case 3:
				return "OPR";
			case 4:
				return "DPR";
			case 5:
				return "Awards";
		}
		return null;
	}

}