package com.crackncrunch.amplain.di.modules;

import android.content.Context;
import android.util.Log;

import com.crackncrunch.amplain.data.managers.RealmManager;
import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class FlavorLocalModule {
    public static final String TAG = "BASE";

    @Provides
    @Singleton
    RealmManager provideRealmManager(Context context) {
        Log.e(TAG, "provideRealmManager init: ");
        Stetho.initialize(Stetho.newInitializerBuilder(context)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(context))
                .enableWebKitInspector(RealmInspectorModulesProvider
                        .builder(context).build()).build());
        return new RealmManager();
    }
}
