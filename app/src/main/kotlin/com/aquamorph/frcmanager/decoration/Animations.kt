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

    fun loadAnimation(
        context: Context?,
        view: View?,
        adapter: RecyclerView.Adapter<*>?,
        firstLoad: Boolean?,
        isNewData: Boolean?
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
            if (isNewData!!) {
                if (firstLoad!!) {
                    view.startAnimation(slideIn)
                } else {
                    view.startAnimation(slideOut)
                }
            }
        }
    }
}
