package com.dabangvr.user.fragment;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dabangvr.comment.adapter.BaseRecyclerHolder;
import com.dabangvr.comment.adapter.RecyclerAdapter;
import com.dbvr.baselibrary.model.VideoMo;
import com.dbvr.baselibrary.view.BaseFragment;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.dabangvr.play.video.activity.UserVideoActivity;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * 纯列表的fragment  作品
 */
public class UserVideoFragment extends BaseFragment {

    @BindView(R.id.recycler_head)
    RecyclerView recyclerView;
    @BindView(R.id.tvText)
    TextView tvTextTips;

    private List<VideoMo>mData = new ArrayList<>();
    private RecyclerAdapter adapter;


    @Override
    public int layoutId() {
        return R.layout.recy_no_bg;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void initView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecyclerAdapter<VideoMo>(getContext(),mData,R.layout.item_user_video) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, VideoMo o) {
                holder.setImageByUrl(R.id.miv_view,o.getCoverUrl());
                holder.setText(R.id.tvCnum,o.getCommentNum());
                holder.setText(R.id.tvLnum,o.getPraseCount());
                holder.setText(R.id.tvTitle,o.getTitle());
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((view, position) -> {
            VideoMo videoMo = (VideoMo) adapter.getData().get(position);
            Map<String,Object>map = new HashMap<>();
            map.put("head",videoMo.getHeadUrl());
            map.put("name",videoMo.getNickName());
            map.put("title",videoMo.getTitle());
            map.put("comment",videoMo.getCommentNum());
            map.put("like",videoMo.getPraseCount());
            map.put("url",videoMo.getVideoUrl());
            goTActivity(UserVideoActivity.class, map);
        });
    }

    @Override
    public void initData() {
        Map<String,Object> map = new HashMap<>();
        map.put("page",1);
        map.put("limit",10);
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.getLiveShortVideoList, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) throws JSONException {
                List<VideoMo> list = new Gson().fromJson(result, new TypeToken<List<VideoMo>>() {
                }.getType());
                if (list!=null && list.size()>0){
                    mData = list;
                    adapter.updateData(mData);
                }



                getActivity().runOnUiThread(()->{
                    if (tvTextTips!=null){
                        tvTextTips.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onFailed(String msg) {
                getActivity().runOnUiThread(()->{
                    if (tvTextTips!=null){
                        tvTextTips.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }
}
