package com.tdr.yunwei.bean;

/**
 * Created by Administrator on 2017/2/4.
 */

/**
 * 所属辖区
 */
public class StationArea {

    String CityCode;
    String CityName;
    String IsValid;
    String AreaName;
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

    public String getAreaName() {
        return AreaName;
    }

    public void setAreaName(String areaname) {
        AreaName = areaname;
    }
}
