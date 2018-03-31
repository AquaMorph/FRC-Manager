package com.aquamorph.frcmanager.decoration

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.View
import com.aquamorph.frcmanager.R

/**
 * Divider for recyclerview.
 *
 * @author Christian Colglazier
 * @version 3/30/2018
 */
class Divider(context: Context, width: Float, indent: Int) : RecyclerView.ItemDecoration() {

    private val mPaint: Paint
    private val mAlpha: Int
    private val indent: Int

    init {
        val typedValue = TypedValue()
        val theme = context.theme
        theme.resolveAttribute(R.attr.dividerColor, typedValue, true)
        val color = typedValue.data
        mPaint = Paint()
        mPaint.color = color
        mPaint.strokeWidth = width
        mAlpha = mPaint.alpha
        this.indent = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indent.toFloat(),
                context.resources.displayMetrics).toInt()
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        val params = view.layoutParams as RecyclerView.LayoutParams

        // we retrieve the position in the list
        val position = params.viewAdapterPosition

        // add space for the separator to the bottom of every view but the last one
        if (position < state!!.itemCount) {
            outRect.set(0, 0, 0, mPaint.strokeWidth.toInt()) // left, top, right, bottom
        } else {
            outRect.setEmpty() // 0, 0, 0, 0
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
        // a line will draw half its size to top and bottom,
        // hence the offset to place it correctly
        val offset = (mPaint.strokeWidth / 2).toInt()


        // this will iterate over every visible view
        for (i in 0 until parent.childCount) {
            val view = parent.getChildAt(i)
            val params = view.layoutParams as RecyclerView.LayoutParams

            // get the position
            val position = params.viewAdapterPosition

            // and finally draw the separator
            if (position < state!!.itemCount) {
                // apply alpha to support animations
                mPaint.alpha = (view.alpha * mAlpha).toInt()

                val positionY = view.bottom.toFloat() + offset.toFloat() + view.translationY
                // do the drawing
                c.drawLine(view.left.toFloat() + view.translationX + indent.toFloat(),
                        positionY,
                        view.right + view.translationX,
                        positionY,
                        mPaint)
            }
        }
    }
}