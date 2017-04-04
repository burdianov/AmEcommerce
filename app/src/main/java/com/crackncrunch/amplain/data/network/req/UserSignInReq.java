package com.crackncrunch.amplain.data.network.req;

public class UserSignInReq {
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private String email;
    private String phone;

    public UserSignInReq(String firstName, String lastName, String avatarUrl, String email, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.avatarUrl = avatarUrl;
        this.email = email;
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
}
