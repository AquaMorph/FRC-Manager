package com.aquamorph.frcmanager.adapters

import android.view.ContextThemeWrapper
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.aquamorph.frcmanager.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.roundToInt

@RunWith(AndroidJUnit4::class)
class AllianceAdapterTest {
    @Test
    fun fourPicksFitAndMissingPicksCollapseWhenCardIsReused() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            for (theme in listOf(R.style.LightTheme, R.style.DarkTheme, R.style.OMOLEDTheme)) {
                val context = ContextThemeWrapper(instrumentation.targetContext, theme)
                val adapter = AllianceAdapter(context, arrayListOf())
                val holder = adapter.onCreateViewHolder(FrameLayout(context), 0)
                val card = holder.itemView
                val tiles = listOf(R.id.team_1, R.id.team_2, R.id.team_3, R.id.team_4)
                    .map { card.findViewById<View>(it) }
                val fourth = tiles.last()
                val captain = tiles.first()
                val width = (296 * context.resources.displayMetrics.density).roundToInt()

                fun measureCard() {
                    card.measure(
                        View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                    )
                    card.layout(0, 0, width, card.measuredHeight)
                }

                holder.bind(1, listOf("frc12345", "frc254", "frc1678", "frc99999"))
                measureCard()
                val fourPickWidth = captain.width
                assertEquals("Captain, team 12345", captain.contentDescription)
                assertEquals("Pick 3, team 99999", fourth.contentDescription)
                for (tile in tiles) {
                    assertEquals(View.VISIBLE, tile.visibility)
                    assertTrue(tile.height >= 48 * context.resources.displayMetrics.density)
                    val number = tile.findViewById<TextView>(R.id.alliance_team_number)
                    assertEquals(number.text.length, number.layout.getLineEnd(0))
                    assertTrue(number.layout.getLineWidth(0) <= number.width)
                }

                holder.bind(2, listOf("frc8", "frc2056", "frc1114"))
                measureCard()
                assertEquals("Alliance 2", card.findViewById<TextView>(R.id.alliance_number).text.toString())
                assertEquals(View.GONE, fourth.visibility)
                assertNull(fourth.contentDescription)
                assertEquals("", fourth.findViewById<TextView>(R.id.alliance_team_number).text.toString())
                assertTrue(captain.width > fourPickWidth)

                holder.bind(3, listOf("frc12345", "frc254", "frc1678", "frc99999"))
                assertEquals(View.VISIBLE, fourth.visibility)
                assertEquals("Pick 3, team 99999", fourth.contentDescription)
            }
        }
    }
}
