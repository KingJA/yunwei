package com.tdr.yunwei.update;

import java.io.Serializable;

/**
 * Created by Linus_Xie on 2016/3/12.
 */
public class VersionInfo implements Serializable {

    private double versionCode;//版本号
    private String versionName;//版本名称
    private String packageName;//包名

    public VersionInfo() {
    }

    public double getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(double versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
