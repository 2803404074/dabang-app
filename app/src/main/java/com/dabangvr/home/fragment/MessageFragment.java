package com.dabangvr.home.fragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dabangvr.comment.activity.MainAc;
import com.dabangvr.comment.adapter.BaseRecyclerHolder;
import com.dabangvr.comment.adapter.RecyclerAdapterPosition;
import com.dabangvr.databinding.FragmentMessageBinding;
import com.dabangvr.im.receiver.MessReceiver;
import com.dabangvr.im.service.MessageService;
import com.dabangvr.im.ChatActivity;
import com.dabangvr.user.activity.FollowActivity;
import com.dbvr.baselibrary.utils.DialogUtil;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.view.BaseFragment;
import com.dbvr.baselibrary.view.BaseFragmentBinding;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hyphenate.EMContactListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.DateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import butterknife.BindView;

/**
 * 纯列表的fragment
 */
public class MessageFragment extends BaseFragmentBinding<FragmentMessageBinding> {

    public static MessageFragment instance;
    private List<EMConversation> conversationList = new ArrayList<>();
    private RecyclerAdapterPosition adapter;

    @Override
    public int layoutId() {
        return R.layout.fragment_message;
    }

    @Override
    public void initView(FragmentMessageBinding fragmentMessageBinding) {
        instance = this;
        mBinding.ivFollow.setOnClickListener((view)->{
            goTActivity(FollowActivity.class,null);
        });
    }

    public void upMess(){
        loadConversationList();
        setView();
    }

    private void setView(){
        if (conversationList!=null && conversationList.size()>0){
            mBinding.tvTips.setVisibility(View.GONE);
        }
        mBinding.recyclerMess.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecyclerAdapterPosition<EMConversation>(getContext(), conversationList, R.layout.my_mess_recyitem) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, int position, EMConversation conversation) {
                // EMMessage emMessage = conversation.getLatestMessageFromOthers();
                EMMessage emMessage2 = conversation.getLatestMessageFromOthers();
                try {
                    SimpleDraweeView sdvHead = holder.getView(R.id.avatar);
                    sdvHead.setImageURI(emMessage2.getStringAttribute("head"));
                    holder.setText(R.id.name, emMessage2.getFrom());
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                if (conversation.getUnreadMsgCount() > 0) {
                    // 显示与此用户的消息未读数
                    TextView tvNumber = holder.getView(R.id.unread_msg_number);
                    tvNumber.setText(String.valueOf(conversation.getUnreadMsgCount()));
                    tvNumber.setVisibility(View.VISIBLE);
                } else {
                    holder.getView(R.id.unread_msg_number).setVisibility(View.INVISIBLE);
                }
                if (conversation.getAllMsgCount() != 0) {
                    // 把最后一条消息的内容作为item的message内容
                    EMMessage lastMessage = conversation.getLastMessage();
                    //如果是图片
                    if (lastMessage.getType() == EMMessage.Type.IMAGE){
                        holder.setText(R.id.message, "图片...");
                    }else {
                        //msg{from:tuhao11, to:52086 body:txt:"得得得"
                        holder.setText(R.id.message, StringUtils.removeStr(lastMessage.getBody().toString()));
                    }
                    holder.setText(R.id.time, DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));

                    if (lastMessage.direct() == EMMessage.Direct.SEND && lastMessage.status() == EMMessage.Status.FAIL) {
                        holder.getView(R.id.msg_state).setVisibility(View.VISIBLE);
                    } else {
                        holder.getView(R.id.msg_state).setVisibility(View.GONE);
                    }
                }
            }
        };
        mBinding.recyclerMess.setAdapter(adapter);

        adapter.setOnItemClickListener((view, position) -> {
            EMConversation conversation = (EMConversation) adapter.getData().get(position);
            EMMessage emMessage = conversation.getLastMessage();
            //mainactivity角标消息数量
            MainAc.mainInstance.setMessageCount(conversation.getUnreadMsgCount());

            // 进入聊天页面
            Intent intent = new Intent(getContext(), ChatActivity.class);
            if (!emMessage.getFrom().equals("admin")){
                intent.putExtra("hyId", emMessage.getTo());//自己
            }else {
                intent.putExtra("hyId", emMessage.getFrom());//对方
            }
            intent.putExtra("nickName", emMessage.getUserName());
            startActivity(intent);
        });

        adapter.setonLongItemClickListener((view, position) -> {
            DialogUtil.getInstance(getActivity()).show(R.layout.dialog_tip, view1 -> {
                TextView title = view1.findViewById(R.id.tv_title);
                title.setText("确定删除"+conversationList.get(position).getLatestMessageFromOthers().getFrom()+"的会话吗？");
                view1.findViewById(R.id.tvConfirm).setOnClickListener((view2)->{
                    EMClient.getInstance().chatManager().deleteConversation(conversationList.get(position).getLatestMessageFromOthers().getUserName(), true);
                    conversationList.remove(position);
                    adapter.updateDataa(conversationList);
                    if (conversationList == null || conversationList.size() == 0){
                        mBinding.tvTips.setVisibility(View.VISIBLE);
                    }
                    DialogUtil.getInstance(getActivity()).des();
                });

                view1.findViewById(R.id.tvCancel).setOnClickListener((view2)->{
                    DialogUtil.getInstance(getActivity()).des();
                });
            });
        });
    }

    @Override
    public void initData() {

    }
    /**
     * 获取会话列表
     *
     * @return
     */
    protected void loadConversationList() {
        // 获取所有会话，包括陌生人
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        // 过滤掉messages size为0的conversation
        /**
         * 如果在排序过程中有新消息收到，lastMsgTime会发生变化 影响排序过程，Collection.sort会产生异常
         * 保证Conversation在Sort过程中最后一条消息的时间不变 避免并发问题
         */
        List<Pair<Long, EMConversation>> sortList = new ArrayList<>();
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    sortList.add(
                            new Pair<>(conversation.getLastMessage().getMsgTime(), conversation));
                }
            }
        }
        try {
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        conversationList.clear();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            if (null == sortItem.second.getLatestMessageFromOthers())continue;
            if (sortItem.second.getLatestMessageFromOthers().getFrom().equals("admin")){
                sortItem.second.getLatestMessageFromOthers().setAttribute("head","http://pili-clickplay.vrzbgw.com/application.png");
            }
            conversationList.add(sortItem.second);
        }
    }

    /**
     * 根据最后一条消息的时间排序
     *
     * @param
     */
    private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
        Collections.sort(conversationList, (con1, con2) -> {
            if (con1.first == con2.first) {
                return 0;
            } else if (con2.first > con1.first) {
                return 1;
            } else {
                return -1;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadConversationList();
        if (conversationList == null || conversationList.size() == 0){
            if (mBinding.tvTips!=null){
                mBinding.tvTips.setVisibility(View.VISIBLE);
            }
        }
        setView();
    }
}
