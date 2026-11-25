package com.aquamorph.frcmanager.activities

import android.view.View
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.fragments.SettingsFragment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScrollingInsetsTest {
    @Test
    fun settingsListDrawsBehindNavigationAndKeepsItsBottomReachable() {
        ActivityScenario.launch(Settings::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val content = activity.findViewById<View>(android.R.id.content)
                val settings = activity.supportFragmentManager.findFragmentById(R.id.content_frame) as SettingsFragment
                val list = settings.listView
                val insets = WindowInsetsCompat.Builder()
                    .setInsets(WindowInsetsCompat.Type.navigationBars(), Insets.of(0, 0, 0, 40))
                val keyboardHidden = insets.build()
                val keyboardShown = insets
                    .setInsets(WindowInsetsCompat.Type.ime(), Insets.of(0, 0, 0, 300))
                    .build()

                ViewCompat.dispatchApplyWindowInsets(content, keyboardShown)
                assertEquals(300, content.paddingBottom)
                val initialBottom = list.paddingBottom

                ViewCompat.dispatchApplyWindowInsets(content, keyboardHidden)
                assertEquals(0, content.paddingBottom)
                assertEquals(initialBottom + 40, list.paddingBottom)
                assertFalse(list.clipToPadding)

                ViewCompat.dispatchApplyWindowInsets(content, keyboardShown)
                assertEquals(initialBottom, list.paddingBottom)
            }
        }
    }
}
