package com.dabangvr.play.video.view;

import android.view.View;

public interface AdapterInter {
    void onItemClick(View view, int position);

    void onLongItemClick(View view, int postion);

    void onLoadMore();
}
