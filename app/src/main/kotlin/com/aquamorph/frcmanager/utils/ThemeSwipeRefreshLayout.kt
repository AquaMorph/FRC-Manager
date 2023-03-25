package com.aquamorph.frcmanager.utils

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.aquamorph.frcmanager.R

class ThemeSwipeRefreshLayout : SwipeRefreshLayout {

    constructor(context: Context): super(context) {
        setColors()
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        setColors()
    }

    private fun setColors() {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.dividerColor, typedValue, true)
        setColorSchemeResources(R.color.accent)
        setProgressBackgroundColorSchemeColor(typedValue.data)
    }
}