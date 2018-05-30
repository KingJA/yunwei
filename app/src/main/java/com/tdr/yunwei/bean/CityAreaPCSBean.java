package com.tdr.yunwei.bean;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Administrator on 2016/8/18.
 */
@Table(name = "CityAreaPCSBean_Table")
public class CityAreaPCSBean extends BeanID{
    @Column(name = "ID", isId = true)
    private int ID;

    @Column(name = "PCSID")
    private String PCSID;

    @Column(name = "PCSMC")
    private String PCSMC;

    @Column(name = "AreaID")
    private String AreaID;

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

    public String getPCSID() {
        return PCSID;
    }

    public void setPCSID(String PCSID) {
        this.PCSID = PCSID;
    }

    public String getPCSMC() {
        return PCSMC;
    }

    public void setPCSMC(String PCSMC) {
        this.PCSMC = PCSMC;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    @Override
    public String toString() {
        return "CityAreaPCSBean{" +
                "ID=" + ID +
                ", PCSID='" + PCSID + '\'' +
                ", PCSMC='" + PCSMC + '\'' +
                ", AreaID='" + AreaID + '\'' +
                ", IsValid='" + IsValid + '\'' +
                ", LastUpdateTime='" + LastUpdateTime + '\'' +
                '}';
    }
}
