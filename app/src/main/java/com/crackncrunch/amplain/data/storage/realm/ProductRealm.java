package com.crackncrunch.amplain.data.storage.realm;

import com.crackncrunch.amplain.data.network.res.ProductRes;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ProductRealm extends RealmObject {
    @PrimaryKey
    private String id;
    private String productName;
    private String imageUrl;
    private String description;
    private int price;
    private float rating;
    private int count = 1;
    private boolean favorite;
    private RealmList<CommentRealm> mCommentsRealm = new RealmList<>(); //one-to-many

    public ProductRealm() {
    }

    public ProductRealm(ProductRes productRes) {
        id = productRes.getId();
        productName = productRes.getProductName();
        imageUrl = productRes.getImageUrl();
        description = productRes.getDescription();
        price = productRes.getPrice();
        rating = productRes.getRating();
    }

    public String getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public float getRating() {
        return rating;
    }

    public int getCount() {
        return count;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public RealmList<CommentRealm> getCommentsRealm() {
        return mCommentsRealm;
    }

    public void add() {
        count++;
    }

    public void remove() {
        count--;
    }

    public void toggleFavorite() {
        setFavorite(!this.favorite);
    }

    private void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
