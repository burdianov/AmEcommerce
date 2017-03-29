package com.crackncrunch.amplain.data.storage.dto;

import java.util.ArrayList;

public class UserDto {
    private int id;
    private String fullName;
    private String avatar;
    private String phone;
    private boolean orderNotification;
    private boolean promoNotification;
    private ArrayList<UserAddressDto> userAddresses;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isOrderNotification() {
        return orderNotification;
    }

    public void setOrderNotification(boolean orderNotification) {
        this.orderNotification = orderNotification;
    }

    public boolean isPromoNotification() {
        return promoNotification;
    }

    public void setPromoNotification(boolean promoNotification) {
        this.promoNotification = promoNotification;
    }

    public ArrayList<UserAddressDto> getUserAddresses() {
        return userAddresses;
    }

    public void setUserAddresses(ArrayList<UserAddressDto> userAddresses) {
        this.userAddresses = userAddresses;
    }
}
