package com.tdr.yunwei.bean;

/**
 * Created by Administrator on 2016/8/12.
 */
public class DeviceQueryBean2 {

    private String DeviceCode;
    private String DeviceType;
    private String DeviceRemark;
    private String Address;

    private String AccessType;

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getDeviceCode() {
        return DeviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        DeviceCode = deviceCode;
    }

    public String getDeviceRemark() {
        return DeviceRemark;
    }

    public void setDeviceRemark(String deviceRemark) {
        DeviceRemark = deviceRemark;
    }

    public String getDeviceType() {
        return DeviceType;
    }

    public void setDeviceType(String deviceType) {
        DeviceType = deviceType;
    }

    public String getAccessType() {
        return AccessType;
    }

    public void setAccessType(String accessType) {
        AccessType = accessType;
    }
}
