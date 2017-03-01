package com.crackncrunch.amplain.data.storage.dto;

import com.crackncrunch.amplain.data.storage.realm.ProductRealm;

public class DescriptionDto {
    private String description;
    private float rating;
    private int count;
    private int price;
    private boolean favorite;

    public DescriptionDto(ProductRealm product) {
        this.description = product.getDescription();
        this.rating = product.getRating();
        this.count = product.getCount();
        this.price = product.getPrice();
        this.favorite = product.isFavorite();
    }

    public String getDescription() {
        return description;
    }

    public float getRating() {
        return rating;
    }

    public int getCount() {
        return count;
    }

    public int getPrice() {
        return price;
    }

    public boolean isFavorite() {
        return favorite;
    }
}
