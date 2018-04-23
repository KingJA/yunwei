package com.tdr.yunwei;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.tdr.yunwei.activity.MyMsgActivity;
import com.tdr.yunwei.bean.CityList;
import com.tdr.yunwei.bean.MyMSG;
import com.tdr.yunwei.bean.StationCity;
import com.tdr.yunwei.fragment.SettingFM;
import com.tdr.yunwei.util.DBUtils;
import com.tdr.yunwei.util.LOG;
//import com.tencent.bugly.crashreport.CrashReport;

import org.xutils.DbManager;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2016/5/20.
 */
public class YunWeiApplication extends Application {
    private static YunWeiApplication yunweiapplication;
    private int WorkOrderSize = 0;
    private  DbManager DB;
    private String RegistrationID;
    private List<MyMSG> msgdate;
    private MyMsgActivity mymsgactivity;
    private SettingFM settingfm;
    private static Context context;
//    private List<CityList> cityBeanList;
    @Override
    public void onCreate() {
        super.onCreate();
        context=this;
        yunweiapplication=this;
        x.Ext.init(this);
        DB= x.getDb(DBUtils.getDb());
//        DB= x.getDb(new DBUtils().getDaoConfig());
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        RegistrationID= JPushInterface.getRegistrationID(getApplicationContext());

        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(getApplicationContext());
//        CrashReport.initCrashReport(getApplicationContext(), "900047111", false);
        MSGdate();
    }
    public void MSGdate(){
        try {
            if(msgdate!=null&&msgdate.size()>0){
                msgdate.clear();
            }
            msgdate=DB.findAll(MyMSG.class);
            if(msgdate==null)
                msgdate=new ArrayList<MyMSG>();
            LOG.D("推送数据量 ："+msgdate.size());
            for(MyMSG m:msgdate){
                LOG.D("getID ："+m.getID());
                LOG.D("getIsOld ："+m.getIsOld());
                LOG.D("getUserID ："+m.getUserID());
                LOG.D("getMessageType ："+m.getMessageType());
                LOG.D("getMsgTitle ："+m.getMsgTitle());
                LOG.D("getMsgText ："+m.getMsgText());
                LOG.D("getMsgTime ："+m.getMsgTime());
                LOG.D("--------");
            }
            if(mymsgactivity!=null){
                mymsgactivity.refresh(msgdate);
            }
            if(settingfm!=null){
                settingfm.setNewMsgNumber(msgdate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public interface ReFreshMSG{
        public void refresh(List<MyMSG> list);
    }
    public static YunWeiApplication getInstance() {
        return yunweiapplication;
    }

    public DbManager getDB() {
        return DB;
    }

    public void setDB(DbManager DB) {
        this.DB = DB;
    }




    public int getWorkOrderSize() {
        return WorkOrderSize;
    }

    public void setWorkOrderSize(int size) {
        this.WorkOrderSize = size;
    }
    public String getRegistrationID() {
        LOG.J("RegistrationID=   "+RegistrationID);
        return RegistrationID;
    }
    public void setRegistrationID(String registrationid) {
        LOG.J("RegistrationID=   "+registrationid);
        RegistrationID=registrationid;
    }

    public List<MyMSG> getMsgdate() {
        return msgdate;
    }

    public void setMsgdate(List<MyMSG> msgdate) {
        this.msgdate = msgdate;
    }

    public MyMsgActivity getMymsgactivity() {
        return mymsgactivity;
    }

    public void setMymsgactivity(MyMsgActivity mymsgactivity) {
        this.mymsgactivity = mymsgactivity;
    }

    public SettingFM getSettingfm() {
        return settingfm;
    }

    public void setSettingfm(SettingFM settingfm) {
        this.settingfm = settingfm;
    }

//    public List<CityList> getCityBeanList() {
//        return cityBeanList;
//    }
//
//    public void setCityBeanList(List<CityList> cityBeanList) {
//        this.cityBeanList = cityBeanList;
//    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        YunWeiApplication.context = context;
    }
}

