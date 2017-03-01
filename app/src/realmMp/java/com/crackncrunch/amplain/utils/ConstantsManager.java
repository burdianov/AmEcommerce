package com.crackncrunch.amplain.utils;

import com.crackncrunch.amplain.BuildConfig;

/**
 * Created by Lilian on 22-Feb-17.
 */

public class ConstantsManager {
    public static final String CUSTOM_FONTS_ROOT = "fonts/";
    public static final String CUSTOM_FONT_NAME = "PTBebasNeueRegular.ttf";

    public static final String AUTH_TOKEN_KEY = "AUTH_TOKEN_KEY";
    public static final String INVALID_TOKEN = "INVALID_TOKEN";
    public static final String PHOTO_FILE_PREFIX = "IMG_";

    public static final int REQUEST_PERMISSION_CAMERA = 3000;
    public static final int REQUEST_PERMISSION_READ_WRITE_STORAGE = 3001;

    public static final int REQUEST_PROFILE_PHOTO_PICKER = 1001;
    public static final int REQUEST_PROFILE_PHOTO_CAMERA = 1002;
    public static final String FILE_PROVIDER_AUTHORITY = BuildConfig
            .APPLICATION_ID + ".fileprovider";

    public static final String LAST_MODIFIED_HEADER = "Last-Modified";
    public static final String IF_MODIFIED_SINCE_HEADER = "If-Modified-Since";

    public static final String SERVER_DATE_FORMAT = "yyyy-MM-dd\'T\'HH:mm:ss.SSS\'Z\'";
    public static final String UNIX_EPOCH_TIME = "Thu, 01 Jan 1970 00:00:00 GMT";

    public static final String REALM_USER = "leo@gmail.com";
    public static final String REALM_PASSWORD = "leo";
    public static final String REALM_AUTH_URL = "http://192.168.1.108:9080/auth";
    public static final String REALM_DB_URL = "realm://192.168.1.108:9080/~/default";

    //public static final String BASE_URL = BuildConfig.HOST; // define variable from build config
}
