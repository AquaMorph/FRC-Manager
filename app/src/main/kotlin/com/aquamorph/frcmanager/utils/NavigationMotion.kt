package com.aquamorph.frcmanager.utils

import android.animation.ValueAnimator
import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.provider.Settings
import android.transition.Transition
import android.view.View
import android.view.ViewGroup
import android.view.animation.PathInterpolator
import com.aquamorph.frcmanager.R
import com.google.android.material.color.MaterialColors
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeoutOrNull

internal object NavigationMotion {
    private const val DETAIL_CONTAINER = "detail_container"
    private const val EXPAND_DETAIL = "com.aquamorph.frcmanager.EXPAND_DETAIL"

    val standard = PathInterpolator(0.4f, 0f, 0.2f, 1f)

    fun surfaceColor(context: Context): Int = MaterialColors.getColor(context, R.attr.backgroundColor, Color.WHITE)

    fun isEnabled(context: Context): Boolean = if (Build.VERSION.SDK_INT >= 26) {
        ValueAnimator.areAnimatorsEnabled()
    } else {
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) != 0f
    }

    fun openDetail(source: View, intent: Intent) {
        val context = source.context
        val activity = context.findActivity()
        val visible = Rect()
        if (activity == null || source.transitionName == null || !source.isAttachedToWindow ||
            !source.isLaidOut || !source.getLocalVisibleRect(visible) || !isEnabled(context)
        ) {
            context.startActivity(intent)
            return
        }

        activity.setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback().apply {
            setShapeProvider { view ->
                ShapeAppearanceModel.builder()
                    .setAllCornerSizes(12f * view.resources.displayMetrics.density)
                    .build()
            }
        })
        activity.window.sharedElementsUseOverlay = false
        val options = ActivityOptions.makeSceneTransitionAnimation(activity, source, DETAIL_CONTAINER)
        activity.startActivity(intent.putExtra(EXPAND_DETAIL, true), options.toBundle())
    }

    private tailrec fun Context.findActivity(): Activity? = when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }

    fun configureDetail(activity: Activity): DetailEntrance? {
        if (!activity.intent.getBooleanExtra(EXPAND_DETAIL, false)) return null
        // Name the destination's layout, not the inset frame. System bars stay outside the morph.
        val content = activity.findViewById<ViewGroup>(android.R.id.content).getChildAt(0)
        content.transitionName = DETAIL_CONTAINER
        activity.window.sharedElementsUseOverlay = false
        activity.setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        val entrance = DetailEntrance()
        activity.window.sharedElementEnterTransition = detailTransform(activity, true).addListener(entrance)
        activity.window.sharedElementReturnTransition = detailTransform(activity, false)
        return entrance
    }

    class DetailEntrance : Transition.TransitionListener {
        private val settled = CompletableDeferred<Unit>()

        // Restored activities can have the original intent but no running shared-element transition.
        suspend fun await() { withTimeoutOrNull(900L) { settled.await() } }

        override fun onTransitionStart(transition: Transition) = Unit
        override fun onTransitionEnd(transition: Transition) { settled.complete(Unit) }
        override fun onTransitionCancel(transition: Transition) { settled.complete(Unit) }
        override fun onTransitionPause(transition: Transition) = Unit
        override fun onTransitionResume(transition: Transition) = Unit
    }

    private fun detailTransform(activity: Activity, opening: Boolean) = MaterialContainerTransform().apply {
        addTarget(DETAIL_CONTAINER)
        transitionDirection = if (opening) {
            MaterialContainerTransform.TRANSITION_DIRECTION_ENTER
        } else {
            MaterialContainerTransform.TRANSITION_DIRECTION_RETURN
        }
        duration = if (opening) 360L else 300L
        interpolator = standard
        // Keep one surface fully visible throughout; avoid a blank midpoint and a per-frame shadow.
        fadeMode = if (opening) MaterialContainerTransform.FADE_MODE_IN else MaterialContainerTransform.FADE_MODE_OUT
        isElevationShadowEnabled = false
        containerColor = surfaceColor(activity)
    }

    fun reveal(view: View) {
        view.animate().cancel()
        val animate = view.isAttachedToWindow && isEnabled(view.context)
        view.alpha = if (animate) 0f else 1f
        view.visibility = View.VISIBLE
        if (animate) view.animate().alpha(1f).setDuration(180L).setInterpolator(standard).start()
    }
}
