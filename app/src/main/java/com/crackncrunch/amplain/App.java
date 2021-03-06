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
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.vk.sdk.VKSdk;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import mortar.MortarScope;
import mortar.bundler.BundleServiceRunner;

/**
 * Created by Lilian on 21-Feb-17.
 */

public class App extends Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "Gouxybe5p4bWmAcExmHaLWRW6";
    private static final String TWITTER_SECRET = "9up33Mc6OTYJhENR1W5u2YoxgxhFKeiVGpAFR6KeHvnyxIWQFY";

    private static AppComponent sAppComponent;
    private static Context sContext;
    private static RootActivity.RootComponent mRootComponent;

    private MortarScope mRootScope;
    private MortarScope mRootActivityScope;

    @Override
    public Object getSystemService(String name) {
        // т.к. запускаем инструментальный тест то делаем вот так (не найдет мортар скоуп иначе)
        if (mRootScope != null) {
            return mRootScope.hasService(name) ? mRootScope.getService(name)
                    : super.getSystemService(name);
        } else {
            return super.getSystemService(name);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        Realm.init(this);
        VKSdk.initialize(this);

        createAppComponent();
        createRootActivityComponent();

        sContext = getApplicationContext();

        mRootScope = MortarScope.buildRootScope()
                .withService(DaggerService.SERVICE_NAME, sAppComponent)
                .build("Root");

        mRootActivityScope = mRootScope.buildChild()
                .withService(DaggerService.SERVICE_NAME, mRootComponent)
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
       mRootComponent = DaggerRootActivity_RootComponent.builder()
               .appComponent(sAppComponent)
               .rootModule(new RootModule())
               .picassoCacheModule(new PicassoCacheModule())
               .build();
    }

    public static RootActivity.RootComponent getRootActivityRootComponent() {
        return mRootComponent;
    }

    public static Context getContext() {
        return sContext;
    }
}
