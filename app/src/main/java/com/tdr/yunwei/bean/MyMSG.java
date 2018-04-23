package com.tdr.yunwei.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Administrator on 2017/2/4.
 */
@Table(name = "MyMSG_Table")
public class MyMSG extends BeanID{
    @Column(name = "ID", isId = true)
    private int ID;

    @Column(name = "MSGTitle")
    private String MsgTitle;

    @Column(name = "MSGText")
    private String MsgText;

    @Column(name = "MsgTime")
    private String MsgTime;

    @Column(name = "IsOld")
    private Boolean IsOld;

    @Column(name = "MessageType")
    private String MessageType;

    @Column(name = "UserID")
    private String UserID;



    public String getMessageType() {
        return MessageType;
    }

    public void setMessageType(String messageType) {
        MessageType = messageType;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getMsgTitle() {
        return MsgTitle;
    }

    public void setMsgTitle(String msgTitle) {
        MsgTitle = msgTitle;
    }

    public String getMsgText() {
        return MsgText;
    }

    public void setMsgText(String msgText) {
        MsgText = msgText;
    }

    public String getMsgTime() {
        return MsgTime;
    }

    public void setMsgTime(String msgTime) {
        MsgTime = msgTime;
    }

    public Boolean getIsOld() {
        return IsOld;
    }

    public void setIsOld(Boolean isOld) {
        IsOld = isOld;
    }
}
