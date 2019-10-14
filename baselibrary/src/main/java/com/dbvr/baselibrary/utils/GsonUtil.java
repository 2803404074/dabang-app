package com.dbvr.baselibrary.utils;

import com.dbvr.baselibrary.model.GiftMo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class GsonUtil <T>{
    private Gson gson;

    public GsonUtil() {
        gson = new Gson();
    }
    public List<T> getList(Class<T> t, String string){
        return gson.fromJson(string, new TypeToken<List<T>>() {}.getType());
    }

    public T getClass(Class<T> t, String string){
        return gson.fromJson(string,t);
    }
}
