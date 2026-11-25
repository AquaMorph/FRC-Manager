package com.aquamorph.frcmanager.utils

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.ViewGroupCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.aquamorph.frcmanager.R

internal fun ComponentActivity.applyEdgeToEdge(drawBehindNavigation: Boolean = true) {
    val background = TypedValue()
    val lightBackground = theme.resolveAttribute(android.R.attr.windowBackground, background, true) &&
        background.type in TypedValue.TYPE_FIRST_COLOR_INT..TypedValue.TYPE_LAST_COLOR_INT &&
        ColorUtils.calculateLuminance(background.data) > 0.5
    enableEdgeToEdge(
        statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        navigationBarStyle = if (lightBackground) {
            // API 23-25 only support light navigation buttons, so they need a dark scrim.
            SystemBarStyle.light(Color.TRANSPARENT, Color.BLACK)
        } else {
            SystemBarStyle.dark(Color.TRANSPARENT)
        }
    )
    if (Build.VERSION.SDK_INT >= 29) {
        // Android can otherwise add an opaque background in three-button navigation.
        window.isNavigationBarContrastEnforced = false
    }

    val content = findViewById<ViewGroup>(android.R.id.content)
    if (drawBehindNavigation) ViewGroupCompat.installCompatInsetsDispatch(content)
    // Preserve the status bar color without painting the navigation area.
    val statusBarBackground = ColorDrawable(getColor(R.color.primary_dark))
    content.overlay.add(statusBarBackground)
    content.addOnLayoutChangeListener { view, _, _, _, _, _, _, _, _ ->
        statusBarBackground.setBounds(0, 0, view.width, view.paddingTop)
    }
    ViewCompat.setOnApplyWindowInsetsListener(content) { view, insets ->
        val safeArea = insets.getInsets(
            WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout() or
                WindowInsetsCompat.Type.ime()
        )
        val navigation = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
        val drawBehindBottom = drawBehindNavigation && safeArea.bottom == navigation.bottom
        val bottom = if (drawBehindBottom) 0 else safeArea.bottom
        view.setPadding(safeArea.left, safeArea.top, safeArea.right, bottom)
        statusBarBackground.setBounds(0, 0, view.width, safeArea.top)
        if (drawBehindNavigation) {
            // Forward only the inset not handled here, to the scrolling views and snackbars.
            val scrollInset = if (drawBehindBottom) navigation.bottom else 0
            WindowInsetsCompat.Builder()
                .setInsets(WindowInsetsCompat.Type.navigationBars(), Insets.of(0, 0, 0, scrollInset))
                .build()
        } else {
            WindowInsetsCompat.CONSUMED
        }
    }
    ViewCompat.requestApplyInsets(content)
}

internal fun ViewGroup.scrollBehindNavigationBar() {
    val initialBottom = paddingBottom
    clipToPadding = false
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val navigation = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
        view.updatePadding(bottom = initialBottom + navigation.bottom)
        insets
    }
    ViewCompat.requestApplyInsets(this)
}
