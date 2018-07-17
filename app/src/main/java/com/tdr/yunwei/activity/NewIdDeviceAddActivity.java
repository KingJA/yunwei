package com.tdr.yunwei.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.google.gson.Gson;
import com.tdr.yunwei.R;
import com.tdr.yunwei.baidumap.BaiDuMapActivity;
import com.tdr.yunwei.baidumap.BaiDuMapDeviceActivity;
import com.tdr.yunwei.bean.DeviceBean;
import com.tdr.yunwei.bean.DeviceBean2;
import com.tdr.yunwei.bean.DictionaryBean;
import com.tdr.yunwei.bean.ParamBean;
import com.tdr.yunwei.util.ActivityUtil;
import com.tdr.yunwei.util.Constants;
import com.tdr.yunwei.util.DBUtils;
import com.tdr.yunwei.util.DeviceInstallUtil;
import com.tdr.yunwei.util.LOG;
import com.tdr.yunwei.util.PhotoUtil;
import com.tdr.yunwei.util.PhotoUtils;
import com.tdr.yunwei.util.SharedUtil;
import com.tdr.yunwei.util.ToastUtil;
import com.tdr.yunwei.util.WebUtil;
import com.tdr.yunwei.util.ZProgressHUD;
import com.tdr.yunwei.util.ZbarUtil;
import com.tdr.yunwei.view.Dialog.DialogUtil;
import com.zbar.lib.CaptureActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2017/11/20.
 * 纠察网关 4个特殊ID
 */
@ContentView(R.layout.activity_newid_device_add)
public class NewIdDeviceAddActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "NewDeviceAddActivity";
    @ViewInject(R.id.image_back)
    private ImageView image_back;


    @ViewInject(R.id.TV_Title)
    private TextView TV_Title;
    @ViewInject(R.id.txt_modify)
    private TextView txt_modify;


    @ViewInject(R.id.et_devicecode)
    private EditText et_devicecode;
    @ViewInject(R.id.et_deviceName)
    private EditText et_deviceName;




    @ViewInject(R.id.txt_devicetype)
    private TextView txt_devicetype;
    @ViewInject(R.id.et_deviceno)
    private EditText et_deviceno;

    @ViewInject(R.id.img_photo1)
    private ImageView img_photo1;
    @ViewInject(R.id.img_photo3)
    private ImageView img_photo3;

    @ViewInject(R.id.ll_baidu)
    private LinearLayout ll_baidu;
    @ViewInject(R.id.img_baidu)
    private ImageView img_baidu;
    @ViewInject(R.id.txt_lng)
    private TextView txt_lng;
    @ViewInject(R.id.txt_lat)
    private TextView txt_lat;
    @ViewInject(R.id.et_deviceaddress)
    private EditText et_deviceaddress;


    @ViewInject(R.id.LL_Type_Visibility)
    private LinearLayout LL_Type_Visibility;




    @ViewInject(R.id.ll_area)
    private LinearLayout ll_area;
    @ViewInject(R.id.txt_area)
    private TextView txt_area;
    @ViewInject(R.id.ll_pcs)
    private LinearLayout ll_pcs;
    @ViewInject(R.id.txt_pcs)
    private TextView txt_pcs;

    @ViewInject(R.id.ll_owner)
    private LinearLayout ll_owner;
    @ViewInject(R.id.txt_owner)
    private TextView txt_owner;
    @ViewInject(R.id.txt_repaircompany)
    private TextView txt_repaircompany;




    @ViewInject(R.id.TV_Submit)
    private TextView TV_Submit;

    private Activity mActivity;

    private DbManager DB;

    private ZProgressHUD zProgressHUD;
    private Gson mGson;
    private DeviceInstallUtil DIU;
    private String SystemID;
    private String Address;
    private String BaiduLAT;
    private String BaiduLNG;
    private String Photo1ToBase64;
    private String Photo3ToBase64;
    private String status;
    private DeviceBean device;
    private Map<String, String> userMap = new HashMap<>();
    private Map<String, String> typeMap = new HashMap<>();
    private Map<String, String> userMap1 = new HashMap<>();
    private Map<String, String> typeMap1 = new HashMap<>();
    private boolean isVISIBLE = true;
    private String deviceType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "当前Activity: ");
        x.view().inject(this);
        initview();
        ActivityUtil.AddActivity(mActivity);
        status = getIntent().getStringExtra("status");
        SystemID = DIU.getSystemID(getIntent().getStringExtra("DeviceType"));
        LOG.E("SystemID=" + SystemID);
        getUsageList1();
        if (status.equals("安装")) {
            Add();
        }
        if (status.equals("详情")) {
            Xiangqing();
        }
    }

    private void initview() {
        mActivity = this;
        mGson = new Gson();
        zProgressHUD = new ZProgressHUD(mActivity);

        deviceType = getIntent().getStringExtra("DeviceType");
        Log.e(TAG, "设备类型: " + deviceType);

        DB = x.getDb(DBUtils.getDb());
        DIU = new DeviceInstallUtil(mActivity, DB);
        image_back.setOnClickListener(this);

        txt_modify.setOnClickListener(this);
        img_photo1.setOnClickListener(this);
        img_photo3.setOnClickListener(this);
        ll_baidu.setOnClickListener(this);
        ll_area.setOnClickListener(this);
        ll_pcs.setOnClickListener(this);
        ll_owner.setOnClickListener(this);
        TV_Submit.setOnClickListener(this);

        txt_repaircompany.setText(SharedUtil.getValue(mActivity, "CompanyName"));
    }


    private void Add() {
        TV_Title.setText("安装设备");
        TV_Submit.setText("确认安装");
        txt_devicetype.setText(getIntent().getStringExtra("DeviceRemark"));
        et_devicecode.setText(getIntent().getStringExtra("DeviceCode"));
        txt_modify.setVisibility(View.GONE);
        LOG.E("DeviceType=" + getIntent().getStringExtra("DeviceType"));
        if (userMap1 != null) {
            String use = userMap1.get("101");
            if (use != null && use.contains("纠章")) {
                setVisible(View.VISIBLE);
            } else {
                setVisible(View.GONE);
            }
        }
    }

    private void Xiangqing() {
        TV_Title.setText("设备详情");
        TV_Submit.setText("返回");
        txt_modify.setVisibility(View.VISIBLE);
        initData();

    }

    private void initData() {

        device = getIntent().getParcelableExtra("deviceBean");
        String pic = getIntent().getStringExtra("pic");

        SystemID = DIU.getSystemID(device.getDeviceType());

        getListType1();

        BaiduLAT = device.getReserve7();
        BaiduLNG = device.getReserve8();


        et_devicecode.setText(device.getDeviceCode());
        txt_devicetype.setText(getIntent().getStringExtra("deviceremark"));
        et_deviceno.setText(device.getSerialNumber());
        String LNG = device.getLNG();
        String LAT = device.getLAT();
        Address = device.getAddress();
        if ("".equals(LNG)) {
            LNG = "0";
        }
        if ("".equals(LAT)) {
            LAT = "0";
        }
        String lat, lng;
        if (!device.getReserve8().equals("") && !device.getReserve7().equals("")) {
            lat = device.getReserve8();
            lng = device.getReserve7();
        } else {
            // 将GPS设备采集的原始GPS坐标转换成百度坐标
            LatLng ll = new LatLng(Double.parseDouble(LAT), Double.parseDouble(LNG));
            CoordinateConverter converter = new CoordinateConverter();
            converter.from(CoordinateConverter.CoordType.GPS);
            // sourceLatLng待转换坐标
            converter.coord(ll);
            LatLng desLatLng = converter.convert();
            lat = desLatLng.latitude + "";
            lng = desLatLng.longitude + "";
        }
        txt_lat.setText(lat);
        txt_lng.setText(lng);

        et_deviceaddress.setText(Address);

        if (userMap1 != null) {
            if (device.getUsage() != null && !device.getUsage().equals("")) {
                if (device.getUsage().equals("101")) {
                    setVisible(View.VISIBLE);
                } else {
                    setVisible(View.GONE);
                }
            }
        }
        String roadType = "";
        LOG.E("AreaID=" + device.getAreaID());
        txt_area.setText(DIU.AreaID2AreaMC(device.getAreaID()));
        txt_pcs.setText(DIU.getPCSMC(device.getPCS()));
        txt_owner.setText(DIU.getParamValue(device.getOwner(), SystemID));
        txt_repaircompany.setText(DIU.getCompanyName(device.getRepairCompany()));


        String AccessType = device.getAccessType();

        LOG.E("AccessType=" + AccessType);

        if (pic.equals("wu")) {
            Photo1ToBase64 = device.getPhoto1();
            Photo3ToBase64 = device.getPhoto3();
        }
        if (pic.equals("you")) {
            Photo1ToBase64 = SharedUtil.getValue(mActivity, "Photo1");
            Photo3ToBase64 = SharedUtil.getValue(mActivity, "Photo3");
        }


        if (!Photo1ToBase64.equals("")) {
            Bitmap bm = PhotoUtil.base64toBitmap(Photo1ToBase64);
            bm = PhotoUtil.thumbnailBitmap(bm);
            img_photo1.setImageBitmap(bm);
        }
        if (!Photo3ToBase64.equals("")) {
            Bitmap bm = PhotoUtil.base64toBitmap(Photo3ToBase64);
            bm = PhotoUtil.thumbnailBitmap(bm);
            img_photo3.setImageBitmap(bm);
        }
        setViewEnabled(false);

    }

    private void setViewEnabled(boolean key) {
        et_devicecode.setEnabled(key);
        et_deviceno.setEnabled(key);
        et_deviceaddress.setEnabled(key);
        ll_area.setEnabled(key);
        ll_pcs.setEnabled(key);
        ll_owner.setEnabled(key);
    }

    private static final int REQUEST_SCAN_SERIAL = 10086;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.iv_scan_serial:
                CaptureActivity.goSimpleCaptureActivity(NewIdDeviceAddActivity.this, REQUEST_SCAN_SERIAL);
                break;
            case R.id.image_back:
                finish();
                break;
            case R.id.txt_modify:
                LOG.E("modify=" + txt_modify.getText().toString());
                if (txt_modify.getText().toString().equals("修改")) {
                    txt_modify.setText("取消");
                    TV_Submit.setText("确认修改");
                    status = "修改";
//                    setViewEnabled(true);
                    setViewEnable();
                } else {
                    txt_modify.setText("修改");
                    TV_Submit.setText("返回");
                    status = "详情";
                    setViewEnabled(false);
                    initData();
                }
                break;
            case R.id.img_photo1:
                if (status.equals("详情")) {
                    ShowBigPic(mActivity, Photo1ToBase64);
                } else {
                    PhotoUtils.takePicture(mActivity, "photo1.jpg");
                }
                break;
            case R.id.img_photo3:
                if (status.equals("详情")) {
                    ShowBigPic(mActivity, Photo3ToBase64);
                } else {
                    PhotoUtils.takePicture(mActivity, "photo3.jpg");
                }

                break;
            case R.id.ll_baidu:
                if (status.equals("安装")) {
                    Intent intent1 = new Intent(mActivity, BaiDuMapActivity.class);
                    startActivityForResult(intent1, 4);
                }
                if (status.equals("详情") || status.equals("修改")) {

                    String lat = txt_lat.getText().toString();
                    String lng = txt_lng.getText().toString();

                    String address = et_deviceaddress.getText().toString();
                    LOG.D("lat=" + lat + "  lng=" + lng);
                    if (lat.equals("") || Double.valueOf(lat) < 0 || lng.equals("") || Double.valueOf(lng) < 0) {
                        ToastUtil.ErrorOrRight(mActivity, "无法定位", 1);
                    }
                    if (!lat.equals("") && Double.valueOf(lat) > 0 && !lng.equals("") && Double.valueOf(lng) > 0) {

                        Intent intent = new Intent(mActivity, BaiDuMapDeviceActivity.class);
                        intent.putExtra("status", status);
                        intent.putExtra("DeviceLAT", lat);
                        intent.putExtra("DeviceLNG", lng);
                        intent.putExtra("DeviceAddress", address);
                        startActivityForResult(intent, 4);
                    }
                }

                break;
            case R.id.ll_area:
                String LastCity = SharedUtil.getValue(mActivity, "CityName");

                if (et_deviceaddress.getText().toString().equals("")) {
                    ToastUtil.showShort(mActivity, "请先选择设备安装地址（经纬度）");
                } else {
                    DialogUtil.ShowList(mActivity, txt_area, DIU.getAreaList(), LastCity + "区域列表");
                    txt_pcs.setText("");
                }
                break;
            case R.id.ll_pcs:
                String AreaMC = txt_area.getText().toString();
                if (AreaMC.equals("")) {
                    ToastUtil.showShort(mActivity, "请先选择辖区");
                } else {
                    DialogUtil.ShowList(mActivity, txt_pcs, DIU.getPCSList(DIU.getAreaID(AreaMC)), AreaMC + "派出所列表");
                }
                break;
            case R.id.ll_owner:
                DialogUtil.ShowList(mActivity, txt_owner, DIU.getOwnerList(SystemID), "产权人");
                break;
            case R.id.TV_Submit:
                String txt = TV_Submit.getText().toString();
                if (CheckData()) {
                    if (txt.equals("确认安装")) {
                        AddDevice();
                    }
                    if (txt.equals("确认修改")) {
                        ModifyDevice();
                    }
                }
                if (txt.equals("返回")) {
                    ActivityUtil.FinishActivity(mActivity);
                }
                break;

        }
    }

    private void setViewEnable() {
        et_devicecode.setEnabled(false);
        et_deviceno.setEnabled(false);
        et_deviceaddress.setEnabled(true);
        ll_area.setEnabled(true);
        ll_pcs.setEnabled(true);
        ll_owner.setEnabled(true);
    }

    private void setVisible(int Visible) {
        LL_Type_Visibility.setVisibility(Visible);
        if (Visible == View.VISIBLE) {
            isVISIBLE = true;
        } else {
            isVISIBLE = false;
        }
    }


    private void getUsageList1() {

        List<DictionaryBean> list = null;
        try {
            list = DB.selector(DictionaryBean.class).where("DictionaryName", "=", "ZD_PURPOSE").and("SystemID", "=",
                    SystemID).findAll();
            LOG.E(SystemID + "   设备用途列表   list。size" + list.size());
            if (list != null && list.size() > 0) {
                userMap1 = new HashMap<>();
                userMap = new HashMap<>();

                String DictionaryID = list.get(0).getDictionaryID();
                List<ParamBean> list2 = DB.selector(ParamBean.class).where("DictionaryID", "=", DictionaryID).findAll();
                LOG.E("设备用途列表   list2。size" + list2.size());
                if (list2 != null && list2.size() > 0) {
                    for (int i = 0; i < list2.size(); i++) {
                        userMap1.put(list2.get(i).getParamCode(), list2.get(i).getParamValue());
                        userMap.put(list2.get(i).getParamValue(), list2.get(i).getParamCode());
                    }
                }
                LOG.E(userMap1.size() + "   设备用途列表   userMap。size" + userMap.size());
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void getListType1() {

        List<DictionaryBean> list = null;
        try {
            list = DB.selector(DictionaryBean.class).where("DictionaryName", "=", "ZD_DEVICETYPE").and("SystemID",
                    "=", SystemID).findAll();
            if (list != null && list.size() > 0) {
                typeMap1 = new HashMap<>();
                typeMap = new HashMap<>();
                String DictionaryID = list.get(0).getDictionaryID();
                List<ParamBean> list2 = DB.selector(ParamBean.class).where("DictionaryID", "=", DictionaryID).findAll();
                if (list2 != null && list2.size() > 0) {
                    for (int i = 0; i < list2.size(); i++) {
                        typeMap1.put(list2.get(i).getParamCode(), list2.get(i).getParamValue());
                        typeMap.put(list2.get(i).getParamValue(), list2.get(i).getParamCode());
                    }
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public void ShowBigPic(Activity mActivity, String strpic) {

        if (strpic.equals("") || strpic == null) {
            ToastUtil.ErrorOrRight(mActivity, "无图片地址", 1);
            return;
        }

        final AlertDialog dialog;

        LOG.E("ShowBigPicpath=" + strpic);

        dialog = new AlertDialog.Builder(mActivity).create();
        dialog.show();//必须放在window前

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setContentView(R.layout.dialog_img);

        ImageView imgView = (ImageView) window.findViewById(R.id.img_open);
        imgView.setImageBitmap(PhotoUtil.base64toBitmap(strpic));


        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private Boolean CheckData() {
        String devicecode = et_devicecode.getText().toString().trim();
        if (devicecode == null || devicecode.equals("")) {
            ToastUtil.showShort(mActivity, "请输入设备编号");
            return false;
        }
        String devicetype = txt_devicetype.getText().toString().trim();
        if (devicetype == null || devicetype.equals("")) {
            ToastUtil.showShort(mActivity, "设备类型为空");
            return false;
        }
        if (Photo1ToBase64 == null || Photo1ToBase64.equals("")) {
            ToastUtil.showShort(mActivity, "请拍摄基站照片");
            return false;
        }
        if (Photo3ToBase64 == null || Photo3ToBase64.equals("")) {
            ToastUtil.showShort(mActivity, "请拍摄沿街照片");
            return false;
        }
        String lng = txt_lng.getText().toString().trim();
        if (lng == null || lng.equals("")) {
            ToastUtil.showShort(mActivity, "请选择经度");
            return false;
        }
        String lat = txt_lat.getText().toString().trim();
        if (lat == null || lat.equals("")) {
            ToastUtil.showShort(mActivity, "请选择纬度");
            return false;
        }
        String deviceaddress = et_deviceaddress.getText().toString().trim();
        if (deviceaddress == null || deviceaddress.equals("")) {
            ToastUtil.showShort(mActivity, "请输入设备地址");
            return false;
        }
        String area = txt_area.getText().toString().trim();
        if (area == null || area.equals("")) {
            ToastUtil.showShort(mActivity, "请选择所属辖区");
            return false;
        }
        String pcs = txt_pcs.getText().toString().trim();
        if (pcs == null || pcs.equals("")) {
            ToastUtil.showShort(mActivity, "请选择所属派出所");
            return false;
        }
        String owner = txt_owner.getText().toString().trim();
        if (owner == null || owner.equals("")) {
            ToastUtil.showShort(mActivity, "请选择产权人");
            return false;
        }
        String repaircompany = txt_repaircompany.getText().toString().trim();
        if (repaircompany == null || repaircompany.equals("")) {
            ToastUtil.showShort(mActivity, "运维公司为空");
            return false;
        }
        return true;
    }

    private DeviceBean setdata() {
        String CarrierOperator = "";
        String roadType = "";
        DeviceBean bean = new DeviceBean();

        bean.setDeviceType(getIntent().getStringExtra("DeviceType"));//设备类型
        LOG.E("DeviceType=" + bean.getDeviceType());

        bean.setDeviceCode(et_devicecode.getText().toString().trim());//设备ID
        LOG.E("DeviceCode=" + bean.getDeviceCode());

        bean.setSerialNumber(et_deviceno.getText().toString().trim());//序列号
        LOG.E("SerialNumber=" + bean.getSerialNumber());

        bean.setPCS(DIU.getPCS_ID(txt_area.getText().toString().trim(), txt_pcs.getText().toString().trim()));//派出所ID
        LOG.E("PCS=" + bean.getPCS());

        bean.setLAT(txt_lat.getText().toString().trim());//经度
        LOG.E("LAT=" + bean.getLAT());

        bean.setLNG(txt_lng.getText().toString().trim());//纬度
        LOG.E("LNG=" + bean.getLNG());

        bean.setPhotoID1(DIU.getUUID());//照片ID1
        LOG.E("PhotoID1=" + bean.getPhotoID1());

        bean.setPhotoID2(DIU.getUUID());//照片ID2
        LOG.E("PhotoID2=" + bean.getPhotoID2());

        bean.setPhotoID3(DIU.getUUID());//照片ID3
        LOG.E("PhotoID3=" + bean.getPhotoID3());

        bean.setPhoto1(Photo1ToBase64);//照片1
        LOG.E(bean.getPhoto1() != null && !bean.getPhoto1().equals("") ? "1有照片" : "1没有照片");

        bean.setPhoto3(Photo3ToBase64);//照片3
        LOG.E(bean.getPhoto3() != null && !bean.getPhoto3().equals("") ? "3有照片" : "3没有照片");

        bean.setAddress(et_deviceaddress.getText().toString());//安装点位
        LOG.E("Address=" + bean.getAddress());

        bean.setOwner(DIU.getParamCode(txt_owner.getText().toString().trim()));//产权所有人名称
        LOG.E("Owner=" + bean.getOwner());

        bean.setRepairCompany(DIU.getCompanyCode(txt_repaircompany.getText().toString().trim()));//运维公司名称
        LOG.E("RepairCompany=" + bean.getRepairCompany());

        bean.setCarrierOperator(CarrierOperator);//运营商名称
        LOG.E("CarrierOperator=" + bean.getCarrierOperator());

        bean.setReserve7(BaiduLNG);//备用字段7，BD_LNG
        LOG.E("Reserve7=" + bean.getReserve7());

        bean.setReserve8(BaiduLAT);//备用字段8，BD_LAT
        LOG.E("Reserve8=" + bean.getReserve8());

        bean.setReserve9("3");//备用字段9，数据来源，1GPS 2百度
        LOG.E("Reserve9=" + bean.getReserve9());

        bean.setReserve10(SharedUtil.getValue(mActivity, "UserName"));//备用字段10，安装人姓名
        LOG.E("Reserve10=" + bean.getReserve10());

        bean.setReserve11(SharedUtil.getValue(mActivity, "UserPhone"));//备用字段11，安装人员手机号码
        LOG.E("Reserve11=" + bean.getReserve11());

        bean.setReserve12(et_deviceName.getText().toString());//基站名称（别名）
        LOG.E("Reserve12=" + bean.getReserve12());

        if (!isVISIBLE) {
            bean.setReserve17("");//路口编号
            LOG.E("Reserve17=" + bean.getReserve17());

            bean.setReserve18("");//路口类型（1大型路口、2中型路口、3小型路口）
            LOG.E("Reserve18=" + bean.getReserve18());

            bean.setReserve19("");//东路口名称
            LOG.E("Reserve19=" + bean.getReserve19());

            bean.setReserve20("");//南路口名称
            LOG.E("Reserve20=" + bean.getReserve20());

            bean.setReserve21("");//西路口名称
            LOG.E("Reserve21=" + bean.getReserve21());

            bean.setReserve22("");//北路口名称
            LOG.E("Reserve22=" + bean.getReserve22());

            bean.setReserve23("");//1号地埋方向（东、南、西、北）
            LOG.E("Reserve23=" + bean.getReserve23());

            bean.setReserve24("");//中心地埋编号
            LOG.E("Reserve24=" + bean.getReserve24());
        } else {

            bean.setReserve18(roadType);//路口类型（1大型路口、2中型路口、3小型路口）
            LOG.E("Reserve18=" + bean.getReserve18());
        }


        if (status.equals("安装")) {
            bean.setDeviceID("");//此处应为空（基站ID  2017-2-14）
            bean.setAreaID("");//区域ID，接口预留，本次不使用
            bean.setXQ("");//辖区分局ID
//            bean.setUsage("");//用途
            bean.setJWH("");//居委会，接口预留，不使用
            bean.setReserve5("");//备用字段5，基站级别
            bean.setReserve6("");//备用字段6，位置类型

            bean.setReserve14("");//备用字段14
            bean.setReserve15("");//备用字段15
            bean.setReserve16("");//备用字段16

        } else if (status.equals("修改")) {
            bean.setDeviceID(device.getDeviceID());
            bean.setAreaID(device.getAreaID());
            bean.setXQ(device.getXQ());
//            bean.setUsage(device.getUsage());
            bean.setJWH(device.getJWH());
            bean.setReserve5(device.getReserve5());
            bean.setReserve6(device.getReserve6());
            bean.setReserve14(device.getReserve14());
            bean.setReserve15(device.getReserve15());
            bean.setReserve16(device.getReserve16());

        }

        bean.setReserve25("");
        bean.setReserve26("");
        bean.setReserve27("");
        bean.setReserve28("");
        bean.setReserve29("");
        bean.setReserve30("");

        LOG.E("JWH=" + bean.getJWH());
        LOG.E("DeviceID=" + bean.getDeviceID());
        LOG.E("AreaID=" + bean.getAreaID());
        LOG.E("XQ=" + bean.getXQ());
//        LOG.E("Usage=" + bean.getUsage());
        LOG.E("Reserve5=" + bean.getReserve5());
        LOG.E("Reserve6=" + bean.getReserve6());
        LOG.E("Reserve13=" + bean.getReserve13());
        LOG.E("Reserve14=" + bean.getReserve14());
        LOG.E("Reserve15=" + bean.getReserve15());
        LOG.E("Reserve16=" + bean.getReserve16());

        return bean;
    }

    private void AddDevice() {

        zProgressHUD = new ZProgressHUD(mActivity);
        zProgressHUD.setMessage("正在提交数据请稍后……");
        zProgressHUD.show();

        String deviceInfo = mGson.toJson(setdata());
        LOG.E("New deviceInfo=" + deviceInfo);

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("accessToken", SharedUtil.getToken(mActivity));
        map.put("deviceInfo", deviceInfo);
        map.put("systemID", SystemID);

        //ToastUtil.showShort(mActivity,SystemID+"//"+AreaID+"//"+PCS);

        WebUtil.getInstance(mActivity).webRequest(Constants.Sys_AddDevice, map, new WebUtil.MyCallback() {
            @Override
            public void onSuccess(String result) {

                zProgressHUD.dismiss();
                if (result.equals("-1")) {
                    return;
                }

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String ErrorCode = jsonObject.getString("ErrorCode");
                    String ErrorMsg = jsonObject.getString("ErrorMsg");
                    if (ErrorCode.equals("0")) {
                        ToastUtil.ErrorOrRight(mActivity, "设备安装成功！", 2);
                        ActivityUtil.SaveAddress(mActivity, Address, et_devicecode.getText().toString().trim());
                        Intent intent = new Intent(mActivity, HomeActivity.class);
                        startActivity(intent);
                        ZbarUtil.setDeviceClear();
                    } else {
                        ToastUtil.ErrorOrRight(mActivity, "设备安装失败！" + ErrorCode + ErrorMsg, 1);

                        if (ErrorMsg.contains("已存在")) {
//                            ll_zbar.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


    }

    private void ModifyDevice() {
        zProgressHUD = new ZProgressHUD(mActivity);
        zProgressHUD.setMessage("正在提交数据请稍后……");
        zProgressHUD.show();
        String deviceInfo = mGson.toJson(setdata());
        LOG.LE("修改数据=" + deviceInfo);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("accessToken", SharedUtil.getToken(mActivity));
        map.put("deviceInfo", deviceInfo);
        map.put("systemID", SystemID);

        WebUtil.getInstance(mActivity).webRequest(Constants.Sys_ModifyDevice, map, new WebUtil.MyCallback() {
            @Override
            public void onSuccess(String result) {

                zProgressHUD.dismiss();
                if (result.equals("-1")) {
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    String ErrorCode = jsonObject.getString("ErrorCode");
                    String ErrorMsg = jsonObject.getString("ErrorMsg");
                    if (ErrorCode.equals("0")) {
                        ToastUtil.ErrorOrRight(mActivity, "设备修改成功！", 2);
                        SharedUtil.setValue(mActivity, "isCodeChange", et_devicecode.getText().toString().trim());
                        hasResult();
                        ActivityUtil.FinishActivity(mActivity);
                    } else {
                        ToastUtil.ErrorOrRight(mActivity, "设备修改失败！" + ErrorMsg, 1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void hasResult() {
        Intent intent = new Intent();
        intent.putExtra("result", "1");
        setResult(RESULT_OK, intent);
    }

    private void CheckAddress() {
        if (ActivityUtil.CheckAddressRepeated(mActivity, Address)) {
            showDialog1("您已使用过 （" + Address + "） 这个地址。");
        }

    }

    private void showDialog1(String content) {
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

    private static final String PRODUCE_NO_TYPE = "1604";
    private String produceNo;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PhotoUtils.CAMERA_REQESTCODE:
                if (resultCode == RESULT_OK) {
                    int degree = PhotoUtils.readPictureDegree(PhotoUtils.imageFile.getAbsolutePath());
                    Bitmap bitmap = PhotoUtils.rotaingImageView(degree, PhotoUtils.getBitmapFromFile(PhotoUtils
                            .imageFile, 800, 800));
                    String PhotoNum = PhotoUtils.mPicName.substring(5, 6);
                    switch (PhotoNum) {
                        case "1":
                            img_photo1.setImageBitmap(bitmap);
                            Photo1ToBase64 = PhotoUtil.bitmapToString(bitmap, mActivity);
                            break;
                        case "3":
                            img_photo3.setImageBitmap(bitmap);
                            Photo3ToBase64 = PhotoUtil.bitmapToString(bitmap, mActivity);
                            break;
                            default:
                                break;
                    }
                }
                break;
            case 4:
                if (resultCode == Activity.RESULT_OK) {
                    String LNG = data.getStringExtra("MapLNG");
                    String LAT = data.getStringExtra("MapLAT");
                    Address = data.getStringExtra("Address");
                    BaiduLAT = data.getStringExtra("BaiduMapLAT");
                    BaiduLNG = data.getStringExtra("BaiduMapLNG");

                    LOG.D("LNG=" + LNG);
                    LOG.D("LAT=" + LAT);
                    LOG.D("Address=" + Address);
                    LOG.D("BaiduLNG=" + BaiduLNG);
                    LOG.D("BaiduLAT=" + BaiduLAT);
                    if (!BaiduLNG.equals("")) {
                        txt_lng.setText(BaiduLNG);
                        txt_lat.setText(BaiduLAT);
                        et_deviceaddress.setText(Address);
                        CheckAddress();
                        String Areatxt;
                        if (Address.length() >= 9) {
                            Areatxt = Address.substring(6, 9);
                        } else {
                            Areatxt = Address;
                        }
                        txt_area.setText(DIU.getAreaMC(Areatxt));
                        txt_pcs.setText("");
                        img_baidu.setImageResource(R.mipmap.imagelocation_on);
                    }
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
