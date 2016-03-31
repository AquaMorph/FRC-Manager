package com.aquamorph.frcmanager.decoration;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

import com.aquamorph.frcmanager.R;

/**
 * Divider for recyclerview.
 *
 * @author Christian Colglazier
 * @version 2/13/2016
 */
public class Divider extends RecyclerView.ItemDecoration {
	private Drawable mDivider;

	public Divider(Context context) {
		mDivider = ContextCompat.getDrawable(context, R.drawable.line_divider);
		if (mDivider != null) {
			TypedValue typedValue = new TypedValue();
			Resources.Theme theme = context.getTheme();
			theme.resolveAttribute(R.attr.textOnBackground, typedValue, true);
			int color = typedValue.data;
			ColorFilter filter = new LightingColorFilter( color, color);
			mDivider.setColorFilter(filter);
		}
	}

	@Override
	public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
		int left = parent.getPaddingLeft();
		int right = parent.getWidth() - parent.getPaddingRight();

		int childCount = parent.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View child = parent.getChildAt(i);

			RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

			int top = child.getBottom() + params.bottomMargin;
			int bottom = top + mDivider.getIntrinsicHeight();

			mDivider.setBounds(left, top, right, bottom);
			mDivider.draw(c);
		}
	}
}
