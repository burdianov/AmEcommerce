package com.crackncrunch.amplain.di.modules;

import com.crackncrunch.amplain.di.scopes.RootScope;
import com.crackncrunch.amplain.mvp.models.AccountModel;
import com.crackncrunch.amplain.mvp.presenters.RootPresenter;

import dagger.Provides;

@dagger.Module
public class RootModule {
    @Provides
    @RootScope
    public RootPresenter provideRootPresenter() {
        return new RootPresenter();
    }

    @Provides
    @RootScope
    public AccountModel provideAccountModel() {
        return new AccountModel();
    }
}
