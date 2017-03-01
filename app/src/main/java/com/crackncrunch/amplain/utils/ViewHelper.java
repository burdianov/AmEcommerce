package com.crackncrunch.amplain.utils;

import android.content.Context;
import android.support.annotation.IdRes;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import java.util.ArrayList;

/**
 * Created by Lilian on 13-Feb-17.
 */
public class ViewHelper {
    public static float getDensity(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.density;
    }

    public static ArrayList<View> getChildrenExcludeView(
            ViewGroup container, @IdRes int... excludedChild) {
        ArrayList<View> children = new ArrayList<>();

        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            for (int exclude : excludedChild) {
                if (child.getId() != exclude) {
                    children.add(child);
                }
            }
        }
        return children;
    }

    public static void waitForMeasure(View view, OnMeasureCallback callback) {
        int width = view.getWidth();
        int height = view.getHeight();

        if (width > 0 && height > 0) {
            callback.onMeasure(view, width, height);
            return;
        }

        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                final ViewTreeObserver observer = view.getViewTreeObserver();
                if (observer.isAlive()) {
                    observer.removeOnPreDrawListener(this);
                }
                callback.onMeasure(view, view.getWidth(), view.getHeight());

                return true;
            }
        });
    }

    public interface OnMeasureCallback {
        void onMeasure(View view, int width, int height);
    }
}
