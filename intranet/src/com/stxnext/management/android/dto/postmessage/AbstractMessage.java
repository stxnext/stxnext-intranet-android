
package com.stxnext.management.android.dto.postmessage;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.google.gson.reflect.TypeToken;

public class AbstractMessage {

    protected Type listStringType = new TypeToken<ArrayList<String>>() {
    }.getType();

    public String serialize() {
        return GsonProvider.get().toJson(this);
    }

    public static <T> T fromJsonString(String jsonString, Type type) {
        return GsonProvider.get().fromJson(
                jsonString,
                type);
    }

}
