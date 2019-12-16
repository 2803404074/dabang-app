package com.dabangvr.comment.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.dabangvr.util.ShareUtils;

public class ShareReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        String cover = intent.getStringExtra("cover");
        String link = intent.getStringExtra("link");
        ShareUtils.getInstance(context).startShare(title,content,cover,link);
    }
}
