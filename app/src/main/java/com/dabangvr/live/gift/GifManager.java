package com.dabangvr.live.gift;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;

import com.dbvr.baselibrary.model.LiveComment;

import java.util.ArrayList;
import java.util.List;


public class GifManager {


    GiftQueue queue;
    List<GiftFrameLayout> viewList;

    public GifManager() {
        viewList = new ArrayList<>();
        queue = new GiftQueue();
    }

    public void addView(GiftFrameLayout giftFrameLayout) {
        viewList.add(giftFrameLayout);
    }

    public void addGift(GiftMo giftSendModel) {

        GiftFrameLayout showView = getShowCurrView(giftSendModel);
        if (showView != null) {
            //当前添加的礼物已经在播放了，直接修改播放次数
            int count = giftSendModel.getGiftNum() + showView.getRepeatCount();
            showView.setRepeatCount(count);
            return;
        }
        queue.add(giftSendModel);
        beingAnimotion();
    }

    public void beingAnimotion() {
        //有可用的控件就立即播放
        GiftFrameLayout hideView = getHideView();
        if (hideView != null) {
            beginAnimotion(hideView);
        }
    }


    private GiftFrameLayout getShowCurrView(GiftMo model) {
        for (GiftFrameLayout giftFrameLayout : viewList) {
            if (giftFrameLayout.isShowing() && giftFrameLayout.equalsCurrentModel(model)) {
                return giftFrameLayout;
            }
        }
        return null;
    }

    /**
     * 获取当前没有展示的view
     *
     * @return
     */
    private GiftFrameLayout getHideView() {
        for (GiftFrameLayout giftFrameLayout : viewList) {
            if (!giftFrameLayout.isShowing()) {
                return giftFrameLayout;
            }
        }
        return null;
    }


    public void beginAnimotion(final GiftFrameLayout view) {

        GiftMo model = queue.removeTop();
        if (model == null) {
            return;
        }
        view.setModel(model);
        AnimatorSet animatorSet = view.startAnimation(model.getGiftNum());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                synchronized (queue) {
                    //礼物队列里还存在礼物的情况
                    if (!queue.isEmpty()) {
                        beginAnimotion(view);
                    }
                }
            }
        });
    }
}