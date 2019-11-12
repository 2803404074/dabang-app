package com.dbvr.baselibrary.model;

import java.io.Serializable;
import java.util.List;

/**
 * 直播短视频商品类型
 */
public class AllTypeMo implements Serializable {
    private int position;//0直播，1短视频，2商品
    private int userId;
    private boolean isFollow;//是否已关注

    private LiveMo oneMos;
    private VideoMo towMos;
    private GoodsMo threeMos;

    public AllTypeMo() {
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    public LiveMo getOneMos() {
        return oneMos;
    }

    public void setOneMos(LiveMo oneMos) {
        this.oneMos = oneMos;
    }

    public VideoMo getTowMos() {
        return towMos;
    }

    public void setTowMos(VideoMo towMos) {
        this.towMos = towMos;
    }

    public GoodsMo getThreeMos() {
        return threeMos;
    }

    public void setThreeMos(GoodsMo threeMos) {
        this.threeMos = threeMos;
    }

    public class LiveMo{
        private String fname;
        private String liveTitle;
        private String coverUrl;
        private String roomId;
        private String liveDescribe;
        private String lookNum;
        private String liveTag;

        public LiveMo() {
        }

        public String getFname() {
            return fname;
        }

        public void setFname(String fname) {
            this.fname = fname;
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

        public String getRoomId() {
            return roomId;
        }

        public void setRoomId(String roomId) {
            this.roomId = roomId;
        }

        public String getLiveDescribe() {
            return liveDescribe;
        }

        public void setLiveDescribe(String liveDescribe) {
            this.liveDescribe = liveDescribe;
        }

        public String getLookNum() {
            return lookNum;
        }

        public void setLookNum(String lookNum) {
            this.lookNum = lookNum;
        }

        public String getLiveTag() {
            return liveTag;
        }

        public void setLiveTag(String liveTag) {
            this.liveTag = liveTag;
        }
    }
    public static class VideoMo{
        private String videoUrl;
        private String title;
        private String coverPath;
        private String praseCount;
        private String nickName;
        private String headUrl;

        public VideoMo() {
        }

        public String getVideoUrl() {
            return videoUrl;
        }

        public void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getCoverPath() {
            return coverPath;
        }

        public void setCoverPath(String coverPath) {
            this.coverPath = coverPath;
        }

        public String getPraseCount() {
            return praseCount;
        }

        public void setPraseCount(String praseCount) {
            this.praseCount = praseCount;
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
    }

    public class GoodsMo{
        private String id;
        private String name;
        private String listUrl;

        public GoodsMo() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getListUrl() {
            return listUrl;
        }

        public void setListUrl(String listUrl) {
            this.listUrl = listUrl;
        }
    }

}
