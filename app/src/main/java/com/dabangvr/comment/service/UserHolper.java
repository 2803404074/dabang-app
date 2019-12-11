package com.dabangvr.comment.service;

import android.content.Context;

import com.dbvr.baselibrary.model.UserMess;
import com.dbvr.baselibrary.utils.SPUtils;

public class UserHolper {
    private Context context;
    public static UserHolper userHolper;
    private UserMess userMess;
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

    public void ondessUser(){
        if (userHolper!=null){
            userHolper = null;
            if (userMess!=null){
                userMess = null;
            }
        }
    }
}
