package com.tdr.yunwei.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Administrator on 2016/12/23.
 */
@Table(name = "test")
public class Test extends BeanID{
    @Column(name = "ID", isId = true)
    private int ID;
    @Column(name = "subtypeid")
    private String SubTypeID;
    @Column(name = "maintypeid")
    private String MainTypeID;
    @Column(name = "isvalid")
    private String IsValid;
    @Column(name = "remark")
    private String Remark;
    @Column(name = "lastupdatetime")
    private String LastUpdateTime;
    @Column(name = "forpoliceuse")
    private String ForPoliceUse;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getSubTypeID() {
        return SubTypeID;
    }

    public void setSubTypeID(String subTypeID) {
        SubTypeID = subTypeID;
    }

    public String getMainTypeID() {
        return MainTypeID;
    }

    public void setMainTypeID(String mainTypeID) {
        MainTypeID = mainTypeID;
    }

    public String getIsValid() {
        return IsValid;
    }

    public void setIsValid(String isValid) {
        IsValid = isValid;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public String getLastUpdateTime() {
        return LastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        LastUpdateTime = lastUpdateTime;
    }

    public String getForPoliceUse() {
        return ForPoliceUse;
    }

    public void setForPoliceUse(String forPoliceUse) {
        ForPoliceUse = forPoliceUse;
    }


}
