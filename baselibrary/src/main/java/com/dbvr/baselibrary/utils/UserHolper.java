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

    public void setUserMess(UserMess userMess){
        this.userMess = userMess;
    }
    public UserMess getUserMess() {
        if (userMess == null) {
            userMess = SPUtils.instance(context).getUser();
        }
        return userMess;
    }

    public UserMess upUserMess() {
        userMess = SPUtils.instance(context).getUser();
        return userMess;
    }

    public String getToken() {
        if (StringUtils.isEmpty(token)) {
            token = SPUtils.instance(context).getToken();
        }
        return token;
    }


    public void upUser(String key, String value) {
        switch (key) {
            case "headUrl":
                this.userMess.setHeadUrl(value);
                break;//头像
            case "nickName":
                this.userMess.setNickName(value);
                break;//昵称
            case "sex":
                this.userMess.setSex(value);
                break;//性别
            case "birthday":
                this.userMess.setBirthday(value);
                break;//生日
            case "autograph":
                this.userMess.setAutograph(value);
                break;//自我介绍
            case "mobile":
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
