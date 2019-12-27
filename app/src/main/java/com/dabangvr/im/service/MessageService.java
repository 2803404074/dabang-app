package com.dabangvr.im.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;

import java.io.Serializable;
import java.util.List;

public class MessageService extends Service implements EMMessageListener {

    @Override
    public void onCreate() {
        super.onCreate();
        EMClient.getInstance().chatManager().addMessageListener(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onMessageReceived(List<EMMessage> list) {
        Intent intent = new Intent("com.haitiaotiao.broadcasereceiver.MESSAGE");
        intent.putExtra("messageList", (Serializable) list);         //向广播接收器传递数据
        sendBroadcast(intent);
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> list) {

    }

    @Override
    public void onMessageRead(List<EMMessage> list) {

    }

    @Override
    public void onMessageDelivered(List<EMMessage> list) {

    }

    @Override
    public void onMessageRecalled(List<EMMessage> list) {

    }

    @Override
    public void onMessageChanged(EMMessage emMessage, Object o) {

    }
}
