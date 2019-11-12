package com.dbvr.baselibrary.model;

import java.io.Serializable;

/**
 * 用户实体类
 */
public class UserMess implements Serializable {
    private Integer id;

    private boolean mutual;//是否已经关注
    private boolean isNewsUser;

    private String mobile; //手机号码

    private String openId; //唯一id

    private String headUrl; //头像url

    private String nickName; //名字

    private String autograph;//个性签名

    private String permanentResidence; //常住地

    private String sex;  //性别

    private int grade;//等级

    private String fansNumber;//粉丝量

    private String praisedNumber;//获赞量

    private String followNumber;//关注量

    private int diamond;//鲜币

    private String integral;//积分

    private String token;

    private String anchorId; //主播id

    private int isAnchor; //主播标识，1是主播 且主播状态正常

    private String isNew;//是否是新人

    public UserMess() {
    }

    @Override
    public String toString() {
        return "UserMess{" +
                "id=" + id +
                ", mutual=" + mutual +
                ", isNewsUser=" + isNewsUser +
                ", mobile='" + mobile + '\'' +
                ", openId='" + openId + '\'' +
                ", headUrl='" + headUrl + '\'' +
                ", nickName='" + nickName + '\'' +
                ", autograph='" + autograph + '\'' +
                ", permanentResidence='" + permanentResidence + '\'' +
                ", sex='" + sex + '\'' +
                ", grade=" + grade +
                ", fansNumber='" + fansNumber + '\'' +
                ", praisedNumber='" + praisedNumber + '\'' +
                ", followNumber='" + followNumber + '\'' +
                ", diamond=" + diamond +
                ", integral='" + integral + '\'' +
                ", token='" + token + '\'' +
                ", anchorId='" + anchorId + '\'' +
                ", isAnchor=" + isAnchor +
                ", isNew='" + isNew + '\'' +
                '}';
    }

    public boolean isMutual() {
        return mutual;
    }

    public void setMutual(boolean mutual) {
        this.mutual = mutual;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public boolean isNewsUser() {
        return isNewsUser;
    }

    public void setNewsUser(boolean newsUser) {
        isNewsUser = newsUser;
    }

    public String getAnchorId() {
        return anchorId;
    }

    public void setAnchorId(String anchorId) {
        this.anchorId = anchorId;
    }

    public String getIsNew() {
        return isNew;
    }

    public void setIsNew(String isNew) {
        this.isNew = isNew;
    }

    public int getDiamond() {
        return diamond;
    }

    public void setDiamond(int diamond) {
        this.diamond = diamond;
    }

    public String getIntegral() {
        return integral;
    }

    public void setIntegral(String integral) {
        this.integral = integral;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getIsAnchor() {
        return isAnchor;
    }

    public void setIsAnchor(int isAnchor) {
        this.isAnchor = isAnchor;
    }

    public String getPermanentResidence() {
        return permanentResidence;
    }

    public void setPermanentResidence(String permanentResidence) {
        this.permanentResidence = permanentResidence;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
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

    public String getAutograph() {
        return autograph;
    }

    public void setAutograph(String autograph) {
        this.autograph = autograph;
    }

    public String getFansNumber() {
        return fansNumber;
    }

    public void setFansNumber(String fansNumber) {
        this.fansNumber = fansNumber;
    }

    public String getPraisedNumber() {
        return praisedNumber;
    }

    public void setPraisedNumber(String praisedNumber) {
        this.praisedNumber = praisedNumber;
    }

    public String getFollowNumber() {
        return followNumber;
    }

    public void setFollowNumber(String followNumber) {
        this.followNumber = followNumber;
    }
}
