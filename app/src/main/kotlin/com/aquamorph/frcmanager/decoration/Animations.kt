package com.aquamorph.frcmanager.decoration

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.aquamorph.frcmanager.utils.Logging

/**
 * Card slide in animations.
 *
 * @author Christian Colglazier
 * @version 3/30/2018
 */

object Animations {

    /**
     * loadAnimation() manages animation for tab views.
     *
     * @param context app activity
     * @param view data view
     * @param adapter recyclerview adapter
     * @param firstLoad first time data has been seen
     * @param isNewData date has been updated
     */
    fun loadAnimation(
        context: Context?,
        view: View?,
        adapter: RecyclerView.Adapter<*>?,
        firstLoad: Boolean,
        isNewData: Boolean
    ) {
        if (context != null && view != null && adapter != null) {
            val slideOut = AnimationUtils.loadAnimation(context, android.R.anim.slide_out_right)
            val slideIn = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)
            slideOut.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}

                override fun onAnimationEnd(animation: Animation) {
                    Logging.info(this, "Animation Ended", 1)
                    adapter.notifyDataSetChanged()
                    view.startAnimation(slideIn)
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
            if (firstLoad) {
                view.startAnimation(slideIn)
            } else if (isNewData) {
                view.startAnimation(slideOut)
            }
        }
    }

    /**
     * loadMatchBreakdownAnimation() manages new activity animation.
     *
     * @param context activity context
     * @param view data view
     */
    fun loadMatchBreakdownAnimation(
        context: Context?,
        view: View?
    ) {
        if (context != null && view != null) {
            val slideIn = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)
            view.startAnimation(slideIn)
        }
    }
}
