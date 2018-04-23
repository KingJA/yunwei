package com.tdr.yunwei.bean;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name="DeviceMainType_Table")
public class DeviceMainTypeBean extends BeanID{
	@Column(name = "ID", isId = true)
	private int ID;

	@Column(name = "MainTypeID")
	private String MainTypeID;

	@Column(name = "Remark")
	private String Remark;

	@Column(name = "IsValid")
	private String IsValid;

	@Column(name = "LastUpdateTime")
	private String LastUpdateTime;


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

	public int getID() {
		return ID;
	}

	public void setID(int ID) {
		this.ID = ID;
	}
}
