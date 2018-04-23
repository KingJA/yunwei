package com.tdr.yunwei.util;

import android.app.Activity;

import com.tdr.yunwei.bean.CityAreaBean;
import com.tdr.yunwei.bean.CityAreaPCSBean;
import com.tdr.yunwei.bean.DASBean;
import com.tdr.yunwei.bean.DictionaryBean;
import com.tdr.yunwei.bean.ParamBean;
import com.tdr.yunwei.bean.RepairCompanyBean;

import org.xutils.DbManager;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Administrator on 2017/12/5.
 */

public class DeviceInstallUtil {
    private final DbManager DB;
    private final Activity mActivity;
    private final String LastCityID;

    public DeviceInstallUtil(Activity A, DbManager db) {
        mActivity = A;
        DB = db;
        LastCityID = SharedUtil.getValue(mActivity, "CityID");
    }

    public String getSystemID(String DeviceType) {
        String systemid = "";
        try {
            List<DASBean> list = DB.selector(DASBean.class).where("DeviceTypeID", "=", DeviceType)
                    .and("AreaID", "like", LastCityID.substring(0, 4) + "%").findAll();
            if (list != null && list.size() > 0) {
                systemid = list.get(0).getSystemID();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return systemid;
    }

    /**
     * 生成32位编码
     *
     * @return string
     */
    public String getUUID() {
        String uuid = UUID.randomUUID().toString().trim();
        return uuid;
    }

    public String getParamValue(String prarmcode,String SystemID) {
        String code = "";
        List<DictionaryBean> list = null;
        try {
//            list = DB.selector(DictionaryBean.class).where("DictionaryName", "=", "ZD_OWNERS").and("SystemID", "=", SystemID).findAll();
            list = DB.selector(DictionaryBean.class).where("DictionaryName", "=", "ZD_OWNERS").findAll();
            if (list != null && list.size() > 0) {
                String DictionaryID = list.get(0).getDictionaryID();
                List<ParamBean> list2 = DB.selector(ParamBean.class).where("ParamCode", "=", prarmcode).and("DictionaryID", "=", DictionaryID).findAll();
                if (list2 != null && list2.size() == 1) {
                    code = list2.get(0).getParamValue();
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return code;
    }
    public String getCompanyName(String companycode) {
        String code = "";
        List<RepairCompanyBean> list = null;
        try {
            list = DB.selector(RepairCompanyBean.class).where("CompanyCode", "=", companycode).findAll();

            if (list != null && list.size() >= 1) {
                code = list.get(0).getCompanyName();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return code;
    }
    public String getPCS_ID(String AreaMC,String pcsMC){
        try {
            List<CityAreaPCSBean> list = DB.selector(CityAreaPCSBean.class).where("PCSMC", "=", pcsMC).findAll();
            if (list != null && list.size() > 0) {
                for (int i = 0; i <list.size(); i++) {
                    if(getAreaID(AreaMC).equals(list.get(i).getAreaID())){
                        return   list.get(i).getPCSID();
                    }
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return "";
    }
    public String getPCSMC(String PCSID) {
        String PCSMC = "";
        if (PCSID.equals("") || PCSID == null) {
            return PCSMC;
        } else {
            List<CityAreaPCSBean> list = null;
            try {
                list = DB.findAll(CityAreaPCSBean.class);
            } catch (DbException e) {
                e.printStackTrace();
            }
            if (list == null) {
                list = new ArrayList<CityAreaPCSBean>();
            }
            for (int i = 0; i < list.size(); i++) {
//                LOG.E("PCSID="+list.get(i).getPCSID() + "  PCSMC=" + list.get(i).getPCSMC());
                if (list.get(i).getPCSID().equals(PCSID)) {
                    PCSMC = list.get(i).getPCSMC();
                }
            }

            return PCSMC;
        }
    }
    /**
     * 获取区域
     */
    public List<String> getAreaList() {
        String l = LastCityID.substring(4, 6);
        LOG.E("LastCityID=" + LastCityID);
        List<CityAreaBean> areaBeanList = null;
        List<String> list = null;
        try {
            if (l.equals("00")) {
                areaBeanList = DB.selector(CityAreaBean.class).where("FAreaID", "like", LastCityID.substring(0, 4) + "%").findAll();
            } else {
                areaBeanList = DB.selector(CityAreaBean.class).where("AreaID", "=", LastCityID).findAll();
            }
            list = new ArrayList<String>();
            if (areaBeanList == null) {
                areaBeanList = new ArrayList<CityAreaBean>();
            }
            for (int i = 0; i < areaBeanList.size(); i++) {
                list.add(areaBeanList.get(i).getAreaMC());
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return list;
    }

    public String getAreaMC(String areamc) {
        List<String> list = getAreaList();
        LOG.E("辖区列表："+list.size());
        for (int i = 0; i < list.size(); i++) {
            LOG.E("辖区列表："+list.get(i));
            if (list.get(i).contains(areamc)) {
                return  list.get(i);
            }
        }
        return "";
    }
    public String AreaID2AreaMC(String AreaID) {
        String AreaMC = "";

//        LOG.D("辖区AreaID="+AreaID);
        if (AreaID.equals("") || AreaID == null) {
            return AreaMC;
        } else {
            List<CityAreaBean> list = null;
            try {
                list = DB.selector(CityAreaBean.class).where("AreaID", "=", AreaID).findAll();
//                LOG.D("辖区列表="+list.size());
            } catch (DbException e) {
                e.printStackTrace();
            }
            if (list != null && list.size() > 0) {
                AreaMC = list.get(0).getAreaMC();
            }
            return AreaMC;
        }

    }
    public Map<String, String> getTypeList(String SystemID) {
        Map<String, String> typeMap = new HashMap<>();
        try {
            List<DictionaryBean> list = DB.selector(DictionaryBean.class).where("DictionaryName", "=", "ZD_DEVICETYPE").and("SystemID", "=", SystemID).findAll();
            if (list != null && list.size() > 0) {
                String DictionaryID = list.get(0).getDictionaryID();
                List<ParamBean> list2 = DB.selector(ParamBean.class).where("DictionaryID", "=", DictionaryID).findAll();
                if (list2 != null && list2.size() > 0) {
                    for (int i = 0; i < list2.size(); i++) {
                        typeMap.put(list2.get(i).getParamValue(), list2.get(i).getParamCode());
                    }
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return typeMap;
    }

    /**
     * 通过区域名字来找区域ID
     */
    public String getAreaID(String AreaMC) {
        String str = "";
        List<CityAreaBean> areaBeanList = null;
        try {
            areaBeanList = DB.selector(CityAreaBean.class).where("AreaMC", "=", AreaMC).findAll();
            if (areaBeanList != null && areaBeanList.size() > 0) {
                str = areaBeanList.get(0).getAreaID();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 通过区域ID来找派出所
     */

    public List<String> getPCSList(String AreaID) {
        List<CityAreaPCSBean> pcsBeanList = null;
        try {
            pcsBeanList = DB.selector(CityAreaPCSBean.class).where("AreaID", "=", AreaID).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (pcsBeanList == null) {
            pcsBeanList = new ArrayList<CityAreaPCSBean>();
        }
        LOG.E("getPCSList=" + pcsBeanList.size() + "");
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < pcsBeanList.size(); i++) {
            LOG.E("PCS：" + pcsBeanList.get(i).getPCSMC() + "" + pcsBeanList.get(i).getPCSID());
            list.add(pcsBeanList.get(i).getPCSMC());
        }

        return list;

    }
    public String getParamCode(String prarmvalue) {
        String code = "";
        try {
            List<ParamBean> list = DB.selector(ParamBean.class).where("ParamValue", "=", prarmvalue).findAll();
            if (list != null && list.size() >= 1) {
                code = list.get(0).getParamCode();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return code;
    }
    public String getCompanyCode(String companyname) {
        String code = "";
        List<RepairCompanyBean> list = null;
        try {
            List<RepairCompanyBean> list2=DB.findAll(RepairCompanyBean.class);
            if(list != null ){
                for (RepairCompanyBean rcb:list2){
                    LOG.E("运维公司代码："+rcb.getCompanyCode());
                }
            }
            list = DB.selector(RepairCompanyBean.class).where("CompanyName", "=", companyname).findAll();

            if (list != null && list.size() >= 1) {
                code = list.get(0).getCompanyCode();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return code;
    }

    public List<String> getOwnerList(String SystemID) {
        List<String> list3 = new ArrayList<String>();
        List<String> list4 = new ArrayList<String>();
        List<DictionaryBean> list = null;
        try {
            LOG.D("SystemID=" +SystemID);
//            list = DB.selector(DictionaryBean.class).where("DictionaryName", "=", "ZD_OWNERS").and("SystemID", "=", SystemID).findAll();
            list = DB.selector(DictionaryBean.class).where("DictionaryName", "=", "ZD_OWNERS").findAll();
            if (list == null) {
                list = new ArrayList<DictionaryBean>();
                LOG.D("产权人总列表=" + list.size());
            }

            if (list != null && list.size() > 0) {

                for (DictionaryBean dictionaryBean : list) {
                    LOG.D("产权人SystemID=" + dictionaryBean.getSystemID());
                    LOG.D("产权人DictionaryID=" + dictionaryBean.getDictionaryID());
                    LOG.D("产权人DictionaryName=" + dictionaryBean.getDictionaryName());
                }
                List<ParamBean> list21 = DB.findAll(ParamBean.class);
                LOG.D("产权人总数=" + list21.size());
                for (ParamBean parambean : list21) {
                    LOG.D("产权人ParamValue=" + parambean.getParamValue());
                    LOG.D("产权人DictionaryID=" + parambean.getDictionaryID());
                }


                String DictionaryID = list.get(0).getDictionaryID();
                //ToastUtil.showShort(mActivity,"DictionaryID="+DictionaryID);
                List<ParamBean> list2 = DB.selector(ParamBean.class).where("DictionaryID", "=", DictionaryID).findAll();
                LOG.D("产权人列表=" + list2.size());
                if (list2 != null && list2.size() > 0) {
                    for (int i = 0; i < list2.size(); i++) {
                        list3.add(list2.get(i).getParamValue());
                    }
                }
                HashSet h = new HashSet(list3);
                list4.clear();
                list4.addAll(h);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return list4;
    }
}
