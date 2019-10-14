package com.dbvr.baselibrary.utils;

public interface OnUploadListener {
    void onStartUpload();//开始上传

    void onUploadProgress(String key, double percent);//上传进度

    void onUploadFailed(String key, String err);//上传失败

    void onUploadBlockComplete(String key);//上传成功

    void onUploadCompleted();//上传成功

    void onUploadCancel();//取消上传
}
