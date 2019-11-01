package com.dbvr.baselibrary.model;

public class DzAndComment {
    private int sayId;
    private int userId;
    private int tag;//0动态   1短视频
    private String coverUrl;
    private String nickName;
    private String headUrl;
    private String addTime;

    public DzAndComment() {
    }

    public int getSayId() {
        return sayId;
    }

    public void setSayId(int sayId) {
        this.sayId = sayId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
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

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }
}
