package com.tdr.yunwei.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tdr.yunwei.R;
import com.tdr.yunwei.YunWeiApplication;
import com.tdr.yunwei.bean.Area2ComanyBean;
import com.tdr.yunwei.bean.CityAreaBean;
import com.tdr.yunwei.bean.CityAreaPCSBean;
import com.tdr.yunwei.bean.CityBean;
import com.tdr.yunwei.bean.CompanyOfSystemBean;
import com.tdr.yunwei.bean.DASBean;
import com.tdr.yunwei.bean.DeviceMainTypeBean;
import com.tdr.yunwei.bean.DeviceSubTypeBean;
import com.tdr.yunwei.bean.DictionaryBean;
import com.tdr.yunwei.bean.MyMSG;
import com.tdr.yunwei.bean.ParamBean;
import com.tdr.yunwei.bean.RepairCompanyBean;
import com.tdr.yunwei.bean.RepairOrderBean;
import com.tdr.yunwei.bean.RequestBean;
import com.tdr.yunwei.reviceandbroad.NetChangeReceiver;
import com.tdr.yunwei.util.ActivityUtil;
import com.tdr.yunwei.util.Constants;
import com.tdr.yunwei.util.DBUtils;
import com.tdr.yunwei.util.JsonUtil;
import com.tdr.yunwei.util.LOG;
import com.tdr.yunwei.util.SharedUtil;
import com.tdr.yunwei.util.ToastUtil;
import com.tdr.yunwei.util.WebUtil;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/4/20.
 */
public class UpdateActivity extends Activity {

    private Activity mActivity;
    private DbManager DB;
    private int max = 0;
    private TextView txt_min, txt_tip;
    String title = "", type;

    String LastCityID = "", ServerTime;
    private String lasttime = "";
    private YunWeiApplication YWA;

    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_update);

        mActivity = this;
        ActivityUtil.AddActivity(mActivity);
        type = getIntent().getStringExtra("type");
        YWA = YunWeiApplication.getInstance();
//        DB = YWA.getDB();
        DB= x.getDb(DBUtils.getDb());
        ServerTime = SharedUtil.getValue(mActivity, "ServerTime");

        RegReceiver();

        txt_min = (TextView) findViewById(R.id.txt_min);
        txt_tip = (TextView) findViewById(R.id.txt_tip);


        String[] RegionIDs = SharedUtil.getValue(mActivity, "RegionID").split(",");
        UpdateDeviceType();

    }


    private List<DeviceMainTypeBean> DMTList;
    private List<DeviceSubTypeBean> DSTList;
    private List<RepairCompanyBean> companyList;
    private List<Area2ComanyBean> area2ComanyList;
    private List<CityAreaBean> cityAreaBeanList;
    private List<CityAreaPCSBean> cityAreaPCSBeanList;
    private List<CompanyOfSystemBean> companyOfSystemBeanList;
    private List<DASBean> dasBeanList;
    //更新

    //省市更新数量、包括省份、城市
    private List<CityBean> cityBeanList;

    private void UpdateDeviceType() {
        lasttime = SharedUtil.getValue(mActivity, "DeviceTypeUpdateTime");
        LOG.D("lasttime="+lasttime);
        if (lasttime.equals("")) {
            lasttime = Constants.DefaultTime;
        }

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("accessToken", SharedUtil.getToken(mActivity));
        map.put("areaID", SharedUtil.getValue(mActivity,"CityID"));
        map.put("lastUpdateTime", lasttime);


        WebUtil.getInstance(mActivity).webRequest(Constants.UpdateDeviceType, map, new WebUtil.MyCallback() {

            @Override
            public void onSuccess(String result) {

                if (result.equals("-1")) {
                    ActivityUtil.ExitApp(mActivity);
                    return;
                }
                Download2 download2 = new Download2(result);
                download2.execute();

            }
        });

    }

    //更新主设备
    private class Download2 extends AsyncTask<String, Integer, String> {

        private String result;

        public Download2(String result) {
            this.result = result;
        }

        @Override
        protected String doInBackground(String... params) {
            List<DeviceMainTypeBean> listDMT = null;
            try {
                JSONObject jsonObject = new JSONObject(result);
                int Count = jsonObject.getInt("DMTCount");

                String DMTID = "", DMTNewID = "";
                String data = jsonObject.getString("DeviceMainTypeList");
//                DMTList = gson.fromJson(data, new TypeToken<List<DeviceMainTypeBean>>() {
//                }.getType());
                DMTList = (List<DeviceMainTypeBean>) DBUtils.SetListID(DB, JsonUtil.J2L_DeviceMainTypeBean(data), DeviceMainTypeBean.class);

                listDMT = DB.findAll(DeviceMainTypeBean.class);

                if (listDMT!=null&&listDMT.size() > 0) {
                    DB.delete(DeviceMainTypeBean.class);
//                    if (DMTList.size() == 0 && lasttime.equals(Constants.DefaultTime)) {
//                    }
                    for (int i = 0; i < listDMT.size(); i++) {
                        DMTID = listDMT.get(i).getMainTypeID();
                        for (int j = 0; j < DMTList.size(); j++) {
                            DMTNewID = DMTList.get(j).getMainTypeID();
                            if (DMTNewID.equals(DMTID)) {
                                DB.delete(DeviceMainTypeBean.class, WhereBuilder.b("MainTypeID", "=", DMTID));
                            }
                        }
                    }
                }

                max = DMTList.size();
                title = "主设备";
                for (int j = 0; j < max; j++) {
                    if (DMTList.get(j).getIsValid().equals("1")) {
                        DB.save(DMTList.get(j));
                        publishProgress(j);
                    }
                }
                LOG.D("DeviceMainTypeBean="+DB.findAll(DeviceMainTypeBean.class).size());
                //更新子设备
                String DSTID = "", DSTNewID = "";
                String data2 = jsonObject.getString("DeviceSubTypeList");
//                DSTList = gson.fromJson(data2, new TypeToken<List<DeviceSubTypeBean>>() {
//                }.getType());

                DSTList = (List<DeviceSubTypeBean>) DBUtils.SetListID(DB, JsonUtil.J2L_DeviceSubTypeBean(data2), DeviceSubTypeBean.class);
                List<DeviceSubTypeBean> listDST = DB.findAll(DeviceSubTypeBean.class);
                if (DSTList != null && listDST != null) {
                    if (listDST.size() > 0) {
                        for (int j = 0; j < DSTList.size(); j++) {
                            DSTNewID = DSTList.get(j).getSubTypeID();
                            for (int i = 0; i < listDST.size(); i++) {
                                DSTID = listDST.get(i).getSubTypeID();
                                if (DSTID.equals(DSTNewID)) {
                                    DB.delete(DeviceSubTypeBean.class, WhereBuilder.b("SubTypeID", "=", DSTID));
                                }
                            }

                        }
                    }
                }
                max = DSTList.size();
//                for (int i = 0; i < DSTList.size(); i++) {
//                    Log.d("Pan", "ID=" + DSTList.get(i).getID());
//                    Log.d("Pan", "getSubTypeID=" + DSTList.get(i).getSubTypeID());
//                    Log.d("Pan", "getForPoliceUse=" + DSTList.get(i).getForPoliceUse());
//                    Log.d("Pan", "getIsValid=" + DSTList.get(i).getIsValid());
//                    Log.d("Pan", "getLastUpdateTime=" + DSTList.get(i).getLastUpdateTime());
//                    Log.d("Pan", "getMainTypeID=" + DSTList.get(i).getMainTypeID());
//                    Log.d("Pan", "getRemark=" + DSTList.get(i).getRemark());
//                }
                title = "子设备";
                for (int j = 0; j < max; j++) {
                    if (DSTList.get(j).getIsValid().equals("1")) {
                        DB.save(DSTList.get(j));
//                        LOG.D("保存子设备到数据库");
                        publishProgress(j);
                    }
                }
                LOG.D("DeviceSubTypeBean="+DB.findAll(DeviceSubTypeBean.class).size());
            } catch (JSONException e) {
                LOG.E("数据库存储异常" + e.toString());
            } catch (DbException e) {
                e.printStackTrace();
            }
            if (DMTList.size() == 0 && !lasttime.equals(Constants.DefaultTime)) {
                return "空数据";
            } else {
                return "完成";
            }
        }

        @Override
        protected void onProgressUpdate(Integer... value) {
            super.onProgressUpdate(value);
            txt_min.setText((value[0] + 1) + "/" + max);

            if (title.equals("主设备")) {
                txt_tip.setText("正在更新主设备");
            }
            if (title.equals("子设备")) {
                txt_tip.setText("正在更新子设备");
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("完成")) {
                SharedUtil.setValueByKey(mActivity, "DeviceTypeUpdateTime", ServerTime);
            } else if (s.equals("空数据")) {
                SharedUtil.setValueByKey(mActivity, "DeviceTypeUpdateTime", Constants.DefaultTime);
            }
            UpdateRepairCompany();
        }
    }

    private void UpdateRepairCompany() {
        String lasttime = SharedUtil.getValueByKey(mActivity, "CompanyUpdateTime");
        if (lasttime.equals("")) {
            lasttime = Constants.DefaultTime;
        }

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("accessToken", SharedUtil.getToken(mActivity));
        map.put("lastUpdateTime", lasttime);
        WebUtil.getInstance(mActivity).webRequest(Constants.UpdateRepairCompany, map, new WebUtil.MyCallback() {

            @Override
            public void onSuccess(String result) {
                if (result.equals("-1")) {
                    ActivityUtil.ExitApp(mActivity);
                    return;
                }
                Download3 download3 = new Download3(result);
                download3.execute();


            }
        });

    }

    //更新运维公司
    private class Download3 extends AsyncTask<String, Integer, String> {
        private String result;

        public Download3(String result) {
            this.result = result;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                JSONObject jsonObject = new JSONObject(result);

                int Count = jsonObject.getInt("COCount");
                if (Count > 0) {
                    String data = jsonObject.getString("RepairCompanyList");
                    companyList = (List<RepairCompanyBean>) DBUtils.SetListID(DB, JsonUtil.J2L_RepairCompanyBean(data), RepairCompanyBean.class);
                    List<RepairCompanyBean> list = DB.findAll(RepairCompanyBean.class);
                    if (list!=null&&list.size() > 0) {
                        String OldID = "", NewID = "";
                        for (int j = 0; j < companyList.size(); j++) {
                            NewID = companyList.get(j).getCompanyID();
                            for (int i = 0; i < list.size(); i++) {
                                OldID = list.get(i).getCompanyID();
                                if (OldID.equals(NewID)) {
                                    DB.delete(RepairCompanyBean.class, WhereBuilder.b("CompanyID", "=", OldID));
                                }
                            }

                        }
                    }
                    max = companyList.size();
                    for (int i = 0; i < max; i++) {
                        if (companyList.get(i).getIsValid().equals("1")) {
                            DB.save(companyList.get(i));
//                            LOG.D("保存运营公司到数据库");
                            publishProgress(i);
                        }
                    }
                    LOG.D("RepairCompanyBean="+DB.findAll(RepairCompanyBean.class).size());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (DbException e) {
                e.printStackTrace();
            }
            return "完成";
        }

        @Override
        protected void onProgressUpdate(Integer... value) {
            super.onProgressUpdate(value);
            txt_min.setText((value[0] + 1) + "/" + max);
            txt_tip.setText("正在更新运维公司");

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("完成")) {
                SharedUtil.setValueByKey(mActivity, "CompanyUpdateTime", ServerTime);
                UpdateArea2CORelation();
            }
        }
    }


    //更新公司区域关联关系
    private void UpdateArea2CORelation() {
        String lasttime = SharedUtil.getValueByKey(mActivity, "UpdateArea2CORelationTime");

        if (lasttime.equals("")) {
            lasttime = Constants.DefaultTime;
        }
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("accessToken", SharedUtil.getToken(mActivity));
        map.put("lastUpdateTime", lasttime);

        WebUtil.getInstance(mActivity).webRequest(Constants.UpdateArea2CORelation, map, new WebUtil.MyCallback() {

            @Override
            public void onSuccess(String result) {

                Download4 download4 = new Download4(result);
                download4.execute();
            }
        });
    }

    private class Download4 extends AsyncTask<String, Integer, String> {
        private String result;


        public Download4(String result) {
            this.result = result;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                int Count = jsonObject.getInt("Count");
                if (Count > 0) {
                    String data = jsonObject.getString("Area2ComanyList");
                    area2ComanyList = (List<Area2ComanyBean>) DBUtils.SetListID(DB, JsonUtil.J2L_Area2ComanyBean(data), Area2ComanyBean.class);
                    List<Area2ComanyBean> list = DB.findAll(Area2ComanyBean.class);
                    String OldID, NewID;

                    if (list!=null&&list.size() > 0) {
                        for (int j = 0; j < area2ComanyList.size(); j++) {
                            NewID = area2ComanyList.get(j).getCompanyID();

                            for (int i = 0; i < list.size(); i++) {
                                OldID = list.get(i).getCompanyID();
                                if (OldID.equals(NewID)) {
                                    DB.delete(Area2ComanyBean.class, WhereBuilder.b("CompanyID", "=", OldID));
                                }
                            }
                        }
                    }


                    max = area2ComanyList.size();
                    for (int i = 0; i < max; i++) {
                        if (area2ComanyList.get(i).getIsValid().equals("1")) {
                            DB.save(area2ComanyList.get(i));
//                            LOG.D("保存area2Comany到数据库");
                            publishProgress(i);
                        }
                    }
                    LOG.D("Area2ComanyBean="+DB.findAll(Area2ComanyBean.class).size());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (DbException e) {
                e.printStackTrace();
            }
            return "完成";
        }

        @Override
        protected void onProgressUpdate(Integer... value) {
            super.onProgressUpdate(value);
            txt_min.setText((value[0] + 1) + "/" + max);
            txt_tip.setText("正在更新公司区域关联");

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("完成")) {
                Log.e("UpdateArea2Time", ServerTime);
                SharedUtil.setValueByKey(mActivity, "UpdateArea2CORelationTime", ServerTime);
                UpdateCompanyOfSystem();
            }
        }
    }


    private void UpdateCompanyOfSystem() {
        String lasttime = SharedUtil.getValueByKey(mActivity, "CompanyOfSystemTime");
        if (lasttime.equals("")) {
            lasttime = Constants.DefaultTime;
        }

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("accessToken", SharedUtil.getToken(mActivity));
        map.put("lastUpdateTime", lasttime);
        WebUtil.getInstance(mActivity).webRequest(Constants.UpdateCompanyOfSystem, map, new WebUtil.MyCallback() {

            @Override
            public void onSuccess(String result) {

                if (result.equals("-1")) {
                    ActivityUtil.ExitApp(mActivity);
                    return;
                }
                Download5 download5 = new Download5(result);
                download5.execute();


            }
        });
    }


    private class Download5 extends AsyncTask<String, Integer, String> {

        private String result;

        public Download5(String result) {
            this.result = result;
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                JSONObject jsonObject = new JSONObject(result);
                int Count = jsonObject.getInt("Count");
                if (Count > 0) {
                    String data = jsonObject.getString("CompanyOfSystemList");
                    companyOfSystemBeanList = (List<CompanyOfSystemBean>) DBUtils.SetListID(DB, JsonUtil.J2L_CompanyOfSystemBean(data), CompanyOfSystemBean.class);
                    List<CompanyOfSystemBean> list = DB.findAll(CompanyOfSystemBean.class);
                    if (list!=null&&list.size() > 0) {
                        String OldID = "", NewID;
                        for (int j = 0; j < companyOfSystemBeanList.size(); j++) {
                            NewID = companyOfSystemBeanList.get(j).getListID();

                            for (int i = 0; i < list.size(); i++) {
                                OldID = list.get(i).getListID();
                                if (OldID.equals(NewID)) {
                                    DB.delete(CompanyOfSystemBean.class, WhereBuilder.b("ListID", "=", OldID));
                                }
                            }
                        }
                    }
                    max = companyOfSystemBeanList.size();
                    for (int i = 0; i < max; i++) {
                        if (companyOfSystemBeanList.get(i).getIsValid().equals("1")) {
                            DB.save(companyOfSystemBeanList.get(i));
//                            LOG.D("保存companyOfSystemBean到数据库");
                            publishProgress(i);
                        }
                    }
                    LOG.D("CompanyOfSystemBean="+DB.findAll(CompanyOfSystemBean.class).size());
                }

            } catch (final Exception e) {
               LOG.E("CompanyOfSystemBean异常"+e.toString());

            }
            return "完成";
        }

        @Override
        protected void onProgressUpdate(Integer... value) {
            super.onProgressUpdate(value);
            txt_min.setText((value[0] + 1) + "/" + max);
            txt_tip.setText("正在更新业务系统");
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("完成")) {
                SharedUtil.setValueByKey(mActivity, "CompanyOfSystemTime", ServerTime);
                UpdateDAS();
            }
        }
    }

    //8.8.	更新设备类型区域业务系统
    private void UpdateDAS() {
        String lasttime = SharedUtil.getValueByKey(mActivity, "UpdateDASTime");
        if (lasttime.equals("")) {
            lasttime = Constants.DefaultTime;
        }

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("accessToken", SharedUtil.getToken(mActivity));
        map.put("lastUpdateTime", lasttime);
        WebUtil.getInstance(mActivity).webRequest(Constants.UpdateDAS, map, new WebUtil.MyCallback() {

            @Override
            public void onSuccess(String result) {

                if (result.equals("-1")) {
                    ActivityUtil.ExitApp(mActivity);
                    return;
                }
                Download6 download6 = new Download6(result);
                download6.execute();


            }
        });
    }

    private class Download6 extends AsyncTask<String, Integer, String> {

        private String result;

        public Download6(String result) {
            this.result = result;
            LOG.E("DASBean=  "+result);
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                JSONObject jsonObject = new JSONObject(result);
                String ErrorCode = jsonObject.getString("ErrorCode");
                int Count = jsonObject.getInt("Count");
                if (Count > 0) {

                    String data = jsonObject.getString("DASList");

                    dasBeanList = (List<DASBean>) DBUtils.SetListID(DB, JsonUtil.J2L_DASBean(data), DASBean.class);
                    List<DASBean> list = DB.findAll(DASBean.class);
                    if (list!=null&&list.size() > 0) {
                        String OldID = "", NewID;
                        for (int j = 0; j < dasBeanList.size(); j++) {
                            NewID = dasBeanList.get(j).getListID();

                            for (int i = 0; i < list.size(); i++) {
                                OldID = list.get(i).getListID();
                                if (OldID.equals(NewID)) {
                                    DB.delete(DASBean.class, WhereBuilder.b("ListID", "=", OldID));
                                }
                            }
                        }
                    }

                    LOG.D("dasBeanList.size="+dasBeanList.size());
                    max = dasBeanList.size();
                    for (int i = 0; i < max; i++) {
//                        LOG.D("getIsValid= "+dasBeanList.get(i).getIsValid());
//                        LOG.D("getLastUpdateTime= "+dasBeanList.get(i).getLastUpdateTime());
//                        LOG.D("getAreaID= "+dasBeanList.get(i).getAreaID());
//                        LOG.D("getDeviceTypeID= "+dasBeanList.get(i).getDeviceTypeID());
//                        LOG.D("getListID= "+dasBeanList.get(i).getListID());
//                        LOG.D("getSystemID= "+dasBeanList.get(i).getSystemID());
                        if (dasBeanList.get(i).getIsValid().equals("1")) {
                            String LastCityID = SharedUtil.getValue(mActivity,"CityID");
                            LOG.E("LastCityID="+LastCityID + "   &&&&&   AreaID()=" + dasBeanList.get(i).getAreaID());
                            if (LastCityID.equals(dasBeanList.get(i).getAreaID())) {
                                DB.save(dasBeanList.get(i));
//                                LOG.D("保存dasBean到数据库");
                                publishProgress(i);
                            }
                        }
                    }
                    LOG.D("DASBean="+DB.findAll(DASBean.class).size());
                }

            } catch (final Exception e) {
                LOG.D("DASBean异常="+e.toString());
            }
            return "完成";
        }

        @Override
        protected void onProgressUpdate(Integer... value) {
            super.onProgressUpdate(value);
            txt_min.setText((value[0] + 1) + "/" + max);
            txt_tip.setText("正在更新设备类型区域业务系统");
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("完成")) {
                SharedUtil.setValueByKey(mActivity, "UpdateDASTime", ServerTime);
                UpdateCityArea();
            }
        }
    }

    //8.4.	更新城市下属区域信息
    private void UpdateCityArea() {

        String lasttime = SharedUtil.getValueByKey(mActivity, "UpdateCityAreaTime");
        if (lasttime.equals("")) {
            lasttime = Constants.DefaultTime;
        }


        HashMap<String, Object> map = new HashMap<String, Object>();

        map.put("accessToken", SharedUtil.getToken(mActivity));
        map.put("cityID",  SharedUtil.getValue(mActivity,"CityID"));
        map.put("lastUpdateTime", lasttime);


        WebUtil.getInstance(mActivity).webRequest(Constants.UpdateCityArea, map, new WebUtil.MyCallback() {
            @Override
            public void onSuccess(String result) {

                Download7 download7 = new Download7(result);
                download7.execute();

            }
        });
    }

    private class Download7 extends AsyncTask<String, Integer, String> {

        private String result;

        public Download7(String result) {
            this.result = result;
        }

        @Override
        protected String doInBackground(String... params) {


            try {

                JSONObject jsonObject = new JSONObject(result);
                String ErrorCode = jsonObject.getString("ErrorCode");
                String ErrorMsg = jsonObject.getString("ErrorMsg");

                int AreaCount = jsonObject.getInt("AreaCount");
                int PCSCount = jsonObject.getInt("PCSCount");
                String OldID = "", NewID = "";

                if (AreaCount > 0) {
                    String data = jsonObject.getString("AreaList");
                    cityAreaBeanList = (List<CityAreaBean>) DBUtils.SetListID(DB, JsonUtil.J2L_CityAreaBean(data), CityAreaBean.class);
                    List<CityAreaBean> list = DB.findAll(CityAreaBean.class);
                    max = cityAreaBeanList.size();
                    title = "区域";
                    if (list!=null&&list.size() > 0) {
                        for (int i = 0; i < max; i++) {
                            NewID = cityAreaBeanList.get(i).getAreaID();
                            for (int j = 0; j < list.size(); j++) {
                                OldID = list.get(j).getAreaID();
                                if (OldID.equals(NewID)) {
                                    DB.delete(CityAreaBean.class, WhereBuilder.b("AreaID", "=", OldID));
                                }
                            }
                        }
                    }
                    for (int i = 0; i < max; i++) {
                        if (cityAreaBeanList.get(i).getIsValid().equals("1")) {
                            DB.save(cityAreaBeanList.get(i));
//                            LOG.D("保存cityAreaBean到数据库");
                            publishProgress(i);
                        }
                    }
                    LOG.D("CityAreaBean="+DB.findAll(CityAreaBean.class).size());
                }

                if (PCSCount > 0) {
                    String data = jsonObject.getString("PCSList");
                    cityAreaPCSBeanList = (List<CityAreaPCSBean>) DBUtils.SetListID(DB, JsonUtil.J2L_CityAreaPCSBean(data), CityAreaBean.class);
                    List<CityAreaPCSBean> list = DB.findAll(CityAreaPCSBean.class);

                    if (list!=null&&list.size() > 0) {
                        for (int i = 0; i < cityAreaPCSBeanList.size(); i++) {

                            NewID = cityAreaPCSBeanList.get(i).getPCSID();
                            for (int j = 0; j < list.size(); j++) {
                                OldID = list.get(j).getPCSID();
                                if (OldID.equals(NewID)) {
                                    DB.delete(CityAreaPCSBean.class, WhereBuilder.b("PCSID", "=", OldID));
                                }
                            }


                        }
                    }
                    max = cityAreaPCSBeanList.size();
                    title = "派出所";
                    for (int i = 0; i < cityAreaPCSBeanList.size(); i++) {
                        if (cityAreaPCSBeanList.get(i).getIsValid().equals("1")) {
                            DB.save(cityAreaPCSBeanList.get(i));
//                            LOG.D("保存cityAreaPCSBean到数据库");
                            publishProgress(i);
                        }
                    }
                    LOG.D("CityAreaPCSBean="+DB.findAll(CityAreaPCSBean.class).size());
                }


            } catch (final Exception e) {
                e.printStackTrace();

            }
            return "完成";
        }

        @Override
        protected void onProgressUpdate(Integer... value) {
            super.onProgressUpdate(value);
            txt_min.setText((value[0] + 1) + "/" + max);

            if (title.equals("区域")) {
                txt_tip.setText("正在更新区域");
            }
            if (title.equals("派出所")) {
                txt_tip.setText("正在更新派出所");
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("完成")) {
                SharedUtil.setValueByKey(mActivity, "UpdateCityAreaTime", ServerTime);
                UpdateDictionary();
            }
        }
    }

    //8.9.	更新业务系统字典表

    List<DictionaryBean> dictionaryBeanList;
    List<ParamBean> paramBeanList;

    private void UpdateDictionary() {

        List<DASBean> list = null;
        try {
            list = DB.findAll(DASBean.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        if(list==null){
//            LOG.D("UpdateDictionary----list=null");
            list=new ArrayList<DASBean>();
        }
//        for (int i = 0; i < list.size(); i++) {
//            LOG.D("list____SystemID"+list.get(i).getSystemID());
//            LOG.D("list____LastUpdateTime"+list.get(i).getLastUpdateTime());
//        }
        List<RequestBean> requestBeanList = new ArrayList<RequestBean>();

        String lasttime = SharedUtil.getValueByKey(mActivity, "UpdateDictionaryTime");
        if(lasttime.equals("")){
            lasttime=Constants.DefaultTime;
        }
        for (int i = 0; i < list.size(); i++) {
            RequestBean bean = new RequestBean();
            bean.setSystemID(list.get(i).getSystemID());
//            if (list.get(i).getLastUpdateTime().equals("")) {
//                bean.setLastUpdateTime(lasttime);
//            } else {
//                bean.setLastUpdateTime(list.get(i).getLastUpdateTime());
//            }
            bean.setLastUpdateTime(lasttime);
            requestBeanList.add(bean);
        }
        Gson gson=new Gson();
        String aa = gson.toJson(requestBeanList);

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("accessToken", SharedUtil.getToken(mActivity));
        map.put("requestList", aa);
        map.put("lastUpdateTime", lasttime);
        WebUtil.getInstance(mActivity).webRequest(Constants.UpdateDictionary, map, new WebUtil.MyCallback() {
            @Override
            public void onSuccess(String result) {
                LOG.E("result="+result);
                Download8 download8 = new Download8(result);
                download8.execute();

            }
        });
    }

    private class Download8 extends AsyncTask<String, Integer, String> {

        private String result;

        public Download8(String result) {
            this.result = result;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String ErrorCode = jsonObject.getString("ErrorCode");

                int DCnt = jsonObject.getInt("DCnt");
                String OldID = "", NewID = "";

                if (DCnt > 0) {
                    String data = jsonObject.getString("DictionaryList");
                    dictionaryBeanList= (List<DictionaryBean>) DBUtils.SetListID(DB,JsonUtil.J2L_DictionaryBean(data),DictionaryBean.class);
                    for (int i = 0; i < dictionaryBeanList.size(); i++) {
                        LOG.E( "getIsValid=" + dictionaryBeanList.get(i).getIsValid());
                        LOG.E( "getSystemID=" + dictionaryBeanList.get(i).getSystemID());
                        LOG.E( "getDictionaryName=" + dictionaryBeanList.get(i).getDictionaryName());
                        LOG.E(  "getDictionaryID=" + dictionaryBeanList.get(i).getDictionaryID());
                        LOG.E(  "===========================");
                    }
                    LOG.D(dictionaryBeanList==null?"dictionaryBeanList.size="+dictionaryBeanList.size():"");
                    List<DictionaryBean> list = DB.findAll(DictionaryBean.class);
                    if (list!=null&&list.size() > 0) {
                        for (int i = 0; i < dictionaryBeanList.size(); i++) {

                            NewID = dictionaryBeanList.get(i).getDictionaryID();
                            for (int j = 0; j < list.size(); j++) {
                                OldID = list.get(j).getDictionaryID();
                                if (OldID.equals(NewID)) {
                                    LOG.D("删除老数据  "+OldID);
                                    DB.delete(DictionaryBean.class,WhereBuilder.b("DictionaryID","=",OldID));
                                }
                            }
                        }
                    }
                    max = dictionaryBeanList.size();
                    title = "公司业务系统";
                    LOG.D("max="+max);
                    for (int i = 0; i < max; i++) {
                        if (dictionaryBeanList.get(i).getIsValid().equals("1")) {
                            LOG.D("保存DictionaryBean="+i);
                            DB.save(dictionaryBeanList.get(i));
//                            LOG.D("保存dictionaryBean到数据库");
                            publishProgress(i);
                        }
                    }
                    LOG.D("DictionaryBean="+DB.findAll(DictionaryBean.class).size());
                }

            } catch (final Exception e) {
                e.printStackTrace();

            }
            return "完成";
        }

        @Override
        protected void onProgressUpdate(Integer... value) {
            super.onProgressUpdate(value);
            txt_min.setText((value[0] + 1) + "/" + max);
            txt_tip.setText("正在更新" + title);


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("完成")) {
                Download9 download9 = new Download9(result);
                download9.execute();
            }
        }
    }


    private class Download9 extends AsyncTask<String, Integer, String> {

        private String result;

        public Download9(String result) {
            this.result = result;
        }

        @Override
        protected String doInBackground(String... params) {


            try {

                JSONObject jsonObject = new JSONObject(result);

                int PCnt = jsonObject.getInt("PCnt");
                String OldID = "", NewID = "";
                LOG.D("result="+result);
                if (PCnt > 0) {
                    String data = jsonObject.getString("ParamList");
                    paramBeanList= (List<ParamBean>) DBUtils.SetListID(DB,JsonUtil.J2L_ParamBean(data),ParamBean.class);
                    LOG.D("paramBeanList="+paramBeanList.size());
                    List<ParamBean> list = DB.findAll(ParamBean.class);
                    if(list==null){
                        list=new ArrayList<ParamBean>();
                    }
                    LOG.D("list="+list.size());
                    if (list!=null&&list.size() > 0) {
                        for (int i = 0; i < paramBeanList.size(); i++) {
                            NewID = paramBeanList.get(i).getParamID();
                            for (int j = 0; j < list.size(); j++) {
                                OldID = list.get(j).getParamID();
                                if (OldID.equals(NewID)) {
                                    DB.delete(ParamBean.class,WhereBuilder.b("ParamID","=",OldID));
                                }
                            }
                        }
                    }

                    max = paramBeanList.size();
                    title = "产权人故障类型……";
                    int a = 0;
                    for (int i = 0; i < max; i++) {
                        if (paramBeanList.get(i).getIsValid().equals("1")) {
                            DB.save(paramBeanList.get(i));
//                            LOG.D("保存paramBean到数据库");
                            publishProgress(i);
                            a++;
                        }
                    }
                    LOG.D("ParamBean="+DB.findAll(ParamBean.class).size());
                }
            } catch (final Exception e) {
                LOG.E("ParamBeany异常"+e.toString());
            }
            return "完成";
        }

        @Override
        protected void onProgressUpdate(Integer... value) {
            super.onProgressUpdate(value);
            txt_min.setText((value[0] + 1) + "/" + max);
            txt_tip.setText("正在更新" + title);


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("完成")) {
                SharedUtil.setValue(mActivity, "isFirst", "isFirst");
                SharedUtil.setValueByKey(mActivity, "UpdateDictionaryTime", ServerTime);
                Intent intent = new Intent(mActivity, HomeActivity.class);
                startActivity(intent);
                ActivityUtil.FinishActivity(mActivity);
            }
        }
    }




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            ToastUtil.showShort(mActivity, "正在更新数据，返回键不可用");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private NetChangeReceiver netChangeReceiver;
    private ConnectivityManager connectivityManager;


    private void RegReceiver() {
        connectivityManager = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        netChangeReceiver = new NetChangeReceiver(new NetChangeReceiver.DoNet() {
            @Override
            public void goToDo() {
                ActivityUtil.ExitApp(mActivity);

            }
        });
        registerReceiver(netChangeReceiver, filter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(netChangeReceiver);
    }


    //清空数据库
    public static void clearTable() throws DbException {
        DbManager  DB= x.getDb(DBUtils.getDb());
        LOG.D("清空数据库");
        DB= x.getDb(DBUtils.getDb());
        DB.delete(DeviceMainTypeBean.class);
        DB.delete(DeviceSubTypeBean.class);
        DB.delete(RepairCompanyBean.class);
        DB.delete(Area2ComanyBean.class);
        DB.delete(RepairOrderBean.class);
        DB.delete(DASBean.class);
        DB.delete(CityAreaBean.class);
        DB.delete(CityAreaPCSBean.class);
        DB.delete(CompanyOfSystemBean.class);
        DB.delete(DictionaryBean.class);
        DB.delete(ParamBean.class);
        DB.delete(MyMSG.class);
    }
}
