package com.crackncrunch.amplain.data.managers;

import com.crackncrunch.amplain.data.network.res.CommentRes;
import com.crackncrunch.amplain.data.network.res.ProductRes;
import com.crackncrunch.amplain.data.storage.realm.CommentRealm;
import com.crackncrunch.amplain.data.storage.realm.OrdersRealm;
import com.crackncrunch.amplain.data.storage.realm.ProductRealm;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import rx.Observable;

/**
 * Created by Lilian on 25-Feb-17.
 */

public class RealmManager {

    private Realm mRealmInstance;
    private int mOrderId;

    public void saveProductResponseToRealm(ProductRes productRes) {
        Realm realm = Realm.getDefaultInstance();
        ProductRealm productRealm = new ProductRealm(productRes);

        if (!productRes.getComments().isEmpty()) {
            Observable.from(productRes.getComments())
                    .doOnNext(commentRes -> {
                        if (!commentRes.isActive()) {
                            deleteFromRealm(CommentRealm.class, commentRes
                                    .getId());
                        }
                    })
                    .filter(CommentRes::isActive)
                    .map(CommentRealm::new) // преобразовываем в RealmObject
                    .subscribe(commentRealm -> productRealm.getCommentsRealm()
                            .add(commentRealm)); // добавляем в ProductRealm
        }
        // добавляет или обновляем продукт в транзакции
        realm.executeTransaction(realm1 -> realm1.insertOrUpdate(productRealm));
        realm.close();
    }

    public void deleteFromRealm(Class<? extends RealmObject> entityRealmClass,
                                String id) {
        Realm realm = Realm.getDefaultInstance();
        RealmObject entity = realm
                .where(entityRealmClass).equalTo("id", id).findFirst();
        if (entity != null) {
            realm.executeTransaction(realm1 -> entity.deleteFromRealm());
            realm.close();
        }
    }

    public Observable<ProductRealm> getAllProductsFromRealm() {
        RealmResults<ProductRealm> managedProduct = getQueryRealmInstance()
                .where(ProductRealm.class).findAllAsync();
        return managedProduct
                .asObservable() // получаем RealmResult как Observable
                .filter(RealmResults::isLoaded) // получаем только загруженные результаты (hot Observable)
                //.first() // convert a hot observable into a cold one
                .flatMap(Observable::from); // преобразуем в Observable<ProductRealm>
    }

    public RealmResults<ProductRealm> getAllFavoriteProducts() {
        RealmResults<ProductRealm> likeQuotes = getQueryRealmInstance().where(ProductRealm.class).equalTo("favorite", true).findAll();
        return likeQuotes;
    }

    public void addOrder(ProductRealm productRealm) {
        Realm realm = Realm.getDefaultInstance();

        OrdersRealm order = new OrdersRealm(productRealm, getOrderId());

        realm.executeTransaction(realm1 -> realm1.insertOrUpdate(order)); //добавляем или обновляем продукт в корзине

        realm.close();
    }

    public RealmResults<OrdersRealm> getAllOrders() {
        RealmResults<OrdersRealm> orders = getQueryRealmInstance().where(OrdersRealm.class).findAllSorted("id");
        return orders;
    }

    public int getOrderId() {
        Realm realm = Realm.getDefaultInstance();
        try {
            mOrderId = realm.where(OrdersRealm.class).max("id").intValue() + 1;
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            mOrderId = 0;
        }
        realm.close();
        return mOrderId;
    }

    public RealmResults<ProductRealm> getProductInCart() {
        return  getQueryRealmInstance().where(ProductRealm.class)
                .greaterThan("count", 0).findAll();
    }

    private Realm getQueryRealmInstance() {
        if (mRealmInstance == null || mRealmInstance.isClosed()) {
            mRealmInstance = Realm.getDefaultInstance();
        }
        return mRealmInstance;
    }
}