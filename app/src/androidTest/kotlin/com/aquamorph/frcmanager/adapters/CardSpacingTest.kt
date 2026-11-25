package com.aquamorph.frcmanager.adapters

import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.aquamorph.frcmanager.R
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CardSpacingTest {
    @Test
    fun cardListsShareTheScheduleOuterSpacing() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            val context = ContextThemeWrapper(instrumentation.targetContext, R.style.LightTheme)
            val inflater = LayoutInflater.from(context)
            val spacing = context.resources.getDimensionPixelSize(R.dimen.card_spacing)
            for ((listLayout, itemLayout) in listOf(
                R.layout.fragment_fastscroll to R.layout.match,
                R.layout.fragment_team_schedule to R.layout.match,
                R.layout.fragment_team_schedule to R.layout.ranking_row,
                R.layout.fragment_team_schedule to R.layout.alliance,
                R.layout.fragment_awards to R.layout.award
            )) {
                val list = inflater.inflate(listLayout, null).findViewById<RecyclerView>(R.id.rv)
                assertEquals(spacing, list.paddingTop)
                assertEquals(0, list.paddingStart)
                assertEquals(0, list.paddingEnd)
                assertEquals(0, list.paddingBottom)

                list.layoutManager = LinearLayoutManager(context)
                val row = inflater.inflate(itemLayout, list, false)
                val card = if (itemLayout == R.layout.match) (row as ViewGroup).getChildAt(0) else row
                val margins = card.layoutParams as ViewGroup.MarginLayoutParams
                assertEquals(spacing, margins.marginStart)
                assertEquals(spacing, margins.marginEnd)
                assertEquals(spacing, margins.bottomMargin)
                assertEquals(0, margins.topMargin)
            }
        }
    }
}
