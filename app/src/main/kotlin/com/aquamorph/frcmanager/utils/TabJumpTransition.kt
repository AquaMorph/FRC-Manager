package com.aquamorph.frcmanager.utils

import android.animation.ValueAnimator
import android.os.Build
import android.view.View
import android.view.animation.PathInterpolator
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import kotlin.math.abs

internal class TabJumpTransition(
    private val tabs: TabLayout,
    private val pager: ViewPager2,
) : TabLayout.OnTabSelectedListener {
    private val easing = PathInterpolator(0.2f, 0f, 0f, 1f)
    private val swipeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrollStateChanged(state: Int) {
            if (state == ViewPager2.SCROLL_STATE_DRAGGING) resetMotion()
        }
    }

    init {
        tabs.addOnTabSelectedListener(this)
        pager.registerOnPageChangeCallback(swipeCallback)
    }

    override fun onTabUnselected(tab: TabLayout.Tab) {
        val destination = tabs.selectedTabPosition
        val distance = destination - pager.currentItem
        if (destination < 0 || distance == 0) return

        resetMotion()
        if (abs(distance) <= 1) return

        // The new tab is already selected, but the mediator hasn't received onTabSelected yet.
        // Moving now makes its subsequent smooth scroll to the same page a no-op.
        pager.setCurrentItem(destination, false)
        if (!pager.isLaidOut || Build.VERSION.SDK_INT >= 26 && !ValueAnimator.areAnimatorsEnabled()) return

        val direction = if (distance > 0) 1 else -1
        val layoutDirection = if (pager.layoutDirection == View.LAYOUT_DIRECTION_RTL) -1 else 1
        pager.alpha = 0.7f
        pager.translationX = direction * layoutDirection * 16f * pager.resources.displayMetrics.density
        pager.animate().alpha(1f).translationX(0f).setDuration(200L).setInterpolator(easing).start()
    }

    override fun onTabSelected(tab: TabLayout.Tab) = Unit

    override fun onTabReselected(tab: TabLayout.Tab) = Unit

    fun detach() {
        tabs.removeOnTabSelectedListener(this)
        pager.unregisterOnPageChangeCallback(swipeCallback)
        resetMotion()
    }

    private fun resetMotion() {
        pager.animate().cancel()
        pager.alpha = 1f
        pager.translationX = 0f
    }
}
