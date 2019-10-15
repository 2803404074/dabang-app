package com.dabangvr.im;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dabangvr.adapter.BaseRecyclerHolder;
import com.dbvr.baselibrary.model.UserMess;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.utils.ScreenUtils;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.view.BaseActivity;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.ChatType;
import com.hyphenate.chat.EMTextMessageBody;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 聊天界面
 */
public class ChatActivity extends BaseActivity {

    private int position;

    @BindView(R.id.tv_toUsername)
    TextView tv_toUsername;

    @BindView(R.id.recy_chat)
    RecyclerView recyclerView;

    @BindView(R.id.btn_send)
    TextView btn_send;

    //加号
    @BindView(R.id.iv_add)
    ImageView ivAdd;

    //选择相册、拍照的视图
    @BindView(R.id.ll_add)
    LinearLayout llAdd;

    @BindView(R.id.et_content_chart)
    EditText et_content;



    private int chatType = 1;
    private String toChatUsername;

    private List<EMMessage> msgList;
    private EMConversation conversation;
    protected int pagesize = 20;
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //用来设置整体下移，状态栏沉浸
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }


    @Override
    public int setLayout() {
        return R.layout.activity_chat;
    }

    /**
     * 发送按钮点击事件
     *
     * @param view
     */
    public void tvSand(View view) {

        String content = et_content.getText().toString();
        if (StringUtils.isEmpty(content)) {
            return;
        }
        setMesaage(content);
    }


    private MyAnimatorUtil animatorUtil;

    private UserMess userMess;

    @Override
    public void initView() {
        userMess = SPUtils.instance(getContext()).getUser();
        animatorUtil = new MyAnimatorUtil(getContext(), btn_send);
        toChatUsername = this.getIntent().getStringExtra("username");
        tv_toUsername.setText(toChatUsername);

        getAllMessage();
        msgList = conversation.getAllMessages();
        position = msgList.size()-1;
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);  //键盘弹出
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(getContext(), msgList) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, EMMessage message, int position) {
                //对方的消息
                if (chatAdapter.getItemViewType(position) == 0) {
                    //如果是文本消息
                    try {
                        if (message.getType() == EMMessage.Type.TXT){
                            holder.getView(R.id.tv_chatcontent).setVisibility(View.VISIBLE);
                            holder.getView(R.id.iv_jt).setVisibility(View.VISIBLE);
                            EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
                            holder.setText(R.id.tv_chatcontent, txtBody.getMessage());
                            //隐藏图片内容区域
                            holder.getView(R.id.iv_content).setVisibility(View.GONE);
                        }
                        //如果是图片消息
                        if (message.getType() == EMMessage.Type.IMAGE){
                            EMImageMessageBody imgBody = (EMImageMessageBody) message.getBody();
                            ImageView imageView = holder.getView(R.id.iv_content);
                            imageView.setVisibility(View.VISIBLE);
                            ViewGroup.LayoutParams params = imageView.getLayoutParams();
                            params.height = imgBody.getHeight();
                            params.width = imgBody.getWidth();
                            imageView.setLayoutParams(params);
                            holder.setImageByUrl(R.id.iv_content, imgBody.getRemoteUrl());

                            //隐藏文本内容区域
                            holder.getView(R.id.iv_jt).setVisibility(View.GONE);
                            holder.getView(R.id.tv_chatcontent).setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

//                自己的消息
                if (chatAdapter.getItemViewType(position) == 1) {
                    //文本消息
                    if (message.getType() == EMMessage.Type.TXT){
                        holder.getView(R.id.iv_content).setVisibility(View.GONE);//隐藏图片内容
                        holder.getView(R.id.tv_chatcontent).setVisibility(View.VISIBLE);//显示文本内容
                        holder.getView(R.id.iv_jt).setVisibility(View.VISIBLE);//显示文本箭头
                        EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
                        holder.setText(R.id.tv_chatcontent, txtBody.getMessage());//消息
                        if (userMess != null) {
                            SimpleDraweeView head = holder.getView(R.id.iv_userhead);
                            head.setImageURI(userMess.getHeadUrl());
                        }
                    }
                    //图片消息
                    if (message.getType() == EMMessage.Type.IMAGE){
                        holder.getView(R.id.iv_content).setVisibility(View.VISIBLE);//显示图片内容
                        holder.getView(R.id.tv_chatcontent).setVisibility(View.GONE);//隐藏文本内容
                        holder.getView(R.id.iv_jt).setVisibility(View.GONE);//隐藏文本箭头
                        EMImageMessageBody imgBody = (EMImageMessageBody) message.getBody();
                        ImageView imageView = holder.getView(R.id.iv_content);
                        ViewGroup.LayoutParams params = imageView.getLayoutParams();
                        params.height = imgBody.getHeight();
                        params.width = imgBody.getWidth();
                        imageView.setLayoutParams(params);
                        holder.setImageByUrl(R.id.iv_content,imgBody.getRemoteUrl());
                        if (userMess != null) {
                            SimpleDraweeView head = holder.getView(R.id.iv_userhead);
                            head.setImageURI(userMess.getHeadUrl());
                        }
                    }
                }
            }
        };
        recyclerView.setAdapter(chatAdapter);
        recyclerView.smoothScrollToPosition(position);

        EMClient.getInstance().chatManager().addMessageListener(msgListener);

        et_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_content.getText().toString().length() > 0) {
                    animatorUtil.startAnimator();
                } else {
                    animatorUtil.stopAnimator();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @OnClick({R.id.iv_add, R.id.iv_selectPhoto, R.id.iv_selectCame})
    public void onTouchClick(View view) {
        switch (view.getId()) {
            case R.id.iv_add:
                // 将键盘隐藏
                if (ScreenUtils.isInputShow(this)){
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
                animatorUtil.startHeight(llAdd);
                break;

                //选择相册
            case R.id.iv_selectPhoto:

                break;
                //选择相机
            case R.id.iv_selectCame:
                break;

        }
    }

    @Override
    public void initData() {
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {//
            @Override
            public void onLayoutChange(View v, int left, int top, int right,
                                       int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                // TODO Auto-generated method stub

                if (bottom < oldBottom) {
                    recyclerView.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
                        }
                    }, 100);
                }
            }
        });
    }

    protected void getAllMessage() {
        // 获取当前conversation对象
        conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername,
                EaseCommonUtils.getConversationType(chatType), true);
        // 把此会话的未读数置为0
        conversation.markAllMessagesAsRead();
        // 初始化db时，每个conversation加载数目是getChatOptions().getNumberOfMessagesLoaded
        // 这个数目如果比用户期望进入会话界面时显示的个数不一样，就多加载一些
        final List<EMMessage> msgs = conversation.getAllMessages();
        int msgCount = msgs != null ? msgs.size() : 0;
        if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize) {
            String msgId = null;
            if (msgs != null && msgs.size() > 0) {
                msgId = msgs.get(0).getMsgId();
            }
            conversation.loadMoreMsgFromDB(msgId, pagesize - msgCount);
        }
    }

    private void setMesaage(String content) {
        // 创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        EMMessage message = EMMessage.createTxtSendMessage(content, toChatUsername);
        // 如果是群聊，设置chattype，默认是单聊
        if (chatType == Constant.CHATTYPE_GROUP)
            message.setChatType(ChatType.GroupChat);
        // 发送消息
        EMClient.getInstance().chatManager().sendMessage(message);
        chatAdapter.addPosition(message);
        position++;
        recyclerView.smoothScrollToPosition(position);
        et_content.setText("");
    }

    private void sendImgs(String imagePath) {
        // imagePath为图片本地路径，false为不发送原图（默认超过100k的图片会压缩后发给对方），需要发送原图传true
        EMMessage message = EMMessage.createImageSendMessage(imagePath, false, toChatUsername);
        // 如果是群聊，设置chattype，默认是单聊
        if (chatType == Constant.CHATTYPE_GROUP)
            message.setChatType(ChatType.GroupChat);
        // 发送消息
        EMClient.getInstance().chatManager().sendMessage(message);

        Bundle bundle = new Bundle();
        bundle.putParcelable("data",message);
        Message msg = new Message();
        msg.what = 100;
        msg.setData(bundle);
        handler.sendMessage(msg);
    }
    Handler handler = new android.os.Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 100) {
                EMMessage emMessage = msg.getData().getParcelable("data");
                position++;
                recyclerView.smoothScrollToPosition(position);
                chatAdapter.addPosition(emMessage);
            }
            return false;
        }
    });

    EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            for (EMMessage message : messages) {
                String username = null;
                // 群组消息
                if (message.getChatType() == ChatType.GroupChat || message.getChatType() == ChatType.ChatRoom) {
                    username = message.getTo();
                } else {
                    // 单聊消息
                    username = message.getFrom();
                }
                // 如果是当前会话的消息，刷新聊天页面
                if (username.equals(toChatUsername)) {
                    chatAdapter.addPosition(message);
                    position++;
                    recyclerView.smoothScrollToPosition(position);
                }
            }
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            // 收到透传消息
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
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
        if (animatorUtil != null) animatorUtil = null;
    }
}
