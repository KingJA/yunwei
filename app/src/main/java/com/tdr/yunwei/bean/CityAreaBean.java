package com.tdr.yunwei.bean;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Administrator on 2016/8/18.
 */
@Table(name = "CityAreaBean_Table")
public class CityAreaBean extends BeanID{
    @Column(name = "ID", isId = true)
    private int ID;

    @Column(name = "AreaID")
    private String AreaID;

    @Column(name = "AreaMC")
    private String AreaMC;

    @Column(name = "AreaSort")
    private String AreaSort;

    @Column(name = "AreaType")
    private String AreaType;

    @Column(name = "FAreaID")
    private String FAreaID;

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

    public String getAreaMC() {
        return AreaMC;
    }

    public void setAreaMC(String areaMC) {
        AreaMC = areaMC;
    }

    public String getAreaSort() {
        return AreaSort;
    }

    public void setAreaSort(String areaSort) {
        AreaSort = areaSort;
    }

    public String getAreaType() {
        return AreaType;
    }

    public void setAreaType(String areaType) {
        AreaType = areaType;
    }

    public String getFAreaID() {
        return FAreaID;
    }

    public void setFAreaID(String FAreaID) {
        this.FAreaID = FAreaID;
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

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    @Override
    public String toString() {
        return "CityAreaBean{" +
                "ID=" + ID +
                ", AreaID='" + AreaID + '\'' +
                ", AreaMC='" + AreaMC + '\'' +
                ", AreaSort='" + AreaSort + '\'' +
                ", AreaType='" + AreaType + '\'' +
                ", FAreaID='" + FAreaID + '\'' +
                ", IsValid='" + IsValid + '\'' +
                ", LastUpdateTime='" + LastUpdateTime + '\'' +
                '}';
    }
}
