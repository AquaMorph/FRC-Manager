package com.aquamorph.frcmanager.decoration

import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils

/**
 * Card slide in animations.
 *
 * @author Christian Colglazier
 * @version 3/30/2018
 */

object Animations {

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
