package com.crackncrunch.amplain.di.components;

import android.content.Context;

import com.crackncrunch.amplain.di.modules.AppModule;

import dagger.Component;

/**
 * Created by Lilian on 21-Feb-17.
 */

@Component(modules = AppModule.class)
public interface AppComponent {
    Context getContext();
}
