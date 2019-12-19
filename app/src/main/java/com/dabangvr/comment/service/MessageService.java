package com.dabangvr.comment.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;

import java.util.List;

public class MessageService extends Service implements EMMessageListener {

    public static MessageService service;
    private MessageCallBack callBack;
    private MessageCallBack2 callBack2;

    public interface MessageCallBack{
        void onMessageReceived(List<EMMessage> messages);
    }
    public interface MessageCallBack2{
        void onMessageReceived(List<EMMessage> messages);
    }

    public void setCallBack(MessageCallBack callBack) {
        this.callBack = callBack;
    }
    public void setCallBack2(MessageCallBack2 callBack) {
        this.callBack2 = callBack;

    }

    public MessageCallBack getCallBack() {
        return callBack;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        service = this;
        EMClient.getInstance().chatManager().addMessageListener(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onMessageReceived(List<EMMessage> list) {
            if (callBack!=null){
                callBack.onMessageReceived(list);
            }
        if (callBack2!=null){
            callBack2.onMessageReceived(list);
        }
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
