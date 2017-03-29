package com.crackncrunch.amplain.data.storage.realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class OrdersRealm extends RealmObject {
    @PrimaryKey
    private int id;
    private String productId;
    private String productName;
    private String imageUrl;
    private String description;
    private int price;
    private float rating;
    private int count = 1;
    private boolean favorite;
    private RealmList<CommentRealm> mCommentRealmList = new RealmList<>(); //one to many
    private boolean isStatusPurchase = false;
    private int total;

    public OrdersRealm() {
    }

    public OrdersRealm(ProductRealm productRealm, int idRealm) {
        id = idRealm;
        productId = productRealm.getId();
        productName = productRealm.getProductName();
        imageUrl = productRealm.getImageUrl();
        description = productRealm.getDescription();
        price = productRealm.getPrice();
        rating = productRealm.getRating();
        count = productRealm.getCount();
        favorite = productRealm.isFavorite();
        mCommentRealmList = productRealm.getCommentsRealm();
        isStatusPurchase = false;
        total = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public RealmList<CommentRealm> getmCommentRealmList() {
        return mCommentRealmList;
    }

    public void setmCommentRealmList(RealmList<CommentRealm> mCommentRealmList) {
        this.mCommentRealmList = mCommentRealmList;
    }

    public boolean isStatusPurchase() {
        return isStatusPurchase;
    }

    public void setStatusPurchase(boolean statusPurchase) {
        isStatusPurchase = statusPurchase;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}

