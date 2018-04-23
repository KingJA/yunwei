package com.tdr.yunwei.util;

import android.app.Activity;
import android.util.Log;

import com.tdr.yunwei.bean.DeviceSubTypeBean;


import org.xutils.DbManager;
import org.xutils.ex.DbException;

import java.util.List;

/**
 * Created by Administrator on 2016/5/12.
 */
public class ZbarUtil {
    public static String DeviceZbar(Activity mActivity, String http) {
        String strZbar = "";
        String devicetype = "";
        String devicecode = "";
        String scanResult = http;
        scanResult = scanResult.substring(scanResult.indexOf("?") + 1);// http://v.iotone.cn/?ABBBAAA1roh7U=
        String type = scanResult.substring(0, 2);

        //治安防控

        if (type.toUpperCase().equals("AB")) {
            String re = scanResult.substring(2);// BBAAA1roh7U=
            LOG.E("re="+re);

            byte[] content = MyBase64.decode(re);
            LOG.E("content="+BytesToHexString(content));

            String str= BytesToHexString(GetByteArrayByLength(content, 0, 2));
            LOG.E("str="+str);

            devicetype = String.valueOf(Long.parseLong(str, 16));
            LOG.E("devicetype="+devicetype);

            byte[] deviceNo = new byte[4];// 设备编号
            System.arraycopy(content, 2, deviceNo, 0, 4);
            LOG.E("deviceNo="+BytesToHexString(deviceNo));

            String s_deviceNo = BytesToHexString(deviceNo);// 16进制设备编号
            LOG.E("s_deviceNo="+s_deviceNo);

            devicecode = String.valueOf(Long.parseLong(s_deviceNo, 16));// 十进制设备编号
            LOG.E("devicecode="+devicecode);

            strZbar = devicecode + "," + devicetype;
        }

        LOG.E("scanResult=" + scanResult + "//type=" + type + "//strZbar=" + strZbar);
        return strZbar;
    }


//    public static void main(String args[]) {
//        byte[] content = MyBase64.decode("BBAAA1roh7U=");
//        System.out.println(content);
//        byte[] deviceNo = new byte[4];// 设备编号
//        System.arraycopy(content, 2, deviceNo, 0, 4);
//        System.out.println(deviceNo);
//    }

    /**
     * byte——>HexString
     *
     * @param src
     * @return
     */
    public static final String BytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }


    /**
     * 数组截取：从arrData的offset开始获取len个长度的byte生成的len的byte[]
     *
     * @param arrData
     * @param offset
     * @param len
     * @return
     */
    public static final byte[] GetByteArrayByLength(byte[] arrData, int offset, int len) {
        byte[] newData = new byte[len];
        try {
            for (int i = offset; i < offset + len; i++) {
                newData[i - offset] = arrData[i];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newData;
    }


    public static String getSubRemark(DbManager DB, String devicecode) {
        String subRemark = "无此设备";

        List<DeviceSubTypeBean> dstList = null;
        try {
            dstList = DB.findAll(DeviceSubTypeBean.class);


        for (int i = 0; i < dstList.size(); i++) {
            if (devicecode.equals(dstList.get(i).getSubTypeID())) {
                subRemark = dstList.get(i).getRemark();
                Log.e("subRemark", subRemark);
            }
        }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return subRemark;
    }


    public static String getSubTypeID(DbManager DB, String deviceremark) {
        String subTypeID = "无此设备";
        List<DeviceSubTypeBean> dstList = null;
        try {
            dstList = DB.findAll(DeviceSubTypeBean.class);


        for (int i = 0; i < dstList.size(); i++) {
            if (deviceremark.equals(dstList.get(i).getRemark())) {
                subTypeID = dstList.get(i).getSubTypeID();
                Log.e("subTypeID", subTypeID);
            }
        }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return subTypeID;
    }


    public static String devicecode = "";
    public static String devicetypeId = "";
    public static String deviceremark = "";

    public static void setDeviceClear() {
        devicecode = "";
        devicetypeId = "";
        deviceremark = "";
    }
}
