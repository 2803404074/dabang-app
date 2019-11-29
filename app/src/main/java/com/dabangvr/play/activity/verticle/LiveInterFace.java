package com.dabangvr.play.activity.verticle;

import com.dabangvr.live.gift.GiftMo;
import com.dabangvr.live.gift.danmu.DanmuEntity;
import com.dabangvr.play.model.LiveData;
import com.dbvr.baselibrary.model.MainMo;
import com.opensource.svgaplayer.SVGAVideoEntity;

import java.util.ArrayList;

public interface LiveInterFace<T> {
    void updateHots(int onLine);//在线人数

    void updateComment(int size, ArrayList data);
    void updateDanMu(DanmuEntity danmuEntity);
    void updateGift(GiftMo giftMo);
    void updateGiftMall(SVGAVideoEntity giftMo, int arg0, boolean arg1);
    void updateDz(int num);
    void updateRoom(MainMo mData);
}
