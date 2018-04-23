package com.tdr.yunwei.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tdr.yunwei.R;
import com.tdr.yunwei.bean.DeviceBean2;
import com.tdr.yunwei.bean.DeviceBindingStatusBean;
import com.tdr.yunwei.util.ActivityUtil;
import com.tdr.yunwei.util.Constants;
import com.tdr.yunwei.util.HexUtil;
import com.tdr.yunwei.util.LOG;
import com.tdr.yunwei.util.SharedUtil;
import com.tdr.yunwei.util.ToastUtil;
import com.tdr.yunwei.util.WebUtil;
import com.tdr.yunwei.util.ZProgressHUD;
import com.tdr.yunwei.util.ZbarUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/5.
 */
@ContentView(R.layout.activity_binding)
public class DeviceBindingActivity extends Activity implements View.OnClickListener {
    @ViewInject(R.id.IV_Back)
    private ImageView IV_Back;
    @ViewInject(R.id.TV_Title)
    private TextView TV_Title;

    @ViewInject(R.id.TV_1)
    private TextView TV_1;
    @ViewInject(R.id.TV_2)
    private TextView TV_2;
    @ViewInject(R.id.TV_3)
    private TextView TV_3;
    @ViewInject(R.id.TV_4)
    private TextView TV_4;

    @ViewInject(R.id.V_O1)
    private View V_O1;
    @ViewInject(R.id.V_O2)
    private View V_O2;
    @ViewInject(R.id.V_O3)
    private View V_O3;
    @ViewInject(R.id.V_O4)
    private View V_O4;

    @ViewInject(R.id.V_L1)
    private View V_L1;
    @ViewInject(R.id.V_L2)
    private View V_L2;
    @ViewInject(R.id.V_L3)
    private View V_L3;

    @ViewInject(R.id.TV_DeviceCode)
    private TextView TV_DeviceCode;
    @ViewInject(R.id.TV_DeviceLastTime)
    private TextView TV_DeviceLastTime;



    @ViewInject(R.id.IV_Device)
    private ImageView IV_Device;
    @ViewInject(R.id.IV_Ground)
    private ImageView IV_Ground;



    @ViewInject(R.id.IV_Ground1)
    private ImageView IV_Ground1;
    @ViewInject(R.id.TV_Ground1)
    private TextView TV_Ground1;
    @ViewInject(R.id.IV_Ground2)
    private ImageView IV_Ground2;
    @ViewInject(R.id.TV_Ground2)
    private TextView TV_Ground2;
    @ViewInject(R.id.IV_Ground3)
    private ImageView IV_Ground3;
    @ViewInject(R.id.TV_Ground3)
    private TextView TV_Ground3;
    @ViewInject(R.id.IV_Ground4)
    private ImageView IV_Ground4;
    @ViewInject(R.id.TV_Ground4)
    private TextView TV_Ground4;
    @ViewInject(R.id.IV_Ground5)
    private ImageView IV_Ground5;
    @ViewInject(R.id.TV_Ground5)
    private TextView TV_Ground5;
    @ViewInject(R.id.IV_Ground6)
    private ImageView IV_Ground6;
    @ViewInject(R.id.TV_Ground6)
    private TextView TV_Ground6;
    @ViewInject(R.id.IV_Ground7)
    private ImageView IV_Ground7;
    @ViewInject(R.id.TV_Ground7)
    private TextView TV_Ground7;
    @ViewInject(R.id.IV_Ground8)
    private ImageView IV_Ground8;
    @ViewInject(R.id.TV_Ground8)
    private TextView TV_Ground8;

    @ViewInject(R.id.IV_RG1)
    private ImageView IV_RG1;
    @ViewInject(R.id.TV_RG1)
    private TextView TV_RG1;
    @ViewInject(R.id.IV_RG2)
    private ImageView IV_RG2;
    @ViewInject(R.id.TV_RG2)
    private TextView TV_RG2;
    @ViewInject(R.id.IV_RG3)
    private ImageView IV_RG3;
    @ViewInject(R.id.TV_RG3)
    private TextView TV_RG3;
    @ViewInject(R.id.IV_RG4)
    private ImageView IV_RG4;
    @ViewInject(R.id.TV_RG4)
    private TextView TV_RG4;
    @ViewInject(R.id.IV_RG5)
    private ImageView IV_RG5;
    @ViewInject(R.id.TV_RG5)
    private TextView TV_RG5;
    @ViewInject(R.id.IV_RG6)
    private ImageView IV_RG6;
    @ViewInject(R.id.TV_RG6)
    private TextView TV_RG6;
    @ViewInject(R.id.IV_RG7)
    private ImageView IV_RG7;
    @ViewInject(R.id.TV_RG7)
    private TextView TV_RG7;
    @ViewInject(R.id.IV_RG8)
    private ImageView IV_RG8;
    @ViewInject(R.id.TV_RG8)
    private TextView TV_RG8;
    @ViewInject(R.id.IV_RG9)
    private ImageView IV_RG9;
    @ViewInject(R.id.TV_RG9)
    private TextView TV_RG9;
    @ViewInject(R.id.IV_RG10)
    private ImageView IV_RG10;
    @ViewInject(R.id.TV_RG10)
    private TextView TV_RG10;
    @ViewInject(R.id.IV_RG11)
    private ImageView IV_RG11;
    @ViewInject(R.id.TV_RG11)
    private TextView TV_RG11;
    @ViewInject(R.id.IV_RG12)
    private ImageView IV_RG12;
    @ViewInject(R.id.TV_RG12)
    private TextView TV_RG12;

    @ViewInject(R.id.TV_Submit)
    private TextView TV_Submit;

    private int Status1 = R.mipmap.binding1;
    private int Status2 = R.mipmap.binding2;
    private int Status3 = R.mipmap.binding3;
    private int Status4 = R.mipmap.binding4;

    private int TStatus1 = Color.parseColor("#999999");
    private int TStatus2 = Color.parseColor("#76d5eb");
    private int TStatus3 = Color.parseColor("#21ba77");
    private int TStatus4 = Color.parseColor("#ff0000");

    private DeviceBean2 device;
    private String Status = "网关配置";
    private Activity mActivity;
    private String systemID;
    private int CMD_Type = 0;
    private int index=0;//指令查询次数
    private static final int CheckResult = 1;
    private static final int CheckBindingResult = 2;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CheckResult:
                    InstructionResult();
                    break;
                case CheckBindingResult:
                    getDeviceBindingStatus();
                    break;

            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        mActivity = this;
        initdata();
    }

    private void initdata() {
        IV_Back.setOnClickListener(this);
        TV_Submit.setOnClickListener(this);
        systemID = getIntent().getStringExtra("systemID");
        device = getIntent().getParcelableExtra("deviceBean");
        LOG.E("device  GroudDeviceStatus=" + device.getGroudDeviceStatus8());
        TV_Title.setText(device.getDeviceCode());
        TV_DeviceCode.setText(device.getDeviceCode());
        TV_DeviceLastTime.setText(device.getROADNAME());
//        TV_DeviceAddress.setText(device.getAddress());
//        TV_CentralBuryingListTime.setText(device.getGround_DeviceTime9());
//        device.setDEVICEGROUD_ID("1");

        setDeviceStatus();

    }

    private void setDeviceStatus(DeviceBindingStatusBean DBDB) {
        LOG.E("Groud1=" + DBDB.getGroudDeviceStatus1());
        LOG.E("Groud2=" + DBDB.getGroudDeviceStatus2());
        LOG.E("Groud3=" + DBDB.getGroudDeviceStatus3());
        LOG.E("Groud4=" + DBDB.getGroudDeviceStatus4());
        LOG.E("Groud5=" + DBDB.getGroudDeviceStatus5());
        LOG.E("Groud6=" + DBDB.getGroudDeviceStatus6());
        LOG.E("Groud7=" + DBDB.getGroudDeviceStatus7());
        LOG.E("Groud8=" + DBDB.getGroudDeviceStatus8());
        LOG.E("Groud9=" + DBDB.getGroudDeviceStatus9());

        LOG.E("Traffic1=" + DBDB.getTrafficLightStatus1());
        LOG.E("Traffic2=" + DBDB.getTrafficLightStatus2());
        LOG.E("Traffic3=" + DBDB.getTrafficLightStatus3());
        LOG.E("Traffic4=" + DBDB.getTrafficLightStatus4());
        LOG.E("Traffic5=" + DBDB.getTrafficLightStatus5());
        LOG.E("Traffic6=" + DBDB.getTrafficLightStatus6());
        LOG.E("Traffic7=" + DBDB.getTrafficLightStatus7());
        LOG.E("Traffic8=" + DBDB.getTrafficLightStatus8());
        LOG.E("Traffic9=" + DBDB.getTrafficLightStatus9());
        LOG.E("Traffic10=" + DBDB.getTrafficLightStatus10());
        LOG.E("Traffic11=" + DBDB.getTrafficLightStatus11());
        LOG.E("Traffic12=" + DBDB.getTrafficLightStatus12());

        setStatus(DBDB.getDeviceStatus(), IV_Device,null);
        setStatus(DBDB.getGroudDeviceStatus9(), IV_Ground,null);

        setStatus(DBDB.getGroudDeviceStatus1(), IV_Ground1,TV_Ground1);
        setStatus(DBDB.getGroudDeviceStatus2(), IV_Ground2,TV_Ground2);
        setStatus(DBDB.getGroudDeviceStatus3(), IV_Ground3,TV_Ground3);
        setStatus(DBDB.getGroudDeviceStatus4(), IV_Ground4,TV_Ground4);
        setStatus(DBDB.getGroudDeviceStatus5(), IV_Ground5,TV_Ground5);
        setStatus(DBDB.getGroudDeviceStatus6(), IV_Ground6,TV_Ground6);
        setStatus(DBDB.getGroudDeviceStatus7(), IV_Ground7,TV_Ground7);
        setStatus(DBDB.getGroudDeviceStatus8(), IV_Ground8,TV_Ground8);

        setStatus(DBDB.getTrafficLightStatus1(), IV_RG1,TV_RG1);
        setStatus(DBDB.getTrafficLightStatus2(), IV_RG2,TV_RG2);
        setStatus(DBDB.getTrafficLightStatus3(), IV_RG3,TV_RG3);
        setStatus(DBDB.getTrafficLightStatus4(), IV_RG4,TV_RG4);
        setStatus(DBDB.getTrafficLightStatus5(), IV_RG5,TV_RG5);
        setStatus(DBDB.getTrafficLightStatus6(), IV_RG6,TV_RG6);
        setStatus(DBDB.getTrafficLightStatus7(), IV_RG7,TV_RG7);
        setStatus(DBDB.getTrafficLightStatus8(), IV_RG8,TV_RG8);
        setStatus(DBDB.getTrafficLightStatus9(), IV_RG9,TV_RG9);
        setStatus(DBDB.getTrafficLightStatus10(), IV_RG10,TV_RG10);
        setStatus(DBDB.getTrafficLightStatus11(), IV_RG11,TV_RG11);
        setStatus(DBDB.getTrafficLightStatus12(), IV_RG12,TV_RG12);
    }

    private void setDeviceStatus() {
        LOG.E("Groud1=" + device.getGroudDeviceStatus1());
        LOG.E("Groud2=" + device.getGroudDeviceStatus2());
        LOG.E("Groud3=" + device.getGroudDeviceStatus3());
        LOG.E("Groud4=" + device.getGroudDeviceStatus4());
        LOG.E("Groud5=" + device.getGroudDeviceStatus5());
        LOG.E("Groud6=" + device.getGroudDeviceStatus6());
        LOG.E("Groud7=" + device.getGroudDeviceStatus7());
        LOG.E("Groud8=" + device.getGroudDeviceStatus8());
        LOG.E("Groud9=" + device.getGroudDeviceStatus9());

        LOG.E("Traffic1=" + device.getTrafficLightStatus1());
        LOG.E("Traffic2=" + device.getTrafficLightStatus2());
        LOG.E("Traffic3=" + device.getTrafficLightStatus3());
        LOG.E("Traffic4=" + device.getTrafficLightStatus4());
        LOG.E("Traffic5=" + device.getTrafficLightStatus5());
        LOG.E("Traffic6=" + device.getTrafficLightStatus6());
        LOG.E("Traffic7=" + device.getTrafficLightStatus7());
        LOG.E("Traffic8=" + device.getTrafficLightStatus8());
        LOG.E("Traffic9=" + device.getTrafficLightStatus9());
        LOG.E("Traffic10=" + device.getTrafficLightStatus10());
        LOG.E("Traffic11=" + device.getTrafficLightStatus11());
        LOG.E("Traffic12=" + device.getTrafficLightStatus12());



        setStatus(device.getDeviceStatus(), IV_Device,null);
        setStatus(device.getGroudDeviceStatus9(), IV_Ground,null);


        setStatus(device.getGroudDeviceStatus1(), IV_Ground1,TV_Ground1);
        setStatus(device.getGroudDeviceStatus2(), IV_Ground2,TV_Ground2);
        setStatus(device.getGroudDeviceStatus3(), IV_Ground3,TV_Ground3);
        setStatus(device.getGroudDeviceStatus4(), IV_Ground4,TV_Ground4);
        setStatus(device.getGroudDeviceStatus5(), IV_Ground5,TV_Ground5);
        setStatus(device.getGroudDeviceStatus6(), IV_Ground6,TV_Ground6);
        setStatus(device.getGroudDeviceStatus7(), IV_Ground7,TV_Ground7);
        setStatus(device.getGroudDeviceStatus8(), IV_Ground8,TV_Ground8);

        setStatus(device.getTrafficLightStatus1(), IV_RG1,TV_RG1);
        setStatus(device.getTrafficLightStatus2(), IV_RG2,TV_RG2);
        setStatus(device.getTrafficLightStatus3(), IV_RG3,TV_RG3);
        setStatus(device.getTrafficLightStatus4(), IV_RG4,TV_RG4);
        setStatus(device.getTrafficLightStatus5(), IV_RG5,TV_RG5);
        setStatus(device.getTrafficLightStatus6(), IV_RG6,TV_RG6);
        setStatus(device.getTrafficLightStatus7(), IV_RG7,TV_RG7);
        setStatus(device.getTrafficLightStatus8(), IV_RG8,TV_RG8);
        setStatus(device.getTrafficLightStatus9(), IV_RG9,TV_RG9);
        setStatus(device.getTrafficLightStatus10(), IV_RG10,TV_RG10);
        setStatus(device.getTrafficLightStatus11(), IV_RG11,TV_RG11);
        setStatus(device.getTrafficLightStatus12(), IV_RG12,TV_RG12);
    }

    private void setStatus(String Status, ImageView IV,TextView TV) {
        if (Status == null) {
            Status = "";
        }
        switch (Status) {
            case "0":
                IV.setBackgroundResource(Status1);
                if(TV!=null){
                    TV.setTextColor(TStatus1);
                }
                break;
            case "1":
                IV.setBackgroundResource(Status2);
                if(TV!=null){
                    TV.setTextColor(TStatus2);
                }
                break;
            case "3":
                IV.setBackgroundResource(Status3);
                if(TV!=null){
                    TV.setTextColor(TStatus3);
                }
                break;
            case "4":
                IV.setBackgroundResource(Status4);
                if(TV!=null){
                    TV.setTextColor(TStatus4);
                }
                break;
        }
    }

    private void sendIntersection() {
        String cmd = "";
        String Subcmd = "";
        String content = "";
        switch (CMD_Type) {
            case 1:
                index=0;
                cmd = "f105";
                Subcmd = "01";
                LOG.E("ROADNO="+device.getROADNO());
                LOG.E("ROADTYPE="+device.getROADTYPE());
                LOG.E("DEVICEGROUD_ID="+device.getDEVICEGROUD_ID());
                String ROADNO = HexUtil.getdata(HexUtil.bytesToHexString(HexUtil.intToBytes(Integer.parseInt(device.getROADNO()))));
                String ROADTYPE = HexUtil.getdata(HexUtil.bytesToHexString(HexUtil.intToBytes(Integer.parseInt(device.getROADTYPE()))));
//                String DEVICEGROUD_ID = HexUtil.getdata2(HexUtil.bytesToHexString(HexUtil.getBytes(Integer.parseInt(device.getDEVICEGROUD_ID()), false)));
//                String DEVICEGROUD_ID = HexUtil.FillSeats(HexUtil.getdata2(HexUtil.bytesToHexString(HexUtil.getBytes(Integer.parseInt(device.getDEVICEGROUD_ID()), false))));
                String DEVICEGROUD_ID = HexUtil.bytesToHexString(HexUtil.getBytes(Integer.parseInt(device.getDEVICEGROUD_ID()), false));
                content = "01" + ROADNO + ROADTYPE + DEVICEGROUD_ID + "00";
//                01 0d 03 00009 00
                break;
            case 2:
                cmd = "f105";
                Subcmd = "02";
                content = "020000";
                break;
            case 3:
                cmd = "f105";
                Subcmd = "03";
                content = "030000";
                break;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("deviceType", device.getDeviceType());
            jsonObject.put("deviceCode", device.getDeviceCode());
            jsonObject.put("cmd", cmd);
            jsonObject.put("Subcmd", Subcmd);
            jsonObject.put("content", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("accessToken", SharedUtil.getToken(mActivity));
        map.put("instructionInfo", jsonObject.toString());
        map.put("systemID", systemID);
        LOG.E("MAP" + map.toString());
        WebUtil.getInstance(mActivity).webRequest(Constants.Sys_InstructionSend, map, new WebUtil.MyCallback() {
            @Override
            public void onSuccess(String result) {
                if (result.equals("-1")) {
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String ErrorCode = jsonObject.getString("ErrorCode");
                    String ErrorMsg = jsonObject.getString("ErrorMsg");
                    if (ErrorCode.equals("0")) {
                        ToastUtil.ErrorOrRight(mActivity, ErrorMsg, 2);
                        switch (CMD_Type) {
                            case 1:
                                TV_1.setTextColor(Color.parseColor("#333333"));
                                V_O1.setBackgroundResource(R.drawable.bkg_oval1);
                                break;
                            case 2:
                                TV_2.setTextColor(Color.parseColor("#333333"));
                                V_O2.setBackgroundResource(R.drawable.bkg_oval1);
                                break;
                            case 3:
                                TV_4.setTextColor(Color.parseColor("#333333"));
                                V_O4.setBackgroundResource(R.drawable.bkg_oval1);
                                break;
                        }
                        mHandler.sendEmptyMessageDelayed(CheckResult, 5000);
                    } else {

                        TV_Submit.setText("网关配置");
                        ToastUtil.ErrorOrRight(mActivity, ErrorCode + "  " + ErrorMsg, 1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    //查询指令结果
    private void InstructionResult() {
        String cmd = "";
        String Subcmd = "";
        LOG.E("CMD_Type=" + CMD_Type);
        switch (CMD_Type) {//指令类型
            case 1:
                cmd = "f105";
                Subcmd = "01";
                break;
            case 2:
                cmd = "f105";
                Subcmd = "02";
                break;
            case 3:
                cmd = "f105";
                Subcmd = "03";
                break;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("deviceCode", device.getDeviceCode());
            jsonObject.put("cmd", cmd);
            jsonObject.put("Subcmd", Subcmd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("accessToken", SharedUtil.getToken(mActivity));
        map.put("instructionInfo", jsonObject.toString());
        map.put("systemID", systemID);
        LOG.E("MAP" + map.toString());
        WebUtil.getInstance(mActivity).webRequest(Constants.Sys_InstructionResult, map, new WebUtil.MyCallback() {
            @Override
            public void onSuccess(String result) {
                if (result.equals("-1")) {
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String ErrorCode = jsonObject.getString("ErrorCode");
                    String ErrorMsg = jsonObject.getString("ErrorMsg");
                    String Result = jsonObject.getString("Result");
//                    ToastUtil.ErrorOrRight(mActivity, Result + "  " + ErrorMsg, 1);
                    switch (CMD_Type) {
                        case 1:

                            if (ErrorCode.equals("0") && Result.equals("2")) {
                                Status = "开启绑定";
                                V_L1.setBackgroundColor(Color.parseColor("#51c3bc"));
                                TV_Submit.setText("开启绑定");
                                TV_2.setTextColor(Color.parseColor("#333333"));
                                ToastUtil.ErrorOrRight(mActivity, ErrorCode + "  " + ErrorMsg, 2);
                            } else if (ErrorCode.equals("0") && Result.equals("3")) {
                                Status = "网关配置";
                                TV_1.setTextColor(Color.parseColor("#CBCBCB"));
                                V_O1.setBackgroundResource(R.drawable.bkg_oval2);
                                TV_Submit.setText("网关配置");
                                ToastUtil.ErrorOrRight(mActivity, "网关配置指令下发失败，请重试。", 1);
                            } else {
                                if(index==8){
                                    Status = "网关配置";
                                    TV_1.setTextColor(Color.parseColor("#CBCBCB"));
                                    V_O1.setBackgroundResource(R.drawable.bkg_oval2);
                                    TV_Submit.setText("网关配置");
                                    ToastUtil.ErrorOrRight(mActivity, "指令下发超时。", 2);
                                }else{
                                    index++;
                                    mHandler.sendEmptyMessageDelayed(CheckResult, 5000);
                                }
                            }
                            break;
                        case 2:
                            if (ErrorCode.equals("0") && Result.equals("2")) {
                                Status="设备绑定中";
                                V_L2.setBackgroundColor(Color.parseColor("#51c3bc"));
                                V_O3.setBackgroundResource(R.drawable.bkg_oval1);
                                TV_Submit.setText("设备绑定中...");
                                TV_3.setTextColor(Color.parseColor("#333333"));
                                ToastUtil.ErrorOrRight(mActivity, ErrorCode + "  " + ErrorMsg, 2);
                                getDeviceBindingStatus();
                                V_L3.setBackgroundColor(Color.parseColor("#51c3bc"));
                                TV_Submit.setText("关闭绑定");
                                Status="关闭绑定";
                            } else if (ErrorCode.equals("0") && Result.equals("3")) {
                                Status="开启绑定";
                                TV_2.setTextColor(Color.parseColor("#CBCBCB"));
                                V_O2.setBackgroundResource(R.drawable.bkg_oval2);
                                TV_Submit.setText("开启绑定");
                                ToastUtil.ErrorOrRight(mActivity, "开启网关指令下发失败，请重试。", 1);
                            } else {
                                mHandler.sendEmptyMessageDelayed(CheckResult, 5000);
                            }
                            break;
                        case 3:
                            if (ErrorCode.equals("0") && Result.equals("2")) {
                                Status = "网关配置";
                                TV_Submit.setText("网关配置");
                                TV_1.setTextColor(Color.parseColor("#CBCBCB"));
                                TV_2.setTextColor(Color.parseColor("#CBCBCB"));
                                TV_3.setTextColor(Color.parseColor("#CBCBCB"));
                                TV_4.setTextColor(Color.parseColor("#CBCBCB"));
                                V_L1.setBackgroundColor(Color.parseColor("#CBCBCB"));
                                V_L2.setBackgroundColor(Color.parseColor("#CBCBCB"));
                                V_L3.setBackgroundColor(Color.parseColor("#CBCBCB"));
                                V_O1.setBackgroundResource(R.drawable.bkg_oval2);
                                V_O2.setBackgroundResource(R.drawable.bkg_oval2);
                                V_O3.setBackgroundResource(R.drawable.bkg_oval2);
                                V_O4.setBackgroundResource(R.drawable.bkg_oval2);
                                ToastUtil.ErrorOrRight(mActivity, ErrorCode + "  " + ErrorMsg, 2);
                            } else if (ErrorCode.equals("0") && Result.equals("3")) {
                                Status = "关闭绑定";
                                TV_4.setTextColor(Color.parseColor("#CBCBCB"));
                                V_O4.setBackgroundResource(R.drawable.bkg_oval2);
                                TV_Submit.setText("关闭绑定");
                                ToastUtil.ErrorOrRight(mActivity, "开启网关指令下发失败，请重试。", 1);
                            }  else {
                                mHandler.sendEmptyMessageDelayed(CheckResult, 5000);
                            }
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void getDeviceBindingStatus() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("deviceType", device.getDeviceType());
            jsonObject.put("deviceCode", device.getDeviceCode());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("accessToken", SharedUtil.getToken(mActivity));
        map.put("systemID", systemID);
        map.put("deviceInfo", jsonObject.toString());

        WebUtil.getInstance(mActivity).webRequest(Constants.Sys_DeviceBinding, map, new WebUtil.MyCallback() {
            @Override
            public void onSuccess(String result) {
                if (result.equals("-1")) {
                    return;
                }
                try {
                    LOG.E("result=" + result);
                    JSONObject jsonObject = new JSONObject(result);
                    String ErrorCode = jsonObject.getString("ErrorCode");
                    String ErrorMsg = jsonObject.getString("ErrorMsg");
//                    ToastUtil.ErrorOrRight(mActivity, ErrorCode + "  " + ErrorMsg, 1);
                    if (ErrorCode.equals("0")) {
                        String DeviceList = jsonObject.getString("DeviceBindingList");

                        Gson gson = new Gson();
                        List<DeviceBindingStatusBean> DBSB = gson.fromJson(DeviceList, new TypeToken<List<DeviceBindingStatusBean>>() {
                        }.getType());
                        setDeviceStatus(DBSB.get(0));


                        mHandler.sendEmptyMessageDelayed(CheckBindingResult, 5000);

//                        if(CheckData(DBSB.get(0))){
//                            V_L3.setBackgroundColor(Color.parseColor("#51c3bc"));
//                            TV_Submit.setText("关闭绑定");
//                            Status="关闭绑定";
//                        }else{
//                            mHandler.sendEmptyMessageDelayed(CheckBindingResult, 5000);
//                        }
                    } else {
                        ToastUtil.ErrorOrRight(mActivity, ErrorCode + ErrorMsg, 1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private boolean CheckData(DeviceBindingStatusBean DBSB) {
        if (DBSB.getGroudDeviceStatus1() == null || DBSB.getGroudDeviceStatus1().equals("0")) {
            return false;
        }
        if (DBSB.getGroudDeviceStatus2() == null || DBSB.getGroudDeviceStatus2().equals("0")) {
            return false;
        }
        if (DBSB.getGroudDeviceStatus3() == null || DBSB.getGroudDeviceStatus3().equals("0")) {
            return false;
        }
        if (DBSB.getGroudDeviceStatus4() == null || DBSB.getGroudDeviceStatus4().equals("0")) {
            return false;
        }
        if (DBSB.getGroudDeviceStatus5() == null || DBSB.getGroudDeviceStatus5().equals("0")) {
            return false;
        }
        if (DBSB.getGroudDeviceStatus6() == null || DBSB.getGroudDeviceStatus6().equals("0")) {
            return false;
        }
        if (DBSB.getGroudDeviceStatus7() == null || DBSB.getGroudDeviceStatus7().equals("0")) {
            return false;
        }
        if (DBSB.getGroudDeviceStatus8() == null || DBSB.getGroudDeviceStatus8().equals("0")) {
            return false;
        }
        if (DBSB.getTrafficLightStatus1() == null || DBSB.getTrafficLightStatus1().equals("0")) {
            return false;
        }
        if (DBSB.getTrafficLightStatus2() == null || DBSB.getTrafficLightStatus2().equals("0")) {
            return false;
        }
        if (DBSB.getTrafficLightStatus3() == null || DBSB.getTrafficLightStatus3().equals("0")) {
            return false;
        }
        if (DBSB.getTrafficLightStatus4() == null || DBSB.getTrafficLightStatus4().equals("0")) {
            return false;
        }
        if (DBSB.getTrafficLightStatus5() == null || DBSB.getTrafficLightStatus5().equals("0")) {
            return false;
        }
        if (DBSB.getTrafficLightStatus6() == null || DBSB.getTrafficLightStatus6().equals("0")) {
            return false;
        }
        if (DBSB.getTrafficLightStatus7() == null || DBSB.getTrafficLightStatus7().equals("0")) {
            return false;
        }
        if (DBSB.getTrafficLightStatus8() == null || DBSB.getTrafficLightStatus8().equals("0")) {
            return false;
        }
        if (DBSB.getTrafficLightStatus9() == null || DBSB.getTrafficLightStatus9().equals("0")) {
            return false;
        }
        if (DBSB.getTrafficLightStatus10() == null || DBSB.getTrafficLightStatus10().equals("0")) {
            return false;
        }
        if (DBSB.getTrafficLightStatus11() == null || DBSB.getTrafficLightStatus11().equals("0")) {
            return false;
        }
        if (DBSB.getTrafficLightStatus12() == null || DBSB.getTrafficLightStatus12().equals("0")) {
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(CheckResult);
        mHandler.removeMessages(CheckBindingResult);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.IV_Back:
                finish();
                break;
            case R.id.TV_Submit:

                if (Status.equals("网关配置")) {
                    if (TV_Submit.getText().toString().trim().equals("网关配置下发中...")) {
                        ToastUtil.showShort(mActivity, "正在下发配置，请稍候...");
                    } else {
                        TV_Submit.setText("网关配置下发中...");
                        CMD_Type = 1;
                        sendIntersection();
                    }
                } else if (Status.equals("开启绑定")) {
                    if (TV_Submit.getText().toString().trim().equals("开启绑定中...")) {
                        ToastUtil.showShort(mActivity, "正在开启绑定，请稍候...");
                    } else {
                        TV_Submit.setText("开启绑定中...");
                        CMD_Type = 2;
                        sendIntersection();
                    }
                } else if (Status.equals("设备绑定中")) {
                    if (TV_Submit.getText().toString().trim().equals("设备绑定中...")) {
                        ToastUtil.showShort(mActivity, "正在绑定设备，请稍候...");
                    }
                } else if (Status.equals("关闭绑定")) {
                    if (TV_Submit.getText().toString().trim().equals("正在关闭绑定...")) {
                        ToastUtil.showShort(mActivity, "正在关闭绑定，请稍候...");
                    }else{
                        TV_Submit.setText("正在关闭绑定...");
                        CMD_Type = 3;
                        sendIntersection();
                    }

                }
                break;
        }
    }
}
