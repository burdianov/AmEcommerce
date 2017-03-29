package com.crackncrunch.amplain.data.network.error;

public class NetworkAvailableError extends Throwable {
    public NetworkAvailableError() {
        super("The Internet is not accessible. Try again later");
    }
}
