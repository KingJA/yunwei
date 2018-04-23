package com.tdr.yunwei.bean;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Administrator on 2016/8/18.
 */
@Table(name = "DictionaryBean_Table")
public class DictionaryBean extends BeanID{
    @Column(name = "ID", isId = true)
    private int ID;

    @Column(name = "DictionaryID")
    private String DictionaryID;

    @Column(name = "DictionaryName")
    private String DictionaryName;

    @Column(name = "SystemID")
    private String SystemID;

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

    public String getDictionaryID() {
        return DictionaryID;
    }

    public void setDictionaryID(String dictionaryID) {
        DictionaryID = dictionaryID;
    }

    public String getDictionaryName() {
        return DictionaryName;
    }

    public void setDictionaryName(String dictionaryName) {
        DictionaryName = dictionaryName;
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

    public String getSystemID() {
        return SystemID;
    }

    public void setSystemID(String systemID) {
        SystemID = systemID;
    }
}
