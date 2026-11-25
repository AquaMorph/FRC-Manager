package com.aquamorph.frcmanager.utils

import android.graphics.Color
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.aquamorph.frcmanager.R

internal fun ComponentActivity.applyEdgeToEdge() {
    enableEdgeToEdge(
        statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
    )

    val content = findViewById<View>(android.R.id.content)
    content.setBackgroundResource(R.color.primary_dark)
    ViewCompat.setOnApplyWindowInsetsListener(content) { view, insets ->
        val safeArea = insets.getInsets(
            WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout() or
                WindowInsetsCompat.Type.ime()
        )
        view.setPadding(safeArea.left, safeArea.top, safeArea.right, safeArea.bottom)
        // The content frame has already inset its children, including the AppIntro pager.
        WindowInsetsCompat.CONSUMED
    }
    ViewCompat.requestApplyInsets(content)
}
