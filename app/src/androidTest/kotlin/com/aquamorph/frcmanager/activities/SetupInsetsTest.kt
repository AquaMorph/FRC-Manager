package com.aquamorph.frcmanager.activities

import android.view.View
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SetupInsetsTest {
    @Test
    fun contentReservesSpaceForSystemBarsCutoutAndKeyboard() {
        ActivityScenario.launch(Setup::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val content = activity.findViewById<View>(android.R.id.content)
                val systemInsets = WindowInsetsCompat.Builder()
                    .setInsets(WindowInsetsCompat.Type.statusBars(), Insets.of(0, 30, 0, 0))
                    .setInsets(WindowInsetsCompat.Type.navigationBars(), Insets.of(0, 0, 0, 40))
                    .setInsets(WindowInsetsCompat.Type.displayCutout(), Insets.of(20, 50, 10, 0))
                val keyboardHidden = systemInsets.build()
                val keyboardShown = systemInsets
                    .setInsets(WindowInsetsCompat.Type.ime(), Insets.of(0, 0, 0, 300))
                    .build()

                ViewCompat.dispatchApplyWindowInsets(content, keyboardShown)
                assertEquals(20, content.paddingLeft)
                assertEquals(50, content.paddingTop)
                assertEquals(10, content.paddingRight)
                assertEquals(300, content.paddingBottom)

                ViewCompat.dispatchApplyWindowInsets(content, keyboardHidden)
                assertEquals(40, content.paddingBottom)
            }
        }
    }
}
