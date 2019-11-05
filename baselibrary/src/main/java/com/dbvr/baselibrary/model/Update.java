package com.dbvr.baselibrary.model;

public class Update {

    /**
     * id : 3
     * url : http://image.vrzbgw.com/upload/20190909/120728330633e8.apk
     * versionCode : 1.1.3
     * updateMessage : 修复已知bug修复人脸识别修复开发人员太帅等
     * qrCode : http://qr.liantu.com/api.php?bg=f3f3f3&fg=ff0000&gc=222222&el=l&w=150&m=10&text=http://image.vrzbgw.com/upload/20190909/120728330633e8.apk
     * type : 1
     */

    private int id;
    private String url;
    private String versionCode;
    private String updateMessage;
    private String qrCode;
    private int type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getUpdateMessage() {
        return updateMessage;
    }

    public void setUpdateMessage(String updateMessage) {
        this.updateMessage = updateMessage;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
