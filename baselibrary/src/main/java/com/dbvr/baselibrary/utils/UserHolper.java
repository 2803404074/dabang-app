package com.dbvr.baselibrary.utils;

import android.content.Context;

import com.dbvr.baselibrary.model.UserMess;

public class UserHolper {
    private Context context;
    public static UserHolper userHolper;
    private UserMess userMess;
    private String token;

    public static UserHolper getUserHolper(Context context) {
        if (userHolper == null) {
            userHolper = new UserHolper(context);
        }
        return userHolper;
    }

    public UserHolper(Context context) {
        this.context = context;
    }

    public UserMess getUserMess() {
        if (userMess == null) {
            userMess = SPUtils.instance(context).getUser();
        }
        return userMess;
    }

    public String getToken() {
        if (StringUtils.isEmpty(token)) {
            token = SPUtils.instance(context).getToken();
        }
        return token;
    }


    public void upUser(int key, String value) {
        switch (key) {
            case 1:
                this.userMess.setHeadUrl(value);
                break;//头像
            case 2:
                this.userMess.setNickName(value);
                break;//昵称
            case 3:
                this.userMess.setSex(value);
                break;//性别
            case 4:
                this.userMess.setBirthday(value);
                break;//生日
            case 5:
                this.userMess.setAutograph(value);
                break;//自我介绍
            case 6:
                this.userMess.setMobile(value);
                break;//手机号码
            default:
                break;
        }
    }

    public void ondessUser() {
        if (userHolper != null) {
            userHolper = null;
            if (userMess != null) {
                userMess = null;
            }
        }
    }
}
