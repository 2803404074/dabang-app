package com.dabangvr.play.video.mo;

public class CommentMo {
    private int id;
    private boolean praseStatus;
    private String nickName;
    private String content;
    private int userId;
    private String addTime;
    private String praseCount;
    private String headUrl;

    public CommentMo() {
    }

    public boolean isPraseStatus() {
        return praseStatus;
    }

    public void setPraseStatus(boolean praseStatus) {
        this.praseStatus = praseStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getPraseCount() {
        return praseCount;
    }

    public void setPraseCount(String praseCount) {
        this.praseCount = praseCount;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
