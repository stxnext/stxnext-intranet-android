
package com.stxnext.management.android.dto;

import java.lang.reflect.Type;

import com.google.gson.Gson;

public class AbstractMessage {

    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static <T> T fromJsonString(String jsonString,Type type) {
        Gson gson = new Gson();
        return gson.fromJson(
                jsonString,
                type);
    }

}
