package com.dabangvr.play.video.fragment;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dabangvr.play.video.mo.CommentMo;
import com.dabangvr.play.video.view.LazyFragment;
import com.dabangvr.play.video.view.SlideAdapter;
import com.dabangvr.play.video.view.VideoPlayRecyclerView;
import com.dbvr.baselibrary.adapter.BaseRecyclerHolder;
import com.dbvr.baselibrary.adapter.RecyclerAdapter;
import com.dbvr.baselibrary.model.MainMo;
import com.dbvr.baselibrary.model.VideoMo;
import com.dbvr.baselibrary.utils.BottomDialogUtil2;
import com.dbvr.baselibrary.utils.KeyBoardShowListener;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.utils.UserHolper;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pili.pldroid.player.widget.PLVideoTextureView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShortVideoFragment extends LazyFragment {

    private PLVideoTextureView superPlayerView;
    private VideoPlayRecyclerView recyclerView;
    private ArrayList<VideoMo> mDatas = new ArrayList<>();
    private SlideAdapter adapter;

    private boolean isFirstCome = true;
    private MainMo mainMo;//首次传递过来需要播放的短视频
    public ShortVideoFragment() {
    }

    public ShortVideoFragment(MainMo mainMo) {
        this.mainMo = mainMo;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (superPlayerView!=null){
            superPlayerView.stopPlayback();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (superPlayerView!=null){
            superPlayerView.pause();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (superPlayerView!=null){
            superPlayerView.pause();
        }
    }
    //关注、鱼、虾、蟹、贝、

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser){
            if (adapter!=null){
                if (superPlayerView!=null){
                    superPlayerView.pause();
                }
            }
        }
    }

    @Override
    protected int setContentView() {
        return R.layout.page_video;
    }

    private RecyclerView recyclerComment;
    private RecyclerAdapter commentAdapter;
    @Override
    protected void lazyLoad() {
        recyclerView = findViewById(R.id.videoPlayRecyclerView);

        adapter = new SlideAdapter<VideoMo>(getContext(),mDatas,R.layout.item_video) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder,int position, VideoMo s) {
                SimpleDraweeView sdvHead = holder.getView(R.id.sdvHead);
                sdvHead.setImageURI(s.getHeadUrl());
                holder.setImageByUrl(R.id.rlImg,s.getVideoUrl());
                holder.setText(R.id.tvNickName,s.getNickName());
                holder.setText(R.id.tvTitle,s.getTitle());
                holder.setText(R.id.tvLick,s.getPraseCount());
                holder.setText(R.id.tvCommentNum,s.getCommentNum());
                //评论
                holder.getView(R.id.ivComment).setOnClickListener((view -> {
                    BottomDialogUtil2.getInstance(getActivity()).showLive(R.layout.dialog_comment, view1 -> {

                        TextView tvShow = view1.findViewById(R.id.tvShow);
                        EditText text = view1.findViewById(R.id.etInput);
                        LinearLayout llInput = view1.findViewById(R.id.llInput);
                        view1.findViewById(R.id.tvInput).setOnClickListener(view23 -> {
                            ToastUtil.showInput(getActivity(),text);
                        });
                        view1.findViewById(R.id.tvSend).setOnClickListener((view2)->{
                            if (StringUtils.isEmpty(text.getText().toString()))return;
                            sendComment(s.getId(),text.getText().toString());
                            text.setText("");
                            tvShow.setVisibility(View.GONE);
                        });
                        view1.findViewById(R.id.ivClose).setOnClickListener(view22 -> {
                            BottomDialogUtil2.getInstance(getActivity()).dess();
                        });

                        new KeyBoardShowListener(getActivity()).setKeyboardListener(visible -> {
                            if (visible){
                                llInput.setVisibility(View.VISIBLE);
                            }else {
                                llInput.setVisibility(View.GONE);
                            }
                        },getActivity());
                        recyclerComment = view1.findViewById(R.id.recycler_com);
                        recyclerComment.setLayoutManager(new LinearLayoutManager(getContext()));
                        List<CommentMo>commentList = new ArrayList<>();
                        commentAdapter = new RecyclerAdapter<CommentMo>(getContext(),commentList,R.layout.item_video_comment) {
                            @Override
                            public void convert(Context mContext, BaseRecyclerHolder holder, CommentMo o) {
                                holder.setHeadByUrl(R.id.sdvHead,o.getHeadUrl());
                                holder.setText(R.id.tvNickName,o.getNickName());
                                holder.setText(R.id.tvContent,o.getContent());
                                holder.setText(R.id.tvDate,o.getAddTime());
                                holder.setText(R.id.tvDzNum,o.getPraseCount());
                                TextView tvDzNum =  holder.getView(R.id.tvDzNum);
                                holder.getView(R.id.ivDz).setOnClickListener(view24 -> {
                                    //点赞评论
                                    if (s.getPraseStatus()){
                                        tvDzNum.setText(String.valueOf(Integer.parseInt(tvDzNum.getText().toString())-1));
                                    }else {
                                        tvDzNum.setText(String.valueOf(Integer.parseInt(tvDzNum.getText().toString())+1));
                                    }
                                    dzCommentFunction(s.getId());
                                });
                            }
                        };
                        recyclerComment.setAdapter(commentAdapter);
                        loadComment(tvShow,s.getId());
                    });
                }));

                //点赞
                ImageView ivDz =  holder.getView(R.id.ivDz);
                if (s.getPraseStatus()){
                    ivDz.setImageResource(R.mipmap.love);
                }else {
                    ivDz.setImageResource(R.mipmap.love_red);
                }
                ivDz.setOnClickListener((view)->{
                    if (s.getPraseStatus()){
                        ivDz.setImageResource(R.mipmap.love);
                        adapter.updatePraseStatus(position,false);
                    }else {
                        ivDz.setImageResource(R.mipmap.love_red);
                        adapter.updatePraseStatus(position,true);
                    }
                    dzFucntion(s.getId());
                });

                //分享
                holder.getView(R.id.ivShare).setOnClickListener(view -> {
                    Intent intent = new Intent("com.dabang.sharex");
                    intent.putExtra("title",s.getTitle());
                    intent.putExtra("content","海跳跳推荐视频");
                    intent.putExtra("cover",s.getCoverUrl());
                    intent.putExtra("link","www.baidu.com");
                    getActivity().sendBroadcast(intent);
                    adapter.startVideo();
                });
            }
        };
        adapter.setSelect((textureView,position) -> {
            this.superPlayerView = textureView;
            superPlayerView.start();
        });
        recyclerView.setAdapter(adapter);
        getVideoData();
    }

    /**
     * 点赞评论
     */
    private void dzCommentFunction(String cId) {
        Map<String,Object>map = new HashMap<>();
        map.put("commentId",cId);
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.praseShortVideoComment, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) throws JSONException { }
            @Override
            public void onFailed(String msg) { }
        });
    }

    private void sendComment(String vId,String content) {
        Map<String,Object>map = new HashMap<>();
        map.put("videoId",vId);
        map.put("content",content);
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.commentShortVideo, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) throws JSONException {
                CommentMo commentMo = new CommentMo();
                commentMo.setNickName(UserHolper.getUserHolper(getContext()).getUserMess().getNickName());
                commentMo.setAddTime("刚刚");
                commentMo.setContent(content);
                commentMo.setHeadUrl(UserHolper.getUserHolper(getContext()).getUserMess().getHeadUrl());
                commentMo.setPraseCount("0");
                commentMo.setUserId(UserHolper.getUserHolper(getContext()).getUserMess().getId());
                commentAdapter.addPosition(commentMo,0);
            }

            @Override
            public void onFailed(String msg) {

            }
        });
    }

    private int commentPage = 1;
    private void loadComment(TextView textView,String vId) {
        Map<String,Object>map = new HashMap<>();
        map.put("videoId",vId);
        map.put("page",commentPage);
        map.put("limit",20);
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.getShortVideoComment, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) throws JSONException {
                List<CommentMo> list = new Gson().fromJson(result,
                        new TypeToken<List<CommentMo>>() {}.getType());
                if (list!=null && list.size()>0){
                    commentAdapter.updateDataa(list);
                    textView.setVisibility(View.GONE);
                }else {
                    textView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailed(String msg) {

            }
        });
    }

    /**
     * 点赞视频
     * @param vId
     */
    private void dzFucntion(String vId){
        Map<String,Object>map = new HashMap<>();
        map.put("videoId",vId);
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.praseShortVideo, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) throws JSONException {

            }

            @Override
            public void onFailed(String msg) {

            }
        });
    }

    private int page = 1;
    private void getVideoData() {
        Map<String,Object>map = new HashMap<>();
        map.put("page",page);
        map.put("limit",10);
        //推荐
        if (mainMo!=null){
            map.put("label",mainMo.getLabelId());
            if (isFirstCome){
                VideoMo videoMo = new VideoMo();
                videoMo.setCommentNum(mainMo.getCommentNum());
                videoMo.setHeadUrl(mainMo.getHeadUrl());
                videoMo.setId(String.valueOf(mainMo.getId()));
                videoMo.setPraseCount(mainMo.getPraseCount());
                videoMo.setTitle(mainMo.getTitle());
                videoMo.setNickName(mainMo.getNickName());
                videoMo.setUserId(mainMo.getUserId());
                videoMo.setVideoUrl(mainMo.getVideoUrl());
                mDatas.add(videoMo);
                adapter.updateData(mDatas);
                isFirstCome = false;
            }
        }else {//附近

        }
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.getLiveShortVideoList, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) throws JSONException {
                List<VideoMo> list = new Gson().fromJson(result, new TypeToken<List<VideoMo>>() {
                }.getType());
                if (list!=null && list.size()>0){
                    mDatas.addAll(list);
                    adapter.updateData(mDatas);
                }
            }

            @Override
            public void onFailed(String msg) {

            }
        });
    }

    @Override
    protected void stopLoad() {
        super.stopLoad();
        if (superPlayerView!=null){
            superPlayerView.stopPlayback();
        }
    }
}
