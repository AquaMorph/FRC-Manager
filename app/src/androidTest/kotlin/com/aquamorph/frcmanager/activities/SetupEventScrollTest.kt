package com.aquamorph.frcmanager.activities

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aquamorph.frcmanager.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SetupEventScrollTest {
    @Test
    fun finalEventScrollsFullyAboveSetupFooter() {
        ActivityScenario.launch(Setup::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val pager = activity.getPager()
                pager.setCurrentItem(checkNotNull(pager.adapter).count - 1, false)
            }
            scenario.onActivity { activity ->
                activity.findViewById<RecyclerView>(R.id.eventRecyclerView).adapter = EventRows()
            }
            scenario.onActivity { activity ->
                val list = activity.findViewById<RecyclerView>(R.id.eventRecyclerView)
                assertTrue(list.canScrollVertically(1))
                list.scrollToPosition(checkNotNull(list.adapter).itemCount - 1)
            }
            scenario.onActivity { activity ->
                val list = activity.findViewById<RecyclerView>(R.id.eventRecyclerView)
                val lastPosition = checkNotNull(list.adapter).itemCount - 1
                val lastRow = checkNotNull(list.findViewHolderForAdapterPosition(lastPosition)).itemView
                val footer = activity.findViewById<View>(R.id.bottom)
                val rowBounds = Rect()
                val footerBounds = Rect()

                assertFalse(list.canScrollVertically(1))
                assertTrue(lastRow.getGlobalVisibleRect(rowBounds))
                assertTrue(footer.getGlobalVisibleRect(footerBounds))
                assertEquals(lastRow.height, rowBounds.height())
                assertTrue("The final event must be above the setup footer", rowBounds.bottom <= footerBounds.top)
            }
        }
    }

    private class EventRows : RecyclerView.Adapter<EventRows.Holder>() {
        class Holder(view: View) : RecyclerView.ViewHolder(view)

        override fun getItemCount() = 40

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false))

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.itemView.findViewById<TextView>(R.id.eventName).text = "Event ${position + 1}"
            holder.itemView.findViewById<TextView>(R.id.eventDate).text = "March 12 2026"
        }
    }
}
