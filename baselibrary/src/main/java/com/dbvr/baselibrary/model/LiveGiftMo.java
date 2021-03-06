package com.dbvr.baselibrary.model;

/**
 * 直播间礼物列表
 */
public class LiveGiftMo {
    private int id;
    private String giftName;
    private String giftUrl;
    private String tag;
    private int giftCoins;//跳币数量
    private boolean isClick;

    public LiveGiftMo() {
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isClick() {
        return isClick;
    }

    public void setClick(boolean click) {
        isClick = click;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    public String getGiftUrl() {
        return giftUrl;
    }

    public void setGiftUrl(String giftUrl) {
        this.giftUrl = giftUrl;
    }

    public int getGiftCoins() {
        return giftCoins;
    }

    public void setGiftCoins(int giftCoins) {
        this.giftCoins = giftCoins;
    }
}
