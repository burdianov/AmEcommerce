package com.crackncrunch.amplain.mvp.presenters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.crackncrunch.amplain.mvp.models.AbstractModel;
import com.crackncrunch.amplain.mvp.views.AbstractView;
import com.crackncrunch.amplain.mvp.views.IRootView;

import javax.inject.Inject;

import mortar.MortarScope;
import mortar.ViewPresenter;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Lilian on 20-Feb-17.
 */

public abstract class AbstractPresenter<V extends AbstractView,
        M extends AbstractModel> extends ViewPresenter<V> {
    private final String TAG = this.getClass().getSimpleName();

    @Inject
    protected M mModel;
    @Inject
    protected RootPresenter mRootPresenter;

    protected CompositeSubscription mCompSubs;
    protected abstract void initActionBar();
    protected abstract void initFab();
    protected abstract void initDagger(MortarScope scope);

    @Override
    protected void onEnterScope(MortarScope scope) {
        super.onEnterScope(scope);
        initDagger(scope);
    }

    @Override
    protected void onLoad(Bundle savedInstanceState) {
        super.onLoad(savedInstanceState);
        mCompSubs = new CompositeSubscription();
        initActionBar();
        initFab();
    }

    @Override
    public void dropView(V view) {
        if (mCompSubs.hasSubscriptions()) {
            mCompSubs.unsubscribe();
        }
        super.dropView(view);
    }

    @Nullable
    protected IRootView getRootView() {
        return mRootPresenter.getRootView();
    }

    protected abstract class ViewSubscriber<T> extends Subscriber<T> {
        @Override
        public void onCompleted() {
            Log.d(TAG, "onComplete observable");
        }

        @Override
        public void onError(Throwable e) {
            if (getRootView() != null) {
                getRootView().showError(e);
            }
        }

        @Override
        public abstract void onNext(T t);
    }

    protected <T> Subscription subscribe(Observable<T> observable,
                                         ViewSubscriber<T> subscriber) {
        return observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
}