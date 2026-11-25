package com.aquamorph.frcmanager.utils

import android.view.ContextThemeWrapper
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.viewpager2.widget.ViewPager2
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.activities.Settings
import com.google.android.material.tabs.TabLayout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class TabJumpTransitionTest {
    @Test
    fun aSecondTapCanInterruptAnAttachedTabJump() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        ActivityScenario.launch(Settings::class.java).use { scenario ->
            lateinit var pager: ViewPager2
            lateinit var tabs: TabLayout
            lateinit var transition: TabJumpTransition
            val selected = CountDownLatch(1)
            scenario.onActivity { activity ->
                pager = ViewPager2(activity).apply { adapter = PageAdapter() }
                tabs = TabLayout(activity)
                activity.setContentView(LinearLayout(activity).apply {
                    orientation = LinearLayout.VERTICAL
                    addView(tabs)
                    addView(pager, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
                })
                transition = TabJumpTransition(tabs, pager) { "Page $it" }
                pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        if (position == 1) selected.countDown()
                    }
                })
            }

            instrumentation.waitForIdleSync()
            try {
                scenario.onActivity {
                    tabs.getTabAt(4)!!.select()
                    tabs.getTabAt(1)!!.select()
                }
                assertTrue(selected.await(5, TimeUnit.SECONDS))
                scenario.onActivity {
                    assertEquals(1, pager.currentItem)
                    assertEquals(1, tabs.selectedTabPosition)
                    assertEquals(ViewPager2.SCROLL_STATE_IDLE, pager.scrollState)
                }
            } finally {
                scenario.onActivity { transition.detach() }
            }
        }
    }

    @Test
    fun pagerNavigationKeepsTheSelectedTabInSync() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            val context = ContextThemeWrapper(instrumentation.targetContext, R.style.LightTheme)
            val adapter = PageAdapter()
            val pager = ViewPager2(context).apply { this.adapter = adapter }
            val tabs = TabLayout(context)
            val transition = TabJumpTransition(tabs, pager) { "Page $it" }
            try {
                pager.setCurrentItem(2, false)
                assertEquals(2, tabs.selectedTabPosition)

                adapter.count = 0
                adapter.notifyDataSetChanged()
                assertEquals(0, tabs.tabCount)
                assertEquals(1f, pager.getChildAt(0).alpha, 0f)
            } finally {
                transition.detach()
            }
        }
    }

    @Test
    fun detachedPagerSwitchesDirectlyAtAnyDistance() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            val context = ContextThemeWrapper(instrumentation.targetContext, R.style.LightTheme)
            val pager = ViewPager2(context).apply { adapter = PageAdapter() }
            val tabs = TabLayout(context)
            val transition = TabJumpTransition(tabs, pager) { "Page $it" }

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
                assertEquals(ViewPager2.SCROLL_STATE_IDLE, pager.scrollState)
                assertEquals(1f, pager.alpha, 0f)
                assertEquals(0f, pager.translationX, 0f)
            } finally {
                transition.detach()
            }
        }
    }

    private class PageAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var count = 5

        override fun getItemCount() = count

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val page = View(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }
            return object : RecyclerView.ViewHolder(page) {}
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = Unit
    }
}
