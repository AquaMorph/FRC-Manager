package com.aquamorph.frcmanager.utils

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.Typeface
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aquamorph.frcmanager.R
import com.aquamorph.frcmanager.adapters.ScheduleAdapter
import kotlin.math.abs
import kotlin.math.roundToInt

internal class ScheduleFastScrollView(context: Context, attrs: AttributeSet) : RecyclerView(context, attrs) {
    private val thumbWidth = resources.getDimensionPixelSize(R.dimen.schedule_fast_scroll_width).toFloat()
    private val gutter = resources.getDimensionPixelSize(R.dimen.schedule_fast_scroll_gutter)
    private val cardMargin = resources.getDimensionPixelSize(R.dimen.schedule_card_side_margin)
    private val touchTarget = resources.getDimensionPixelSize(R.dimen.schedule_fast_scroll_target).toFloat()
    private val thumb = RectF()
    private val paint = TextPaint(TextPaint.ANTI_ALIAS_FLAG)
    private val roundPaint = labelPaint(12f, "sans-serif-medium")
    private val matchPaint = labelPaint(17f, "sans-serif-medium")
    private val textColor = themeColor(R.attr.textOnBackground)
    private val thumbColor = context.getColor(R.color.primary_dark)
    private val activeThumbColor = context.getColor(R.color.primary)
    private val accentColor = themeColor(R.attr.scheduleScrollAccent)
    private val previewColor = themeColor(R.attr.scheduleScrollPreview)
    private val outlineColor = themeColor(R.attr.scheduleRule)
    private val gap = thumbWidth
    private val stroke = resources.displayMetrics.density
    private var scrollable = false
    private var fade: ValueAnimator? = null
    private var fadeTarget = 0f
    internal var thumbOpacity = 0f
        private set
    private val visibleGutter get() = gutter * thumbOpacity
    private val hideThumb = Runnable { animateThumb(0f) }
    private var pointerId = MotionEvent.INVALID_POINTER_ID
    private val dragging get() = pointerId != MotionEvent.INVALID_POINTER_ID
    private var grabOffset = 0f
    private var previewPosition = NO_POSITION
    internal var previewLabel: ScheduleAdapter.FastScrollLabel? = null
        private set

    private fun labelPaint(size: Float, family: String) = TextPaint(TextPaint.ANTI_ALIAS_FLAG).apply {
        textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, size, resources.displayMetrics)
        typeface = Typeface.create(family, Typeface.NORMAL)
    }

    private fun themeColor(attr: Int): Int = TypedValue().let {
        context.theme.resolveAttribute(attr, it, true)
        it.data
    }

    private fun updateThumb(): Boolean {
        val extent = computeVerticalScrollExtent()
        val range = computeVerticalScrollRange()
        val height = height - paddingTop - paddingBottom
        if (extent <= 0 || range <= extent || height <= touchTarget) return false
        val thumbHeight = (height * (extent.toFloat() / range)).coerceAtLeast(touchTarget)
        val fraction = (computeVerticalScrollOffset().toFloat() / (range - extent)).coerceIn(0f, 1f)
        val top = paddingTop + (height - thumbHeight) * fraction
        val edgeSpace = visibleGutter + cardMargin * cardScale()
        val center = if (isRtl) edgeSpace / 2 else width - edgeSpace / 2
        // Keep the outline inside the available space even at the start and end of the fade.
        val halfWidth = minOf(thumbWidth / 2, (edgeSpace / 2 - stroke).coerceAtLeast(0f))
        thumb.set(center - halfWidth, top, center + halfWidth, top + thumbHeight)
        return true
    }

    private val isRtl get() = layoutDirection == LAYOUT_DIRECTION_RTL

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        val wasScrollable = scrollable
        scrollable = updateThumb()
        if (!scrollable) {
            endDrag()
            resetThumb()
        } else if (!wasScrollable && isAttachedToWindow) {
            showThumb()
        }
    }

    override fun onScrolled(dx: Int, dy: Int) {
        super.onScrolled(dx, dy)
        if (dy != 0) showThumb()
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        if (state == SCROLL_STATE_IDLE) {
            scheduleHide()
        } else if (thumbOpacity > 0f) {
            showThumb()
        }
    }

    private fun showThumb() {
        animateThumb(1f)
        scheduleHide()
    }

    private fun animateThumb(target: Float) {
        if (fadeTarget == target) return
        fadeTarget = target
        fade?.cancel()
        fade = null
        if (thumbOpacity == target || !isAttachedToWindow) {
            applyThumbOpacity(target)
            return
        }
        fade = ValueAnimator.ofFloat(thumbOpacity, target).apply {
            duration = ((if (target == 1f) 200L else 220L) * abs(target - thumbOpacity)).toLong()
            addUpdateListener { applyThumbOpacity(it.animatedValue as Float) }
            start()
        }
    }

    private fun applyThumbOpacity(opacity: Float) {
        thumbOpacity = opacity
        if (opacity == 0f) setGutter(0)
        if (opacity == 1f) setGutter(gutter)
        invalidate()
    }

    private fun scheduleHide() {
        removeCallbacks(hideThumb)
        if (isAttachedToWindow && !dragging && scrollState == SCROLL_STATE_IDLE && fadeTarget > 0f) {
            postDelayed(hideThumb, 1_500L)
        }
    }

    private fun resetThumb() {
        removeCallbacks(hideThumb)
        fade?.cancel()
        fade = null
        fadeTarget = 0f
        applyThumbOpacity(0f)
    }

    private fun setGutter(size: Int) {
        if (paddingEnd != size) setPaddingRelative(paddingStart, paddingTop, size, paddingBottom)
    }

    private fun cardScale(): Float {
        val rowWidth = getChildAt(0)?.width ?: return 1f
        return if (rowWidth > 0) ((width - visibleGutter) / rowWidth).coerceAtLeast(0f) else 1f
    }

    override fun dispatchDraw(canvas: Canvas) {
        // Animate the cards' rendered width; only commit padding at the end of each transition.
        val save = canvas.save()
        canvas.scale(cardScale(), 1f, if (isRtl) width.toFloat() else 0f, 0f)
        super.dispatchDraw(canvas)
        canvas.restoreToCount(save)
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        if (thumbOpacity <= 0f || !updateThumb()) return
        val radius = thumb.width() / 2
        paint.color = previewColor
        paint.alpha = (thumbOpacity * 220).roundToInt()
        canvas.drawRoundRect(
            thumb.left - stroke, thumb.top - stroke, thumb.right + stroke, thumb.bottom + stroke,
            radius + stroke, radius + stroke, paint
        )
        paint.color = if (dragging) activeThumbColor else thumbColor
        paint.alpha = (thumbOpacity * 255).roundToInt()
        canvas.drawRoundRect(thumb, radius, radius, paint)
        previewLabel?.let { drawPreview(canvas, it) }
    }

    private fun drawPreview(canvas: Canvas, label: ScheduleAdapter.FastScrollLabel) {
        val horizontalPadding = 1.5f * gap
        val maxTextWidth = width - touchTarget - 2 * horizontalPadding - 2 * gap
        if (maxTextWidth <= 0) return
        val round = label.round?.let { ellipsize(it, roundPaint, maxTextWidth) }
        val match = ellipsize(label.match, matchPaint, maxTextWidth)
        val roundHeight = if (round == null) 0f else roundPaint.descent() - roundPaint.ascent() + 2 * stroke
        val labelWidth = maxOf(roundPaint.measureText(round.orEmpty()), matchPaint.measureText(match)) + 2 * horizontalPadding
        val labelHeight = roundHeight + matchPaint.descent() - matchPaint.ascent() + 2 * gap
        val left = if (isRtl) touchTarget else width - touchTarget - labelWidth
        val top = (thumb.centerY() - labelHeight / 2).coerceIn(gap, (height - labelHeight - gap).coerceAtLeast(gap))
        val right = left + labelWidth
        val bottom = top + labelHeight
        val radius = 1.5f * gap
        paint.color = outlineColor
        canvas.drawRoundRect(left, top, right, bottom, radius, radius, paint)
        paint.color = previewColor
        canvas.drawRoundRect(left + stroke, top + stroke, right - stroke, bottom - stroke, radius - stroke, radius - stroke, paint)
        roundPaint.color = accentColor
        matchPaint.color = textColor
        val textTop = top + gap
        fun start(text: String, textPaint: TextPaint) =
            if (isRtl) right - horizontalPadding - textPaint.measureText(text) else left + horizontalPadding
        if (round != null) canvas.drawText(round, start(round, roundPaint), textTop - roundPaint.ascent(), roundPaint)
        canvas.drawText(match, start(match, matchPaint), textTop + roundHeight - matchPaint.ascent(), matchPaint)
    }

    private fun ellipsize(text: String, paint: TextPaint, width: Float) =
        TextUtils.ellipsize(text, paint, width, TextUtils.TruncateAt.END).toString()

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_DOWN && thumbOpacity > 0f && updateThumb()) {
            val fromEdge = if (isRtl) event.x else width - event.x
            if (fromEdge <= touchTarget && event.y in thumb.top..thumb.bottom) {
                stopScroll()
                pointerId = event.getPointerId(0)
                grabOffset = event.y - thumb.top
                parent?.requestDisallowInterceptTouchEvent(true)
                updatePreview()
                showThumb()
                return true
            }
        }
        return super.onInterceptTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!dragging) return super.onTouchEvent(event)
        when (event.actionMasked) {
            MotionEvent.ACTION_MOVE -> {
                val index = event.findPointerIndex(pointerId)
                if (index >= 0 && updateThumb()) {
                    val travel = height - paddingTop - paddingBottom - thumb.height()
                    if (travel > 0) {
                        val fraction = ((event.getY(index) - grabOffset - paddingTop) / travel).coerceIn(0f, 1f)
                        val range = computeVerticalScrollRange() - computeVerticalScrollExtent()
                        scrollBy(0, (fraction * range).roundToInt() - computeVerticalScrollOffset())
                    }
                    updatePreview()
                    invalidate()
                } else {
                    endDrag()
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> endDrag()
            MotionEvent.ACTION_POINTER_UP -> if (event.getPointerId(event.actionIndex) == pointerId) endDrag()
        }
        return true
    }

    private fun updatePreview() {
        val manager = layoutManager as? LinearLayoutManager ?: return
        val schedule = adapter as? ScheduleAdapter ?: return
        val first = manager.findFirstVisibleItemPosition()
        val last = manager.findLastVisibleItemPosition()
        if (first == NO_POSITION || last == NO_POSITION) return
        val range = computeVerticalScrollRange() - computeVerticalScrollExtent()
        val fraction = if (range > 0) (computeVerticalScrollOffset().toFloat() / range).coerceIn(0f, 1f) else 0f
        // Preview the thumb's position within the visible rows, including the final row at the bottom.
        val position = first + ((last - first) * fraction).roundToInt()
        if (position != previewPosition) {
            previewPosition = position
            previewLabel = schedule.fastScrollLabel(position)
        }
    }

    private fun endDrag() {
        if (!dragging) return
        pointerId = MotionEvent.INVALID_POINTER_ID
        previewPosition = NO_POSITION
        previewLabel = null
        scheduleHide()
        invalidate()
    }

    override fun onDetachedFromWindow() {
        endDrag()
        resetThumb()
        scrollable = false
        super.onDetachedFromWindow()
    }
}
