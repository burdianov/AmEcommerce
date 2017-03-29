package com.crackncrunch.amplain.mvp.models;

import com.crackncrunch.amplain.data.storage.dto.ProductDto;
import com.crackncrunch.amplain.data.storage.realm.ProductRealm;
import com.fernandocejas.frodo.annotation.RxLogObservable;

import java.util.List;

import rx.Observable;

public class CatalogModel extends AbstractModel {

    public CatalogModel() {

    }

    public List<ProductDto> getProductList() {
        return mDataManager.getPreferencesManager().getProductList();
    }

    public boolean isUserAuth() {
        return mDataManager.getPreferencesManager().isUserAuth();
    }

    public Observable<ProductRealm> getProductObs() {
        Observable<ProductRealm> disk = fromDisk();
        Observable<ProductRealm> network = fromNetwork();

        return Observable.mergeDelayError(disk, network)
                .distinct(ProductRealm::getId);
    }

    @RxLogObservable
    public Observable<ProductRealm> fromNetwork() {
        return mDataManager.getProductsObsFromNetwork();
    }

    @RxLogObservable
    public Observable<ProductRealm> fromDisk() {
        return mDataManager.getProductFromRealm();
    }

    public void addOrder(ProductRealm product) {
        mDataManager.addOrderFromRealm(product);
    }

    public void addBasketCounter() {
        mDataManager.getPreferencesManager().saveBasketCounter(mDataManager.getPreferencesManager().getBasketCounter() + 1);
    }

    public void saverCartCounter(){
        mDataManager.getPreferencesManager().saveBasketCounter(mDataManager.getRealmManager().getAllOrders().size());
    }
}
