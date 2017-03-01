package com.crackncrunch.amplain.di.modules;

import com.crackncrunch.amplain.data.managers.DataManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Lilian on 21-Feb-17.
 */

@Module
public class ModelModule extends FlavorModelModule{

    @Provides
    @Singleton
    DataManager provideDataManager() {
        return DataManager.getInstance();
    }
}
