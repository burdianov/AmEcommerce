package com.crackncrunch.amplain.data.network.req;

/**
 * Created by Lilian on 11-Mar-17.
 */

public class UserLoginReq {
    private String login;
    private String password;

    public UserLoginReq(String login, String password) {
        this.login = login;
        this.password = password;
    }
}
