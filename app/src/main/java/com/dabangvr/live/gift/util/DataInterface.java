package com.dabangvr.live.gift.util;

import com.dabangvr.R;
import com.dbvr.baselibrary.model.LiveComment;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by duanliuyi on 2018/5/10.
 */

/*数据接口
*
* 由于本demo没有App Server，用户信息，聊天室信息 等全部通过DataInterface的接口返回，目前都是写死的数据。 开发者可以修改这些接口，去自己的app server取数据。
* */
public class DataInterface {

    /*获取礼物列表*/
    public static ArrayList<LiveComment.GifMo> getGiftList() {
        ArrayList<LiveComment.GifMo> gifts = new ArrayList<>();
        String[] giftNames = new String[]{"蛋糕", "气球", "花儿", "项链", "戒指"};
        int[] giftRes = new int[]{R.mipmap.test, R.mipmap.test2, R.mipmap.test4, R.mipmap.test5, R.mipmap.test6};

        for (int i = 0; i < giftNames.length; i++) {
            LiveComment.GifMo gift = new LiveComment.GifMo();
            gift.setGiftId("GiftId_" + (i + 1));
            gift.setGiftName(giftNames[i]);
            gift.setGiftTag(giftRes[i]);
            gifts.add(gift);
        }
        return gifts;
    }


    /*获取礼物名*/
    public static String getGiftNameById(String giftId) {
        switch (giftId) {
            case "GiftId_1":
                return "蛋糕";
            case "GiftId_2":
                return "气球";
            case "GiftId_3":
                return "花儿";
            case "GiftId_4":
                return "项链";
            case "GiftId_5":
                return "戒指";
        }
        return null;
    }

    /*根据giftId获取礼物信息*/
    public static LiveComment.GifMo getGiftInfo(String giftId) {
        ArrayList<LiveComment.GifMo> gifts = getGiftList();
        for (int i = 0; i < gifts.size(); i++) {
            if (gifts.get(i).getGiftId().equals(giftId)) {
                return gifts.get(i);
            }
        }
        return null;
    }


    /*生成随机数*/
    public static int getRandomNum(int max) {
        Random r = new Random();
        return r.nextInt(max);
    }
}
