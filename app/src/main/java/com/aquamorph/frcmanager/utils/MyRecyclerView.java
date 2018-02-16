package com.aquamorph.frcmanager.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Custom loading animation.
 *
 * @author Christian Colglazier
 * @version 7/27/2016
 */

public class MyRecyclerView extends RecyclerView {
	private boolean mScrollable;

	public MyRecyclerView(Context context) {
		this(context, null);
	}

	public MyRecyclerView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mScrollable = false;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return !mScrollable || super.dispatchTouchEvent(ev);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		for (int i = 0; i < getChildCount(); i++) {
			animate(getChildAt(i), i);

			if (i == getChildCount() - 1) {
				getHandler().postDelayed(new Runnable() {
					@Override
					public void run() {
						mScrollable = true;
					}
				}, i * 100L);
			}
		}
	}

	private void animate(View view, final int pos) {
//		view.animate().cancel();
//		Animation animation = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_in_left);
//		view.startAnimation(animation);

//		view.setTranslationY(200);
//		view.setAlpha(0);
//		view.animate().alpha(1.0f).translationY(0).setDuration(300).setStartDelay(pos * 125);
	}
}