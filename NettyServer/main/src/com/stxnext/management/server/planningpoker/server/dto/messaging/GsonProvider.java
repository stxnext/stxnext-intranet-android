package com.stxnext.management.server.planningpoker.server.dto.messaging;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonProvider {
    private static Gson _gson;
    
    public static Gson get(){
        if(_gson == null){
            _gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        }
        return _gson;
    }
}
