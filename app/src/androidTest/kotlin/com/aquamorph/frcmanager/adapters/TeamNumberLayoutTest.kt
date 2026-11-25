package com.aquamorph.frcmanager.adapters

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.Choreographer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.aquamorph.frcmanager.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

@RunWith(AndroidJUnit4::class)
class TeamNumberLayoutTest {
    @Test
    fun eventScheduleKeepsFullWidthWhenFastScrollThumbAppears() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        lateinit var list: RecyclerView
        var rowWidth = 0
        val thumbFadeIn = CountDownLatch(1)
        instrumentation.runOnMainSync {
            val context = ContextThemeWrapper(instrumentation.targetContext, R.style.LightTheme)
            list = LayoutInflater.from(context).inflate(R.layout.fragment_fastscroll, null).findViewById(R.id.rv)
            val alliance = ScheduleAlliance(listOf("8", "254", "10023"), null, null, 0, false)
            list.layoutManager = LinearLayoutManager(context)
            list.adapter = ScheduleAdapter(context).apply {
                submitList(List(50) { ScheduleRow("event_qm$it", "qm", 1, it, alliance, alliance, null, null) })
            }
            val bitmap = Bitmap.createBitmap(320, 400, Bitmap.Config.ARGB_8888)
            list.measure(
                View.MeasureSpec.makeMeasureSpec(bitmap.width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(bitmap.height, View.MeasureSpec.EXACTLY)
            )
            list.layout(0, 0, bitmap.width, bitmap.height)
            val canvas = Canvas(bitmap)
            list.draw(canvas)
            assertEquals(0, list.paddingEnd)
            rowWidth = list.getChildAt(0).width
            list.scrollBy(0, 240)
            list.draw(canvas)
            assertTrue(list.computeVerticalScrollOffset() > 0)
            bitmap.recycle()

            // Let several animation frames pass before checking for a padding change.
            val choreographer = Choreographer.getInstance()
            choreographer.postFrameCallbackDelayed({ thumbFadeIn.countDown() }, 250L)
        }
        assertTrue(thumbFadeIn.await(5, TimeUnit.SECONDS))
        instrumentation.runOnMainSync {
            assertEquals(0, list.paddingEnd)
            assertEquals(rowWidth, list.getChildAt(0).width)
        }
    }

    @Test
    fun schedulesHaveTopPaddingMatchingTheCardSpacing() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            val context = ContextThemeWrapper(instrumentation.targetContext, R.style.LightTheme)
            val spacing = context.resources.getDimensionPixelSize(R.dimen.schedule_card_spacing)
            val inflater = LayoutInflater.from(context)
            for (layout in listOf(R.layout.fragment_fastscroll, R.layout.fragment_team_schedule)) {
                val schedule = inflater.inflate(layout, null)
                assertEquals(spacing, schedule.findViewById<RecyclerView>(R.id.rv).paddingTop)
            }
        }
    }

    @Test
    fun fiveDigitNumbersAlignRightAndStayAlignedWhenAResultPosts() =
        checkTeamNumberLayout(View.LAYOUT_DIRECTION_LTR)

    @Test
    fun fiveDigitNumbersAlignLeftInRightToLeftLayouts() =
        checkTeamNumberLayout(View.LAYOUT_DIRECTION_RTL)

    @Test
    fun matchNumberUsesWinnerColorExceptInOledTheme() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            for (theme in listOf(R.style.LightTheme, R.style.DarkTheme, R.style.OMOLEDTheme)) {
                val context = ContextThemeWrapper(instrumentation.targetContext, theme)
                val holder = ScheduleAdapter(context).onCreateViewHolder(FrameLayout(context), 0)
                val matchNumber = holder.itemView.findViewById<TextView>(R.id.match_number)
                val alliance = ScheduleAlliance(emptyList(), null, 100, 0, false)
                val tied = ScheduleRow("event_qm125", "qm", 1, 125, alliance, alliance, null, null)

                fun backgroundColor() = (matchNumber.background as ColorDrawable).color

                for ((winner, attr) in listOf(
                    "red" to R.attr.scheduleRedWinBackground,
                    "blue" to R.attr.scheduleBlueWinBackground
                )) {
                    val tint = TypedValue()
                    assertTrue(context.theme.resolveAttribute(attr, tint, true))
                    holder.bind(tied.copy(winningAlliance = winner))
                    assertEquals(tint.data, backgroundColor())
                    assertEquals(theme == R.style.OMOLEDTheme, backgroundColor() == Color.TRANSPARENT)
                    holder.bind(tied)
                    assertEquals(Color.TRANSPARENT, backgroundColor())
                }
            }
        }
    }

    private fun checkTeamNumberLayout(direction: Int) {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            val context = ContextThemeWrapper(instrumentation.targetContext, R.style.LightTheme)
            val parent = FrameLayout(context)
            val adapter = ScheduleAdapter(context)
            val holder = adapter.onCreateViewHolder(parent, 0)
            val card = holder.itemView
            card.layoutDirection = direction
            val width = (320 * context.resources.displayMetrics.density).roundToInt()
            val red = ScheduleAlliance(listOf("8", "254", "10023"), "10023", null, 0, false)
            val blue = ScheduleAlliance(listOf("12345", "1678", "99999"), null, null, 0, false)
            val upcoming = ScheduleRow("event_qm125", "qm", 1, 125, red, blue, 10, SchedulePrediction(0.71, "red", false))
            val redIds = listOf(R.id.red_team_1, R.id.red_team_2, R.id.red_team_3)
            val blueIds = listOf(R.id.blue_team_1, R.id.blue_team_2, R.id.blue_team_3)
            val redSlots = redIds.map { card.findViewById<TextView>(it) }
            val blueSlots = blueIds.map { card.findViewById<TextView>(it) }
            val status = card.findViewById<View>(R.id.match_status)
            val time = card.findViewById<TextView>(R.id.match_time)
            val scores = card.findViewById<View>(R.id.score_table)
            val prediction = card.findViewById<TextView>(R.id.prediction)
            val redScore = card.findViewById<TextView>(R.id.red_score)
            val blueScore = card.findViewById<TextView>(R.id.blue_score)

            fun measure(row: ScheduleRow) {
                holder.bind(row)
                card.measure(
                    View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )
                card.layout(0, 0, width, card.measuredHeight)
                val rowContents = (card as ViewGroup).getChildAt(0) as ViewGroup
                val firstCell = rowContents.getChildAt(0)
                val lastCell = rowContents.getChildAt(rowContents.childCount - 1)
                val sideMargin = context.resources.getDimensionPixelSize(R.dimen.schedule_card_side_margin)
                assertEquals(sideMargin, rowContents.left + minOf(firstCell.left, lastCell.left))
                assertEquals(width - sideMargin, rowContents.left + maxOf(firstCell.right, lastCell.right))
                for ((top, bottom) in redSlots.zip(blueSlots)) {
                    assertEquals(top.left, bottom.left)
                    assertEquals(top.width, bottom.width)
                }
                (redSlots + blueSlots).forEach { slot ->
                    assertEquals(slot.text.length, slot.layout.getLineEnd(0))
                    val textWidth = slot.width - slot.totalPaddingLeft - slot.totalPaddingRight
                    assertTrue(slot.layout.getLineWidth(0) <= textWidth)
                    if (direction == View.LAYOUT_DIRECTION_RTL) {
                        assertEquals(0f, slot.layout.getLineLeft(0), 1f)
                    } else {
                        assertEquals(textWidth.toFloat(), slot.layout.getLineRight(0), 1f)
                    }
                }
            }

            measure(upcoming)
            assertEquals(View.VISIBLE, status.visibility)
            assertEquals(View.GONE, scores.visibility)
            assertEquals(View.VISIBLE, prediction.visibility)
            assertTrue(prediction.layout.getLineWidth(0) <= prediction.width - prediction.totalPaddingLeft - prediction.totalPaddingRight)
            val highlighted = redSlots.last().text as Spanned
            assertEquals(1, highlighted.getSpans(0, highlighted.length, UnderlineSpan::class.java).size)
            val slotBounds = redSlots.map { it.left to it.width }
            val allianceTops = listOf(redSlots.first(), blueSlots.first()).map { (it.parent as View).top }
            val rowHeight = card.height
            assertEquals(2 * context.resources.getDimensionPixelSize(R.dimen.schedule_alliance_height) +
                context.resources.getDimensionPixelSize(R.dimen.schedule_card_spacing), rowHeight)
            measure(upcoming.copy(
                time = null, prediction = null,
                red = red.copy(score = 120, bonusRp = 2, emphasized = true), blue = blue.copy(score = 0)
            ))
            assertEquals("10023", redSlots.last().text.toString())
            assertEquals(slotBounds, redSlots.map { it.left to it.width })
            assertEquals(allianceTops, listOf(redSlots.first(), blueSlots.first()).map { (it.parent as View).top })
            assertEquals(rowHeight, card.height)
            assertEquals(View.GONE, status.visibility)
            assertEquals(View.VISIBLE, scores.visibility)
            assertEquals(View.INVISIBLE, prediction.visibility)
            assertEquals(context.getString(R.string.scheduleScoreWithBonus, bonusRpDots(2), 120), redScore.text.toString())
            assertEquals("0", blueScore.text.toString())

            measure(upcoming.copy(time = null, red = red.copy(highlightedTeam = null)))
            assertEquals(context.getString(R.string.scheduleTimeTbd), time.text.toString())
            assertEquals(View.GONE, scores.visibility)
            assertEquals(View.VISIBLE, prediction.visibility)
        }
    }
}
