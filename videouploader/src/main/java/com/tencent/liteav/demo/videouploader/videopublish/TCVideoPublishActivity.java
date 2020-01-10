package com.tencent.liteav.demo.videouploader.videopublish;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dbvr.baselibrary.adapter.BaseRecyclerHolder;
import com.dbvr.baselibrary.adapter.RecyclerAdapter;
import com.dbvr.baselibrary.model.TagMo;
import com.dbvr.baselibrary.utils.ToastUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.httplibrart.constans.DyUrl;
import com.dbvr.httplibrart.utils.ObjectCallback;
import com.dbvr.httplibrart.utils.OkHttp3Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.liteav.basic.log.TXCLog;
import com.tencent.liteav.demo.videouploader.R;
import com.tencent.liteav.demo.videouploader.common.utils.TCConstants;
import com.tencent.liteav.demo.videouploader.common.view.VideoWorkProgressFragment;
import com.tencent.liteav.demo.videouploader.videopublish.server.PublishSigListener;
import com.tencent.liteav.demo.videouploader.videopublish.server.ReportVideoInfoListener;
import com.tencent.liteav.demo.videouploader.videopublish.server.VideoDataMgr;
import com.tencent.liteav.demo.videouploader.videoupload.TXUGCPublish;
import com.tencent.liteav.demo.videouploader.videoupload.TXUGCPublishTypeDef;
import com.tencent.rtmp.ITXVodPlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLog;
import com.tencent.rtmp.TXVodPlayConfig;
import com.tencent.rtmp.TXVodPlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tencent.ugc.TXVideoInfoReader;

import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vinsonswang on 2018/3/26.
 */

public class TCVideoPublishActivity extends FragmentActivity implements View.OnClickListener, ITXVodPlayListener {
    private final String TAG = "TCVideoPublishActivity";
    private LinearLayout llBack;
    private String signature;
    private String mVideoPath, mCoverImagePath;
    private EditText mEtVideoTitle;
    private ImageView mIvCover;
    private Button mBtnPublish;
    private TXCloudVideoView mTXCloudVideoView;
    private VideoWorkProgressFragment mWorkLoadingProgress; // 进度
    private TXUGCPublish mTXugcPublish;
    private TXVodPlayer mTXVodPlayer;
    private TXVodPlayConfig mTXPlayConfig = null;
    private String mTitleStr;
    private boolean isCancelPublish;
    private PublishSigListener mPublishSiglistener;
    private ReportVideoInfoListener mReportVideoInfoListener;

    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private List<TagMo> mTypeList = new ArrayList<>();
    private String typeId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_publish);

        initView();

        initData();

        getType();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startPlay();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPlay(false);
    }

    private void startPlay() {
        mTXVodPlayer.setPlayerView(mTXCloudVideoView);
        mTXVodPlayer.enableHardwareDecode(false);
        mTXVodPlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
        mTXVodPlayer.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION);
        mTXVodPlayer.setConfig(mTXPlayConfig);
        mTXVodPlayer.setVodListener(this);
        mTXVodPlayer.setLoop(true);

        mTXVodPlayer.startPlay(mVideoPath);
    }

    private void initData() {
        mVideoPath = getIntent().getStringExtra(TCConstants.VIDEO_RECORD_VIDEPATH);
        mCoverImagePath = "/sdcard/cover.jpg";
        final Bitmap coverBitmap = TXVideoInfoReader.getInstance().getSampleImage(0, mVideoPath);
        if(coverBitmap != null){
            mIvCover.setImageBitmap(coverBitmap);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    saveBitmap(coverBitmap, mCoverImagePath);
                }
            }).start();
        }

        mTXVodPlayer = new TXVodPlayer(this);
        mTXPlayConfig = new TXVodPlayConfig();

        mTXugcPublish = new TXUGCPublish(this.getApplicationContext(), "customID");

        initListener();
    }

    private void getType(){
        OkHttp3Utils.getInstance(this).doPostJson(DyUrl.getOnlineCategorys, null, new ObjectCallback<String>(this) {
            @Override
            public void onUi(String result) {
                mTypeList = new Gson().fromJson(result, new TypeToken<List<TagMo>>() {
                }.getType());
                if (mTypeList != null && mTypeList.size() > 0) {
                    mTypeList.get(0).setCheck(true);
                    adapter = new RecyclerAdapter<TagMo>(TCVideoPublishActivity.this, mTypeList, R.layout.item_txt) {
                        @Override
                        public void convert(Context mContext, BaseRecyclerHolder holder, TagMo o) {
                            TextView checkBox = holder.getView(R.id.cb_txt);
                            checkBox.setText(o.getName());
                            if (o.isCheck()) {
                                checkBox.setBackgroundResource(R.drawable.shape_db);
                                checkBox.setTextColor(getResources().getColor(R.color.colorWhite));
                                typeId = o.getId();
                            } else {
                                checkBox.setBackgroundResource(R.drawable.shape_gray_w);
                                checkBox.setTextColor(getResources().getColor(R.color.textTitle));
                            }
                        }
                    };
                    recyclerView.setAdapter(adapter);
                    adapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            for (int i = 0; i < mTypeList.size(); i++) {
                                if (i == position) {
                                    mTypeList.get(i).setCheck(true);
                                } else {
                                    mTypeList.get(i).setCheck(false);
                                }
                            }
                            adapter.updateDataa(mTypeList);
                        }
                    });
                }else {
                    mBtnPublish.setText("未完成商户认证，请到“商户/入驻”进行入驻");
                    mBtnPublish.setClickable(false);
                }
            }
            @Override
            public void onFailed(String msg) {
                mBtnPublish.setText("未完成商户认证，请到“商户/入驻”进行入驻");
                mBtnPublish.setClickable(false);
            }
        });
    }

    private void initListener() {
        mPublishSiglistener = new PublishSigListener() {
            @Override
            public void onSuccess(String signatureStr) {
                signature = signatureStr;
                publish();
            }

            @Override
            public void onFail(final int errCode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mWorkLoadingProgress != null && mWorkLoadingProgress.isAdded()) {
                            mWorkLoadingProgress.dismiss();
                        }
                        Toast.makeText(TCVideoPublishActivity.this, "err code = " + errCode, Toast.LENGTH_SHORT).show();
                        startPlay();
                    }
                });
            }
        };
        VideoDataMgr.getInstance().setPublishSigListener(mPublishSiglistener);

        mReportVideoInfoListener = new ReportVideoInfoListener() {
            @Override
            public void onFail(int errCode) {
                TXCLog.e(TAG, "reportVideoInfo, report video info fail");
            }

            @Override
            public void onSuccess() {
                TXCLog.i(TAG, "reportVideoInfo, report video info success");
            }
        };
        VideoDataMgr.getInstance().setReportVideoInfoListener(mReportVideoInfoListener);
    }

    private void initView() {


        recyclerView = findViewById(R.id.recy_type);
        recyclerView.setLayoutManager(new GridLayoutManager(this,4));
        mBtnPublish = (Button) findViewById(R.id.btn_publish);
        mEtVideoTitle = (EditText) findViewById(R.id.et_video_title);
        mTXCloudVideoView = (TXCloudVideoView) findViewById(R.id.video_view);
        mTXCloudVideoView.disableLog(true);
        mIvCover = (ImageView) findViewById(R.id.iv_video_cover);
        llBack = (LinearLayout) findViewById(R.id.back_ll);

        mBtnPublish.setOnClickListener(this);
        llBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_publish) {
            publishVideo();
        } else if (id == R.id.back_ll) {
            finish();
        }
    }

    private void publishVideo() {
        stopPlay(false);
        if (mWorkLoadingProgress == null) {
            initWorkLoadingProgress();
        }
        mWorkLoadingProgress.setProgress(0);
        mWorkLoadingProgress.show(getSupportFragmentManager(), "progress_dialog");
        getPublishSig();
        isCancelPublish = false;
    }

    private void getPublishSig() {
        VideoDataMgr.getInstance().getPublishSig(this);
    }

    private void publish() {
        mTXugcPublish.setListener(new TXUGCPublishTypeDef.ITXVideoPublishListener() {
            @Override
            public void onPublishProgress(long uploadBytes, long totalBytes) {
                TXLog.d(TAG, "onPublishProgress [" + uploadBytes + "/" + totalBytes + "]");
                if(isCancelPublish){
                    return;
                }
                mWorkLoadingProgress.setProgress((int) ((uploadBytes * 100) / totalBytes));
            }

            @Override
            public void onPublishComplete(TXUGCPublishTypeDef.TXPublishResult result) {
                TXLog.d(TAG, "onPublishComplete [" + result.retCode + "/" + (result.retCode == 0 ? result.videoURL : result.descMsg) + "]");
                if (mWorkLoadingProgress != null && mWorkLoadingProgress.isAdded()) {
                    mWorkLoadingProgress.dismiss();
                }

                if(isCancelPublish){
                    return;
                }

                // 这里可以把上传返回的视频信息以及自定义的视频信息上报到自己的业务服务器
                reportVideoInfo(result);

                // 注意：如果取消发送时，是取消的剩余未上传的分片发送，如果视频比较小，分片已经进入任务队列了是无法取消的。此时不跳转到下一个界面。
//                if (result.retCode == TXUGCPublishTypeDef.PUBLISH_RESULT_OK) {
//                    Toast.makeText(TCVideoPublishActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(TCVideoPublishActivity.this, SuperPlayerActivity.class);
//                    intent.putExtra(TCConstants.PLAYER_DEFAULT_VIDEO, false);
//                    intent.putExtra(TCConstants.PLAYER_VIDEO_ID, result.videoId);
//                    intent.putExtra(TCConstants.PLAYER_VIDEO_NAME, mTitleStr);
//                    startActivity(intent);
//
//                } else {
//                    if (result.descMsg.contains("java.net.UnknownHostException") || result.descMsg.contains("java.net.ConnectException")) {
//                        Toast.makeText(TCVideoPublishActivity.this, "网络连接断开，视频上传失败" + result.descMsg, Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(TCVideoPublishActivity.this, "发布失败，errCode = " + result.retCode + ", msg = " + result.descMsg, Toast.LENGTH_SHORT).show();
//                    }
//                    startPlay();
//                }
            }
        });

        TXUGCPublishTypeDef.TXPublishParam param = new TXUGCPublishTypeDef.TXPublishParam();
        // signature计算规则可参考 https://www.qcloud.com/document/product/266/9221
        param.signature = signature;
        param.videoPath = mVideoPath;
        param.coverPath = mCoverImagePath;
        mTitleStr = mEtVideoTitle.getText().toString();
        if(TextUtils.isEmpty(mTitleStr)){
            mTitleStr = "测试";
        }
        param.fileName = mTitleStr;
        mTXugcPublish.publishVideo(param);
    }

    /**
     * 视频上传成功后返回后端数据
     * @param result
     */
    private void reportVideoInfo(TXUGCPublishTypeDef.TXPublishResult result) {
        Map<String,Object>map = new HashMap<>();
        map.put("title",mEtVideoTitle.getText().toString());
        map.put("videoUrl",result.getVideoURL());
        map.put("coverUrl",result.getCoverURL());
        map.put("label",typeId);
        OkHttp3Utils.getInstance(this).doPostJson(DyUrl.saveVideo, map, new ObjectCallback<String>(this) {
            @Override
            public void onUi(String result) throws JSONException {
                ToastUtil.showShort(TCVideoPublishActivity.this,"发布成功");
                finish();
            }
            @Override
            public void onFailed(String msg) {
                ToastUtil.showShort(TCVideoPublishActivity.this,"发布失败");
                finish();
            }
        });

        //VideoDataMgr.getInstance().reportVideoInfo(result.videoId, "腾讯云");
    }

    private void initWorkLoadingProgress() {
        if (mWorkLoadingProgress == null) {
            mWorkLoadingProgress = VideoWorkProgressFragment.newInstance("发布中...");
            mWorkLoadingProgress.setOnClickStopListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTXugcPublish != null) {
                        mTXugcPublish.canclePublish();
                        isCancelPublish = true;
                        mWorkLoadingProgress.setProgress(0);
                        mWorkLoadingProgress.dismiss();
                        startPlay();
                    }
                }
            });
        }
        mWorkLoadingProgress.setProgress(0);
    }

    protected void stopPlay(boolean clearLastFrame) {
        if (mTXVodPlayer != null) {
            mTXVodPlayer.setVodListener(null);
            mTXVodPlayer.stopPlay(clearLastFrame);
        }
    }

    @Override
    public void onPlayEvent(TXVodPlayer player, int event, Bundle param) {

    }

    @Override
    public void onNetStatus(TXVodPlayer player, Bundle status) {

    }

    public static void saveBitmap(Bitmap bitmap, String filePath) {
        File f = new File(filePath);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VideoDataMgr.getInstance().setReportVideoInfoListener(null);
        VideoDataMgr.getInstance().setPublishSigListener(null);
        if (mWorkLoadingProgress != null) {
            mWorkLoadingProgress.setOnClickStopListener(null);
        }
    }
}
