package com.tdr.yunwei.bean;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Administrator on 2016/4/21.
 */
@Table(name="RepairCompany_Table")
public class RepairCompanyBean extends BeanID{
    @Column(name = "ID", isId = true)
    private int ID;

    @Column(name = "CompanyID")
    private String CompanyID;

    @Column(name = "CompanyName")
    private String CompanyName;

    @Column(name = "IsInstall")
    private String IsInstall;

    @Column(name = "IsRepair")
    private String IsRepair;

    @Column(name = "IsValid")
    private String IsValid;

    @Column(name = "LastUpdateTime")
    private String LastUpdateTime;

    @Column(name = "CompanyCode")
    private String CompanyCode;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getCompanyID() {
        return CompanyID;
    }

    public void setCompanyID(String companyID) {
        CompanyID = companyID;
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(String companyName) {
        CompanyName = companyName;
    }

    public String getIsInstall() {
        return IsInstall;
    }

    public void setIsInstall(String isInstall) {
        IsInstall = isInstall;
    }

    public String getIsRepair() {
        return IsRepair;
    }

    public void setIsRepair(String isRepair) {
        IsRepair = isRepair;
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

    public String getCompanyCode() {
        return CompanyCode;
    }

    public void setCompanyCode(String companyCode) {
        CompanyCode = companyCode;
    }
}
