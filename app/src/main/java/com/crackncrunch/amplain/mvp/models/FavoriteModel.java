package com.crackncrunch.amplain.mvp.models;

import com.crackncrunch.amplain.data.storage.realm.ProductRealm;

import io.realm.RealmResults;

/**
 * Created by Lilian on 23-Mar-17.
 */

public class FavoriteModel extends AbstractModel {
    public RealmResults<ProductRealm> getAllFavorites() {
        return mDataManager.getAllFavoriteProducts();
    }
}
