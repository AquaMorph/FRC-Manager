package com.aquamorph.frcmanager.utils

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.aquamorph.frcmanager.R

internal class ScheduleFastScrollView(context: Context, attrs: AttributeSet) : RecyclerView(context, attrs) {
    private val gutterWidth = resources.getDimensionPixelSize(R.dimen.schedule_fast_scroll_width)
    private var gutterAnimator: ValueAnimator? = null
    private var gutterTarget = 0
    private var touchingThumbEdge = false
    private val closeGutter = Runnable { animateGutter(0) }

    init {
        addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (!hasScrollableContent()) {
                    removeCallbacks(closeGutter)
                    animateGutter(0)
                } else if (dy != 0 || (dx == 0 && gutterTarget == 0 && paddingEnd == 0)) {
                    openGutter()
                }
            }
        })
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val distanceToEdge = if (layoutDirection == LAYOUT_DIRECTION_RTL) event.x else width - event.x
                touchingThumbEdge = distanceToEdge <= gutterWidth && gutterTarget == gutterWidth
                if (touchingThumbEdge) openGutter()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (touchingThumbEdge) {
                    touchingThumbEdge = false
                    scheduleClose()
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun hasScrollableContent() = canScrollVertically(1) || canScrollVertically(-1)

    private fun openGutter() {
        animateGutter(gutterWidth)
        scheduleClose()
    }

    private fun scheduleClose() {
        removeCallbacks(closeGutter)
        // Leave room through RecyclerView's idle timeout and thumb fade-out (1500 + 500 ms).
        if (!touchingThumbEdge) postDelayed(closeGutter, 2_000L)
    }

    private fun animateGutter(target: Int) {
        if (gutterTarget == target) return
        gutterTarget = target
        gutterAnimator?.cancel()
        gutterAnimator = ValueAnimator.ofInt(paddingEnd, target).apply {
            duration = if (target == 0) 220L else 160L
            interpolator = DecelerateInterpolator()
            addUpdateListener { setGutter(it.animatedValue as Int) }
            start()
        }
    }

    private fun setGutter(size: Int) {
        if (paddingEnd != size) setPaddingRelative(paddingStart, paddingTop, size, paddingBottom)
    }

    override fun onDetachedFromWindow() {
        removeCallbacks(closeGutter)
        gutterAnimator?.cancel()
        gutterAnimator = null
        gutterTarget = 0
        touchingThumbEdge = false
        setGutter(0)
        super.onDetachedFromWindow()
    }
}
