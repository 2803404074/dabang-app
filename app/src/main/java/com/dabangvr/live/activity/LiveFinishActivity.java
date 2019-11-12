package com.dabangvr.live.activity;

import android.view.View;
import android.widget.TextView;

import com.dabangvr.R;
import com.dbvr.baselibrary.model.UserMess;
import com.dbvr.baselibrary.utils.SPUtils;
import com.dbvr.baselibrary.view.BaseActivity;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.OnClick;

public class LiveFinishActivity extends BaseActivity {

    @BindView(R.id.sdvHead_finish)
    SimpleDraweeView sdvHead;

    @BindView(R.id.tvNickName)
    TextView tvNickName;

    @BindView(R.id.tvLiveTime)
    TextView tvLiveTime;

    @BindView(R.id.tvSeeNum)
    TextView tvSeeNum;

    @BindView(R.id.tvFansNum)
    TextView tvFansNum;

    @BindView(R.id.tvDzNum)
    TextView tvDzNum;

    @BindView(R.id.tvGiftNum)
    TextView tvGiftNum;

    @Override
    public int setLayout() {
        return R.layout.activity_live_finish;
    }

    @Override
    public void initView() {
        UserMess userMess = SPUtils.instance(this).getUser();

        String liveTime = getIntent().getStringExtra("liveTime");
        String liveSeeNum = getIntent().getStringExtra("liveSeeNum");
        int liveAddFansNum = getIntent().getIntExtra("liveAddFansNum",0);
        String liveDzNum = getIntent().getStringExtra("liveDzNum");
        int liveDropNum = getIntent().getIntExtra("liveDropNum",0);

        sdvHead.setImageURI(userMess.getHeadUrl());
        tvNickName.setText(userMess.getNickName());

        tvLiveTime.setText("直播时长："+liveTime);
        tvSeeNum.setText(liveSeeNum);//观看人数
        tvFansNum.setText(String.valueOf(liveAddFansNum));//新增粉丝
        tvDzNum.setText(liveDzNum);//点赞数量
        tvGiftNum.setText(String.valueOf(liveDropNum));//跳币增加量

    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.tvConfirm})
    public void click(View view){
        switch (view.getId()){
            case R.id.tvConfirm:
                finish();
                break;
                default:break;
        }
    }
}
