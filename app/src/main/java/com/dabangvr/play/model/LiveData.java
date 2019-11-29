package com.dabangvr.play.model;

public class LiveData {
    private String roomId;
    private String nickName;
    private String headUrl;
    private int liveCount;
    private int seeNum;

    public LiveData() {
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
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

    public int getLiveCount() {
        return liveCount;
    }

    public void setLiveCount(int liveCount) {
        this.liveCount = liveCount;
    }

    public int getSeeNum() {
        return seeNum;
    }

    public void setSeeNum(int seeNum) {
        this.seeNum = seeNum;
    }
}
