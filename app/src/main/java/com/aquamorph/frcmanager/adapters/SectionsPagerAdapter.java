package com.aquamorph.frcmanager.adapters;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.aquamorph.frcmanager.fragments.AllianceFragment;
import com.aquamorph.frcmanager.fragments.AwardFragment;
import com.aquamorph.frcmanager.fragments.EventScheduleFragment;
import com.aquamorph.frcmanager.fragments.RankFragment;
import com.aquamorph.frcmanager.fragments.RefreshFragment;
import com.aquamorph.frcmanager.fragments.TeamFragment;
import com.aquamorph.frcmanager.fragments.TeamScheduleFragment;
import com.aquamorph.frcmanager.utils.Data;
import com.aquamorph.frcmanager.utils.DataContainer;


/**
 * Populates a tab layout with fragments.
 *
 * @author Christian Colglazier
 * @version 3/29/2016
 */
public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

	private final FragmentManager mFragmentManager;
	private SparseArray<Fragment> mFragments;
	private FragmentTransaction mCurTransaction;
	private String[] tabNames = {"Team Schedule", "Event Schedule", "Rankings", "Teams",
			"Alliances", "Awards"};

	public SectionsPagerAdapter(FragmentManager fragmentManager) {
		super(fragmentManager);
		mFragmentManager = fragmentManager;
		mFragments = new SparseArray<>();
	}

	public boolean isDataLoading() {
		return !Data.awardDC.complete || !Data.teamDC.complete || !Data.rankDC.complete
				|| !Data.matchDC.complete || !Data.allianceDC.complete;
	}

	/**
	 * refreshAll() reloads all data in the fragments.
	 */
	public void refreshAll(boolean force) {
		if(force || !isDataLoading()) {
			refrestData(force);
			for (int i = 0; i < tabNames.length; i++) {
				((RefreshFragment) mFragmentManager.findFragmentByTag("fragment:" + i)).refresh(force);
			}
		}
	}

	public void refrestData(boolean force) {
		Data.teamDC.complete = false;
		Data.rankDC.complete = false;
		Data.awardDC.complete = false;
		Data.allianceDC.complete = false;
		Data.matchDC.complete= false;
		Data.refresh(force);
	}

	@NonNull
	@Override
	public Object instantiateItem(ViewGroup container, int position) {

		Fragment fragment = mFragmentManager.findFragmentByTag("fragment:" + position);
		if (fragment == null) {
			fragment = getItem(position);
			if (mCurTransaction == null) {
				mCurTransaction = mFragmentManager.beginTransaction();
			}
			mCurTransaction.add(container.getId(), fragment, "fragment:" + position);
		}
		return fragment;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		if (mCurTransaction == null) {
			mCurTransaction = mFragmentManager.beginTransaction();
		}
		mCurTransaction.detach(mFragments.get(position));
		mFragments.remove(position);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return tabNames[position];
	}

	public Fragment getItem(int position) {
		switch (position) {
			case 0:
				return TeamScheduleFragment.newInstance();
			case 1:
				return EventScheduleFragment.newInstance();
			case 2:
				return RankFragment.newInstance();
			case 3:
				return TeamFragment.newInstance();
			case 4:
				return AllianceFragment.newInstance();
			case 5:
				return AwardFragment.newInstance();
			default:
				return TeamFragment.newInstance();
		}
	}

	@Override
	public void finishUpdate(ViewGroup container) {
		if (mCurTransaction != null) {
			mCurTransaction.commitAllowingStateLoss();
			mCurTransaction = null;
			mFragmentManager.executePendingTransactions();
		}
	}

	@Override
	public boolean isViewFromObject(android.view.View view, Object object) {
		return ((Fragment) object).getView() == view;
	}

	@Override
	public int getCount() {
		return tabNames.length;
	}

}