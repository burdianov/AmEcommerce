package com.crackncrunch.amplain.flow;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.crackncrunch.amplain.R;
import com.crackncrunch.amplain.mortar.ScreenScoper;
import com.crackncrunch.amplain.utils.ViewHelper;

import java.util.Collections;
import java.util.Map;

import flow.Direction;
import flow.Dispatcher;
import flow.KeyChanger;
import flow.State;
import flow.Traversal;
import flow.TraversalCallback;
import flow.TreeKey;

public class TreeKeyDispatcher implements KeyChanger, Dispatcher {

    private Activity mActivity;
    private Object inKey;
    @Nullable
    private Object outKey;
    private FrameLayout mRootFrame;

    public TreeKeyDispatcher(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void dispatch(@NonNull Traversal traversal,
                         @NonNull TraversalCallback callback) {
        Map<Object, Context> contexts;
        State inState = traversal.getState(traversal.destination.top());
        inKey = inState.getKey();
        State outState = traversal.origin == null ? null : traversal.getState
                (traversal.origin.top());
        outKey = outState == null ? null : outState.getKey();

        mRootFrame = (FrameLayout) mActivity.findViewById(R.id.root_frame);

        if (inKey.equals(outKey)) {
            callback.onTraversalCompleted();
            return;
        }

        if (inKey instanceof TreeKey) {
            // TODO: 25-Nov-16 implement treekey case for those who will rewriter ScreenScoper
        }

        Context flowContext = traversal.createContext(inKey, mActivity);
        Context mortarContext = ScreenScoper.getScreenScope((AbstractScreen)
                inKey).createContext(flowContext);
        contexts = Collections.singletonMap(inKey, mortarContext);
        changeKey(outState, inState, traversal.direction, contexts, callback);
    }

    @Override
    public void changeKey(@Nullable State outgoingState,
                          @NonNull State incomingState,
                          @NonNull Direction direction,
                          @NonNull Map<Object, Context> incomingContexts,
                          @NonNull TraversalCallback callback) {

        Context context = incomingContexts.get(inKey);

        //save prev View
        if (outgoingState != null) {
            outgoingState.save(mRootFrame.getChildAt(0));
        }

        //create new view
        Screen screen;
        screen = inKey.getClass().getAnnotation(Screen.class);
        if (screen == null) {
            throw new IllegalStateException("@Screen annotation is missing on " +
                    "screen " + ((AbstractScreen) inKey).getScopeName());
        } else {
            int layout = screen.value();

            LayoutInflater inflater = LayoutInflater.from(context);
            View newView = inflater.inflate(layout, mRootFrame, false);
            View oldView = mRootFrame.getChildAt(0);

            //restore state to new view
            incomingState.restore(newView);

            //delete old view
            if ((outKey) != null && !(inKey instanceof TreeKey)) {
                ((AbstractScreen) outKey).unregisterScope();
            }

            /*if (mRootFrame.getChildAt(0) != null) {
                mRootFrame.removeView(mRootFrame.getChildAt(0));
            }*/

            mRootFrame.addView(newView);

            ViewHelper.waitForMeasure(newView, new ViewHelper
                    .OnMeasureCallback() { // дожидаемся когда станут известны размеры View которая придет в контейнер
                @Override
                public void onMeasure(View view, int width, int height) {
                    runAnimation(mRootFrame, oldView, newView, direction, new
                            TraversalCallback() { // запускаем анимацию
                                @Override
                                public void onTraversalCompleted() {
                                    // анимация окончена, делаем что-то, что небходимо, например удаляем область видимости Mortar
                                    if (outKey != null && !(inKey instanceof
                                            TreeKey)) {
                                        ((AbstractScreen)outKey).unregisterScope();
                                    }
                                    callback.onTraversalCompleted();
                                }
                            });
                }
            });
        }
    }

    private void runAnimation(FrameLayout container, View from,
                              View to, Direction direction,
                              TraversalCallback callback) {
        Animator animator = createAnimation(from, to, direction); // создаем анимацию
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (from != null) {
                    container.removeView(from); // удаляем вью из контейнера по окончанию анимации
                }
                callback.onTraversalCompleted(); // вызываем колбек успешного
                // окончания перехода, в котором выше происходит очистка области видимости
            }
        });
        animator.setInterpolator(new FastOutLinearInInterpolator()); // устанавливаем временную функцию анимации перехода
        animator.start(); // запускаем анимацию
    }

    @NonNull
    private Animator createAnimation(@Nullable View from, View to, Direction
            direction) {
        boolean backward = direction == Direction.BACKWARD;

        AnimatorSet set = new AnimatorSet();

        int fromTranslation;
        if (from != null) {
            fromTranslation = backward ? from.getWidth() : -from.getWidth();
            // если движемся по истории назад, то смещение по (с лева на право на ширину View) установленной в контейнере, если вперед, то смещение отрицательное (справа - на лево на ширину View)
            final ObjectAnimator outAnimation = ObjectAnimator.ofFloat(from,
                    "translationX", fromTranslation); // анимируем смещение по X
            set.play(outAnimation); // добавляет в сет аниматора (если from != null)
        }

        int toTranslation = backward ? -to.getWidth() : to.getWidth(); // аналогично анимируем новую (приходящую) View
        final ObjectAnimator toAnimation = ObjectAnimator.ofFloat(to,
                "translationX", toTranslation, 0); // смещаем (с позиции ширины View) на лево до нуля
        set.play(toAnimation); // добавляет в сет аниматора

        return set;
    }
}

