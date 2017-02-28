package com.aquamorph.frcmanager.decoration;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * <p></p>
 *
 * @author Christian Colglazier
 * @version 1/7/2017
 */

public class Animations {
	private static final String TAG = "Animations";

	public static void loadAnimation(final Context context, final View view,
	                                 final RecyclerView.Adapter adapter,
	                                 final Boolean firstLoad,
	                                 final Boolean isNewData) {
		Animation slideOut = AnimationUtils.loadAnimation(context, android.R.anim
				.slide_out_right);
		final Animation slideIn = AnimationUtils.loadAnimation(context, android.R.anim
				.slide_in_left);
		slideOut.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				Log.d(TAG, "Animation Ended");
				(adapter).notifyDataSetChanged();

				view.startAnimation(slideIn);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
		if(isNewData) {
			if (firstLoad) view.startAnimation(slideIn);
			else view.startAnimation(slideOut);
		}
	}
}
