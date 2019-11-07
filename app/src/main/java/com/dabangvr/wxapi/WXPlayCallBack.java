package com.dabangvr.wxapi;

public interface WXPlayCallBack {
    void success();
    void error(String errorMessage);
    void cancel();
}
