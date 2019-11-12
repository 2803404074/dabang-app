package com.dbvr.baselibrary.model;

import java.io.Serializable;

/**
 * 直播间消息体
 */
public class LiveComment implements Serializable {
    private int msgTag;//标签
    private int dzNum;
    private String userName;//打赏的用户名
    private String headUrl;//打赏的用户的头像

    private String msgComment;//普通/弹幕消息
    private GifMo msgDsComment;//打赏消息
    private OrderMes msgOrder;//订单消息

    public LiveComment(int msgTag, String userName, String headUrl, String msgComment) {
        this.msgTag = msgTag;
        this.userName = userName;
        this.headUrl = headUrl;
        this.msgComment = msgComment;
    }

    @Override
    public String toString() {
        return "LiveComment{" +
                "msgTag=" + msgTag +
                ", dzNum=" + dzNum +
                ", userName='" + userName + '\'' +
                ", headUrl='" + headUrl + '\'' +
                ", msgComment='" + msgComment + '\'' +
                ", msgDsComment=" + msgDsComment +
                ", msgOrder=" + msgOrder +
                '}';
    }

    public int getDzNum() {
        return dzNum;
    }

    public void setDzNum(int dzNum) {
        this.dzNum = dzNum;
    }

    public LiveComment() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    //礼物消息类
    public static class GifMo{
        private String giftId;//礼物Id
        private String giftUrl;//礼物url
        private String giftName;//打赏的礼品的名称
        private int giftNum ;//打赏的礼品的数量
        private int dropNum;//折合跳币的数量

        public GifMo() {
        }

        public int getDropNum() {
            return dropNum;
        }

        public void setDropNum(int dropNum) {
            this.dropNum = dropNum;
        }

        public GifMo(String giftUrl, String giftName, int giftNum) {
            this.giftUrl = giftUrl;
            this.giftName = giftName;
            this.giftNum = giftNum;
        }

        public String getGiftId() {
            return giftId;
        }

        public void setGiftId(String giftId) {
            this.giftId = giftId;
        }

        public String getGiftUrl() {
            return giftUrl;
        }

        public void setGiftUrl(String giftUrl) {
            this.giftUrl = giftUrl;
        }

        public String getGiftName() {
            return giftName;
        }

        public void setGiftName(String giftName) {
            this.giftName = giftName;
        }

        public int getGiftNum() {
            return giftNum;
        }

        public void setGiftNum(int giftNum) {
            this.giftNum = giftNum;
        }
    }


    //订单消息类
    public static class OrderMes{
        private String goodsName;//下单的商品

        public String getGoodsName() {
            return goodsName;
        }

        public void setGoodsName(String goodsName) {
            this.goodsName = goodsName;
        }
    }

    public int getMsgTag() {
        return msgTag;
    }

    public void setMsgTag(int msgTag) {
        this.msgTag = msgTag;
    }

    public String getMsgComment() {
        return msgComment;
    }

    public void setMsgComment(String msgComment) {
        this.msgComment = msgComment;
    }

    public GifMo getMsgDsComment() {
        return msgDsComment;
    }

    public void setMsgDsComment(GifMo msgDsComment) {
        this.msgDsComment = msgDsComment;
    }

    public OrderMes getMsgOrder() {
        return msgOrder;
    }

    public void setMsgOrder(OrderMes msgOrder) {
        this.msgOrder = msgOrder;
    }
}
