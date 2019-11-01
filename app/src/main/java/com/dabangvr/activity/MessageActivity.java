package com.dabangvr.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dabangvr.adapter.BaseRecyclerHolder;
import com.dabangvr.adapter.RecyclerAdapter;
import com.dabangvr.im.ChatActivity;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.view.BaseActivity;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.util.DateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 消息fragment
 */
public class MessageActivity extends BaseActivity {

    @BindView(R.id.recycler_mess)
    RecyclerView recyclerView;
    private List<EMConversation> conversationList = new ArrayList<EMConversation>();
    private RecyclerAdapter adapter;

    @Override
    public int setLayout() {
        return R.layout.activity_message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //用来设置整体下移，状态栏沉浸
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public void initView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        conversationList.addAll(loadConversationList());
        adapter = new RecyclerAdapter<EMConversation>(getContext(), conversationList, R.layout.my_mess_recyitem) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, EMConversation conversation) {
                EMMessage emMessage = conversation.getLastMessage();

                holder.setText(R.id.name, "与 " + emMessage.getUserName() + " 的会话");
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

        adapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                EMConversation conversation = (EMConversation) adapter.getData().get(position);
                EMMessage emMessage = conversation.getLastMessage();
                String username = emMessage.getUserName();
                // 进入聊天页面
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.ivDz,R.id.ivComment,R.id.ivFans})
    public void onclick(View view){
        switch (view.getId()){
            case R.id.ivDz:
                Map<String,Object>map = new HashMap<>();
                map.put("tag","dz");
                goTActivity(GetDzActivity.class,map);
                break;
            case R.id.ivComment:
                Map<String,Object>map2 = new HashMap<>();
                map2.put("tag","comment");
                goTActivity(GetDzActivity.class,map2);
                break;
            case R.id.ivFans:
                goTActivity(FansAndFollowActivity.class,null);
                break;
                default:break;
        }
    }

    /**
     * 获取会话列表
     *
     * @return
     */
    protected List<EMConversation> loadConversationList() {
        // 获取所有会话，包括陌生人
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        // 过滤掉messages size为0的conversation
        /**
         * 如果在排序过程中有新消息收到，lastMsgTime会发生变化 影响排序过程，Collection.sort会产生异常
         * 保证Conversation在Sort过程中最后一条消息的时间不变 避免并发问题
         */
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    sortList.add(
                            new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                }
            }
        }
        try {
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }

    /**
     * 根据最后一条消息的时间排序
     *
     * @param
     */
    private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
            @Override
            public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {

                if (con1.first == con2.first) {
                    return 0;
                } else if (con2.first > con1.first) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
    }
}
