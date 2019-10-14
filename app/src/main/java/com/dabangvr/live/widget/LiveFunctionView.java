package com.dabangvr.live.widget;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.dabangvr.R;

public class LiveFunctionView {
    private  Activity mActivity;
    private PopupWindow popupWindow;
    private static LiveFunctionView liveFunctionView;
    private OnclickCallBack onclickCallBack;

    public interface OnclickCallBack{
        void click(View view, int id);
    }

    public void setOnclickCallBack(OnclickCallBack onclickCallBack) {
        this.onclickCallBack = onclickCallBack;
    }

    public static LiveFunctionView getInstance(Activity mActivity){
        if (liveFunctionView == null){
            liveFunctionView = new LiveFunctionView(mActivity);
        }
        return liveFunctionView;
    }

    public LiveFunctionView(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void showWindow(View view) {
        View layout = LayoutInflater.from(mActivity).inflate(
                R.layout.popup_view, null);
        popupWindow = new PopupWindow(view);
        // 设置弹框的宽度为布局文件的宽
        popupWindow.setWidth(350);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置一个透明的背景，不然无法实现点击弹框外，弹框消失
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置点击弹框外部，弹框消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setContentView(layout);
        //popupWindow.setAnimationStyle(R.anim.dialog_in);
        // 设置弹框出现的位置，在v的正下方横轴偏移textview的宽度，为了对齐~纵轴不偏移
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            popupWindow.showAsDropDown(view, 0, 0, Gravity.BOTTOM);
        }

        layout.findViewById(R.id.llCancelMy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //关闭美颜
                onclickCallBack.click(view,R.id.llCancelMy);
            }
        });

        layout.findViewById(R.id.llSetMy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //设置美颜
                onclickCallBack.click(view,R.id.llSetMy);
            }
        });

        layout.findViewById(R.id.llOpenLight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //开启闪光灯
                onclickCallBack.click(view,R.id.llOpenLight);
            }
        });

        layout.findViewById(R.id.llScreen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //录屏
                onclickCallBack.click(view,R.id.llScreen);
            }
        });

    }
    public void destroy(){
        if (popupWindow!=null){
            popupWindow.dismiss();
            popupWindow = null;
        }
    }
}
