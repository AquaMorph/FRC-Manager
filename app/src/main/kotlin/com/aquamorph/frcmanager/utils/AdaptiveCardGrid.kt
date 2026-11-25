package com.aquamorph.frcmanager.utils

import androidx.annotation.DimenRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.aquamorph.frcmanager.R

internal fun RecyclerView.adaptiveCardGrid(@DimenRes minCardWidth: Int): GridLayoutManager {
    val cardWidth = resources.getDimensionPixelSize(minCardWidth)
    val fastScrollGutter = if (this is ScheduleFastScrollView) {
        resources.getDimensionPixelSize(R.dimen.schedule_fast_scroll_gutter)
    } else null
    val configuration = resources.configuration
    return object : GridLayoutManager(context, 1) {
        private val vertical = OrientationHelper.createVerticalHelper(this)

        init {
            setUsingSpansToEstimateScrollbarDimensions(true)
        }

        override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
            if (!state.isPreLayout) {
                // Budget for the thumb even while it is hidden so scrolling can't toggle the grid.
                val available = width - paddingStart - (fastScrollGutter ?: paddingEnd)
                val columns = if (available >= 2 * cardWidth && configuration.fontScale < 1.3f) 2 else 1
                if (spanCount != columns) {
                    // Keep the first visible card in view instead of letting the grid choose a new anchor.
                    val position = findFirstVisibleItemPosition()
                    val offset = findViewByPosition(position)?.let { vertical.getDecoratedStart(it) - paddingTop }
                    spanCount = columns
                    if (offset != null) scrollToPositionWithOffset(position, offset)
                }
            }
            super.onLayoutChildren(recycler, state)
        }
    }
}
