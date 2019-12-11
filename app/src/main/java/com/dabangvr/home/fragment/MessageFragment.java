package com.dabangvr.home.fragment;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dabangvr.comment.adapter.BaseRecyclerHolder;
import com.dabangvr.comment.adapter.RecyclerAdapterPosition;
import com.dabangvr.im.ChatActivity;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.view.BaseFragment;
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
public class MessageFragment extends BaseFragment{
    @BindView(R.id.recycler_mess)
    RecyclerView recyclerView;
    @BindView(R.id.tvTips)
    TextView tvTips;
    private List<EMConversation> conversationList = new ArrayList<>();
    private RecyclerAdapterPosition adapter;

    @Override
    public int layoutId() {
        return R.layout.fragment_message;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter!=null){
            loadConversationList();
            adapter.updateDataa(conversationList);
            if (conversationList == null || conversationList.size()==0){
                tvTips.setVisibility(View.VISIBLE);
            }else {
                tvTips.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void initView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecyclerAdapterPosition<EMConversation>(getContext(), conversationList, R.layout.my_mess_recyitem) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, int position, EMConversation conversation) {
                // EMMessage emMessage = conversation.getLatestMessageFromOthers();
                EMMessage emMessage2 = conversation.getLatestMessageFromOthers();
                try {
                    SimpleDraweeView sdvHead = holder.getView(R.id.avatar);
                    sdvHead.setImageURI(emMessage2.getStringAttribute("head"));
                    holder.setText(R.id.name, emMessage2.getStringAttribute("nickName"));
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
                    //msg{from:tuhao11, to:52086 body:txt:"得得得"
                    holder.setText(R.id.message, StringUtils.removeStr(lastMessage.getBody().toString()));
                    holder.setText(R.id.time, DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));

                    if (lastMessage.direct() == EMMessage.Direct.SEND && lastMessage.status() == EMMessage.Status.FAIL) {
                        holder.getView(R.id.msg_state).setVisibility(View.VISIBLE);
                    } else {
                        holder.getView(R.id.msg_state).setVisibility(View.GONE);
                    }
                }
            }
        };
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener((view, position) -> {
            EMConversation conversation = (EMConversation) adapter.getData().get(position);
            EMMessage emMessage = conversation.getLastMessage();
            String username = emMessage.getUserName();
            // 进入聊天页面
            try {
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("hyId", username);
                intent.putExtra("nickName", emMessage.getStringAttribute("nickName"));
                intent.putExtra("head", emMessage.getStringAttribute("head"));
                startActivity(intent);
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        });
    }


    @Override
    public void initData() {
        EMClient.getInstance().contactManager().setContactListener(new EMContactListener() {

            @Override
            public void onContactInvited(String username, String reason) {
                //收到好友邀请
                ToastUtil.showShort(getContext(),username+"关注了你");
            }

            @Override
            public void onFriendRequestAccepted(String s) {

            }

            @Override
            public void onFriendRequestDeclined(String s) {

            }

            @Override
            public void onContactDeleted(String username) {
                //被删除时回调此方法
            }


            @Override
            public void onContactAdded(String username) {
                //增加了联系人时回调此方法
            }
        });
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
            try {
                EMMessage emMessage = sortItem.second.getLastMessage();
                if (!StringUtils.isEmpty(emMessage.getStringAttribute("nickName"))){
                    conversationList.add(sortItem.second);
                }
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
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

}
