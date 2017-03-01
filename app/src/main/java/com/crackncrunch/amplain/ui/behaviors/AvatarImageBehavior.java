package com.crackncrunch.amplain.ui.behaviors;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("unused")
public class AvatarImageBehavior extends CoordinatorLayout
        .Behavior<CircleImageView> {

    private int mMaxScrollDistance;
    private int mMaxHeight;

    public AvatarImageBehavior(Context context, AttributeSet attrs) {
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, CircleImageView child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, CircleImageView child, View dependency) {
        if (mMaxScrollDistance == 0) mMaxScrollDistance = dependency.getBottom();
        if (mMaxHeight == 0) mMaxHeight = child.getHeight();

        int newSize = (int) ((float) dependency.getBottom() / mMaxScrollDistance * mMaxHeight);

        child.setY(dependency.getBottom() - (float) child.getHeight() / 2);

        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        lp.width = newSize;
        lp.height = newSize;
        child.setLayoutParams(lp);

        return true;
    }
}
