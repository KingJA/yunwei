package com.tdr.yunwei.bean;

/**
 * Created by Administrator on 2017/2/18.
 */

public class DeviceInfo {
    String StationID = "";
    String StationName = "";
    String CityName = "";
    String AreaName = "";
    String PCSName = "";
    String LNG = "";
    String LAT = "";
    String Address = "";
    String StationType = "";
    String StationClass = "";
    String Distance = "";

    public String getStationID() {
        return StationID;
    }

    public void setStationID(String stationID) {
        StationID = stationID;
    }

    public String getStationName() {
        return StationName;
    }

    public void setStationName(String stationName) {
        StationName = stationName;
    }

    public String getCityName() {
        return CityName;
    }

    public void setCityName(String cityName) {
        CityName = cityName;
    }

    public String getAreaName() {
        return AreaName;
    }

    public void setAreaName(String areaName) {
        AreaName = areaName;
    }

    public String getPCSName() {
        return PCSName;
    }

    public void setPCSName(String PCSName) {
        this.PCSName = PCSName;
    }

    public String getLNG() {
        return LNG;
    }

    public void setLNG(String LNG) {
        this.LNG = LNG;
    }

    public String getLAT() {
        return LAT;
    }

    public void setLAT(String LAT) {
        this.LAT = LAT;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getStationType() {
        return StationType;
    }

    public void setStationType(String stationType) {
        StationType = stationType;
    }

    public String getStationClass() {
        return StationClass;
    }

    public void setStationClass(String stationClass) {
        StationClass = stationClass;
    }

    public String getDistance() {
        return Distance;
    }

    public void setDistance(String distance) {
        Distance = distance;
    }
}
