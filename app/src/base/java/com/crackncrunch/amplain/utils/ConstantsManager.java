package com.crackncrunch.amplain.utils;

/**
 * Created by Lilian on 22-Feb-17.
 */

public class ConstantsManager {
    public static final String CUSTOM_FONTS_ROOT = "fonts/";
    public static final String CUSTOM_FONT_NAME = "PTBebasNeueRegular.ttf";

    public static final String AUTH_TOKEN_KEY = "AUTH_TOKEN_KEY";
    public static final String INVALID_TOKEN = "INVALID_TOKEN";
    public static final String PHOTO_FILE_PREFIX = "IMG_";

    public static final int PICK_PHOTO_FROM_GALLERY = 111;
    public static final int PICK_PHOTO_FROM_CAMERA = 222;

    public static final int REQUEST_PERMISSION_CAMERA = 3000;
    public static final int REQUEST_PERMISSION_READ_WRITE_STORAGE = 3001;

    public static final int REQUEST_PROFILE_PHOTO_GALLERY = 1001;
    public static final int REQUEST_PROFILE_PHOTO_CAMERA = 1002;
    public static final String FILE_PROVIDER_AUTHORITY = "com.crackncrunch" +
            ".amplain.fileprovider";

    public static final String LAST_MODIFIED_HEADER = "Last-Modified";
    public static final String IF_MODIFIED_SINCE_HEADER = "If-Modified-Since";

    public static final String SERVER_DATE_FORMAT = "yyyy-MM-dd\'T\'HH:mm:ss.SSS\'Z\'";

    public static final String UNIX_EPOCH_TIME = "Thu, 01 Jan 1970 00:00:00 GMT";
}
