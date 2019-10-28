package com.dabangvr.live.gift;

import com.dbvr.baselibrary.model.LiveComment;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class GiftQueue {
    Map<String, List<LiveComment>> queue;

    public GiftQueue() {
        queue = new LinkedHashMap<>();
    }


    public List<LiveComment> getList(String key) {
        return queue.get(key);
    }

    /**
     * 获取当前队列的第一个的礼物
     *
     * @return
     */
    public synchronized LiveComment removeTop() {
        LiveComment model = null;
        if (queue.size() > 0) {
            List<LiveComment> giftSendModels = getTopList();

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
    private List<LiveComment> getTopList() {
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
    public synchronized void add(LiveComment model) {
        List<LiveComment> mapList = getList(model.getUserName());
        if (mapList == null) {
            //新来的用户添加到任务队列中
            List<LiveComment> l = new ArrayList<>();
            l.add(model);
            queue.put(model.getUserName(), l);
        } else {
            boolean isRepeat = false;
            for (LiveComment giftModel : mapList) {
                if (giftModel.getMsgDsComment().getGiftId() == model.getMsgDsComment().getGiftId()) {
                    //礼物相同时
                    giftModel.getMsgDsComment().setGiftNum(model.getMsgDsComment().getGiftNum());
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
