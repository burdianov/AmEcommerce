package com.crackncrunch.amplain.di.modules;

import com.crackncrunch.amplain.di.scopes.RootScope;
import com.crackncrunch.amplain.mvp.models.AccountModel;
import com.crackncrunch.amplain.mvp.presenters.RootPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Lilian on 22-Feb-17.
 */
@Module
public class RootModule {
    @Provides
    @RootScope
    RootPresenter provideRootPresenter() {
        return new RootPresenter();
    }

    @Provides
    @RootScope
    AccountModel provideAccountModel() {
        return new AccountModel();
    }
}
