package com.crackncrunch.amplain;

/**
 * Created by Lilian on 21-Feb-17.
 */

public class AppConfig {
    // public static final String BASE_URL = "https://skba1.mgbeta.ru/api/v1/";
    public static final String BASE_URL = "https://private-8967d4-middleappskillbranch.apiary-mock.com";
    public static final String VK_BASE_URL = "https://api.vk.com/method/";
    public static final String FB_BASE_URL = "https://graph.facebook.com/v2.8/";

    public static final int MAX_CONNECTION_TIMEOUT = 5000;
    public static final int MAX_READ_TIMEOUT = 5000;
    public static final int MAX_WRITE_TIMEOUT = 5000;

    public static final int MIN_CONSUMER_COUNT = 1;
    public static final int MAX_CONSUMER_COUNT = 3;
    public static final int LOAD_FACTOR = 3;
    public static final int KEEP_ALIVE = 120;
    public static final int INITIAL_BACK_OFF_IN_MS = 1000;
    public static final int UPDATE_DATA_INTERVAL = 30; // update data every 30 seconds
    public static final int RETRY_REQUEST_COUNT = 5;
    public static final int RETRY_REQUEST_BASE_DELAY = 500;
}
