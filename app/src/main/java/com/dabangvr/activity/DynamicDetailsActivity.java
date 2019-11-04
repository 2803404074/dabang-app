package com.dabangvr.activity;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dabangvr.adapter.BaseRecyclerHolder;
import com.dabangvr.adapter.RecyclerAdapterPosition;
import com.dabangvr.util.ShareUtils;
import com.dbvr.baselibrary.model.DynamicMo;
import com.dbvr.baselibrary.model.QiniuUploadFile;
import com.dbvr.baselibrary.utils.OSUtils;
import com.dbvr.baselibrary.utils.OnUploadListener;
import com.dbvr.baselibrary.utils.QiniuUploadManager;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.utils.ScreenUtils;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.dbvr.imglibrary2.utils.TDevice;
import com.dbvr.imglibrary2.widget.recyclerview.SpaceGridItemDecoration;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @BindView(R.id.et_content_chart)
    EditText editText;

    @BindView(R.id.ivLove)
    ImageView ivLove;
    @BindView(R.id.tvDzNum)
    TextView tvDzNum;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
//    }

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
        tvDzNum.setText(String.valueOf(dynamicMo.getPraisedNumber()));
        if (dynamicMo.isLove()){
            ivLove.setImageResource(R.mipmap.love_db);
        }else {
            ivLove.setImageResource(R.mipmap.love_black);
        }


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

    @OnClick({R.id.ivShare,R.id.ivBack,R.id.btn_send,R.id.ivLove})
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
            case R.id.btn_send:
                sendComment();
                break;
            case R.id.ivLove:
                int dzNum = dynamicMo.getPraisedNumber();
                if (dynamicMo.isLove()){
                    ivLove.setImageResource(R.mipmap.love_black);
                    dzNum--;
                    tvDzNum.setText(String.valueOf(dzNum));
                    dynamicMo.setLove(false);
                }else {
                    dzNum++;
                    tvDzNum.setText(String.valueOf(dzNum));
                    ivLove.setImageResource(R.mipmap.love_db);
                    dynamicMo.setLove(true);
                }
                dynamicMo.setPraisedNumber(dzNum);
                updataLoveFunction(dynamicMo.getId());
                break;
                default:break;
        }
    }

    private void updataLoveFunction(int id) {
        Map<String,Object>map = new HashMap<>();
        map.put("sayId",id);
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.praisedSay, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result){
            }
            @Override
            public void onFailed(String msg) {
                //ToastUtil.showShort(getContext(),msg);
            }
        });
    }

    private void sendComment(){
        if (StringUtils.isEmpty(editText.getText().toString())){
            return;
        }
        Map<String,Object>map = new HashMap<>();
        map.put("content",editText.getText().toString());
        map.put("sayId",dynamicMo.getId());

        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.commentSay, map,
                new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result){
                List<DynamicMo.commentMo>list = dynamicMo.getCommentVoList();
                if (list==null){
                    list = new ArrayList<>();
                }
                DynamicMo.commentMo commentMo = new DynamicMo.commentMo();
                commentMo.setContent(editText.getText().toString());
                commentMo.setNickName(SPUtils.instance(getContext()).getUser().getNickName());
                list.add(commentMo);
                dynamicMo.setCommentVoList(list);
                adapterComment.updateDataa(dynamicMo.getCommentVoList());
                editText.setText("");
            }

            @Override
            public void onFailed(String msg) {

            }
        });
    }
}
