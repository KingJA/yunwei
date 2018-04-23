package com.tdr.yunwei.bean;

/**
 * Created by Administrator on 2017/2/4.
 */

/**
 * 城市
 */
public class StationCity {
    String CityCode;
    String CityName;
    String IsValid;
    String PinYing;
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

    public String getPinYing() {
        return PinYing;
    }

    public void setPinYing(String pinYing) {
        PinYing = pinYing;
    }
}
