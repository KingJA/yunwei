package com.tdr.yunwei.bean;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Administrator on 2016/8/18.
 */
@Table(name = "ParamBean_Table")
public class ParamBean extends BeanID{
    @Column(name = "ID", isId = true)
    private int ID;

    @Column(name = "ParamID")
    private String ParamID;

    @Column(name = "FDictionarID")
    private String FDictionarID;

    @Column(name = "FParamId")
    private String FParamId;

    @Column(name = "DictionaryID")
    private String DictionaryID;

    @Column(name = "ParamValue")
    private String ParamValue;

    @Column(name = "IsValid")
    private String IsValid;

    @Column(name = "LastUpdateTime")
    private String LastUpdateTime;

    @Column(name = "ParamCode")
    private String ParamCode;

    public String getDictionaryID() {
        return DictionaryID;
    }

    public void setDictionaryID(String dictionaryID) {
        DictionaryID = dictionaryID;
    }

    public String getFDictionarID() {
        return FDictionarID;
    }

    public void setFDictionarID(String FDictionarID) {
        this.FDictionarID = FDictionarID;
    }

    public String getFParamId() {
        return FParamId;
    }

    public void setFParamId(String FParamId) {
        this.FParamId = FParamId;
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

    public String getParamID() {
        return ParamID;
    }

    public void setParamID(String paramID) {
        ParamID = paramID;
    }

    public String getParamValue() {
        return ParamValue;
    }

    public void setParamValue(String paramValue) {
        ParamValue = paramValue;
    }

    public String getParamCode() {
        return ParamCode;
    }

    public void setParamCode(String paramCode) {
        ParamCode = paramCode;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
