package com.crackncrunch.amplain.di.components;

import com.crackncrunch.amplain.data.managers.DataManager;
import com.crackncrunch.amplain.di.modules.LocalModule;
import com.crackncrunch.amplain.di.modules.NetworkModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Lilian on 21-Feb-17.
 */

@Component(dependencies = AppComponent.class,
        modules = {NetworkModule.class, LocalModule.class})
@Singleton
public interface DataManagerComponent {
    void inject(DataManager dataManager);
}