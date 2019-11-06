package com.dbvr.baselibrary.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;

import com.dbvr.baselibrary.R;
import com.rey.material.app.BottomSheetDialog;

public class BottomDialogUtil2 {
    private Activity mContext;
    private BottomSheetDialog dialog;
    private static BottomDialogUtil2 bottomDialogUtil2;

    public static BottomDialogUtil2 getInstance(Activity context) {
        if (bottomDialogUtil2 == null) {
            bottomDialogUtil2 = new BottomDialogUtil2(context);
        }
        return bottomDialogUtil2;
    }

    private OnDismissCallBack onDismissCallBack;
    private OnShowCallBack onShowCallBack;

    public interface OnDismissCallBack {
        void onDismiss();
    }

    public interface OnShowCallBack {
        void onShow();
    }

    public void setOnShowCallBack(OnShowCallBack onShowCallBack) {
        this.onShowCallBack = onShowCallBack;
    }

    public void setOnDismissCallBack(OnDismissCallBack onDismissCallBack) {
        this.onDismissCallBack = onDismissCallBack;
    }

    public BottomDialogUtil2(Activity mContext) {
        this.mContext = mContext;
    }

    public void show(int layoutId, double h, Conver convers) {
        dialog = new BottomSheetDialog(mContext, R.style.dialog);

        View view = LayoutInflater.from(mContext).inflate(layoutId, null);
        convers.setView(view);
        int height = (int) (Double.valueOf(ScreenUtils.getScreenHeight(mContext)) / h);
        dialog.setOnShowListener(dialogInterface -> {

            if (onShowCallBack != null) {
                onShowCallBack.onShow();
            }
        });

        dialog.setOnDismissListener(dialogInterface -> {
            dess();
            if (onDismissCallBack != null) {
                onDismissCallBack.onDismiss();
            }
        });

        if (mContext.isFinishing()) {
            return;
        }

        if (h == 0) {
            dialog.contentView(view)
                    .inDuration(200)
                    .outDuration(200)
                    .cancelable(true)
                    .show();
        } else {
            dialog.contentView(view)
                    .heightParam(height)
                    .inDuration(200)
                    .outDuration(200)
                    .cancelable(true)
                    .show();
        }
    }


    public void showLive(int layoutId,Conver convers) {
        dialog = new BottomSheetDialog(mContext, R.style.dialog2);
        View view = LayoutInflater.from(mContext).inflate(layoutId, null);
        convers.setView(view);
        dialog.setOnShowListener(dialogInterface -> {

            if (onShowCallBack != null) {
                onShowCallBack.onShow();
            }
        });

        dialog.setOnDismissListener(dialogInterface -> {
            if (onDismissCallBack != null) {
                onDismissCallBack.onDismiss();
            }
        });

        if (mContext.isFinishing()) {
            return;
        }
        dialog.contentView(view)
                .inDuration(200)
                .outDuration(200)
                .cancelable(true)
                .show();
    }


    public void dess() {
        if (bottomDialogUtil2 != null) {
            if (dialog != null) {
                dialog.dismiss();
                dialog = null;
            }
            bottomDialogUtil2 = null;
        }
    }
}
