package com.aquamorph.frcmanager.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aquamorph.frcmanager.R;

/**
 * <p></p>
 *
 * @author Christian Colglazier
 * @version 2/27/2017
 */

public class BracketFragment extends Fragment {

	/**
	 * newInstance creates and returns a new BracketFragment
	 *
	 * @return BracketFragment
	 */
	public static BracketFragment newInstance() {
		return new BracketFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.bracket, container, false);
		return view;
	}

	public void refresh() {

	}
}
