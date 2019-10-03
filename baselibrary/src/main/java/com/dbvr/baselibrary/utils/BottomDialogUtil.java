package com.dbvr.baselibrary.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;

import com.rey.material.app.BottomSheetDialog;

public abstract class BottomDialogUtil {

    private Context mContext;
    private BottomSheetDialog dialog;
    private View view;
    private int height;
    private int LayoutId;

    private OnDismissCallBack onDismissCallBack;
    private OnShowCallBack onShowCallBack;

    public interface OnDismissCallBack{
        void onDismiss();
    }

    public interface OnShowCallBack{
        void onShow();
    }

    public void setOnShowCallBack(OnShowCallBack onShowCallBack) {
        this.onShowCallBack = onShowCallBack;
    }

    public void setOnDismissCallBack(OnDismissCallBack onDismissCallBack) {
        this.onDismissCallBack = onDismissCallBack;
    }

    public View getView() {
        return view;
    }

    public BottomDialogUtil(Context mContext, int layoutId, double h) {
        this.mContext = mContext;
        LayoutId = layoutId;
        init(h);
    }
    private void init(double h) {
        dialog = new BottomSheetDialog(mContext);
        view = LayoutInflater.from(mContext).inflate(LayoutId, null);
        convert(view);
        height = (int) (Double.valueOf(ScreenUtils.getScreenHeight(mContext)) / h);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                if (onShowCallBack!=null){
                    onShowCallBack.onShow();
                }
            }
        });
    }

    public void show(){
        dialog.contentView(view)
                .heightParam(height)
                .inDuration(200)
                .outDuration(200)
                .cancelable(true)
                .show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (onDismissCallBack!=null){
                    onDismissCallBack.onDismiss();
                }
            }
        });
    }

    public void showAuto(){
        dialog.contentView(view)
                .inDuration(200)
                .outDuration(200)
                .cancelable(true)
                .show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (onDismissCallBack!=null){
                    onDismissCallBack.onDismiss();
                }
            }
        });
    }

    public abstract void convert(View holder);


    public void dess(){
        if (dialog!=null){
            dialog.dismiss();
        }
    }
}
