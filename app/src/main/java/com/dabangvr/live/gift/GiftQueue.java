package com.dabangvr.live.gift;

import com.dbvr.baselibrary.model.LiveComment;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class GiftQueue {
    Map<String, List<GiftMo>> queue;

    public GiftQueue() {
        queue = new LinkedHashMap<>();
    }


    public List<GiftMo> getList(String key) {
        return queue.get(key);
    }

    /**
     * 获取当前队列的第一个的礼物
     *
     * @return
     */
    public synchronized GiftMo removeTop() {
        GiftMo model = null;
        if (queue.size() > 0) {
            List<GiftMo> giftSendModels = getTopList();

            if (giftSendModels == null) {
                return model;
            }
            if (giftSendModels.size() > 0) {
                model = giftSendModels.remove(0);
            }
            if (giftSendModels.size() == 0) {
                queue.remove(model.getUserName());
            }
        }
        return model;
    }

    /**
     * 获取第一个添加礼物的用户的 礼物列表
     *
     * @return
     */
    private List<GiftMo> getTopList() {
        Set<String> strings = queue.keySet();
        for (String string : strings) {
            return queue.get(string);
        }
        return null;
    }

    /**
     * 添加一个礼物到队列
     *
     * @param model
     * @return
     */
    public synchronized void add(GiftMo model) {
        List<GiftMo> mapList = getList(model.getUserName());
        if (mapList == null) {
            //新来的用户添加到任务队列中
            List<GiftMo> l = new ArrayList<>();
            l.add(model);
            queue.put(model.getUserName(), l);
        } else {
            boolean isRepeat = false;
            for (GiftMo giftModel : mapList) {
                if (giftModel.getGiftName() == model.getGiftName()) {
                    //礼物相同时
                    giftModel.setGiftNum(model.getGiftNum());
                    isRepeat = true;
                    break;
                }
            }
            if (!isRepeat) {
                //没有重复 是一个新的礼物
                mapList.add(model);
            }
        }
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

}
