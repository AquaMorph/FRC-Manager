package com.aquamorph.frcmanager.utils

import android.view.ContextThemeWrapper
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.viewpager2.widget.ViewPager2
import com.aquamorph.frcmanager.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TabJumpTransitionTest {
    @Test
    fun distantSelectionJumpsDirectlyWhileAdjacentSelectionSmoothScrolls() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            val context = ContextThemeWrapper(instrumentation.targetContext, R.style.LightTheme)
            val pager = ViewPager2(context).apply { adapter = PageAdapter() }
            val tabs = TabLayout(context)
            val mediator = TabLayoutMediator(tabs, pager) { tab, position -> tab.text = "Page $position" }
            mediator.attach()
            val transition = TabJumpTransition(tabs, pager)

            try {
                val size = View.MeasureSpec.makeMeasureSpec(300, View.MeasureSpec.EXACTLY)
                pager.measure(size, size)
                pager.layout(0, 0, 300, 300)

                tabs.getTabAt(4)!!.select()
                assertEquals(4, pager.currentItem)
                assertEquals(ViewPager2.SCROLL_STATE_IDLE, pager.scrollState)

                pager.layout(0, 0, 300, 300)
                tabs.getTabAt(3)!!.select()
                assertEquals(3, pager.currentItem)
                assertEquals(ViewPager2.SCROLL_STATE_SETTLING, pager.scrollState)
                assertEquals(1f, pager.alpha, 0f)
                assertEquals(0f, pager.translationX, 0f)
            } finally {
                transition.detach()
                mediator.detach()
            }
        }
    }

    private class PageAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun getItemCount() = 5

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val page = View(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }
            return object : RecyclerView.ViewHolder(page) {}
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = Unit
    }
}
