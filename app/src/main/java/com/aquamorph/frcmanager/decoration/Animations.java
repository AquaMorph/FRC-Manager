package com.aquamorph.frcmanager.decoration;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.aquamorph.frcmanager.utils.Logging;

/**
 * Card slide in animations.
 *
 * @author Christian Colglazier
 * @version 1/7/2017
 */

public class Animations {

	public static void loadAnimation(final Context context, final View view,
	                                 final RecyclerView.Adapter adapter,
	                                 final Boolean firstLoad,
	                                 final Boolean isNewData) {
		if (context != null && view != null && adapter != null) {
			Animation slideOut = AnimationUtils.loadAnimation(context, android.R.anim
					.slide_out_right);
			final Animation slideIn = AnimationUtils.loadAnimation(context, android.R.anim
					.slide_in_left);
			slideOut.setAnimationListener(new Animation.AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {}

				@Override
				public void onAnimationEnd(Animation animation) {
					Logging.INSTANCE.info(this, "Animation Ended", 1);
					(adapter).notifyDataSetChanged();
					view.startAnimation(slideIn);
				}

				@Override
				public void onAnimationRepeat(Animation animation) {}
			});
			if (isNewData) {
				if (firstLoad) view.startAnimation(slideIn);
				else view.startAnimation(slideOut);
			}
		}
	}
}
