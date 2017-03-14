package com.crackncrunch.amplain.data.network.error;

/**
 * Created by Lilian on 12-Mar-17.
 */

public class AccessError extends Exception {
    public AccessError() {
        super("Incorrect login or password");
    }
}
