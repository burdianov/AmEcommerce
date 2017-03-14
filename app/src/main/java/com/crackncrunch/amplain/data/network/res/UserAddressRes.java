package com.crackncrunch.amplain.data.network.res;

import com.squareup.moshi.Json;

/**
 * Created by Lilian on 11-Mar-17.
 */

class UserAddressRes {
    @Json(name = "_id")
    private String id;
    private String name;
    private String street;
    private String house;
    private String apartment;
    private int floor;
    private String comment;
}
