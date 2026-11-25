package com.aquamorph.frcmanager.utils

import android.view.View
import androidx.core.view.OneShotPreDrawListener
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout

/** A tap switches directly to its destination; a swipe stays under the pager's control. */
internal class TabJumpTransition(
    private val tabs: TabLayout,
    private val pager: ViewPager2,
    private val titleAt: (Int) -> CharSequence,
) : TabLayout.OnTabSelectedListener {
    private val adapter = checkNotNull(pager.adapter)
    private val pages = pager.getChildAt(0)
    private var pendingDraw: OneShotPreDrawListener? = null
    private var syncingTabs = false
    private var settlingFromSwipe = false
    private var requestedPage: Int? = null
    private val adapterObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() = populateTabs()
    }
    private val pageCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrollStateChanged(state: Int) {
            if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                settlingFromSwipe = true
                resetMotion()
            } else if (state == ViewPager2.SCROLL_STATE_IDLE) {
                if (requestedPage == null) selectCurrentPage()
                settlingFromSwipe = false
            }
        }

        override fun onPageScrolled(position: Int, offset: Float, pixels: Int) {
            if (settlingFromSwipe && requestedPage == null) tabs.setScrollPosition(position, offset, true)
        }

        override fun onPageSelected(position: Int) {
            if (!settlingFromSwipe && requestedPage == null) selectCurrentPage()
        }
    }

    init {
        populateTabs()
        tabs.addOnTabSelectedListener(this)
        pager.registerOnPageChangeCallback(pageCallback)
        adapter.registerAdapterDataObserver(adapterObserver)
    }

    private fun populateTabs() {
        resetMotion()
        syncingTabs = true
        tabs.removeAllTabs()
        repeat(adapter.itemCount) { tabs.addTab(tabs.newTab().setText(titleAt(it)), false) }
        if (adapter.itemCount > 0) {
            val current = pager.currentItem.coerceAtMost(adapter.itemCount - 1)
            tabs.selectTab(tabs.getTabAt(current))
        }
        syncingTabs = false
    }

    private fun selectCurrentPage() {
        syncingTabs = true
        tabs.getTabAt(pager.currentItem)?.let {
            if (tabs.selectedTabPosition != it.position) tabs.selectTab(it, !settlingFromSwipe)
        }
        syncingTabs = false
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
        if (syncingTabs) return
        navigateTo(tab.position)
    }

    override fun onTabReselected(tab: TabLayout.Tab) {
        if (!syncingTabs) navigateTo(tab.position)
    }

    override fun onTabUnselected(tab: TabLayout.Tab) = Unit

    private fun navigateTo(destination: Int) {
        if (destination == requestedPage) return
        pages.animate().cancel()
        pendingDraw?.removeListener()
        pendingDraw = null
        requestedPage = destination
        if (destination == pager.currentItem) {
            requestedPage = null
            revealPage()
            return
        }
        if (!pager.isAttachedToWindow || !pager.isLaidOut || !NavigationMotion.isEnabled(pager.context)) {
            resetMotion()
            pager.setCurrentItem(destination, false)
            return
        }

        val direction = if (destination > pager.currentItem) 1 else -1
        // Fade the live page to its themed background, then swap while the content is hidden.
        // No bitmap capture, GPU upload, or scroll through intermediate fragments on the tap path.
        pages.animate().alpha(0f).setDuration((90 * pages.alpha).toLong())
            .setInterpolator(NavigationMotion.standard).withLayer().withEndAction {
                pager.setCurrentItem(destination, false)
                pendingDraw = OneShotPreDrawListener.add(pager) {
                    pendingDraw = null
                    requestedPage = null
                    selectCurrentPage()
                    val layoutDirection = if (pager.layoutDirection == View.LAYOUT_DIRECTION_RTL) -1 else 1
                    pages.translationX = direction * layoutDirection * 8f * pager.resources.displayMetrics.density
                    revealPage()
                }
            }.start()
    }

    private fun revealPage() {
        if (pages.alpha == 1f && pages.translationX == 0f) return
        pages.animate().alpha(1f).translationX(0f).setDuration(180L)
            .setInterpolator(NavigationMotion.standard).withLayer().start()
    }

    fun detach() {
        tabs.removeOnTabSelectedListener(this)
        pager.unregisterOnPageChangeCallback(pageCallback)
        adapter.unregisterAdapterDataObserver(adapterObserver)
        resetMotion()
    }

    private fun resetMotion() {
        pages.animate().cancel()
        pendingDraw?.removeListener()
        pendingDraw = null
        requestedPage = null
        pages.alpha = 1f
        pages.translationX = 0f
    }
}
