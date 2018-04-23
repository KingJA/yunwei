package com.tdr.yunwei.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Administrator on 2016/8/18.
 */

@Table(name = "CompanyOfSystemBean_Table")
public class CompanyOfSystemBean extends BeanID{
    @Column(name = "ID", isId = true)
    private int ID;

    @Column(name = "ListID")
    private String ListID;

    @Column(name = "CompanyID")
    private String CompanyID;

    @Column(name = "SystemID")
    private String SystemID;

    @Column(name = "IsValid")
    private String IsValid;

    @Column(name = "LastUpdateTime")
    private String LastUpdateTime;


    public String getCompanyID() {
        return CompanyID;
    }

    public void setCompanyID(String companyID) {
        CompanyID = companyID;
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
