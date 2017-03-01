package com.crackncrunch.amplain.mvp.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.crackncrunch.amplain.mvp.presenters.AbstractPresenter;

import javax.inject.Inject;

import butterknife.ButterKnife;

public abstract class AbstractView<P extends AbstractPresenter> extends FrameLayout
        implements IView {
    @Inject
    protected P mPresenter;

    public AbstractView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            initDagger(context);
        }
    }

    protected abstract void initDagger(Context context);

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isInEditMode()) {
            mPresenter.takeView(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (!isInEditMode()) {
            mPresenter.dropView(this);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }
}
