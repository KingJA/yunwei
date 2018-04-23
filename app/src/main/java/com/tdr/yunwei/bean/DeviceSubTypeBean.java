package com.tdr.yunwei.bean;


import android.os.Parcel;
import android.os.Parcelable;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name="DeviceSubType_Table")
public class DeviceSubTypeBean extends BeanID implements Parcelable{
	@Column(name = "ID",isId = true)
    private int ID;

	@Column(name = "SubTypeID")
	private String SubTypeID;

	@Column(name = "MainTypeID")
	private String MainTypeID;

	@Column(name = "IsValid")
	private String IsValid;

	@Column(name = "Remark")
	private String Remark;

	@Column(name = "LastUpdateTime")
	private String LastUpdateTime;

	@Column(name = "ForPoliceUse")
	private String ForPoliceUse;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

	public String getForPoliceUse() {
		return ForPoliceUse;
	}

	public void setForPoliceUse(String forPoliceUse) {
		ForPoliceUse = forPoliceUse;
	}

	public String getLastUpdateTime() {
		return LastUpdateTime;
	}

	public void setLastUpdateTime(String lastUpdateTime) {
		LastUpdateTime = lastUpdateTime;
	}

	public String getIsValid() {
		return IsValid;
	}

	public void setIsValid(String isValid) {
		IsValid = isValid;
	}

	public String getMainTypeID() {
		return MainTypeID;
	}

	public void setMainTypeID(String mainTypeID) {
		MainTypeID = mainTypeID;
	}

	public String getRemark() {
		return Remark;
	}

	public void setRemark(String remark) {
		Remark = remark;
	}

	public String getSubTypeID() {
		return SubTypeID;
	}

	public void setSubTypeID(String subTypeID) {
		SubTypeID = subTypeID;
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.SubTypeID);
		dest.writeString(this.MainTypeID);
		dest.writeString(this.IsValid);
		dest.writeString(this.Remark);
	}

	public DeviceSubTypeBean() {
	}

	protected DeviceSubTypeBean(Parcel in) {
		this.SubTypeID = in.readString();
		this.MainTypeID = in.readString();
		this.IsValid = in.readString();
		this.Remark = in.readString();
	}

	public static final Creator<DeviceSubTypeBean> CREATOR = new Creator<DeviceSubTypeBean>() {
		@Override
		public DeviceSubTypeBean createFromParcel(Parcel source) {
			return new DeviceSubTypeBean(source);
		}

		@Override
		public DeviceSubTypeBean[] newArray(int size) {
			return new DeviceSubTypeBean[size];
		}
	};
}
