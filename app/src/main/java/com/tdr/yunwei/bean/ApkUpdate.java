package com.tdr.yunwei.bean;

/**
 * Created by Administrator on 2016/12/23.
 */

public class ApkUpdate {
    String VersionID;
    String UpdateUrl;
    String ErrorCode;
    String ErrorMsg;
    public ApkUpdate(String versionid,String updateurl,String errorcode,String errormsg){
        this.VersionID=versionid;
        this.UpdateUrl=updateurl;
        this.ErrorCode=errorcode;
        this.ErrorMsg=errormsg;

    }
    public String getVersionID() {
        return VersionID;
    }

    public void setVersionID(String versionID) {
        VersionID = versionID;
    }

    public String getUpdateUrl() {
        return UpdateUrl;
    }

    public void setUpdateUrl(String updateUrl) {
        UpdateUrl = updateUrl;
    }

    public String getErrorCode() {
        return ErrorCode;
    }

    public void setErrorCode(String errorCode) {
        ErrorCode = errorCode;
    }

    public String getErrorMsg() {
        return ErrorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        ErrorMsg = errorMsg;
    }


}
