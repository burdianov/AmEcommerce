package com.crackncrunch.amplain.data.storage.dto;

import com.crackncrunch.amplain.data.storage.realm.UserAddressRealm;

public class UserAddressDto{
    private int id;
    private String name;
    private String street;
    private String building;
    private String apartment;
    private int floor;
    private String comment;
    private boolean favorite;

    public UserAddressDto() {

    }

    public UserAddressDto(int id, String name, String street,
                          String building, String apartment, int floor,
                          String comment) {
        this.id = id;
        this.name = name;
        this.street = street;
        this.building = building;
        this.apartment = apartment;
        this.floor = floor;
        this.comment = comment;
    }

    public UserAddressDto(UserAddressRealm addressRealm) {
        this.id = addressRealm.getId();
        this.name = addressRealm.getName();
        this.street = addressRealm.getStreet();
        this.building = addressRealm.getBuilding();
        this.apartment = addressRealm.getApartment();
        this.floor = addressRealm.getFloor();
        this.comment = addressRealm.getComment();
        this.favorite = addressRealm.getFavorite();
    }

    @Override
    public boolean equals(Object obj) {
        return this.hashCode() == obj.hashCode();
    }

    public void update(UserAddressDto address) {
        this.name = address.getName();
        this.street = address.getStreet();
        this.building = address.getBuilding();
        this.apartment = address.getApartment();
        this.floor = address.getFloor();
        this.comment = address.getComment();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getApartment() {
        return apartment;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}