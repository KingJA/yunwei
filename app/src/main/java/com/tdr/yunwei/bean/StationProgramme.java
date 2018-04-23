package com.tdr.yunwei.bean;

/**
 * Created by Administrator on 2017/2/4.
 */

/**
 * 所属方案
 */
public class StationProgramme {

//                            "CityCode":"330700",
//                            "CityName":"义乌监控",
//                            "ProgrammeName":"义乌基站布点",
//                            "IsValid":"1",
//                            "LastUpdateTime":
    String CityCode;
    String CityName;
    String ProgrammeName;
    String IsValid;
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

    public String getProgrammeName() {
        return ProgrammeName;
    }

    public void setProgrammeName(String programmename) {
        ProgrammeName = programmename;
    }
}
