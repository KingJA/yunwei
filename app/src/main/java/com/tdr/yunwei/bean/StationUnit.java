package com.tdr.yunwei.bean;

/**
 * Created by Administrator on 2017/2/4.
 */

/**
 * 所属单位
 */
public class StationUnit {

    String CityCode;
    String CityName;
    String IsValid;
    String PCSName;
    String LastUpdateTime;

    public String getCityCode() {
        return CityCode;
    }

    public void setCityCode(String cityCode) {
        CityCode = cityCode;
    }

    public String getCityName() {
        return CityName;
    }

    public void setCityName(String cityName) {
        CityName = cityName;
    }

    public String getIsValid() {
        return IsValid;
    }

    public void setIsValid(String isValid) {
        IsValid = isValid;
    }

    public String getLastUpdateTime() {
        return LastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        LastUpdateTime = lastUpdateTime;
    }

    public String getPCSName() {
        return PCSName;
    }

    public void setPCSName(String pcsname) {
        PCSName = pcsname;
    }
}
