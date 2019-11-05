package com.dbvr.baselibrary.model;

import java.util.List;

public class DynamicMo {
    private int id;
    private String content;
    private String sendTime;
    private String commentNumber;
    private String nickName;
    private int praisedNumber;//点赞数量
    private String headUrl;
    private List<commentMo> commentVoList;

    private List<String>picUrl;

    private boolean isLove;
    private boolean isNews;

    public DynamicMo() {
    }

    public int getPraisedNumber() {
        return praisedNumber;
    }

    public void setPraisedNumber(int praisedNumber) {
        this.praisedNumber = praisedNumber;
    }

    public boolean isLove() {
        return isLove;
    }

    public void setLove(boolean love) {
        isLove = love;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getCommentNumber() {
        return commentNumber;
    }

    public void setCommentNumber(String commentNumber) {
        this.commentNumber = commentNumber;
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

    public List<commentMo> getCommentVoList() {
        return commentVoList;
    }

    public void setCommentVoList(List<commentMo> commentVoList) {
        this.commentVoList = commentVoList;
    }

    public List<String> getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(List<String> picUrl) {
        this.picUrl = picUrl;
    }

    public boolean isNews() {
        return isNews;
    }

    public void setNews(boolean news) {
        isNews = news;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public static class commentMo{
        private String nickName;
        private String content;
        private String time;

        public commentMo() {
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }
}