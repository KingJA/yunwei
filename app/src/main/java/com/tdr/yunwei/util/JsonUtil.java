package com.tdr.yunwei.util;

import android.app.Activity;

import com.tdr.yunwei.bean.Area2ComanyBean;
import com.tdr.yunwei.bean.CityAreaBean;
import com.tdr.yunwei.bean.CityAreaPCSBean;
import com.tdr.yunwei.bean.CompanyOfSystemBean;
import com.tdr.yunwei.bean.DASBean;
import com.tdr.yunwei.bean.DeviceMainTypeBean;
import com.tdr.yunwei.bean.DeviceSubTypeBean;
import com.tdr.yunwei.bean.DictionaryBean;
import com.tdr.yunwei.bean.MyMSG;
import com.tdr.yunwei.bean.ParamBean;
import com.tdr.yunwei.bean.RepairCompanyBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/23.
 */

public class JsonUtil {
    public static List<DeviceMainTypeBean> J2L_DeviceMainTypeBean(String date) {
        List<DeviceMainTypeBean> list = null;
        try {
            list = new ArrayList<DeviceMainTypeBean>();
            DeviceMainTypeBean dmtb ;
            JSONArray JA = new JSONArray(date);
            JSONObject JB;
            for (int i = 0; i < JA.length(); i++) {
                JB = new JSONObject(JA.get(i).toString());
                dmtb = new DeviceMainTypeBean();
                dmtb.setMainTypeID(JB.getString("MainTypeID"));
                dmtb.setRemark(JB.getString("Remark"));
                dmtb.setIsValid(JB.getString("IsValid"));
                dmtb.setLastUpdateTime(JB.getString("LastUpdateTime"));
                list.add(dmtb);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<DeviceSubTypeBean> J2L_DeviceSubTypeBean(String date) {
        List<DeviceSubTypeBean> list = null;
        try {
            list = new ArrayList<DeviceSubTypeBean>();
            DeviceSubTypeBean dstb;
            JSONArray JA = new JSONArray(date);
            JSONObject JB;
            for (int i = 0; i < JA.length(); i++) {
                JB = new JSONObject(JA.get(i).toString());
                dstb = new DeviceSubTypeBean();
                dstb.setMainTypeID(JB.getString("MainTypeID"));
                dstb.setRemark(JB.getString("Remark"));
                dstb.setIsValid(JB.getString("IsValid"));
                dstb.setLastUpdateTime(JB.getString("LastUpdateTime"));
                dstb.setSubTypeID(JB.getString("SubTypeID"));
                dstb.setForPoliceUse(JB.getString("ForPoliceUse"));
                list.add(dstb);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<RepairCompanyBean> J2L_RepairCompanyBean(String date) {
        List<RepairCompanyBean> list = null;
        try {
            list = new ArrayList<RepairCompanyBean>();
            RepairCompanyBean dstb ;
            JSONArray JA = new JSONArray(date);
            JSONObject JB;
            for (int i = 0; i < JA.length(); i++) {
                JB = new JSONObject(JA.get(i).toString());
                dstb = new RepairCompanyBean();
                dstb.setLastUpdateTime(JB.getString("LastUpdateTime"));
                dstb.setIsValid(JB.getString("IsValid"));
                dstb.setCompanyCode(JB.getString("CompanyCode"));
                dstb.setCompanyID(JB.getString("CompanyID"));
                dstb.setCompanyName(JB.getString("CompanyName"));
                dstb.setIsInstall(JB.getString("IsInstall"));
                dstb.setIsRepair(JB.getString("IsRepair"));
                list.add(dstb);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<Area2ComanyBean> J2L_Area2ComanyBean(String date) {
        List<Area2ComanyBean> list = null;
        try {
            list = new ArrayList<Area2ComanyBean>();
            Area2ComanyBean dstb ;
            JSONArray JA = new JSONArray(date);
            JSONObject JB;
            for (int i = 0; i < JA.length(); i++) {
                JB = new JSONObject(JA.get(i).toString());
                dstb= new Area2ComanyBean();
                dstb.setCompanyID(JB.getString("CompanyID"));
                dstb.setIsValid(JB.getString("IsValid"));
                dstb.setAreaID(JB.getString("AreaID"));
                dstb.setLastUpdateTime(JB.getString("LastUpdateTime"));
                list.add(dstb);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<CompanyOfSystemBean> J2L_CompanyOfSystemBean(String date) {
        List<CompanyOfSystemBean> list = null;
        try {
            list = new ArrayList<CompanyOfSystemBean>();
            CompanyOfSystemBean dstb ;
            JSONArray JA = new JSONArray(date);
            JSONObject JB;
            for (int i = 0; i < JA.length(); i++) {
                JB = new JSONObject(JA.get(i).toString());
                dstb = new CompanyOfSystemBean();
                dstb.setLastUpdateTime(JB.getString("LastUpdateTime"));
                dstb.setIsValid(JB.getString("IsValid"));
                dstb.setCompanyID(JB.getString("CompanyID"));
                dstb.setListID(JB.getString("ListID"));
                dstb.setSystemID(JB.getString("SystemID"));
                list.add(dstb);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<DASBean> J2L_DASBean(String date) {
        List<DASBean> list = null;
        try {
            list = new ArrayList<DASBean>();
            DASBean dstb ;
            JSONArray JA = new JSONArray(date);
            LOG.D(JA.length()+" JA="+JA.toString());
            JSONObject JB;
            for (int i = 0; i < JA.length(); i++) {
                JB = new JSONObject(JA.get(i).toString());
                dstb = new DASBean();
                dstb.setLastUpdateTime(JB.getString("LastUpdateTime"));
                dstb.setIsValid(JB.getString("IsValid"));
                dstb.setListID(JB.getString("ListID"));
                dstb.setSystemID(JB.getString("SystemID"));
                dstb.setAreaID(JB.getString("AreaID"));
                dstb.setDeviceTypeID(JB.getString("DeviceTypeID"));
                list.add(dstb);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<CityAreaBean> J2L_CityAreaBean(String date) {
        List<CityAreaBean> list = null;
        try {
            list = new ArrayList<CityAreaBean>();
            JSONArray JA = new JSONArray(date);
            CityAreaBean dstb ;
            JSONObject JB;
            for (int i = 0; i < JA.length(); i++) {
                JB = new JSONObject(JA.get(i).toString());
                dstb= new CityAreaBean();
                dstb.setLastUpdateTime(JB.getString("LastUpdateTime"));
                dstb.setIsValid(JB.getString("IsValid"));
                dstb.setAreaID(JB.getString("AreaID"));
                dstb.setAreaMC(JB.getString("AreaMC"));
                dstb.setAreaSort(JB.getString("AreaSort"));
                dstb.setAreaType(JB.getString("AreaType"));
                dstb.setFAreaID(JB.getString("FAreaID"));
                list.add(dstb);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<CityAreaPCSBean> J2L_CityAreaPCSBean(String date) {
        List<CityAreaPCSBean> list = null;
        try {
            list = new ArrayList<CityAreaPCSBean>();
            CityAreaPCSBean dstb;
            JSONArray JA = new JSONArray(date);
            JSONObject JB;
            for (int i = 0; i < JA.length(); i++) {
                JB = new JSONObject(JA.get(i).toString());
                dstb = new CityAreaPCSBean();
                dstb.setLastUpdateTime(JB.getString("LastUpdateTime"));
                dstb.setIsValid(JB.getString("IsValid"));
                dstb.setAreaID(JB.getString("AreaID"));
                dstb.setPCSID(JB.getString("PCSID"));
                dstb.setPCSMC(JB.getString("PCSMC"));
                list.add(dstb);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<DictionaryBean> J2L_DictionaryBean(String date) {
        List<DictionaryBean> list = null;
        try {
            list = new ArrayList<DictionaryBean>();
            DictionaryBean dstb ;
            JSONArray JA = new JSONArray(date);
            JSONObject JB;
            for (int i = 0; i < JA.length(); i++) {
                JB = new JSONObject(JA.get(i).toString());
                dstb = new DictionaryBean();
                dstb.setLastUpdateTime(JB.getString("LastUpdateTime"));
                dstb.setIsValid(JB.getString("IsValid"));
                dstb.setDictionaryID(JB.getString("DictionaryID"));
                dstb.setDictionaryName(JB.getString("DictionaryName"));
                dstb.setSystemID(JB.getString("SystemID"));
                list.add(dstb);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<ParamBean> J2L_ParamBean(String date) {
        List<ParamBean> list = null;
        try {
            list = new ArrayList<ParamBean>();
            ParamBean dstb ;
            JSONArray JA = new JSONArray(date);
            LOG.D("J2L_ParamBean_JA="+JA.length());
            JSONObject JB;
            for (int i = 0; i < JA.length(); i++) {
                JB = new JSONObject(JA.get(i).toString());
                dstb= new ParamBean();
                dstb.setLastUpdateTime(JB.getString("LastUpdateTime"));
                dstb.setIsValid(JB.getString("IsValid"));
                dstb.setDictionaryID(JB.getString("DictionaryID"));
                dstb.setFDictionarID(JB.getString("FDictionaryID"));
                dstb.setFParamId(JB.getString("FParamId"));
                dstb.setParamCode(JB.getString("ParamCode"));
                dstb.setParamID(JB.getString("ParamID"));
                dstb.setParamValue(JB.getString("ParamValue"));
                list.add(dstb);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        LOG.D("J2L_ParamBean_list="+list.size());
        return list;
    }
    public static List<MyMSG> J2L_MyMSG(Activity mActivity, String date) {
        List<MyMSG> list = null;
        try {
            list = new ArrayList<MyMSG>();
            MyMSG dstb ;
            JSONArray JA = new JSONArray(date);
            JSONObject JB;
            for (int i = 0; i < JA.length(); i++) {
                JB = new JSONObject(JA.get(i).toString());
                dstb = new MyMSG();
                dstb.setUserID(SharedUtil.getValue(mActivity, "UserId"));
                dstb.setMessageType(JB.getString("MessageType"));
                dstb.setMsgTitle("设备消息");
                dstb.setMsgText(JB.getString("Content"));
                dstb.setMsgTime(JB.getString("InTime"));
                dstb.setIsOld(false);
                list.add(dstb);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

}
