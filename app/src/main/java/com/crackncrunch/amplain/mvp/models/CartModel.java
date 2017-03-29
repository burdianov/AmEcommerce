package com.crackncrunch.amplain.mvp.models;

import com.crackncrunch.amplain.data.storage.realm.OrdersRealm;

import io.realm.RealmResults;

public class CartModel extends AbstractModel {
    public RealmResults<OrdersRealm> getAllOrders() {
        return mDataManager.getAllOrders();
    }

    public void saverCartCounter(){
        mDataManager.getPreferencesManager().saveBasketCounter(
                mDataManager.getRealmManager().getAllOrders().size());
    }
}
