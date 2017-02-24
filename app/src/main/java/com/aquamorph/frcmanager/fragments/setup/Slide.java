package com.aquamorph.frcmanager.fragments.setup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Generic slide fragment that can be passed a layout.
 *
 * @author Christian Colglazier
 * @version 3/29/2016
 */
public class Slide extends Fragment {

	private static final String ARG_LAYOUT_RES_ID = "layoutResId";
	private int layoutResId;

	public static Slide newInstance(int layoutResId) {
		Slide sampleSlide = new Slide();
		Bundle args = new Bundle();
		args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
		sampleSlide.setArguments(args);
		return sampleSlide;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null && getArguments().containsKey(ARG_LAYOUT_RES_ID)) {
			layoutResId = getArguments().getInt(ARG_LAYOUT_RES_ID);
		}
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
	                         @Nullable Bundle savedInstanceState) {
		return inflater.inflate(layoutResId, container, false);
	}
}