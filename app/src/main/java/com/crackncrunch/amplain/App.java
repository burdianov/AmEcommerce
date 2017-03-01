package com.crackncrunch.amplain;

import android.app.Application;
import android.content.Context;

import com.crackncrunch.amplain.di.DaggerService;
import com.crackncrunch.amplain.di.components.AppComponent;
import com.crackncrunch.amplain.di.components.DaggerAppComponent;
import com.crackncrunch.amplain.di.modules.AppModule;
import com.crackncrunch.amplain.di.modules.PicassoCacheModule;
import com.crackncrunch.amplain.di.modules.RootModule;
import com.crackncrunch.amplain.mortar.ScreenScoper;
import com.crackncrunch.amplain.ui.activities.DaggerRootActivity_RootComponent;
import com.crackncrunch.amplain.ui.activities.RootActivity;

import io.realm.Realm;
import mortar.MortarScope;
import mortar.bundler.BundleServiceRunner;

/**
 * Created by Lilian on 21-Feb-17.
 */

public class App extends Application {

    private static AppComponent sAppComponent;
    private static Context sContext;

    private MortarScope mRootScope;
    private MortarScope mRootActivityScope;
    private static RootActivity.RootComponent mRootActivityRootComponent;

    @Override
    public Object getSystemService(String name) {
        return mRootScope.hasService(name)
                ? mRootScope.getService(name)
                : super.getSystemService(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);

        createAppComponent();
        createRootActivityComponent();

        sContext = getApplicationContext();

        mRootScope = MortarScope.buildRootScope()
                .withService(DaggerService.SERVICE_NAME, sAppComponent)
                .build("Root");

        mRootActivityScope = mRootScope.buildChild()
                .withService(DaggerService.SERVICE_NAME, mRootActivityRootComponent)
                .withService(BundleServiceRunner.SERVICE_NAME, new BundleServiceRunner())
                .build(RootActivity.class.getName());

        ScreenScoper.registerScope(mRootScope);
        ScreenScoper.registerScope(mRootActivityScope);
    }

    public static AppComponent getAppComponent() {
        return sAppComponent;
    }
    
    private void createAppComponent() {
        sAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(getApplicationContext()))
                .build();
    }

    private void createRootActivityComponent() {
       mRootActivityRootComponent = DaggerRootActivity_RootComponent.builder()
               .appComponent(sAppComponent)
               .rootModule(new RootModule())
               .picassoCacheModule(new PicassoCacheModule())
               .build();
    }

    public static RootActivity.RootComponent getmRootActivityRootComponent() {
        return mRootActivityRootComponent;
    }

    public static Context getContext() {
        return sContext;
    }
}
