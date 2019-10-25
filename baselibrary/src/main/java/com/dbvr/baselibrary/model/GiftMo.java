package com.dbvr.baselibrary.model;

/**
 * 直播间礼物列表
 */
public class GiftMo {
    private int id;
    private String giftName;
    private String giftUrl;
    private int giftCoins;
    private boolean isClick;

    public GiftMo() {
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
