package com.aquamorph.frcmanager.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
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
    private var outgoingPage: BitmapDrawable? = null
    private var outgoingPosition = 0
    private var syncingTabs = false
    private var settlingFromSwipe = false
    private var switchingPage = false
    private val adapterObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() = populateTabs()
    }
    private val pageCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrollStateChanged(state: Int) {
            if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                settlingFromSwipe = true
                resetMotion()
            } else if (state == ViewPager2.SCROLL_STATE_IDLE) {
                if (!switchingPage) selectCurrentPage()
                settlingFromSwipe = false
            }
        }

        override fun onPageScrolled(position: Int, offset: Float, pixels: Int) {
            if (settlingFromSwipe && !switchingPage) tabs.setScrollPosition(position, offset, true)
        }

        override fun onPageSelected(position: Int) {
            if (!settlingFromSwipe && !switchingPage) selectCurrentPage()
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
        if (destination == pager.currentItem) return
        val source = if (pendingDraw != null) outgoingPosition else pager.currentItem
        val snapshot = outgoingPage.takeIf { pendingDraw != null }
        resetMotion()
        if (destination == source || !pager.isAttachedToWindow || !pager.isLaidOut ||
            pages.width == 0 || pages.height == 0 || !NavigationMotion.isEnabled(pager.context)
        ) {
            pager.setCurrentItem(destination, false)
            return
        }

        // Keep the old page visible while the pager jumps straight to the destination.
        val width = pages.width
        val height = pages.height
        val outgoing = snapshot ?: capturePage(width, height)
        val direction = if (destination > source) 1 else -1
        val layoutDirection = if (pager.layoutDirection == View.LAYOUT_DIRECTION_RTL) -1 else 1
        val distance = direction * layoutDirection * width
        outgoing.setBounds(0, 0, width, height)
        outgoingPage = outgoing
        outgoingPosition = source
        pager.overlay.add(outgoing)
        switchingPage = true
        pager.setCurrentItem(destination, false)
        pendingDraw = OneShotPreDrawListener.add(pager) {
            pendingDraw = null
            switchingPage = false
            selectCurrentPage()
            pages.translationX = distance.toFloat()
            pages.animate().translationX(0f).setDuration(280L)
                .setInterpolator(NavigationMotion.standard)
                .setUpdateListener {
                    val left = pages.translationX.toInt() - distance
                    outgoing.setBounds(left, 0, left + width, height)
                    pager.invalidate()
                }
                .withEndAction { resetMotion() }.start()
        }
    }

    private fun capturePage(width: Int, height: Int): BitmapDrawable {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        Canvas(bitmap).apply {
            drawColor(NavigationMotion.surfaceColor(pager.context))
            pages.draw(this)
        }
        return BitmapDrawable(pager.resources, bitmap)
    }

    fun detach() {
        tabs.removeOnTabSelectedListener(this)
        pager.unregisterOnPageChangeCallback(pageCallback)
        adapter.unregisterAdapterDataObserver(adapterObserver)
        resetMotion()
    }

    private fun resetMotion() {
        pages.animate().cancel()
        pages.animate().setUpdateListener(null)
        pendingDraw?.removeListener()
        pendingDraw = null
        outgoingPage?.let { pager.overlay.remove(it) }
        outgoingPage = null
        switchingPage = false
        pages.translationX = 0f
    }
}
