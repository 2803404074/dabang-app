package com.dabangvr.im.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMMessage;

import java.util.ArrayList;
import java.util.List;

public class MessReceiver extends BroadcastReceiver {

    public ImMessage imMessage;

    @Override
    public void onReceive(Context context, Intent intent) {
        List<EMMessage> listObj =  (List<EMMessage>) intent.getSerializableExtra("messageList");
        if (listObj!=null){
            if (imMessage!=null){
                imMessage.msg(listObj);
            }
        }
    }

    public interface ImMessage{
        void msg(List<EMMessage> list);
    }

    public void setImMessage(ImMessage imMessage) {
        this.imMessage = imMessage;
    }

    public ImMessage getImMessage() {
        return imMessage;
    }
}
