package com.tdr.yunwei.bean;



import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Administrator on 2016/4/22.
 */
@Table(name = "Area2Company_Table")
public class Area2ComanyBean extends BeanID{
    @Column(name = "ID", isId = true)
    private int ID;
    @Column(name = "AreaID")
    private String AreaID;

    @Column(name = "CompanyID")
    private String CompanyID;

    @Column(name = "IsValid")
    private String IsValid;

    @Column(name = "LastUpdateTime")
    private String LastUpdateTime;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getAreaID() {
        return AreaID;
    }

    public void setAreaID(String areaID) {
        AreaID = areaID;
    }

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

}
