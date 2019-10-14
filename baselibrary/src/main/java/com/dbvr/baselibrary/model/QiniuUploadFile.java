package com.dbvr.baselibrary.model;

public class QiniuUploadFile {
    private String filePath;  // 文件的路径
    private String key;       // 文件上传到服务器的路径，如：files/images/test.jpg
    private String mimeType;  // 文件类型
    private String upLoadToken;     // 从后台获取的token值，只在一定时间内有效

    public QiniuUploadFile(String filePath, String key, String mimeType, String upLoadToken) {
        this.filePath = filePath;
        this.key = key;
        this.mimeType = mimeType;
        this.upLoadToken = upLoadToken;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getKey() {
        return key;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getUpLoadToken() {
        return upLoadToken;
    }
}
