package com.tdr.yunwei.bean;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Administrator on 2016/8/18.
 */

@Table(name = "DASBean_Table")
public class DASBean extends BeanID{
    @Column(name = "ID", isId = true)
    private int ID;

    @Column(name = "ListID")
    private String ListID;

    @Column(name = "AreaID")
    private String AreaID;

    @Column(name = "DeviceTypeID")
    private String DeviceTypeID;

    @Column(name = "SystemID")
    private String SystemID;

    @Column(name = "IsValid")
    private String IsValid;

    @Column(name = "LastUpdateTime")
    private String LastUpdateTime;



    public String getAreaID() {
        return AreaID;
    }

    public void setAreaID(String areaID) {
        AreaID = areaID;
    }

    public String getDeviceTypeID() {
        return DeviceTypeID;
    }

    public void setDeviceTypeID(String deviceTypeID) {
        DeviceTypeID = deviceTypeID;
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

    public String getListID() {
        return ListID;
    }

    public void setListID(String listID) {
        ListID = listID;
    }

    public String getSystemID() {
        return SystemID;
    }

    public void setSystemID(String systemID) {
        SystemID = systemID;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
