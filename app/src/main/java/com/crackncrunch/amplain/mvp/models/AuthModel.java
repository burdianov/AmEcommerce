package com.crackncrunch.amplain.mvp.models;

/**
 * Created by Lilian on 19-Feb-17.
 */

public class AuthModel extends AbstractModel {

    public AuthModel() {

    }

    public boolean isAuthUser() {
        // TODO: 19-Feb-17 search token in SharedPreferences
        return false;
    }

    public void loginUser(String email, String password) {
        // TODO: 19-Feb-17 send data to server for auth
    }
}
