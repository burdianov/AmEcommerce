package com.crackncrunch.amplain.ui.screens.account;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.view.View;

import com.crackncrunch.amplain.R;

public abstract class ItemSwipeCallback extends ItemTouchHelper.SimpleCallback {
    private static final String TAG = "ItemSwipeCallback";

    private float mDensity;
    private float mScaleDensity;
    private final Drawable mRightIcon;
    private final Drawable mLeftIcon;
    private final int mRightColor;
    private final int mLeftColor;
    private final String mRightText;
    private final String mLeftText;

    //Settings
    private final int RIGHT_ICON = R.drawable.ic_delete_white_24dp; //use only square vector drawables
    private final int LEFT_ICON = R.drawable.ic_edit_white_24dp; //use only square vector drawables
    private final int RIGHT_COLOR = R.color.color_swipe_delete;
    private final int LEFT_COLOR = R.color.color_swipe_edit;
    private final int RIGHT_TEXT = R.string.delete;
    private final int LEFT_TEXT = R.string.edit;
    private final float TEXT_SIZE_SP = 15f;
    private final float ICON_SIZE_DP = 24f;
    private final float HORIZONTAL_MARGIN_ICON_DP = 16f;
    private final float HORIZONTAL_MARGIN_TEXT_DP = 48f;
    private final float VERTICAL_OFFSET_TEXT_DP = 1f;

    public ItemSwipeCallback(Context context, int dragDirs, int swipeDirs) {
        super(dragDirs, swipeDirs);

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        mDensity = metrics.density;
        mScaleDensity = metrics.scaledDensity;

        mRightIcon = AppCompatDrawableManager.get().getDrawable(context, RIGHT_ICON);
        mLeftIcon = AppCompatDrawableManager.get().getDrawable(context, LEFT_ICON);
        mRightColor = ContextCompat.getColor(context, RIGHT_COLOR);
        mLeftColor = ContextCompat.getColor(context, LEFT_COLOR);
        mRightText = context.getString(RIGHT_TEXT).toUpperCase();
        mLeftText = context.getString(LEFT_TEXT).toUpperCase();
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public abstract void onSwiped(RecyclerView.ViewHolder viewHolder, int direction);

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

            float horizontalTextMarginPx = dpToPx(HORIZONTAL_MARGIN_TEXT_DP);
            float offsetY = dpToPx(VERTICAL_OFFSET_TEXT_DP);
            float textSizePx = spToPx(TEXT_SIZE_SP);
            Paint textPaint = new Paint();
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(textSizePx);
            textPaint.setAntiAlias(true);

            View itemView = viewHolder.itemView;
            float itemHeightPx = (float) itemView.getBottom() - (float) itemView.getTop();
            float horizontalIconMarginPx = dpToPx(HORIZONTAL_MARGIN_ICON_DP);
            float iconSizePx = dpToPx(ICON_SIZE_DP);

            if (dX > 0) {
                //right swipe

                //drawing background
                float leftBound = (float) itemView.getLeft();
                float topBound = (float) itemView.getTop();
                float rightBound = dX;
                float bottomBound = (float) itemView.getBottom();
                RectF clipShape = new RectF(leftBound, topBound, rightBound, bottomBound);
                c.clipRect(clipShape);
                c.drawColor(mLeftColor);

                //drawing text
                textPaint.setTextAlign(Paint.Align.LEFT);
                int textX = (int) (itemView.getLeft() + horizontalTextMarginPx);
                int textY = (int) (itemView.getTop() + itemHeightPx / 2 + textSizePx / 2 - offsetY);
                c.drawText(mLeftText, textX, textY, textPaint);

                //drawing icon
                int iconLeft = (int) (itemView.getLeft() + horizontalIconMarginPx);
                int iconTop = (int) (itemView.getTop() + itemHeightPx / 2 - iconSizePx / 2);
                int iconRight = (int) (itemView.getLeft() + horizontalIconMarginPx + iconSizePx);
                int iconBottom = (int) (itemView.getBottom() - itemHeightPx / 2 + iconSizePx / 2);
                mLeftIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                mLeftIcon.draw(c);

            } else {
                //left swipe

                //drawing background
                float leftBound = (float) itemView.getRight() + dX;
                float topBound = (float) itemView.getTop();
                float rightBound = (float) itemView.getRight();
                float bottomBound = (float) itemView.getBottom();
                RectF clipShape = new RectF(leftBound, topBound, rightBound, bottomBound);
                c.clipRect(clipShape);
                c.drawColor(mRightColor);

                //drawing text
                textPaint.setTextAlign(Paint.Align.RIGHT);
                int textX = (int) (itemView.getRight() - horizontalTextMarginPx);
                int textY = (int) (itemView.getTop() + itemHeightPx / 2 + textSizePx / 2 - offsetY);
                c.drawText(mRightText, textX, textY, textPaint);

                //drawing icon
                int iconLeft = (int) (itemView.getRight() - horizontalIconMarginPx - iconSizePx);
                int iconTop = (int) (itemView.getTop() + itemHeightPx / 2 - iconSizePx / 2);
                int iconRight = (int) (itemView.getRight() - horizontalIconMarginPx);
                int iconBottom = (int) (itemView.getBottom() - itemHeightPx / 2 + iconSizePx / 2);
                mRightIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                mRightIcon.draw(c);
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private float dpToPx(float dp) {
        return dp * mDensity;
    }

    private float spToPx(float sp) {
        return sp * mScaleDensity;
    }
}