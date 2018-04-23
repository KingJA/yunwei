package com.tdr.yunwei.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.DialogInterface;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.google.gson.Gson;
import com.tdr.yunwei.R;
import com.tdr.yunwei.YunWeiApplication;
import com.tdr.yunwei.bean.ApkUpdate;
import com.tdr.yunwei.bean.BingingDeviceBean;
import com.tdr.yunwei.bean.DeviceBean;
import com.tdr.yunwei.bean.Test;
import com.tdr.yunwei.reviceandbroad.BaiDuSDKReceiver;
import com.tdr.yunwei.update.UpdateManager;
import com.tdr.yunwei.util.ActivityUtil;
import com.tdr.yunwei.util.Constants;
import com.tdr.yunwei.util.DBUtils;
import com.tdr.yunwei.util.DateUtil;
import com.tdr.yunwei.util.LOG;
import com.tdr.yunwei.util.NotificationsUtils;
import com.tdr.yunwei.util.PermissionsActivity;
import com.tdr.yunwei.util.PermissionsCheckerUtil;
import com.tdr.yunwei.util.SharedUtil;
import com.tdr.yunwei.util.ToastUtil;
import com.tdr.yunwei.util.WebUtil;
import com.tdr.yunwei.util.ZProgressHUD;
import com.tdr.yunwei.view.Dialog.DialogUtil;
import com.tdr.yunwei.view.Dialog.DoOk;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/4/19.
 */
public class LoginActivity extends Activity {
    /**
     * 储存登录相关信息
     */
    String UserId = "";
    String AccessToken = "";
    String DispName = "";
    String UserName = "";
    String RoleName = "";
    String RolePowers = "";
    String DeviceName = "";
    String DeviceType = "";
    String RegionID = "";
    String ServerTime = "";
    String UpdateURL = "";

    boolean isFirstLoc = true;
    boolean isLogining = false;
    private String username = "", pwd = "", AutoLogin = "";
    private String txt_username = "", txt_password = "";

    // 定位相关
    LocationClient mLocClient;
    BaiDuSDKReceiver mReceiver;

    private DeviceBean devicebean = new DeviceBean();

    private TextView txt_version;
    private Button btn_login;
    private EditText txt_user, txt_pwd;
    private CheckBox cb_pwd, cb_autologin;

    /**
     * 需要上传值服务端的手机参数字段
     */
    private String imei = "", version = "";
    private Activity mActivity;
    String PushID;

    private ZProgressHUD zProgressHUD;
    PermissionsCheckerUtil pcu;
    private LinearLayout ll_city_list;
    private TextView tv_city_name;
    private String CityID = "", CityName = "";
    public static final int ChangeCity = 1000;
    private boolean ischeck = true;
    private YunWeiApplication YWA;
    private String CompanyName = "", UserPhone = "";
    private boolean CheckedRemberPwd, CheckedAutoLogin;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ChangeCity:
                    LOG.E("获取失败,进入程序");
                    if (!CityName.equals("")) {
                        tv_city_name.setText(CityName);
                    } else {
                        tv_city_name.setText("选择城市");
                    }
                    break;

            }
        }
    };


    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mActivity = this;
        ActivityUtil.AddActivity(mActivity);
        YWA = YunWeiApplication.getInstance();
        if(!NotificationsUtils.isNotificationEnabled(mActivity)){
            LOG.D("----------------");
//            requestPermission();
        }
//        DeviceQueryActivity.isSerial("11我11-2222-3333-4444-5555");
//        intoDB();
        String WorkOrderSize = SharedUtil.getValue(mActivity, "WorkOrderSize");
        if (!WorkOrderSize.equals("")) {
            YWA.setWorkOrderSize(Integer.parseInt(WorkOrderSize));
        }
//        ActivityUtil.SaveAddress(mActivity,"浙江省温州市龙湾区永安江路88号","55555555");
        initView();
        pcu = new PermissionsCheckerUtil(this);
    }
//    private void requestPermission() {
//        // TODO Auto-generated method stub
//        // 6.0以上系统才可以判断权限
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BASE) {
//            // 进入设置系统应用权限界面
//            Intent intent = new Intent(Settings.ACTION_SETTINGS);
//            startActivity(intent);
//            return;
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {// 运行系统在5.x环境使用
//            // 进入设置系统应用权限界面
//            Intent intent = new Intent(Settings.ACTION_SETTINGS);
//            startActivity(intent);
//            return;
//        }
//        return;
//    }
    private  void intoDB() {
        try {
            DbManager db= x.getDb(DBUtils.getDb());
            List<Test> l = new ArrayList<Test>();
            List<Test> l2 = db.findAll(Test.class);
            int index = 0;
            if (l2 != null && l2.size() > 0) {
                for (Test CB : l2) {
                    if (CB.getID() > index) {
                        index = CB.getID();
                    }
                }
        }
            for (int i = 0; i < 20; i++) {
                Test t = new Test();
//                t.setID(++index);
                t.setMainTypeID("" + i);
                t.setLastUpdateTime("" + i);
                t.setForPoliceUse("" + i);
                t.setIsValid("" + i);
                t.setRemark("" + i);
                t.setSubTypeID("" + i);
                l.add(t);
            }
            l=(List<Test>) DBUtils.SetListID(db,l,Test.class);
            db.save(l);
            LOG.D("保存数据库");
            List<Test> t2 = db.findAll(Test.class);
            LOG.D("读取所有数据  " + t2.size());
            for (int i = 0; i < t2.size(); i++) {
                LOG.D("ID= " + t2.get(i).getID() + "   SubTypeID= " + t2.get(i).getSubTypeID());
            }
        } catch (DbException e) {
            LOG.E(e.toString());
        }
    }

    /**
     * 控件载入
     */
    private void initView() {
        String VC = SharedUtil.getValue(mActivity, "VersionCode");
        String VC2= String.valueOf(ActivityUtil.GetVersionCode(mActivity));
        if(!VC.equals(VC2)){
            SharedUtil.clearDataByKey(mActivity);
            SharedUtil.clearUserInfoData(mActivity);
            SharedUtil.setValue(mActivity, "VersionCode",VC2);
            LOG.D(VC+" 版本号不一样 "+VC2);
        }else{
            LOG.D(VC+" 版本号一样 "+VC2);
        }
        username = SharedUtil.getValue(mActivity, "UserName");
        pwd = SharedUtil.getValue(mActivity, "Pwd");
        imei = SharedUtil.getValue(mActivity, "IMEI");
        version = SharedUtil.getValue(mActivity, "Version");
        AutoLogin = SharedUtil.getValue(mActivity, "AutoLogin");

        CityName = SharedUtil.getValue(mActivity, "CityName");
        CityID = SharedUtil.getValue(mActivity, "CityID");

        LOG.E(CityName+"CityName-------------------CityID="+CityID);
        LOG.E(  "username==" + username + "//pwd==" + pwd + "//imei==" +
                imei + "//version==" + version + "//AutoLogin==" + AutoLogin);


        txt_user = (EditText) findViewById(R.id.txt_user);
        txt_pwd = (EditText) findViewById(R.id.txt_pwd);

        txt_user.setText(username);
        txt_pwd.setText(pwd);
//        txt_user.setText("kunming4");
//        txt_pwd.setText("123456");

        ll_city_list = (LinearLayout) findViewById(R.id.ll_city_list);
        tv_city_name = (TextView) findViewById(R.id.tv_city_name);

        cb_pwd = (CheckBox) findViewById(R.id.cb_pwd);
        cb_autologin = (CheckBox) findViewById(R.id.cb_autologin);


        btn_login = (Button) findViewById(R.id.btn_login);

        txt_version = (TextView) findViewById(R.id.txt_version);
        if (!CityName.equals("")) {
            tv_city_name.setText(CityName);
        } else {
            tv_city_name.setText("选择城市");
        }
        DeviceName = Build.MODEL;

        version = ActivityUtil.GetVersion(mActivity);
        txt_version.setText("当前软件版本：" + version);


        SharedUtil.setValue(mActivity, "Version", version);


        if (!pwd.equals("")) {
            cb_pwd.setTextColor(Color.WHITE);
            cb_pwd.setChecked(true);
        }
        if (!AutoLogin.equals("")) {
            cb_autologin.setTextColor(Color.WHITE);
            cb_autologin.setChecked(true);
        }

        ll_city_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, SortListActivity.class);
                startActivityForResult(intent, 0);
            }
        });
        cb_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_password = txt_pwd.getText().toString().trim();
                if (txt_password.equals("")) {
                    ToastUtil.showShort(mActivity, "请输入密码");
                    cb_pwd.setChecked(false);
                }
            }
        });
        cb_pwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CheckedRemberPwd = isChecked;
                if (isChecked) {
                    cb_pwd.setTextColor(Color.WHITE);
                } else {
                    cb_pwd.setTextColor(Color.GRAY);
                }
            }
        });
        cb_autologin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CheckedAutoLogin = isChecked;
                if (isChecked) {
                    cb_pwd.setChecked(true);
                    cb_autologin.setTextColor(Color.WHITE);
                } else {
                    cb_autologin.setTextColor(Color.GRAY);
                }
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isLogining){
                    LOG.D("登录中");
                    AutoLogin();
                }
            }
        });
        GetVersionCode();

        if (AutoLogin.equals("AutoLogin")) {
            AutoLogin();
            return;
        }


    }

    /**
     * 自动登录
     */
    private void AutoLogin() {
        isLogining=true;
        zProgressHUD = new ZProgressHUD(mActivity);
        zProgressHUD.setMessage("正在获取登录信息");
        zProgressHUD.show();

        txt_username = txt_user.getText().toString().trim();
        txt_password = txt_pwd.getText().toString().trim();

        HashMap<String, Object> mapLogin = new HashMap<String, Object>();
        mapLogin.put("userName", txt_username);
        mapLogin.put("password", txt_password);
        mapLogin.put("imei", imei);
        mapLogin.put("areaID", CityID);
        mapLogin.put("version", version);
        mapLogin.put("registrationID", YWA.getRegistrationID());

        LOG.D( "mapLogin= " + mapLogin.toString());
        if ("".equals(txt_username)) {
            isLogining=false;
            ToastUtil.showShort(mActivity, "用户名不能为空");
            zProgressHUD.dismiss();
        } else if ("".equals(txt_password)) {
            isLogining=false;
            ToastUtil.showShort(mActivity, "密码不能为空");
            zProgressHUD.dismiss();
        } else if (CityID.equals("")) {
            isLogining=false;
            ToastUtil.showShort(mActivity, "请选择工作城市");
            zProgressHUD.dismiss();
        } else {

            WebUtil.getInstance(mActivity).webRequest(Constants.Login, mapLogin, new WebUtil.MyCallback() {
                @Override
                public void onSuccess(String result) {
                    LOG.D(  "登录：  " + result);
                    if (result.equals("-1")) {
                        isLogining=false;
                        zProgressHUD.dismiss();
                        return;
                    }
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String ErrorCode = jsonObject.getString("ErrorCode");
                        String ErrorMsg = jsonObject.getString("ErrorMsg");
                        String ResultMsg = jsonObject.getString("ResultMsg");
                        String ResultCode = jsonObject.getString("ResultCode");
                        LOG.D( "ErrorCode="+ErrorCode);

                        if (ErrorCode.equals("0")) {

                            UserId = jsonObject.getString("UserId");
                            AccessToken = jsonObject.getString("AccessToken");
                            DispName = jsonObject.getString("DispName");

                            UserName = jsonObject.getString("UserName");
                            RoleName = jsonObject.getString("RoleName");
                            RolePowers = jsonObject.getString("RolePowers");
                            ServerTime = jsonObject.getString("ServerTime");
                            RegionID = jsonObject.getString("RegionID");
                            UserPhone = jsonObject.getString("UserPhone");
                            CompanyName = jsonObject.getString("CompanyName");
                            UpdateURL = jsonObject.getString("UpdateURL");

                            if (RolePowers.equals("null")) {
                                isLogining=false;
                                DialogUtil.Show(mActivity, "您没有任何权限，无法进入系统！请联系管理员！\n" +
                                        "退出？", new DoOk() {
                                    @Override
                                    public void goTodo() {
                                        ActivityUtil.ExitApp(mActivity);
                                        return;
                                    }
                                });
                            }

                            if (ResultCode.equals("1") || ResultCode.equals("2")) {
                                isLogining=false;
                                showDialog(ResultMsg);

                            } else if (ResultCode.equals("3")) {
                                isLogining=false;
                                DialogUtil.Show(mActivity, ResultMsg, new DoOk() {
                                    @Override
                                    public void goTodo() {
                                        BingingDevice();
                                    }
                                });
                            } else {
                                AskUpdateDataCnt();
                            }
//                            AskUpdateDataCnt();
                        }
                        if (ErrorCode.equals("1")) {
                            isLogining=false;
                            ToastUtil.showShort(mActivity, "账号或密码错误");
                        }
                        if (ErrorCode.equals("2")) {
                            isLogining=false;
                            ToastUtil.showShort(mActivity, "异常" + ErrorMsg);
                        }
                    } catch (final Exception e) {
                        e.printStackTrace();
                        ToastUtil.showShort(mActivity, "数据转换异常");

                    } finally {
                        if (zProgressHUD.isShowing() == true) {
                            zProgressHUD.dismiss();
                            isLogining=false;
                        }

                    }
                }
            });
        }

    }

    /**
     * 消息提示
     * @param content
     */
    private void showDialog(String content) {
        AlertDialog.Builder builder = DialogUtil.getAlertDialogBuilder(this);
        builder.setTitle("")
                .setMessage(content)
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).setCancelable(false).show();
    }

    /**
     * 查询需要更新的数据量
     */
    private void AskUpdateDataCnt() {
        Log.e("Pan","AskUpdateDataCnt");
        isLogining=true;
        SaveUserDate();
        String lasttime = SharedUtil.getValue(mActivity, "CountUpdateTime");
        LOG.D(  "lasttime=" + lasttime);
        if (lasttime.equals("")) {
            lasttime = Constants.DefaultTime;
        }
        HashMap<String, Object> mapAskUpdate = new HashMap<String, Object>();
        mapAskUpdate.put("accessToken", SharedUtil.getToken(mActivity));
        mapAskUpdate.put("areaID", CityID);
        mapAskUpdate.put("lastUpdateTime", lasttime);

        WebUtil.getInstance(mActivity).webRequest(Constants.AskUpdateDataCnt, mapAskUpdate, new WebUtil.MyCallback() {

            @Override
            public void onSuccess(String result) {
                LOG.D(  "检查内容result=" + result);
                if (result.equals("-1")) {
                    zProgressHUD.dismiss();
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    int DeviceMainTypeCnt = StringToInt(jsonObject.getString("DeviceMainTypeCnt"));
                    int DeviceSubTypeCnt = StringToInt(jsonObject.getString("DeviceSubTypeCnt"));
                    int RepairCompanyCnt = StringToInt(jsonObject.getString("RepairCompanyCnt"));
                    int CO2SystemCnt = StringToInt(jsonObject.getString("CO2SystemCnt"));
                    int CityCnt = StringToInt(jsonObject.getString("CityCnt"));
                    int CityAreaCnt = StringToInt(jsonObject.getString("CityAreaCnt"));
                    int DASCnt = StringToInt(jsonObject.getString("DASCnt"));

                    int CO2AreaCnt = StringToInt(jsonObject.getString("CO2AreaCnt"));
                    LOG.D(  "-----------------DeviceMainTypeCnt=" + DeviceMainTypeCnt);
                    LOG.D(  "-----------------DeviceSubTypeCnt=" + DeviceSubTypeCnt);
                    LOG.D(  "-----------------RepairCompanyCnt=" + RepairCompanyCnt);
                    LOG.D(  "-----------------CO2SystemCnt=" + CO2SystemCnt);
                    LOG.D(  "-----------------CityCnt=" + CityCnt);
                    LOG.D(  "-----------------CityAreaCnt=" + CityAreaCnt);
                    LOG.D(  "-----------------DASCnt=" + DASCnt);
                    LOG.D(  "-----------------CO2AreaCnt=" + CO2AreaCnt);

                    if (DeviceMainTypeCnt > 0 || DeviceSubTypeCnt > 0 || DASCnt > 0 || CityAreaCnt > 0 ||
                            RepairCompanyCnt > 0 || CO2SystemCnt > 0 || CityCnt > 0 || CO2AreaCnt > 0) {
                        LOG.D(  "-----------------UpdateActivity");
                        SharedUtil.setValue(mActivity, "CountUpdateTime", ServerTime);
                        isLogining=false;
                        Intent intent1 = new Intent(mActivity, UpdateActivity.class);
                        startActivity(intent1);
                        ActivityUtil.FinishActivity(mActivity);

                    } else {
                        LOG.D(  "-----------------HomeActivity");
                        isLogining=false;
                        Intent intent2 = new Intent(mActivity, HomeActivity.class);
                        startActivity(intent2);
                        ActivityUtil.FinishActivity(mActivity);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * 保存用户基本信息
     */
    private  void SaveUserDate(){
        //登录成功后保存用户名和运维公司



        String username = SharedUtil.getValue(mActivity, "User_Name");
        if (!username.equals(txt_username)) {

            String cityname = SharedUtil.getValue(mActivity, "CityName");
            String cityid = SharedUtil.getValue(mActivity, "CityID");
            SharedUtil.clearUserInfoData(mActivity);
            SharedUtil.clearDataByKey(mActivity);
            SharedUtil.setValue(mActivity, "VersionCode",String.valueOf(ActivityUtil.GetVersionCode(mActivity)));
            SharedUtil.setValue(mActivity, "CityName", cityname);
            SharedUtil.setValue(mActivity, "CityID", cityid);
            LOG.D(  "----------切换用户，清空原有数据-----------");
        }
        LOG.D(  "----------DeviceTypeUpdateTime="+ SharedUtil.getValue(mActivity, "DeviceTypeUpdateTime"));
        SharedUtil.setValue(mActivity, "User_Name", txt_username);
        SharedUtil.setValue(mActivity, "UserName", UserName);
        SharedUtil.setValue(mActivity, "UserId", UserId);
        SharedUtil.setValue(mActivity, "AccessToken", AccessToken);
        SharedUtil.setValue(mActivity, "RolePowers", RolePowers);
        SharedUtil.setValue(mActivity, "RegionID", RegionID);
        SharedUtil.setValue(mActivity, "ServerTime", ServerTime);


        SharedUtil.setValue(mActivity, "UserPhone", UserPhone);
        Log.e("Pan","UserPhone"+UserPhone);
        SharedUtil.setValue(mActivity, "CompanyName", CompanyName);
        Log.e("Pan","CompanyName"+CompanyName);
        SharedUtil.setValue(mActivity, "DispName", DispName);
        Log.e("Pan","DispName"+DispName);
        LOG.D(  "Checked=" + CheckedRemberPwd);


        if (CheckedRemberPwd) {
            SharedUtil.setValue(mActivity, "RemberPwd", "RemberPwd");
            SharedUtil.setValue(mActivity, "Pwd", txt_password);
            LOG.D(  "保存密码");
        } else {
            SharedUtil.setValue(mActivity, "RemberPwd", "");
            LOG.D(  "不保存密码");
        }
        if (CheckedAutoLogin) {
            SharedUtil.setValue(mActivity, "AutoLogin", "AutoLogin");
        } else {
            SharedUtil.setValue(mActivity, "AutoLogin", "");
        }
    }

    /**
     * StringToInt
     * @param str
     * @return
     */
    private int StringToInt(String str) {
        try {
            if (!str.equals("") || !str.equals("null")) {
                return Integer.parseInt(str);
            }
        } catch (Exception e) {
            return 0;
        }
        return 0;
    }



    /**
     * 绑定设备
     */
    private void BingingDevice() {
        BingingDeviceBean bean = new BingingDeviceBean();
        bean.setUserId(UserId);
        bean.setType(DeviceType);
        bean.setDeviceName(DeviceName);
        bean.setIMEI(imei);
        //     bean.setIMEI("55556668855268");
        bean.setCreatedTime(DateUtil.getNowDate());
        bean.setPushID(PushID);
        Gson gson=new Gson();
        String bindingInfo = gson.toJson(bean);//对象转化字符串
        LOG.D( "bindingInfo="+ bindingInfo + "//PushID==" + PushID);


        HashMap<String, Object> mapBinging = new HashMap<String, Object>();
        mapBinging.put("accessToken", SharedUtil.getToken(mActivity));
        mapBinging.put("bindingInfo", bindingInfo);
        mapBinging.put("pushId", SharedUtil.getValue(mActivity, "PushID"));


        WebUtil.getInstance(mActivity).webRequest(Constants.Binding, mapBinging, new WebUtil.MyCallback() {
            @Override
            public void onSuccess(String result) {

                if (result.equals("-1")) {
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String ErrorCode = jsonObject.getString("ErrorCode");
                    if (ErrorCode.equals("0")) {
                        ToastUtil.showShort(mActivity, "绑定成功！");
                        SharedUtil.setValue(mActivity, "BindingInfo", "BindingInfo");
                        SharedUtil.setValue(mActivity, "LoginName", txt_username);
                        AskUpdateDataCnt();

                    }
                    if (ErrorCode.equals("2")) {
                        ToastUtil.showShort(mActivity, "绑定失败！");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }


    /**
     * 开启百度地图定位服务
     */
    @SuppressLint("MissingPermission")
    private void StartLocationSever() {
        TelephonyManager tm = (TelephonyManager) mActivity.getSystemService(Context.TELEPHONY_SERVICE);
        imei = tm.getDeviceId();
        LOG.D(  "有权限 获取imei  " + imei);
        SharedUtil.setValue(mActivity, "IMEI", imei);
        // 注册 SDK 广播监听者
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mReceiver = new BaiDuSDKReceiver();
        registerReceiver(mReceiver, iFilter);
        // 定位初始化
        mLocClient = new LocationClient(this);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        option.setIsNeedAddress(true);
        mLocClient.setLocOption(option);
        mLocClient.start();
        MyLocationListenner myListener = new MyLocationListenner();
        mLocClient.registerLocationListener(myListener);
    }

    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null) {
                return;
            }
            if (isFirstLoc) {
                isFirstLoc = false;

                double LAT = location.getLatitude();
                double LNG = location.getLongitude();
                String city = location.getCity();
                String area = location.getDistrict();
                String street = location.getStreet();
                String address = location.getAddress().address;

//                ToastUtil.showShort(mActivity,location.getCity());
                LOG.D("LAT="+LAT);
                LOG.D("LNG="+LNG);
                LOG.D("city="+city);
                LOG.D("area="+area);
                LOG.D("street="+street);
                LOG.D("address="+address);

                SharedUtil.setValue(mActivity, "lng", LNG + "");
                SharedUtil.setValue(mActivity, "lat", LAT + "");
                SharedUtil.setValue(mActivity, "city", city);
                SharedUtil.setValue(mActivity, "area", area);
                SharedUtil.setValue(mActivity, "street", street);
                if (address != null) {
                    SharedUtil.setValue(mActivity, "cityaddress", address.substring(2));
                }
                mLocClient.stop();
            }


        }

    }

    /**
     * 获取版本信息并升级
     */
    private void GetVersionCode() {
        LOG.E("检查版本更新");
        if (CityID.equals("")) {
            return;
        }
        HashMap<String, Object> update = new HashMap<String, Object>();
        update.put("areaID", CityID);
        update.put("deviceType", "1");
        WebUtil.getInstance(mActivity).webRequest(Constants.GetAppVersion, update, new WebUtil.MyCallback() {
            @Override
            public void onSuccess(String result) {
                LOG.D(  "GetVersionCode= " + result);
                try {
                    JSONObject jsb = new JSONObject(result);
                    String VersionID = jsb.getString("VersionID");
                    String UpdateUrl = jsb.getString("UpdateUrl");
                    String ErrorCode = jsb.getString("ErrorCode");
                    String ErrorMsg = jsb.getString("ErrorMsg");
                    if (ErrorCode.equals("0")) {
                        ischeck = false;
                        Double versioncode = 0.0;
                        try {
                            versioncode = Double.parseDouble(VersionID);
                        } catch (Exception e) {
                            LOG.D(  "版本号格式错误" + VersionID);
                        }
                        if (ActivityUtil.GetVersionCode(mActivity) < versioncode) {
                            ApkUpdate apkUpdate = new ApkUpdate(VersionID, UpdateUrl, ErrorCode, ErrorMsg);
                            UpdateManager updateManager = new UpdateManager(mActivity, apkUpdate);
                            updateManager.showNoticeDialog();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 缺少权限时, 进入权限配置页面
        if (pcu.lacksPermissions()) {
            PermissionsActivity.startActivityForResult(this, PermissionsActivity.REQUEST_CODE, pcu.PERMISSIONS);
        } else {
            StartLocationSever();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LOG.D( resultCode + "----------onActivityResult-----------" + requestCode);
        switch (resultCode) {
            case RESULT_OK:
                Bundle b = data.getExtras();
                CityName = b.getString("cityname");
                CityID = b.getString("cityid");

                mHandler.sendEmptyMessage(ChangeCity);
                LOG.D(  "cityname=" + CityName);
                if (ischeck) {
                    GetVersionCode();
                }
                break;
            // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
            case PermissionsActivity.REQUEST_CODE:
                if (resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
                    ActivityUtil.FinishActivity(mActivity);
                } else {
                    StartLocationSever();
                }
                break;
            default:
                break;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            // 退出时销毁定位
            mLocClient.stop();
            // 关闭定位图层
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK&& event.getAction() == KeyEvent.ACTION_DOWN) {
            DialogUtil.Show(mActivity, "是否要退出程序？", new DoOk() {
                @Override
                public void goTodo() {
                    ActivityUtil.ExitApp(mActivity);
                }
            });
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
