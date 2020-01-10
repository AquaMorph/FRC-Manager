package com.aquamorph.frcmanager.utils

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Custom loading animation.
 *
 * @author Christian Colglazier
 * @version 3/30/2018
 */

class MyRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RecyclerView(context, attrs, defStyle) {
    private var mScrollable: Boolean = false

    init {
        mScrollable = false
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return !mScrollable || super.dispatchTouchEvent(ev)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        for (i in 0 until childCount) {
            animate(getChildAt(i), i)

            if (i == childCount - 1) {
                handler.postDelayed({ mScrollable = true }, i * 100L)
            }
        }
    }

    private fun animate(view: View, pos: Int) {
        // 		view.animate().cancel();
        // 		Animation animation = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_in_left);
        // 		view.startAnimation(animation);

        // 		view.setTranslationY(200);
        // 		view.setAlpha(0);
        // 		view.animate().alpha(1.0f).translationY(0).setDuration(300).setStartDelay(pos * 125);
    }
}
