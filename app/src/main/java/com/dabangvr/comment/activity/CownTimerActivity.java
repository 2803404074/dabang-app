package com.dabangvr.comment.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dabangvr.R;
import com.dbvr.baselibrary.utils.StatusBarUtil;
import com.dbvr.baselibrary.view.AppManager;
import com.dbvr.baselibrary.view.BaseActivity;

import butterknife.BindView;

public class CownTimerActivity extends BaseActivity {

    @BindView(R.id.tvDown)
    TextView tvDown;
    @BindView(R.id.ivContent)
    ImageView ivContent;
    private MyCountDownTimer mCountDownTimer;

    private MyRunable myRunable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_cown_timer;
    }

    @Override
    public void initView() {
        tvDown.setText("3s 跳过");
        //创建倒计时类
        mCountDownTimer = new MyCountDownTimer(5000, 1000);
        mCountDownTimer.start();
        myRunable = new MyRunable();
        //这是一个 Handler 里面的逻辑是从 Splash 界面跳转到 Main 界面，这里的逻辑每个公司基本上一致
        Handler handler = new Handler();
        handler.postDelayed(myRunable, 5000); // 延迟5秒，再运行splashhandler的run()


        tvDown.setOnClickListener(view -> {
            handler.removeCallbacks(myRunable);
            goTActivity(MainActivity.class,null);
            AppManager.getAppManager().finishActivity(this);
        });

        Glide.with(getContext()).load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1575624858990&di=b3d396cae16ec0dcd290682d1d054475&imgtype=0&src=http%3A%2F%2Fimg.redocn.com%2Fsheji%2F20161101%2Fxinxianhaixianhaibao_7378673.jpg").into(ivContent);
    }


    class MyRunable implements Runnable{

        @Override
        public void run() {
            goTActivity(MainActivity.class,null);
            AppManager.getAppManager().finishActivity(CownTimerActivity.this);
        }
    }
    @Override
    public void initData() {

    }

    class MyCountDownTimer extends CountDownTimer {
        /**
         * @param millisInFuture
         *      表示以「 毫秒 」为单位倒计时的总数
         *      例如 millisInFuture = 1000 表示1秒
         *
         * @param countDownInterval
         *      表示 间隔 多少微秒 调用一次 onTick()
         *      例如: countDownInterval = 1000 ; 表示每 1000 毫秒调用一次 onTick()
         *
         */

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }


        public void onFinish() {
            tvDown.setText("0s 跳过");
        }

        public void onTick(long millisUntilFinished) {
            tvDown.setText( millisUntilFinished / 1000 + "s 跳过");
        }

    }

}
