package com.dabangvr.fragment.other;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.EditText;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dabangvr.R;
import com.dabangvr.adapter.BaseRecyclerHolder;
import com.dabangvr.adapter.RecyclerAdapter;
import com.dabangvr.util.ShareUtils;
import com.dbvr.baselibrary.model.DynamicMo;
import com.dbvr.baselibrary.model.QiniuUploadFile;
import com.dbvr.baselibrary.model.TagMo;
import com.dbvr.baselibrary.model.UserMess;
import com.dbvr.baselibrary.utils.BottomDialogUtil2;
import com.dbvr.baselibrary.utils.OnUploadListener;
import com.dbvr.baselibrary.utils.QiniuUploadManager;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.utils.StringUtils;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.view.BaseFragment;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.dbvr.imglibrary2.model.Image;
import com.dbvr.imglibrary2.utils.TDevice;
import com.dbvr.imglibrary2.widget.recyclerview.SpaceGridItemDecoration;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import butterknife.BindView;

/**
 * 纯列表的fragment
 */
public class UserDynamicFragment extends BaseFragment {

    protected int position;

    @BindView(R.id.recycler_head)
    RecyclerView recyclerView;

    private List<DynamicMo> mData = new ArrayList<>();
    private RecyclerAdapter adapter;

    @Override
    public int layoutId() {
        return R.layout.recy_no_bg;
    }


    private HeadCallBack headCallBack;

    public interface HeadCallBack {
        void onclickCallBack();
    }

    public UserDynamicFragment() {

    }


    public UserDynamicFragment(HeadCallBack headCallBack,int position) {
        this.headCallBack = headCallBack;
        this.position = position;
    }

    @Override
    public void initView() {
        setLoaddingView(true);
        if (position == 0){
            initBra();
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new SpaceGridItemDecoration((int) TDevice.dipToPx(getResources(), 3)));
        adapter = new RecyclerAdapter<DynamicMo>(getContext(), mData, R.layout.item_user_dynamic) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, DynamicMo dynamicMo) {
                //内容
                holder.setText(R.id.tvContent,dynamicMo.getContent());
                //头像
                SimpleDraweeView simpleDraweeView = holder.getView(R.id.sdvHead);
                simpleDraweeView.setImageURI(dynamicMo.getHeadUrl());
                simpleDraweeView.setOnClickListener(view -> {
                    if (headCallBack != null) {
                        headCallBack.onclickCallBack();
                    }
                });
                //时间
                holder.setText(R.id.tvDate,dynamicMo.getSendTime());
                //评论数量
                holder.setText(R.id.tvCommentNum,dynamicMo.getCommentNumber());

                //点击评论
                holder.getView(R.id.ivComment).setOnClickListener((view)->{
                    BottomDialogUtil2.getInstance(getActivity()).showLive(R.layout.dialog_input, view1 -> {
                        EditText editText = view1.findViewById(R.id.et_content_chart);
                        view1.findViewById(R.id.btn_send).setOnClickListener(view2 -> {
                            if (!StringUtils.isEmpty(editText.getText().toString())){
                                BottomDialogUtil2.getInstance(getActivity()).dess();
                            }
                        });
                    });
                });
                //点击分享
                holder.getView(R.id.ivShare).setOnClickListener((view)->{
                    ShareUtils.getInstance(getContext()).startShare(
                            StringUtils.isEmptyTxt(dynamicMo.getNickName())+"发表了新动态",
                            dynamicMo.getContent(),
                            dynamicMo.getPicUrl().get(0),
                            "www.baidu.com");
                });
                if (dynamicMo.getPicUrl()!=null){
                    RecyclerView recyclerViewx = holder.getView(R.id.recycle_img);
                    recyclerViewx.setNestedScrollingEnabled(false);
                    recyclerViewx.setLayoutManager(new GridLayoutManager(getContext(), 3));
                    recyclerViewx.addItemDecoration(new SpaceGridItemDecoration((int) TDevice.dipToPx(getResources(), 1)));


                    RecyclerAdapter adapterx = new RecyclerAdapter<String>(getContext(), dynamicMo.getPicUrl(), R.layout.selected_image_item) {
                        @Override
                        public void convert(Context mContext, BaseRecyclerHolder holder, String o) {
                            if (dynamicMo.isNews()){
                                holder.setImageByPath(R.id.iv_selected_image,o);
                            }else {
                                holder.setImageByUrl(R.id.iv_selected_image, o);
                            }
                        }
                    };
                    recyclerViewx.setAdapter(adapterx);
                }
            }
        };
        recyclerView.setAdapter(adapter);
    }


    private void initBra() {
        IntentFilter filter = new IntentFilter("android.intent.action.DYNAMIC");
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, filter);
    }
    private MessageBroadcastReceiver receiver = new MessageBroadcastReceiver();
    private class MessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.requireNonNull(intent.getAction()).equals("android.intent.action.DYNAMIC")){

                String content = intent.getStringExtra("content");
                double mJd = intent.getDoubleExtra("mJd",0);
                double mWd = intent.getDoubleExtra("mWd",0);

                ArrayList<Image>mSelectedImages = intent.getParcelableArrayListExtra("img");
                List<String>list = new ArrayList<>();
                for (int i = 0; i < mSelectedImages.size(); i++) {
                    list.add(mSelectedImages.get(i).getPath());
                }

                new Thread(()->{
                    getQiniuToken(content,list,mJd,mWd);
                }).start();
                UserMess userMess = SPUtils.instance(getContext()).getUser();
                DynamicMo dynamicMo = new DynamicMo();
                dynamicMo.setContent(content);
                dynamicMo.setNews(true);
                dynamicMo.setPicUrl(list);
                dynamicMo.setSendTime("刚刚");
                dynamicMo.setCommentNumber("0");
                dynamicMo.setHeadUrl(userMess.getHeadUrl());
                dynamicMo.setNickName(userMess.getNickName());
                mData.add(0,dynamicMo);
                adapter.updateDataa(mData);
            }
        }
    }

    private int page = 1;
    @Override
    public void initData() {
        Map<String,Object>map = new HashMap<>();
        map.put("page",page);
        map.put("limit",10);
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.getSayList, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result) {
                List<DynamicMo> list = new Gson().fromJson(result, new TypeToken<List<DynamicMo>>() {}.getType());
                if (list!=null&&list.size()>0){
                    mData = list;
                    if (page>1){
                        adapter.addAll(mData);
                    }else {
                        adapter.updateDataa(mData);
                    }
                }

                setLoaddingView(false);
            }

            @Override
            public void onFailed(String msg) {
                setLoaddingView(false);
            }
        });
    }
    /**
     * 获取七牛token
     */
    private void getQiniuToken(String content,List<String> img,double mJd,double mWd){
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.getUploadConfigToken, null, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result){
                QiniuUploadFile qiniuUploadFile = new Gson().fromJson(result,QiniuUploadFile.class);
                upLoadImg(qiniuUploadFile,content,img,mJd,mWd);//上传图片
            }
            @Override
            public void onFailed(String msg) {
                setLoaddingView(false);
            }
        });
    }

    /**
     * 上传动态图片
     * @param content
     * @param img
     * @param mJd
     * @param mWd
     */
    public void upLoadImg(QiniuUploadFile qiniuUploadFile,String content,List<String> img,double mJd,double mWd){
        resultPath = new ArrayList<>();
        List<QiniuUploadFile>qiniu = new ArrayList<>();
        for (int i = 0; i < img.size(); i++) {
            qiniu.add(new QiniuUploadFile(img.get(i),"dynamic-"+UUID.randomUUID(),qiniuUploadFile.getMimeType(),qiniuUploadFile.getUpLoadToken()));
        }
        QiniuUploadManager.getInstance(getContext()).upload(qiniu, new OnUploadListener() {
            @Override
            public void onStartUpload() { }
            @Override
            public void onUploadProgress(String key, double percent) { }
            @Override
            public void onUploadFailed(String key, String err) { }

            @Override
            public void onUploadBlockComplete(String key) {
                //上传成功一个文件
                resultPath.add(key);
            }
            @Override
            public void onUploadCompleted() {
                //上传完所有文件
                publishDynamic(content,mJd,mWd);
            }
            @Override
            public void onUploadCancel() {//取消上传
            }
        });
    }

    private List<String>resultPath;
    private void publishDynamic(String content,double mJd,double mWd){
        Map<String,Object>map = new HashMap<>();
        map.put("longitude",mJd);
        map.put("latitude",mWd);
        map.put("content",content);
        map.put("imgUrl",new Gson().toJson(resultPath));
        OkHttp3Utils.getInstance(getContext()).doPostJson(DyUrl.sendSay, map, new ObjectCallback<String>(getContext()) {
            @Override
            public void onUi(String result){
                ToastUtil.showShort(getContext(),"跳跳等级增长+1");
            }

            @Override
            public void onFailed(String msg) {
                ToastUtil.showShort(getContext(),msg);
            }
        });

    }
}
