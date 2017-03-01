package com.crackncrunch.amplain.di.modules;

import android.content.Context;

import com.crackncrunch.amplain.data.managers.PreferencesManager;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Lilian on 21-Feb-17.
 */

@Module
public class LocalModule extends FlavorLocalModule {

    @Provides
    PreferencesManager providePreferencesManager(Context context) {
        return new PreferencesManager(context);
    }
}
