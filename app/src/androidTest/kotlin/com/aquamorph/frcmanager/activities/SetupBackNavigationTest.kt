package com.aquamorph.frcmanager.activities

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SetupBackNavigationTest {
    @Test
    fun backReturnsToPreviousSlideAndLetsSystemHandleBackOnFirstSlide() {
        ActivityScenario.launch(Setup::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val pager = activity.getPager()
                val firstSlide = pager.currentItem
                pager.goToNextSlide()
                assertTrue(firstSlide != pager.currentItem)
                assertTrue(activity.onBackPressedDispatcher.hasEnabledCallbacks())

                activity.onBackPressedDispatcher.onBackPressed()
                assertEquals(firstSlide, pager.currentItem)
                assertFalse(activity.isFinishing)

                assertFalse(activity.onBackPressedDispatcher.hasEnabledCallbacks())
            }
        }
    }
}
