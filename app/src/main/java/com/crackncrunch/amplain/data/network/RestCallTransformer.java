package com.crackncrunch.amplain.data.network;

import com.crackncrunch.amplain.data.managers.DataManager;
import com.crackncrunch.amplain.data.network.error.ErrorUtils;
import com.crackncrunch.amplain.data.network.error.NetworkAvailableError;
import com.crackncrunch.amplain.utils.ConstantsManager;
import com.crackncrunch.amplain.utils.NetworkStatusChecker;
import com.fernandocejas.frodo.annotation.RxLogObservable;

import retrofit2.Response;
import rx.Observable;

public class RestCallTransformer<R> implements Observable
        .Transformer<Response<R>, R> {

    @Override
    @RxLogObservable
    public Observable<R> call(Observable<Response<R>> responseObservable) {
        return NetworkStatusChecker.isInternetAvailable()
                .flatMap(aBoolean -> aBoolean ? responseObservable : Observable.error(new
                        NetworkAvailableError()))
                .flatMap(rResponse -> {
                    switch (rResponse.code()) {
                        case 200:
                            String lastModified = rResponse.headers().get
                                    (ConstantsManager.LAST_MODIFIED_HEADER);
                            if (lastModified != null) {
                                DataManager.getInstance().getPreferencesManager()
                                        .saveLastProductUpdate(lastModified);
                            }
                            return Observable.just(rResponse.body()); // вернуть все данные которые есть после даты последнего обновления сущности
                        case 304:
                            return Observable.empty(); // если код ответа 304, значит данные на сервере не обновились
                        default:
                            return Observable.error(ErrorUtils.parseError
                                    (rResponse)); // если вернулась какая-либо ошибка
                    }
                });
    }
}
