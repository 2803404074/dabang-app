package com.dbvr.baselibrary.model;

import java.io.Serializable;

/**
 * 直播短视频商品类型
 */
public class MainMo implements Serializable {
    private int id;
    private boolean isFollow;

    private String userId;
    private String nickName;
    private String headUrl;
    private String permanentResidence;//常住地
    private String jumpNo;//跳跳号
    private String fansNumber;//粉丝量
    private String  followNumber;//灌注量
    private String  goodsNumber;//商品数量

    private String roomId;
    private String liveTitle;
    private String coverUrl;
    private String lookNum;//观看人数
    private String praseCount;//点赞数量

    private String goodsTitle;
    private String goodsPrice;
    private String goodsCover;

    public MainMo() {
    }

    public boolean isFollow() {
        return isFollow;
    }

    public String getPermanentResidence() {
        return permanentResidence;
    }

    public void setPermanentResidence(String permanentResidence) {
        this.permanentResidence = permanentResidence;
    }

    public String getJumpNo() {
        return jumpNo;
    }

    public void setJumpNo(String jumpNo) {
        this.jumpNo = jumpNo;
    }

    public String getFansNumber() {
        return fansNumber;
    }

    public void setFansNumber(String fansNumber) {
        this.fansNumber = fansNumber;
    }

    public String getFollowNumber() {
        return followNumber;
    }

    public void setFollowNumber(String followNumber) {
        this.followNumber = followNumber;
    }

    public String getGoodsNumber() {
        return goodsNumber;
    }

    public void setGoodsNumber(String goodsNumber) {
        this.goodsNumber = goodsNumber;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getLiveTitle() {
        return liveTitle;
    }

    public void setLiveTitle(String liveTitle) {
        this.liveTitle = liveTitle;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getLookNum() {
        return lookNum;
    }

    public void setLookNum(String lookNum) {
        this.lookNum = lookNum;
    }

    public String getPraseCount() {
        return praseCount;
    }

    public void setPraseCount(String praseCount) {
        this.praseCount = praseCount;
    }

    public String getGoodsTitle() {
        return goodsTitle;
    }

    public void setGoodsTitle(String goodsTitle) {
        this.goodsTitle = goodsTitle;
    }

    public String getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(String goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public String getGoodsCover() {
        return goodsCover;
    }

    public void setGoodsCover(String goodsCover) {
        this.goodsCover = goodsCover;
    }
}
