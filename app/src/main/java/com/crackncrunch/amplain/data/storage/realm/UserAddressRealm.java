package com.crackncrunch.amplain.data.storage.realm;

import com.crackncrunch.amplain.data.storage.dto.UserAddressDto;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class UserAddressRealm extends RealmObject {
    @PrimaryKey
    private String id;
    private String name;
    private String street;
    private String building;
    private String apartment;
    private int floor;
    private String comment;
    private boolean favorite;

    public UserAddressRealm() {
    }

    public UserAddressRealm(UserAddressDto userAddressDto) {
        this.id = userAddressDto.getId();
        this.name = userAddressDto.getName();
        this.street = userAddressDto.getStreet();
        this.building = userAddressDto.getBuilding();
        this.apartment = userAddressDto.getApartment();
        this.floor = userAddressDto.getFloor();
        this.comment = userAddressDto.getComment();
        this.favorite = userAddressDto.isFavorite();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStreet() {
        return street;
    }

    public String getBuilding() {
        return building;
    }

    public String getApartment() {
        return apartment;
    }

    public int getFloor() {
        return floor;
    }

    public String getComment() {
        return comment;
    }

    public boolean getFavorite() {
        return favorite;
    }
}
