package com.aquamorph.frcmanager.adapters

import android.view.ContextThemeWrapper
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.aquamorph.frcmanager.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.roundToInt

@RunWith(AndroidJUnit4::class)
class TeamNumberLayoutTest {
    @Test
    fun fiveDigitNumbersFitAndStayAlignedWhenAResultPosts() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            val context = ContextThemeWrapper(instrumentation.targetContext, R.style.LightTheme)
            val parent = FrameLayout(context)
            val adapter = ScheduleAdapter(context)
            val holder = adapter.onCreateViewHolder(parent, 0)
            val card = holder.itemView
            val width = (320 * context.resources.displayMetrics.density).roundToInt()
            val red = ScheduleAlliance(listOf("8", "254", "10023"), "10023", null, 0, false)
            val blue = ScheduleAlliance(listOf("12345", "1678", "99999"), null, null, 0, false)
            val upcoming = ScheduleRow("event_qm125", "qm", 1, 125, red, blue, 10, SchedulePrediction(0.71, "red", false))
            val redIds = listOf(R.id.red_team_1, R.id.red_team_2, R.id.red_team_3)
            val blueIds = listOf(R.id.blue_team_1, R.id.blue_team_2, R.id.blue_team_3)
            val redSlots = redIds.map { card.findViewById<TextView>(it) }
            val blueSlots = blueIds.map { card.findViewById<TextView>(it) }

            fun measure(row: ScheduleRow) {
                holder.bind(row)
                card.measure(
                    View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )
                card.layout(0, 0, width, card.measuredHeight)
                for ((top, bottom) in redSlots.zip(blueSlots)) {
                    assertEquals(top.left, bottom.left)
                    assertEquals(top.width, bottom.width)
                }
                (redSlots + blueSlots).forEach { slot ->
                    assertEquals(slot.text.length, slot.layout.getLineEnd(0))
                    assertTrue(slot.layout.getLineWidth(0) <= slot.width)
                }
            }

            measure(upcoming)
            val slotBounds = redSlots.map { it.left to it.width }
            val rowHeight = card.height
            measure(upcoming.copy(
                time = null, prediction = null,
                red = red.copy(score = 120, bonusRp = 2, emphasized = true), blue = blue.copy(score = 0)
            ))
            assertEquals("10023", redSlots.last().text.toString())
            assertEquals(slotBounds, redSlots.map { it.left to it.width })
            assertEquals(rowHeight, card.height)
            assertEquals(View.VISIBLE, card.findViewById<View>(R.id.score_table).visibility)
        }
    }
}
