package com.aquamorph.frcmanager.adapters

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.models.tba.Award
import com.aquamorph.frcmanager.models.tba.AwardRecipient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.roundToInt

@RunWith(AndroidJUnit4::class)
class AwardAdapterTest {
    @Test
    fun oledAwardCardsUsePureBlackFills() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            val context = ContextThemeWrapper(instrumentation.targetContext, R.style.OMOLEDTheme)
            val adapter = AwardAdapter(listOf(award(1), award(10)), emptyMap())
            val holder = adapter.onCreateViewHolder(FrameLayout(context), 0)

            for (position in 0 until adapter.itemCount) {
                adapter.onBindViewHolder(holder, position)
                val background = holder.itemView.background as GradientDrawable
                assertEquals(Color.BLACK, background.color?.defaultColor)
            }
        }
    }

    @Test
    fun teamNamesStartInTheSameColumnAcrossAwards() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            val context = ContextThemeWrapper(instrumentation.targetContext, R.style.LightTheme)
            val parent = FrameLayout(context)
            val adapter = AwardAdapter(
                listOf(
                    award(1, AwardRecipient("frc1323", null), AwardRecipient("frc254", null)),
                    award(10, AwardRecipient("frc11296", null))
                ),
                mapOf("frc1323" to "MadTown Robotics", "frc254" to "The Cheesy Poofs", "frc11296" to "Merced Fusion Robotics")
            )
            val width = (320 * context.resources.displayMetrics.density).roundToInt()
            val nameColumns = (0 until adapter.itemCount).flatMap { position ->
                val holder = adapter.onCreateViewHolder(parent, adapter.getItemViewType(position))
                adapter.onBindViewHolder(holder, position)
                val card = holder.itemView
                card.measure(
                    View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )
                card.layout(0, 0, width, card.measuredHeight)
                val recipients = card.findViewById<LinearLayout>(R.id.award_recipients)
                (0 until recipients.childCount).map { index ->
                    val row = recipients.getChildAt(index)
                    val teamLabel = team(row)
                    val nameLabel = teamName(row)
                    assertNull(teamLabel.background)
                    assertTrue(nameLabel.width > 0)
                    assertTrue((nameLabel.parent as View).left > teamLabel.right)
                    recipients.left + row.left + (nameLabel.parent as View).left + nameLabel.left
                }
            }
            assertEquals(3, nameColumns.size)
            assertEquals(1, nameColumns.distinct().size)
        }
    }

    @Test
    fun recycledCardsKeepTeamNamesPairedAndDescribeBannerArtwork() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            val awards = mutableListOf(
                award(1, AwardRecipient("frc254", "Alex Chen"), AwardRecipient("frc1678", null), AwardRecipient(null, null)),
                award(2, AwardRecipient(null, "Sam Rivera"))
            )
            for (theme in listOf(R.style.LightTheme, R.style.DarkTheme, R.style.OMOLEDTheme)) {
                val context = ContextThemeWrapper(instrumentation.targetContext, theme)
                val parent = FrameLayout(context)
                val teamNames = mutableMapOf("frc254" to "The Cheesy Poofs")
                val adapter = AwardAdapter(awards, teamNames)
                assertEquals(2, adapter.itemCount)

                val card = adapter.onCreateViewHolder(parent, adapter.getItemViewType(0))
                val art = card.itemView.findViewById<View>(R.id.award_art)
                val recipients = card.itemView.findViewById<LinearLayout>(R.id.award_recipients)
                adapter.onBindViewHolder(card, 0)
                assertEquals(context.getString(R.string.award_blue_banner), art.contentDescription)
                assertEquals(2, recipients.childCount)
                val first = recipients.getChildAt(0)
                assertEquals(context.getString(R.string.award_team_number, "254"), team(first).text.toString())
                assertEquals("The Cheesy Poofs", teamName(first).text.toString())
                assertEquals("Alex Chen", name(first).text.toString())
                assertEquals(View.GONE, name(recipients.getChildAt(1)).visibility)
                assertEquals(View.GONE, teamName(recipients.getChildAt(1)).visibility)

                adapter.onBindViewHolder(card, 1)
                assertEquals(null, art.contentDescription)
                assertEquals(1, recipients.childCount)
                val rebound = recipients.getChildAt(0)
                assertEquals(View.GONE, team(rebound).visibility)
                assertEquals(View.GONE, teamName(rebound).visibility)
                assertEquals("Sam Rivera", name(rebound).text.toString())

                teamNames["frc1678"] = "Citrus Circuits"
                adapter.onBindViewHolder(card, 0)
                assertEquals("Citrus Circuits", teamName(recipients.getChildAt(1)).text.toString())
            }
            assertEquals(0, AwardAdapter(emptyList(), emptyMap()).itemCount)
        }
    }

    private fun award(type: Int, vararg recipients: AwardRecipient) =
        Award("Event Award", type, "event", arrayListOf(*recipients), "2026")

    private fun team(row: View): TextView = row.findViewById(R.id.award_recipient_team)

    private fun teamName(row: View): TextView = row.findViewById(R.id.award_recipient_team_name)

    private fun name(row: View): TextView = row.findViewById(R.id.award_recipient_name)
}
