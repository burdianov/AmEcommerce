package com.crackncrunch.amplain.data.storage.dto;

import android.os.Parcel;
import android.os.Parcelable;

public class UserAddressDto implements Parcelable {
    private String id;
    private String name;
    private String street;
    private String building;
    private String apartment;
    private int floor;
    private String comment;
    private boolean favorite;

    public UserAddressDto() {

    }

    public UserAddressDto(String id, String name, String street, String building,
                          String
                                  apartment, int floor, String comment) {
        this.id = id;
        this.name = name;
        this.street = street;
        this.building = building;
        this.apartment = apartment;
        this.floor = floor;
        this.comment = comment;
    }

    protected UserAddressDto(Parcel in) {
        id = in.readString();
        name = in.readString();
        street = in.readString();
        building = in.readString();
        apartment = in.readString();
        floor = in.readInt();
        comment = in.readString();
        favorite = in.readByte() != 0;
    }

    public static final Creator<UserAddressDto> CREATOR = new Creator<UserAddressDto>() {
        @Override
        public UserAddressDto createFromParcel(Parcel in) {
            return new UserAddressDto(in);
        }

        @Override
        public UserAddressDto[] newArray(int size) {
            return new UserAddressDto[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(street);
        dest.writeString(building);
        dest.writeString(apartment);
        dest.writeInt(floor);
        dest.writeString(comment);
        dest.writeByte((byte) (favorite ? 1 : 0));
    }
}