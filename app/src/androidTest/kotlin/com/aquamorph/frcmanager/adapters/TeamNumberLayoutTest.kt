package com.aquamorph.frcmanager.adapters

import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.activities.Setup
import com.aquamorph.frcmanager.utils.ScheduleFastScrollView
import com.aquamorph.frcmanager.utils.adaptiveCardGrid
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

@RunWith(AndroidJUnit4::class)
class TeamNumberLayoutTest {
    @Test
    fun eventScheduleReflowsInPlaceAsAvailableWidthChanges() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            val context = ContextThemeWrapper(instrumentation.targetContext, R.style.LightTheme)
            val list = LayoutInflater.from(context).inflate(R.layout.fragment_fastscroll, null)
                .findViewById<ScheduleFastScrollView>(R.id.rv)
            val cardWidth = context.resources.getDimensionPixelSize(R.dimen.event_grid_card_min_width)
            val gutter = context.resources.getDimensionPixelSize(R.dimen.schedule_fast_scroll_gutter)
            val height = context.resources.getDimensionPixelSize(R.dimen.schedule_alliance_height) * 16
            val manager = list.adaptiveCardGrid(R.dimen.event_grid_card_min_width)
            list.layoutManager = manager
            val schedule = ScheduleAdapter(context).apply {
                val alliance = ScheduleAlliance(emptyList(), null, null, 0, false)
                submitList(List(60) { ScheduleRow("event_$it", "qm", 1, it + 1, alliance, alliance, null, null) })
            }
            list.adapter = schedule

            fun resize(width: Int) {
                list.measure(
                    View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
                )
                list.layout(0, 0, width, height)
            }

            val narrow = 2 * cardWidth + gutter - 1
            val wide = narrow + 1
            resize(narrow)
            assertEquals(1, manager.spanCount)
            manager.scrollToPositionWithOffset(20, 0)
            resize(narrow)
            assertEquals(20, manager.findFirstVisibleItemPosition())

            resize(wide)
            assertEquals(2, manager.spanCount)
            assertEquals(20, manager.findFirstVisibleItemPosition())
            val first = manager.findViewByPosition(20)!!
            val second = manager.findViewByPosition(21)!!
            assertEquals(first.top, second.top)
            assertTrue(first.left != second.left)

            resize(narrow)
            assertEquals(1, manager.spanCount)
            assertEquals(20, manager.findFirstVisibleItemPosition())
            assertEquals(schedule, list.adapter)

            list.setPaddingRelative(0, list.paddingTop, gutter, 0)
            resize(wide)
            assertEquals(2, manager.spanCount)
        }
    }

    @Test
    fun largeTextKeepsCardsInOneColumn() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            val base = instrumentation.targetContext
            val config = Configuration(base.resources.configuration).apply { fontScale = 1.5f }
            val context = ContextThemeWrapper(base.createConfigurationContext(config), R.style.LightTheme)
            val list = RecyclerView(context)
            val manager = list.adaptiveCardGrid(R.dimen.event_grid_card_min_width)
            list.layoutManager = manager
            list.adapter = ScheduleAdapter(context)
            val width = context.resources.getDimensionPixelSize(R.dimen.event_grid_card_min_width) * 3
            val spec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
            list.measure(spec, spec)
            list.layout(0, 0, width, width)
            assertEquals(1, manager.spanCount)
        }
    }

    @Test
    fun eventScheduleThumbWorksInOneColumn() = checkEventScheduleThumb(1)

    @Test
    fun eventScheduleThumbWorksInTwoColumns() = checkEventScheduleThumb(2)

    private fun checkEventScheduleThumb(columns: Int) {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            val context = ContextThemeWrapper(instrumentation.targetContext, R.style.LightTheme)
            val inflater = LayoutInflater.from(context)
            val target = context.resources.getDimensionPixelSize(R.dimen.schedule_fast_scroll_target)
            val gutter = context.resources.getDimensionPixelSize(R.dimen.schedule_fast_scroll_gutter)
            val margin = context.resources.getDimensionPixelSize(R.dimen.card_spacing)
            val width = if (columns == 1) target * 7 else target * 15
            val height = target * 9
            for (direction in listOf(View.LAYOUT_DIRECTION_LTR, View.LAYOUT_DIRECTION_RTL)) {
                val list = inflater.inflate(R.layout.fragment_fastscroll, null)
                    .findViewById<ScheduleFastScrollView>(R.id.rv)
                list.layoutDirection = direction
                val alliance = ScheduleAlliance(listOf("8", "254", "10023"), null, null, 0, false)
                val manager = list.adaptiveCardGrid(R.dimen.event_grid_card_min_width)
                list.layoutManager = manager
                list.adapter = ScheduleAdapter(context).apply {
                    submitList(List(51) {
                        val round = if (it == 50) "f" else "qm"
                        ScheduleRow("event_$it", round, 1, it + 1, alliance, alliance, null, null)
                    })
                }
                list.measure(
                    View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
                )
                list.layout(0, 0, width, height)
                assertEquals(columns, manager.spanCount)
                assertEquals(0, list.paddingEnd)
                assertEquals(width, (0 until columns).sumOf { list.getChildAt(it).width })
                list.scrollBy(0, 1)
                list.scrollBy(0, -1)
                list.measure(
                    View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
                )
                list.layout(0, 0, width, height)
                val cards = (0 until columns).map { list.getChildAt(it) }
                val rowLeft = cards.minOf { it.left }
                val rowRight = cards.maxOf { it.right }
                assertEquals(gutter, if (direction == View.LAYOUT_DIRECTION_RTL) rowLeft else width - rowRight)
                assertEquals(width - gutter, rowRight - rowLeft)
                val x = if (direction == View.LAYOUT_DIRECTION_RTL) target / 2f else width - target / 2f
                val centerFromEdge = (gutter + margin) / 2f
                val center = if (direction == View.LAYOUT_DIRECTION_RTL) centerFromEdge else width - centerFromEdge
                val thumbX = center.toInt()
                val thumbY = list.paddingTop + target / 2
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                fun thumbColor(): Int {
                    list.draw(Canvas(bitmap))
                    return bitmap.getPixel(thumbX, thumbY)
                }
                fun touch(action: Int, y: Float): Boolean {
                    val event = MotionEvent.obtain(0L, 0L, action, x, y, 0)
                    return try {
                        list.dispatchTouchEvent(event)
                    } finally {
                        event.recycle()
                    }
                }
                assertEquals(direction, list.layoutDirection)
                assertEquals(context.getColor(R.color.primary_dark), thumbColor())
                val redPixels = (0 until width).filter { bitmap.getPixel(it, thumbY) == context.getColor(R.color.primary_dark) }
                assertEquals(center, (redPixels.first() + redPixels.last() + 1) / 2f, 0.5f)
                assertTrue(touch(MotionEvent.ACTION_DOWN, thumbY.toFloat()))
                assertEquals(context.getColor(R.color.primary), thumbColor())
                bitmap.recycle()
                fun label(round: Int, match: String) = ScheduleAdapter.FastScrollLabel(context.getString(round), match)

                assertEquals(label(R.string.scheduleRoundQualification, "QM-1"), list.previewLabel)
                assertTrue(touch(MotionEvent.ACTION_MOVE, height.toFloat()))
                assertEquals(50, manager.findLastVisibleItemPosition())
                assertEquals(label(R.string.scheduleRoundFinal, "F-1-51"), list.previewLabel)
                assertEquals(gutter, list.paddingEnd)
                val bottomCards = (0 until columns).map { list.getChildAt(it) }
                assertEquals(rowLeft, bottomCards.minOf { it.left })
                assertEquals(rowRight, bottomCards.maxOf { it.right })
                assertTrue(touch(MotionEvent.ACTION_UP, height.toFloat()))
                assertNull(list.previewLabel)
            }
        }
    }

    @Test
    fun eventScheduleThumbHidesAfterIdleAndReturnsOnScroll() {
        lateinit var list: ScheduleFastScrollView
        val faded = CountDownLatch(1)
        val shownAgain = CountDownLatch(1)
        ActivityScenario.launch(Setup::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val context = ContextThemeWrapper(activity, R.style.DarkTheme)
                val schedule = LayoutInflater.from(context).inflate(R.layout.fragment_fastscroll, null)
                list = schedule.findViewById(R.id.rv)
                list.layoutManager = LinearLayoutManager(context)
                list.adapter = ScheduleAdapter(context).apply {
                    val alliance = ScheduleAlliance(emptyList(), null, null, 0, false)
                    submitList(List(60) { ScheduleRow("event_$it", "qm", 1, it + 1, alliance, alliance, null, null) })
                }
                activity.addContentView(schedule, ViewGroup.LayoutParams(600, 600))
                schedule.measure(
                    View.MeasureSpec.makeMeasureSpec(600, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(600, View.MeasureSpec.EXACTLY)
                )
                schedule.layout(0, 0, 600, 600)
                assertTrue(list.thumbOpacity < 1f)
                Handler(Looper.getMainLooper()).postDelayed({ faded.countDown() }, 2_200L)
            }
            assertTrue(faded.await(5, TimeUnit.SECONDS))
            scenario.onActivity {
                assertEquals(0f, list.thumbOpacity, 0f)
                assertEquals(0, list.paddingEnd)
                assertEquals(list.width, list.getChildAt(0).width)
                val target = list.resources.getDimensionPixelSize(R.dimen.schedule_fast_scroll_target)
                val event = MotionEvent.obtain(0L, 0L, MotionEvent.ACTION_DOWN, list.width - target / 2f, list.paddingTop + target / 2f, 0)
                try {
                    list.dispatchTouchEvent(event)
                    assertNull(list.previewLabel)
                    event.action = MotionEvent.ACTION_CANCEL
                    list.dispatchTouchEvent(event)
                } finally {
                    event.recycle()
                }
                list.scrollBy(0, 100)
                assertTrue(list.thumbOpacity < 1f)
                assertEquals(0, list.paddingEnd)
                Handler(Looper.getMainLooper()).postDelayed({ shownAgain.countDown() }, 350L)
            }
            assertTrue(shownAgain.await(5, TimeUnit.SECONDS))
            scenario.onActivity {
                assertEquals(1f, list.thumbOpacity, 0f)
                assertEquals(list.resources.getDimensionPixelSize(R.dimen.schedule_fast_scroll_gutter), list.paddingEnd)
                assertEquals(list.width - list.paddingEnd, list.getChildAt(0).width)
            }
        }
    }

    @Test
    fun schedulesHaveTopPaddingMatchingTheCardSpacing() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            val context = ContextThemeWrapper(instrumentation.targetContext, R.style.LightTheme)
            val spacing = context.resources.getDimensionPixelSize(R.dimen.card_spacing)
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
                val sideMargin = context.resources.getDimensionPixelSize(R.dimen.card_spacing)
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
                context.resources.getDimensionPixelSize(R.dimen.card_spacing), rowHeight)
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
