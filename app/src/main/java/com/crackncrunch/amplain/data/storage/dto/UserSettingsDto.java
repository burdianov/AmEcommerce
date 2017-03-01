package com.crackncrunch.amplain.data.storage.dto;

public class UserSettingsDto {
    private boolean orderNotification;
    private boolean promoNotification;

    public UserSettingsDto(boolean orderNotification, boolean promoNotification) {
        this.orderNotification = orderNotification;
        this.promoNotification = promoNotification;
    }

    public boolean isOrderNotification() {
        return orderNotification;
    }

    public boolean isPromoNotification() {
        return promoNotification;
    }
}
