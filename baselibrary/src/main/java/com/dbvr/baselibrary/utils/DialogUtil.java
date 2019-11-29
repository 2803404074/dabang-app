package com.dbvr.baselibrary.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.dbvr.baselibrary.R;


/**
 * 弹窗，多类型
 */
public class DialogUtil {
    private static DialogUtil dialogUtil;
    private Context context;
    private AlertDialog dialog;

    public static DialogUtil getInstance(Context mContext) {
        if (dialogUtil == null) {
            dialogUtil = new DialogUtil(mContext);
        }
        return dialogUtil;
    }

    public DialogUtil(Context context) {
        this.context = context;
    }

    /**
     * @param layout 该布局需要有一个iv_close id，用于通用关闭
     */
    public void show(int layout, Conver convers) {
        View view = LayoutInflater.from(context).inflate(layout, null, false);
        convers.setView(view);
        dialog = new AlertDialog.Builder(context, R.style.TransparentDialog).setView(view).create();
        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = (ScreenUtils.getScreenWidth(context) / 4 * 3);
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setWindowAnimations(R.style.dialog_animation);
    }

    public boolean isShow() {
        if (dialog != null) {
            return dialog.isShowing();
        }
        return false;
    }


    public void des() {
        if (dialogUtil != null) {
            dialogUtil = null;
            if (dialog != null) {
                dialog.dismiss();
                dialog = null;
            }
        }
    }
}
