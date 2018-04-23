package com.tdr.yunwei.bean;
/**
 * Created by Administrator on 2016/12/23.
 */
public class CityList extends BeanID{

    private String CityID;

    private String CityName;

    private String PinYing;

    private String LastUpdateTime;

    public String getPinYing() {
        return PinYing;
    }

    public void setPinYing(String pinYing) {
        PinYing = pinYing;
    }

    public String getCityID() {
        return CityID;
    }

    public void setCityID(String cityID) {
        CityID = cityID;
    }


    public String getCityName() {
        return CityName;
    }

    public void setCityName(String cityName) {
        this.CityName = cityName;
    }


    public String getLastUpdateTime() {
        return LastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        LastUpdateTime = lastUpdateTime;
    }

}