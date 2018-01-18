package com.aquamorph.frcmanager.decoration;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
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

	private final Paint mPaint;
	private final int mAlpha;
	private int indent;

	public Divider(Context context, float width, int indent) {
		TypedValue typedValue = new TypedValue();
		Resources.Theme theme = context.getTheme();
		theme.resolveAttribute(R.attr.dividerColor, typedValue, true);
		int color = typedValue.data;
		mPaint = new Paint();
		mPaint.setColor(color);
		mPaint.setStrokeWidth(width);
		mAlpha = mPaint.getAlpha();
		this.indent = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indent,
				context.getResources().getDisplayMetrics());
	}

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();

		// we retrieve the position in the list
		final int position = params.getViewAdapterPosition();

		// add space for the separator to the bottom of every view but the last one
		if (position < state.getItemCount()) {
			outRect.set(0, 0, 0, (int) mPaint.getStrokeWidth()); // left, top, right, bottom
		} else {
			outRect.setEmpty(); // 0, 0, 0, 0
		}
	}

	@Override
	public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
		// a line will draw half its size to top and bottom,
		// hence the offset to place it correctly
		final int offset = (int) (mPaint.getStrokeWidth() / 2);


		// this will iterate over every visible view
		for (int i = 0; i < parent.getChildCount(); i++) {
			final View view = parent.getChildAt(i);
			final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();

			// get the position
			final int position = params.getViewAdapterPosition();

			// and finally draw the separator
			if (position < state.getItemCount()) {
				// apply alpha to support animations
				mPaint.setAlpha((int) (view.getAlpha() * mAlpha));

				float positionY = view.getBottom() + offset + view.getTranslationY();
				// do the drawing
				c.drawLine(view.getLeft() + view.getTranslationX() + indent,
						positionY,
						view.getRight() + view.getTranslationX(),
						positionY,
						mPaint);
			}
		}
	}
}