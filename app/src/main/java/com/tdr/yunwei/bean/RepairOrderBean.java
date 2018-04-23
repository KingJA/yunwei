package com.tdr.yunwei.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;


/**
 * Created by Administrator on 2016/4/26.
 */

@Table(name="Order_Table")
public class RepairOrderBean extends BeanID implements Parcelable{
    @Column(name = "mID",isId = true)
    private int mID;
    @Column(name = "RepairListID")
    private String RepairListID;

    @Column(name = "RepairListNO")
    private String RepairListNO;

    @Column(name = "MakeTime")
    private String MakeTime;

    @Column(name = "DeviceType")
    private String DeviceType;

    @Column(name = "DeviceID")
    private String DeviceID;

    @Column(name = "DeviceCode")
    private String DeviceCode;

    @Column(name = "RequestMan")
    private String RequestMan;

    @Column(name = "RequestPhone")
    private String RequestPhone;

    @Column(name = "Description")
    private String Description;

    @Column(name = "LimitTime")
    private String LimitTime;

    @Column(name = "SendTime")
    private String SendTime;

    @Column(name = "CheckTime")
    private String CheckTime;

    @Column(name = "StartTime")
    private String StartTime;

    @Column(name = "EndTime")
    private String EndTime;

    @Column(name = "IsUrgent")
    private String IsUrgent;

    @Column(name = "Status")
    private String Status;

    @Column(name = "FaultType")
    private String FaultType;

    @Column(name = "Fault")
    private String Fault;

    @Column(name = "ChangeDeviceCode")
    private String ChangeDeviceCode;

    @Column(name = "LastUpdateTime")
    private String LastUpdateTime;

    @Column(name = "Address")
    private String Address;

    @Column(name = "WorkOrderID")
    private String WorkOrderID;

    @Column(name = "WorkOrderNO")
    private String WorkOrderNO;

    @Column(name = "WorkSendTime")
    private String WorkSendTime;

    @Column(name = "WorkSendUserID")
    private String WorkSendUserID;

    @Column(name = "WorkSender")
    private String WorkSender;

    @Column(name = "WorkStatus")
    private String WorkStatus;

    @Column(name = "User")
    private String User;

    @Column(name = "CloseReason")
    private String CloseReason;


    public int getID() {
        return mID;
    }

    public void setID(int ID) {
        this.mID = ID;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getChangeDeviceCode() {
        return ChangeDeviceCode;
    }

    public void setChangeDeviceCode(String changeDeviceCode) {
        ChangeDeviceCode = changeDeviceCode;
    }

    public String getCheckTime() {
        return CheckTime;
    }

    public void setCheckTime(String checkTime) {
        CheckTime = checkTime;
    }

    public String getCloseReason() {
        return CloseReason;
    }

    public void setCloseReason(String closeReason) {
        CloseReason = closeReason;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getDeviceCode() {
        return DeviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        DeviceCode = deviceCode;
    }

    public String getDeviceID() {
        return DeviceID;
    }

    public void setDeviceID(String deviceID) {
        DeviceID = deviceID;
    }

    public String getDeviceType() {
        return DeviceType;
    }

    public void setDeviceType(String deviceType) {
        DeviceType = deviceType;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getFault() {
        return Fault;
    }

    public void setFault(String fault) {
        Fault = fault;
    }

    public String getFaultType() {
        return FaultType;
    }

    public void setFaultType(String faultType) {
        FaultType = faultType;
    }

    public String getIsUrgent() {
        return IsUrgent;
    }

    public void setIsUrgent(String isUrgent) {
        IsUrgent = isUrgent;
    }

    public String getLastUpdateTime() {
        return LastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        LastUpdateTime = lastUpdateTime;
    }

    public String getLimitTime() {
        return LimitTime;
    }

    public void setLimitTime(String limitTime) {
        LimitTime = limitTime;
    }

    public String getMakeTime() {
        return MakeTime;
    }

    public void setMakeTime(String makeTime) {
        MakeTime = makeTime;
    }

    public String getRepairListID() {
        return RepairListID;
    }

    public void setRepairListID(String repairListID) {
        RepairListID = repairListID;
    }

    public String getRepairListNO() {
        return RepairListNO;
    }

    public void setRepairListNO(String repairListNO) {
        RepairListNO = repairListNO;
    }

    public String getRequestMan() {
        return RequestMan;
    }

    public void setRequestMan(String requestMan) {
        RequestMan = requestMan;
    }

    public String getRequestPhone() {
        return RequestPhone;
    }

    public void setRequestPhone(String requestPhone) {
        RequestPhone = requestPhone;
    }

    public String getSendTime() {
        return SendTime;
    }

    public void setSendTime(String sendTime) {
        SendTime = sendTime;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public String getWorkOrderID() {
        return WorkOrderID;
    }

    public void setWorkOrderID(String workOrderID) {
        WorkOrderID = workOrderID;
    }

    public String getWorkOrderNO() {
        return WorkOrderNO;
    }

    public void setWorkOrderNO(String workOrderNO) {
        WorkOrderNO = workOrderNO;
    }

    public String getWorkSender() {
        return WorkSender;
    }

    public void setWorkSender(String workSender) {
        WorkSender = workSender;
    }

    public String getWorkSendTime() {
        return WorkSendTime;
    }

    public void setWorkSendTime(String workSendTime) {
        WorkSendTime = workSendTime;
    }

    public String getWorkSendUserID() {
        return WorkSendUserID;
    }

    public void setWorkSendUserID(String workSendUserID) {
        WorkSendUserID = workSendUserID;
    }

    public String getWorkStatus() {
        return WorkStatus;
    }

    public void setWorkStatus(String workStatus) {
        WorkStatus = workStatus;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.RepairListID);
        dest.writeString(this.RepairListNO);
        dest.writeString(this.MakeTime);
        dest.writeString(this.DeviceType);
        dest.writeString(this.DeviceID);
        dest.writeString(this.DeviceCode);
        dest.writeString(this.RequestMan);
        dest.writeString(this.RequestPhone);
        dest.writeString(this.Description);
        dest.writeString(this.LimitTime);
        dest.writeString(this.SendTime);
        dest.writeString(this.CheckTime);
        dest.writeString(this.StartTime);
        dest.writeString(this.EndTime);
        dest.writeString(this.IsUrgent);
        dest.writeString(this.Status);
        dest.writeString(this.FaultType);
        dest.writeString(this.Fault);
        dest.writeString(this.ChangeDeviceCode);
        dest.writeString(this.LastUpdateTime);
        dest.writeString(this.Address);
        dest.writeString(this.WorkOrderID);
        dest.writeString(this.WorkOrderNO);
        dest.writeString(this.WorkSendTime);
        dest.writeString(this.WorkSendUserID);
        dest.writeString(this.WorkSender);
        dest.writeString(this.WorkStatus);
        dest.writeString(this.User);
        dest.writeString(this.CloseReason);
    }

    public RepairOrderBean() {
    }

    protected RepairOrderBean(Parcel in) {
        this.RepairListID = in.readString();
        this.RepairListNO = in.readString();
        this.MakeTime = in.readString();
        this.DeviceType = in.readString();
        this.DeviceID = in.readString();
        this.DeviceCode = in.readString();
        this.RequestMan = in.readString();
        this.RequestPhone = in.readString();
        this.Description = in.readString();
        this.LimitTime = in.readString();
        this.SendTime = in.readString();
        this.CheckTime = in.readString();
        this.StartTime = in.readString();
        this.EndTime = in.readString();
        this.IsUrgent = in.readString();
        this.Status = in.readString();
        this.FaultType = in.readString();
        this.Fault = in.readString();
        this.ChangeDeviceCode = in.readString();
        this.LastUpdateTime = in.readString();
        this.Address = in.readString();
        this.WorkOrderID = in.readString();
        this.WorkOrderNO = in.readString();
        this.WorkSendTime = in.readString();
        this.WorkSendUserID = in.readString();
        this.WorkSender = in.readString();
        this.WorkStatus = in.readString();
        this.User = in.readString();
        this.CloseReason = in.readString();
    }

    public static final Creator<RepairOrderBean> CREATOR = new Creator<RepairOrderBean>() {
        @Override
        public RepairOrderBean createFromParcel(Parcel source) {
            return new RepairOrderBean(source);
        }

        @Override
        public RepairOrderBean[] newArray(int size) {
            return new RepairOrderBean[size];
        }
    };
}
