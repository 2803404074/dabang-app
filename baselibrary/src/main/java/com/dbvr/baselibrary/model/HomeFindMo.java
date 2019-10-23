package com.dbvr.baselibrary.model;

import java.io.Serializable;
import java.util.List;

public class HomeFindMo implements Serializable {
    private int position;//0-4,
    private List<PlayMode> oneMos;//顶部的头像
    private List<TowMo> towMos;//大封面图和下面四个封面图
    private List<ThreeMo> threeMos;//轮播图
    private List<FourMo> fourMos;//分类列表
    private List<FiveMo> fiveMos;//底下的列表

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public List<PlayMode> getOneMos() {
        return oneMos;
    }

    public void setOneMos(List<PlayMode> oneMos) {
        this.oneMos = oneMos;
    }

    public List<TowMo> getTowMos() {
        return towMos;
    }

    public void setTowMos(List<TowMo> towMos) {
        this.towMos = towMos;
    }

    public List<ThreeMo> getThreeMos() {
        return threeMos;
    }

    public void setThreeMos(List<ThreeMo> threeMos) {
        this.threeMos = threeMos;
    }

    public List<FourMo> getFourMos() {
        return fourMos;
    }

    public void setFourMos(List<FourMo> fourMos) {
        this.fourMos = fourMos;
    }

    public List<FiveMo> getFiveMos() {
        return fiveMos;
    }

    public void setFiveMos(List<FiveMo> fiveMos) {
        this.fiveMos = fiveMos;
    }

    public HomeFindMo() {
    }


    public static class OneMo{
        private int id;

    }

    public static class TowMo{
        private int id;
        private int userId;
        //直播类型的字段
        private String fname;
        private String liveTitle;
        private String coverUrl;
        private String liveDescribe;
        private String lookNum;

        //短视频类型的字段
        private String videoUrl;
        private String title;
        private String coverPath;
        private String praseCount;

        private String nickName;


        public TowMo() {
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
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

        public String getLiveDescribe() {
            return liveDescribe;
        }

        public void setLiveDescribe(String liveDescribe) {
            this.liveDescribe = liveDescribe;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getLookNum() {
            return lookNum;
        }

        public void setLookNum(String lookNum) {
            this.lookNum = lookNum;
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
    }

    public static class ThreeMo{
        private int id;
        private String chartUrl;
        private String title;
        private String jumpUrl;

        public ThreeMo() {
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getChartUrl() {
            return chartUrl;
        }

        public void setChartUrl(String chartUrl) {
            this.chartUrl = chartUrl;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getJumpUrl() {
            return jumpUrl;
        }

        public void setJumpUrl(String jumpUrl) {
            this.jumpUrl = jumpUrl;
        }
    }

    public static class FourMo{
        private int id;
        private String name;

        public FourMo() {
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
    public static class FiveMo{
        private int id;
        private int userId;
        //直播类型的字段
        private String fname;
        private String liveTitle;
        private String coverUrl;
        private String liveDescribe;
        private String lookNum;

        //短视频类型的字段
        private String videoUrl;
        private String title;
        private String coverPath;
        private String praseCount;

        private String nickName;


        public FiveMo() {
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
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

        public String getLiveDescribe() {
            return liveDescribe;
        }

        public void setLiveDescribe(String liveDescribe) {
            this.liveDescribe = liveDescribe;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getLookNum() {
            return lookNum;
        }

        public void setLookNum(String lookNum) {
            this.lookNum = lookNum;
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
    }

}
