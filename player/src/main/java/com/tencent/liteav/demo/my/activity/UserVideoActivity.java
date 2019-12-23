package com.tencent.liteav.demo.my.activity;

import android.widget.ProgressBar;
import android.widget.TextView;

import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;
import com.facebook.drawee.view.SimpleDraweeView;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.pili.pldroid.player.widget.PLVideoView;
import com.tencent.liteav.demo.my.view.MediaController;
import com.tencent.liteav.demo.play.R;

public class UserVideoActivity extends BaseActivity {

    private ProgressBar mProgressBar;
    private PLVideoTextureView mVideoView;
    private MediaController mMediaController;
    private AVOptions options;
    @Override
    public int setLayout() {
        return R.layout.activity_user_video;
    }

    @Override
    public void initView() {
        mProgressBar = findViewById(R.id.mProgressBar);
        findViewById(R.id.ivBack).setOnClickListener(view -> {
            AppManager.getAppManager().finishActivity(this);
        });
        SimpleDraweeView sdvHead = findViewById(R.id.sdvHead);
        sdvHead.setImageURI(getIntent().getStringExtra("head"));
        TextView tvNickName = findViewById(R.id.tvNickName);
        tvNickName.setText(getIntent().getStringExtra("name"));
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(getIntent().getStringExtra("title"));
        TextView tvLick = findViewById(R.id.tvLick);
        tvLick.setText(getIntent().getStringExtra("like"));
        TextView tvCommentNum = findViewById(R.id.tvCommentNum);
        tvCommentNum.setText(getIntent().getStringExtra("comment"));

        mVideoView = findViewById(R.id.video_view_paly);
        mMediaController = new MediaController(getContext());
    }


    private void setAvOption() {
        options = new AVOptions();
        // 解码方式:
        // codec＝AVOptions.MEDIA_CODEC_HW_DECODE，硬解
        // codec=AVOptions.MEDIA_CODEC_SW_DECODE, 软解
        int codec = AVOptions.MEDIA_CODEC_AUTO; //硬解优先，失败后自动切换到软解
        // 默认值是：MEDIA_CODEC_SW_DECODE
        options.setInteger(AVOptions.KEY_MEDIACODEC, codec);

        // 快开模式，启用后会加快该播放器实例再次打开相同协议的视频流的速度
        options.setInteger(AVOptions.KEY_FAST_OPEN, 1);
    }

    @Override
    public void initData() {
        mVideoView.setMediaController(mMediaController);
        mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_PAVED_PARENT);
        mVideoView.setLooping(true);
        mVideoView.setVideoPath(getIntent().getStringExtra("url"));
        setAvOption();
        mVideoView.setAVOptions(options);
        mVideoView.setBufferingIndicator(mProgressBar);

        //mVideoView.setVideoPath(getIntent().getStringExtra("url"));
        try {
            mVideoView.start();
        }catch (Exception e){
            e.getMessage();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoView!=null){
            mVideoView.stopPlayback();
        }
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (mVideoView!=null){
//            mVideoView.pause();
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (mVideoView!=null){
//            mVideoView.pause();
//        }
//    }
}
