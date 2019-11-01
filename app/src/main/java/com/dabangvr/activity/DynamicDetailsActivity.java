package com.dabangvr.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dabangvr.adapter.BaseRecyclerHolder;
import com.dabangvr.adapter.RecyclerAdapterPosition;
import com.dabangvr.util.ShareUtils;
import com.dbvr.baselibrary.model.DynamicMo;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;
import com.dbvr.imglibrary2.utils.TDevice;
import com.dbvr.imglibrary2.widget.recyclerview.SpaceGridItemDecoration;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.OnClick;

public class DynamicDetailsActivity extends BaseActivity {

    @BindView(R.id.recycle_img)
    RecyclerView recyclerImg;
    @BindView(R.id.recycle_comment)
    RecyclerView recyclerComment;
    private RecyclerAdapterPosition adapterImg;
    private RecyclerAdapterPosition adapterComment;


    @BindView(R.id.sdvHead)
    SimpleDraweeView sdvHead;
    @BindView(R.id.tvNick)
    TextView tvNick;
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.tvContent)
    TextView tvContent;
    @BindView(R.id.tvCommentNum)
    TextView tvCommentNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_dynamic_details;
    }

    private DynamicMo dynamicMo;
    @Override
    public void initView() {
        String st = getIntent().getStringExtra("data");
        dynamicMo = new Gson().fromJson(st, DynamicMo.class);
        sdvHead.setImageURI(dynamicMo.getHeadUrl());
        tvNick.setText(dynamicMo.getNickName());
        tvDate.setText(dynamicMo.getSendTime());
        tvContent.setText(dynamicMo.getContent());
        tvCommentNum.setText(dynamicMo.getCommentNumber());

        recyclerImg.setLayoutManager(new GridLayoutManager(getContext(),3));
        recyclerImg.addItemDecoration(new SpaceGridItemDecoration((int) TDevice.dipToPx(getResources(), 1)));
        adapterImg = new RecyclerAdapterPosition<String>(getContext(), dynamicMo.getPicUrl(), R.layout.selected_image_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder,int position, String o) {
                if (dynamicMo.isNews()){
                    holder.setImageByPath(R.id.iv_selected_image,o);
                }else {
                    holder.setImageByUrl(R.id.iv_selected_image, o);
                }
            }
        };
        recyclerImg.setAdapter(adapterImg);


        //评论内容
        recyclerComment.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerComment.setNestedScrollingEnabled(false);
        adapterComment = new RecyclerAdapterPosition<DynamicMo.commentMo>(getContext(),
                dynamicMo.getCommentVoList(),R.layout.item_comment_dy) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, int position, DynamicMo.commentMo o) {
                holder.setText(R.id.tvUser,o.getNickName());
                holder.setText(R.id.tvMess,o.getContent());
            }
        };
        recyclerComment.setAdapter(adapterComment);

    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.ivShare,R.id.ivBack})
    public void onclick(View view){
        switch (view.getId()){
            case R.id.ivShare:
                ShareUtils.getInstance(getContext()).startShare(
                        StringUtils.isEmptyTxt(dynamicMo.getNickName())+"发表了新动态",
                        dynamicMo.getContent(),
                        dynamicMo.getPicUrl()==null?"":dynamicMo.getPicUrl().get(0),
                        "www.baidu.com");
                break;
            case R.id.ivBack:
                AppManager.getAppManager().finishActivity(this);
                break;
                default:break;
        }
    }
}
