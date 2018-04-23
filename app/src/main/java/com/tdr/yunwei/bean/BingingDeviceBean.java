package com.tdr.yunwei.bean;

/**
 * Created by Administrator on 2016/4/26.
 */
public class BingingDeviceBean {

    private String UserId;
    private String Type;
    private String IMEI;
    private String DeviceName;
    private String CreatedTime;
    private String pushID;

    public String getPushID() {
        return pushID;
    }

    public void setPushID(String pushID) {
        this.pushID = pushID;
    }

    public String getCreatedTime() {
        return CreatedTime;
    }

    public void setCreatedTime(String createdTime) {
        CreatedTime = createdTime;
    }

    public String getDeviceName() {
        return DeviceName;
    }

    public void setDeviceName(String deviceName) {
        DeviceName = deviceName;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }
}
