package com.crackncrunch.amplain.di.modules;

import android.content.Context;
import android.util.Log;

import com.crackncrunch.amplain.data.managers.RealmManager;
import com.crackncrunch.amplain.utils.ConstantsManager;
import com.facebook.stetho.Stetho;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import io.realm.SyncConfiguration;
import io.realm.SyncCredentials;
import io.realm.SyncUser;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Module
public class FlavorLocalModule {
    private static final String TAG = "REALM_MP";

    @Provides
    @Singleton
    RealmManager provideRealmManager(Context context) {
        Log.e(TAG, "provideRealmManager init: ");
        Stetho.initializeWithDefaults(context);

        Observable.create(new Observable.OnSubscribe<SyncUser>() {
            @Override
            public void call(Subscriber<? super SyncUser> subscriber) {
                SyncCredentials syncCredentials = SyncCredentials
                        .usernamePassword(
                                ConstantsManager.REALM_USER,
                                ConstantsManager.REALM_PASSWORD,
                                false);
                // create observer from sync result
                if (!subscriber.isUnsubscribed()) {
                    try {
                        subscriber.onNext(SyncUser.login(syncCredentials,
                                ConstantsManager.REALM_AUTH_URL));
                        subscriber.onCompleted();
                    } catch (Exception e) {
                        subscriber.onError(e);
                    }
                }

                // create observer from callback result
                /*if (!subscriber.isUnsubscribed()) {
                    SyncUser.loginAsync(syncCredentials, ConstantsManager
                            .REALM_AUTH_URL, new SyncUser.Callback() {
                        @Override
                        public void onSuccess(SyncUser user) {
                            subscriber.onNext(user);
                            subscriber.onCompleted();
                        }

                        @Override
                        public void onError(ObjectServerError error) {
                            subscriber.onError(error);
                        }
                    });
                }*/
            }
        })
        .subscribeOn(Schedulers.io()) // создать последовательность на io потоке
        .observeOn(AndroidSchedulers.mainThread()) // вернуть результат в main thread
        /*.onErrorResumeNext(Observable.empty())*/
                .subscribe(syncUser -> {
            SyncConfiguration syncConfig = new SyncConfiguration.Builder
                    (syncUser, ConstantsManager.REALM_DB_URL).build(); //
            // указываем пользователя и адресс realm object server
            Realm.setDefaultConfiguration(syncConfig); // устанавливаем sync конфигурацию для realm mobile platform
            Log.e(TAG, "set sync config for realm: ");
        });

        return new RealmManager();
    }
}
