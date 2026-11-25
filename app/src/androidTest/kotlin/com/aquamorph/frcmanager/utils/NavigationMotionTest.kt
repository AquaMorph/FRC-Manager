package com.aquamorph.frcmanager.utils

import android.app.Activity
import android.content.Intent
import android.transition.Transition
import android.view.View
import android.view.ViewGroup
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.activities.Settings
import com.aquamorph.frcmanager.activities.TeamSummary
import com.google.android.material.transition.platform.MaterialContainerTransform
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class NavigationMotionTest {
    @Test
    fun attachedContainerOpensAndReturnsWithMaterialTransform() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        assumeTrue(NavigationMotion.isEnabled(instrumentation.targetContext))
        val monitor = instrumentation.addMonitor(TeamSummary::class.java.name, null, false)
        ActivityScenario.launch(Settings::class.java).use { scenario ->
            try {
                scenario.onActivity { source ->
                    val toolbar = source.findViewById<View>(R.id.toolbar)
                    toolbar.transitionName = "test-team-row"
                    NavigationMotion.openDetail(toolbar, Intent(source, TeamSummary::class.java).putExtra("teamNumber", "254"))
                }

                val detail = checkNotNull(instrumentation.waitForMonitorWithTimeout(monitor, 5000))
                val returned = CountDownLatch(1)
                try {
                    instrumentation.runOnMainSync {
                        assertTrue(detail.window.sharedElementEnterTransition is MaterialContainerTransform)
                        val root = detail.findViewById<ViewGroup>(android.R.id.content).getChildAt(0)
                        assertEquals("detail_container", root.transitionName)
                        detail.window.sharedElementReturnTransition.addListener(object : Transition.TransitionListener {
                            override fun onTransitionStart(transition: Transition) = Unit
                            override fun onTransitionCancel(transition: Transition) = Unit
                            override fun onTransitionPause(transition: Transition) = Unit
                            override fun onTransitionResume(transition: Transition) = Unit
                            override fun onTransitionEnd(transition: Transition) = returned.countDown()
                        })
                    }
                    instrumentation.sendKeyDownUpSync(android.view.KeyEvent.KEYCODE_BACK)
                    assertTrue("Back should run the container return transition", returned.await(5, TimeUnit.SECONDS))
                    instrumentation.waitForIdleSync()
                    scenario.onActivity { source ->
                        assertEquals(1f, source.findViewById<View>(R.id.toolbar).alpha, 0f)
                    }
                } finally {
                    instrumentation.runOnMainSync { if (!detail.isFinishing) detail.finish() }
                }
            } finally {
                instrumentation.removeMonitor(monitor)
            }
        }
    }
}
