package com.aquamorph.frcmanager.adapters

import android.content.res.Configuration
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.models.tba.DistrictRank
import com.aquamorph.frcmanager.models.tba.Rank
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.roundToInt

@RunWith(AndroidJUnit4::class)
class RankingAdapterTest {
    @Test
    fun longTeamNameTruncatesAfterTheNumberOnTheSameLine() = onMain {
        for (fontScale in listOf(1f, 2f)) {
            val holder = RankingViewHolder.create(parent(fontScale))
            holder.bind(1, "frc10023", "A very long team name that cannot fit on a narrow screen", emptyList(), true)
            val row = holder.itemView
            row.layoutDirection = View.LAYOUT_DIRECTION_LTR
            val width = (320 * row.resources.displayMetrics.density).roundToInt()
            row.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY), View.MeasureSpec.UNSPECIFIED)
            row.layout(0, 0, width, row.measuredHeight)

            val number = row.findViewById<TextView>(R.id.team_number)
            val name = row.findViewById<TextView>(R.id.ranking_team_name)
            assertEquals("10023", number.text.toString())
            assertTrue(number.layout.getLineWidth(0) <= number.width)
            assertTrue(number.right <= name.left)
            assertEquals(number.top + number.baseline, name.top + name.baseline)
            assertEquals(1, name.lineCount)
            assertTrue(name.layout.getEllipsisCount(0) > 0)
        }
    }

    @Test
    fun districtRowsShowOfficialRankAndDoNotLeaveOldMetricsAfterRebinding() = onMain {
        val district = DistrictRank("frc10023", 12, 10, 90, arrayListOf())
        district.eventPoints.add(district.EventPoints("event", false, 0, 0, 0, 24))
        val next = DistrictRank("frc254", 13, 0, 20, arrayListOf())
        val adapter = DistrictRankAdapter(listOf(district, next), emptyList())
        val holder = adapter.onCreateViewHolder(parent(), 0)
        val row = holder.itemView
        val rank = row.findViewById<TextView>(R.id.rank)
        val metrics = row.findViewById<LinearLayout>(R.id.ranking_metrics)

        adapter.onBindViewHolder(holder, 0)
        assertFalse(rank.isActivated)

        adapter.highlightCutoff = 12
        adapter.onBindViewHolder(holder, 0)
        assertEquals("#12", rank.text.toString())
        assertTrue(rank.isActivated)
        assertEquals("Team 10023", row.findViewById<TextView>(R.id.ranking_team_name).text.toString())
        assertEquals("24", metrics.getChildAt(1).findViewById<TextView>(R.id.ranking_metric_value).text.toString())

        adapter.onBindViewHolder(holder, 1)
        assertEquals("#13", rank.text.toString())
        assertFalse(rank.isActivated)
        assertEquals(View.GONE, metrics.getChildAt(1).visibility)
    }

    @Test
    fun eventRowsKeepSortOrderAndRecordAtLargeTextSizes() = onMain {
        val event = Rank(arrayListOf(), arrayListOf(), arrayListOf(Rank.SortOrderInfo("Ranking score", 2)))
        event.rankings?.add(event.Rankings(0, 10, 0.0, 12, Rank.WLTRecord(3, 7, 0), doubleArrayOf(), doubleArrayOf(2.5), "frc10023"))
        val adapter = RankAdapter(listOf(event), emptyList())
        val holder = adapter.onCreateViewHolder(parent(2f), 0)
        adapter.onBindViewHolder(holder, 0)
        val row = holder.itemView
        val width = (320 * row.resources.displayMetrics.density).roundToInt()
        row.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY), View.MeasureSpec.UNSPECIFIED)
        row.layout(0, 0, width, row.measuredHeight)

        assertEquals("#12", row.findViewById<TextView>(R.id.rank).text.toString())
        val metrics = row.findViewById<LinearLayout>(R.id.ranking_metrics)
        assertEquals(2, metrics.childCount)
        assertEquals("Ranking score", metrics.getChildAt(0).findViewById<TextView>(R.id.ranking_metric_label).text.toString())
        val record = metrics.getChildAt(1).findViewById<TextView>(R.id.ranking_metric_value)
        assertEquals("7-3-0", record.text.toString())
        assertEquals(1, record.lineCount)
        assertTrue(record.layout.getLineWidth(0) <= record.width)
    }

    private fun onMain(block: () -> Unit) = InstrumentationRegistry.getInstrumentation().runOnMainSync(block)

    private fun parent(fontScale: Float = 1f): FrameLayout {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val config = Configuration(context.resources.configuration).apply { this.fontScale = fontScale }
        return FrameLayout(ContextThemeWrapper(context.createConfigurationContext(config), R.style.LightTheme))
    }
}
