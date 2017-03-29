package com.crackncrunch.amplain.data.network;

import android.support.annotation.VisibleForTesting;

import com.crackncrunch.amplain.data.managers.DataManager;
import com.crackncrunch.amplain.data.network.error.ErrorUtils;
import com.crackncrunch.amplain.data.network.error.ForbiddenApiError;
import com.crackncrunch.amplain.data.network.error.NetworkAvailableError;
import com.crackncrunch.amplain.utils.ConstantsManager;
import com.crackncrunch.amplain.utils.NetworkStatusChecker;
import com.fernandocejas.frodo.annotation.RxLogObservable;

import retrofit2.Response;
import rx.Observable;

import static android.support.annotation.VisibleForTesting.NONE;

public class RestCallTransformer<R> implements Observable.Transformer<Response<R>, R> {
    private boolean mTestMode;

    @Override
    @RxLogObservable
    public Observable<R> call(Observable<Response<R>> responseObservable) {
        Observable<Boolean> networkStatus;

        if (mTestMode) {
            networkStatus = Observable.just(true);
        } else {
            networkStatus = NetworkStatusChecker.isInternetAvailable();
        }
        return networkStatus
                .flatMap(aBoolean -> aBoolean ? responseObservable : Observable.error(new NetworkAvailableError()))
                .flatMap(rResponse -> {
                    switch (rResponse.code()) {
                        case 200:
                            String lastModified = rResponse.headers().get
                                    (ConstantsManager.LAST_MODIFIED_HEADER);
                            if (lastModified != null) {
                                DataManager.getInstance().getPreferencesManager().saveLastProductUpdate(lastModified);
                            }
                            return Observable.just(rResponse.body());
                        case 304:
                            return Observable.empty();

                        case 403:
                            return Observable.error(new ForbiddenApiError());
                        default:
                            return Observable.error(ErrorUtils.parseError(rResponse));
                    }
                });
    }

    @VisibleForTesting(otherwise = NONE)
    public void setTestMode() {
        mTestMode = true;
    }
}
