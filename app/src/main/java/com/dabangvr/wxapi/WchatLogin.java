package com.dabangvr.wxapi;

public interface WchatLogin {
    void success(String openID, final String uName, final String icon, final String type);
    void error(String errorMessage);
}
