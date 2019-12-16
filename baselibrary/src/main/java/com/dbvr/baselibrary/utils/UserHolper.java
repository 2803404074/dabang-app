package com.dbvr.baselibrary.utils;

import android.content.Context;

import com.dbvr.baselibrary.model.UserMess;

public class UserHolper {
    private Context context;
    public static UserHolper userHolper;
    private UserMess userMess;
    private String token;
    public static UserHolper getUserHolper(Context context){
        if (userHolper == null){
            userHolper = new UserHolper(context);
        }
        return userHolper;
    }

    public UserHolper(Context context) {
        this.context = context;
    }

    public UserMess getUserMess() {
        if (userMess == null){
            userMess =  SPUtils.instance(context).getUser();
        }
        return userMess;
    }

    public String getToken() {
        if (StringUtils.isEmpty(token)){
             token =  SPUtils.instance(context).getToken();
        }
        return token;
    }

    public void upUser(UserMess userMess){
        this.userMess = userMess;
    }

    public void ondessUser(){
        if (userHolper!=null){
            userHolper = null;
            if (userMess!=null){
                userMess = null;
            }
        }
    }
}
