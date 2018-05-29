package com.tdr.yunwei.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.google.gson.Gson;
import com.tdr.yunwei.R;
import com.tdr.yunwei.YunWeiApplication;
import com.tdr.yunwei.baidumap.BaiDuMapActivity;
import com.tdr.yunwei.baidumap.BaiDuMapDeviceActivity;
import com.tdr.yunwei.bean.Area2ComanyBean;
import com.tdr.yunwei.bean.CityAreaBean;
import com.tdr.yunwei.bean.CityAreaPCSBean;
import com.tdr.yunwei.bean.DASBean;
import com.tdr.yunwei.bean.DeviceBean;
import com.tdr.yunwei.bean.DictionaryBean;
import com.tdr.yunwei.bean.ParamBean;
import com.tdr.yunwei.bean.RepairCompanyBean;
import com.tdr.yunwei.util.ActivityUtil;
import com.tdr.yunwei.util.Constants;
import com.tdr.yunwei.util.DBUtils;
import com.tdr.yunwei.util.DeviceInstallUtil;
import com.tdr.yunwei.util.LOG;
import com.tdr.yunwei.util.MatchUtil;
import com.tdr.yunwei.util.PhotoUtil;
import com.tdr.yunwei.util.PhotoUtils;
import com.tdr.yunwei.util.SharedUtil;
import com.tdr.yunwei.util.ToastUtil;
import com.tdr.yunwei.util.WebUtil;
import com.tdr.yunwei.util.ZProgressHUD;
import com.tdr.yunwei.util.ZbarUtil;
import com.tdr.yunwei.view.Dialog.DialogUtil;
import com.tdr.yunwei.view.Dialog.DoOk;
import com.zbar.lib.CaptureActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * Created by Administrator on 2016/4/20.
 * 城市网关
 */
public class DeviceAddActivity extends Activity {
    private static final String TAG = "DeviceAddActivity";
    private Activity mActivity;

    DbManager DB;

    private ScrollView scrollView;
    private LinearLayout ll_zbar;
    private LinearLayout ll_baidu;

    private LinearLayout ll_usage, ll_owner, ll_company, ll_devicelevel, ll_locationtype;

    private LinearLayout ll_area, ll_pcs;

    private TextView txt_devicetype;
    private EditText txt_devicecode, txt_deviceno;
    private TextView txt_deviceusag;
    private ImageView img_zbar;
    private ImageView img_photo1, img_photo2, img_photo3;

    private TextView txt_lng, txt_lat;
    private EditText txt_deviceaddress;
    private TextView txt_area, txt_pcs;

    private EditText txt_phone;
    private TextView txt_owner, txt_repaircompany, txt_Reserve3, txt_locationtype, txt_devicelevel;
    private LinearLayout ll_Reserve3;
    private EditText txt_Reserve1, txt_Reserve2, txt_Reserve4;


    private RadioGroup rg_yunyingshang;
    private RadioButton rb_yidong, rb_liantong, rb_dianxin;


    private RadioGroup rg_accesstype;
    private RadioButton rb_youxian, rb_wuxian, rb_weizhi;

    private LinearLayout ll_youxian, ll_wuxian, ll_devicelasttime;


    private EditText txt_sim, txt_ip, txt_yanma, txt_wangguan, txt_beizhu;

    private ImageView img_baidu;

    private Map<String, String> levelMap = null;
    private Map<String, String> ltypeMap = null;

    private Map<String, String> userMap = null;
    private Map<String, String> typeMap = null;

    private Map<String, String> userMap1 = null;
    private Map<String, String> typeMap1 = null;


    private Map<String, String> levelMap1 = null;
    private Map<String, String> ltypeMap1 = null;


    Button btn_ok;

    String status = "";
    TextView tv_title, txt_modify, txt_device_last_time;
    ImageView img_title;
    String DeviceRemark = "", SystemID = "";

    String Areatxt = "";
    String LastCityID = "";
    Gson gson = new Gson();
    String posStr = "";
    private YunWeiApplication YWA;
    private String BaiduLNG, BaiduLAT;
    private String MyLat = "";
    private String MyLng = "";
    private ImageView iv_scan_serial;
    private EditText et_produceNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "城市网关: ");
        setContentView(R.layout.activity_deviceadd);

        mActivity = DeviceAddActivity.this;
        ActivityUtil.AddActivity(mActivity);
        LastCityID = SharedUtil.getValue(mActivity, "CityID");
        YWA = YunWeiApplication.getInstance();
//        DB = YWA.getDB();
        DB = x.getDb(DBUtils.getDb());
//        ToastUtil.showShort(mActivity,HomeActivity.IsMapiIn==true?"IsMapiIn=true":"IsMapiIn=false");
        findView();
        MyOnClickListener();

        levelMap = new HashMap<>();
        ltypeMap = new HashMap<>();

        userMap = new HashMap<>();
        typeMap = new HashMap<>();

        userMap1 = new HashMap<>();
        typeMap1 = new HashMap<>();


        levelMap1 = new HashMap<>();
        typeMap1 = new HashMap<>();

        status = getIntent().getStringExtra("status");

        if (status.equals("安装")) {
            Add();
        }
        if (status.equals("详情")) {
            Xiangqing();
        }
//        ToastUtil.showShort(mActivity,"StationID"+HomeActivity.StationID);

    }

    private String getSystemID(String DeviceType) {
        String systemid = "";
        try {
            LastCityID = SharedUtil.getValue(mActivity, "CityID");
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


    private void Add() {
        tv_title.setText("安装设备");
        btn_ok.setText("确认安装");
        ll_devicelasttime.setVisibility(View.GONE);
        DeviceCode = getIntent().getStringExtra("DeviceCode");
        DeviceRemark = getIntent().getStringExtra("DeviceRemark");
        DeviceType = getIntent().getStringExtra("DeviceType");
        Log.e(TAG, "DeviceType: " + DeviceType);
        SystemID = getSystemID(DeviceType);
        LOG.D("SystemID=" + SystemID);
        SharedUtil.setValue(mActivity, "DeviceType", DeviceType);
        txt_devicecode.setText(DeviceCode);
        txt_devicetype.setText(DeviceRemark);
        txt_devicecode.setInputType(InputType.TYPE_CLASS_TEXT);
    }

    private void Xiangqing() {
        tv_title.setText("设备详情");
        btn_ok.setText("返回");
        txt_modify.setVisibility(View.VISIBLE);
        initData();
        setViewUnEnabled();
        txt_devicecode.setInputType(InputType.TYPE_NULL);
    }

    private static final int REQUEST_SCAN_SERIAL = 10086;

    private void findView() {
        ll_zbar = (LinearLayout) findViewById(R.id.ll_zbar);
        img_zbar = (ImageView) findViewById(R.id.img_zbar);
        et_produceNo = (EditText) findViewById(R.id.et_produceNo);
        iv_scan_serial = (ImageView) findViewById(R.id.iv_scan_serial);
        iv_scan_serial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaptureActivity.goSimpleCaptureActivity(DeviceAddActivity.this, REQUEST_SCAN_SERIAL);
            }
        });

        img_title = (ImageView) findViewById(R.id.image_back);
        tv_title = (TextView) findViewById(R.id.text_title);
        txt_modify = (TextView) findViewById(R.id.txt_modify);

        scrollView = (ScrollView) findViewById(R.id.scrollView);

        txt_devicecode = (EditText) findViewById(R.id.txt_devicecode);
        txt_devicetype = (TextView) findViewById(R.id.txt_devicetype);

        txt_deviceno = (EditText) findViewById(R.id.txt_deviceno);

        txt_deviceusag = (TextView) findViewById(R.id.txt_deviceusag);

        txt_lng = (TextView) findViewById(R.id.txt_lng);
        txt_lat = (TextView) findViewById(R.id.txt_lat);
        txt_deviceaddress = (EditText) findViewById(R.id.txt_deviceaddress);

        txt_area = (TextView) findViewById(R.id.txt_area);
        txt_pcs = (TextView) findViewById(R.id.txt_pcs);


        txt_phone = (EditText) findViewById(R.id.txt_phone);
        txt_owner = (TextView) findViewById(R.id.txt_owner);
        txt_locationtype = (TextView) findViewById(R.id.txt_locationtype);
        txt_devicelevel = (TextView) findViewById(R.id.txt_devicelevel);
        txt_repaircompany = (TextView) findViewById(R.id.txt_repaircompany);

        txt_repaircompany.setText(SharedUtil.getValue(mActivity, "CompanyName"));
        txt_sim = (EditText) findViewById(R.id.txt_sim);
        txt_ip = (EditText) findViewById(R.id.txt_ip);
        txt_yanma = (EditText) findViewById(R.id.txt_yanma);
        txt_wangguan = (EditText) findViewById(R.id.txt_wangguan);
        txt_beizhu = (EditText) findViewById(R.id.et_txt);


        txt_Reserve1 = (EditText) findViewById(R.id.txt_Reserve1);
        txt_Reserve2 = (EditText) findViewById(R.id.txt_Reserve2);
        txt_Reserve4 = (EditText) findViewById(R.id.txt_Reserve4);

        ll_Reserve3 = (LinearLayout) findViewById(R.id.ll_Reserve3);
        txt_Reserve3 = (TextView) findViewById(R.id.txt_Reserve3);


        img_baidu = (ImageView) findViewById(R.id.img_baidu);


        img_photo1 = (ImageView) findViewById(R.id.img_photo1);
        img_photo2 = (ImageView) findViewById(R.id.img_photo2);
        img_photo3 = (ImageView) findViewById(R.id.img_photo3);

        txt_device_last_time = (TextView) findViewById(R.id.txt_device_last_time);
        ll_devicelasttime = (LinearLayout) findViewById(R.id.ll_devicelasttime);
        ll_baidu = (LinearLayout) findViewById(R.id.ll_baidu);
        ll_usage = (LinearLayout) findViewById(R.id.ll_usage);

        ll_owner = (LinearLayout) findViewById(R.id.ll_owner);
        ll_company = (LinearLayout) findViewById(R.id.ll_company);

        ll_devicelevel = (LinearLayout) findViewById(R.id.ll_level);

        ll_locationtype = (LinearLayout) findViewById(R.id.ll_locationtype);

        ll_area = (LinearLayout) findViewById(R.id.ll_area);
        ll_pcs = (LinearLayout) findViewById(R.id.ll_pcs);


        ll_youxian = (LinearLayout) findViewById(R.id.ll_youxian);
        ll_wuxian = (LinearLayout) findViewById(R.id.ll_wuxian);


        rg_accesstype = (RadioGroup) findViewById(R.id.rg_accesstype);
        rb_youxian = (RadioButton) findViewById(R.id.rb_youxian);
        rb_wuxian = (RadioButton) findViewById(R.id.rb_wuxian);
        rb_weizhi = (RadioButton) findViewById(R.id.rb_weizhi);

        rg_yunyingshang = (RadioGroup) findViewById(R.id.rg_yunyingshang);
        rb_dianxin = (RadioButton) findViewById(R.id.rb_dianxin);
        rb_yidong = (RadioButton) findViewById(R.id.rb_yidong);
        rb_liantong = (RadioButton) findViewById(R.id.rb_liantong);


        btn_ok = (Button) findViewById(R.id.btn_ok);

    }


    private void setViewEnabled() {

        ll_zbar.setVisibility(View.VISIBLE);
        ll_zbar.setEnabled(true);

        txt_deviceno.setEnabled(false);

        txt_phone.setEnabled(true);
        txt_sim.setEnabled(true);
        txt_ip.setEnabled(true);
        txt_yanma.setEnabled(true);
        txt_wangguan.setEnabled(true);
        txt_beizhu.setEnabled(true);
        txt_Reserve1.setEnabled(true);
        txt_Reserve2.setEnabled(true);
        txt_Reserve4.setEnabled(true);
        ll_usage.setEnabled(true);

        ll_area.setEnabled(true);
        ll_pcs.setEnabled(true);

        ll_owner.setEnabled(true);
        ll_locationtype.setEnabled(true);
        ll_devicelevel.setEnabled(true);
//        ll_company.setEnabled(true);
        ll_Reserve3.setEnabled(true);

        rb_weizhi.setVisibility(View.GONE);

        if (AccessType.equals("1")) {
            ll_youxian.setVisibility(View.VISIBLE);
            ll_wuxian.setVisibility(View.GONE);
        }
        if (AccessType.equals("2")) {
            ll_wuxian.setVisibility(View.VISIBLE);
            ll_youxian.setVisibility(View.GONE);
        }


        rb_wuxian.setVisibility(View.VISIBLE);
        rb_youxian.setVisibility(View.VISIBLE);

        rb_yidong.setVisibility(View.VISIBLE);
        rb_liantong.setVisibility(View.VISIBLE);
        rb_dianxin.setVisibility(View.VISIBLE);
    }

    private void setViewUnEnabled() {
        ll_zbar.setVisibility(View.GONE);
        ll_zbar.setEnabled(false);

        txt_deviceno.setEnabled(false);
        txt_phone.setEnabled(false);
        txt_sim.setEnabled(false);
        txt_ip.setEnabled(false);
        txt_yanma.setEnabled(false);
        txt_wangguan.setEnabled(false);
        txt_beizhu.setEnabled(false);
        txt_Reserve1.setEnabled(false);
        txt_Reserve2.setEnabled(false);
        txt_Reserve4.setEnabled(false);
        ll_usage.setEnabled(false);

        ll_area.setEnabled(false);
        ll_pcs.setEnabled(false);
        ll_locationtype.setEnabled(false);
        ll_devicelevel.setEnabled(false);
        ll_owner.setEnabled(false);
//        ll_company.setEnabled(false);
        ll_Reserve3.setEnabled(false);


    }

    DeviceBean deviceBean;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    public void showbean(DeviceBean devicebean) {

        LOG.D("------------deviceBean------------");
        LOG.D("getDeviceID=  " + devicebean.getDeviceID());
        LOG.D("getDeviceType=  " + devicebean.getDeviceType());
        LOG.D("getDeviceCode=  " + devicebean.getDeviceCode());
        LOG.D("getSerialNumber=  " + devicebean.getSerialNumber());
        LOG.D("getAreaID=  " + devicebean.getAreaID());

        LOG.D("getXQ=  " + devicebean.getXQ());
        LOG.D("getGAJ=  " + devicebean.getGAJ());
        LOG.D("getPCS=  " + devicebean.getPCS());
        LOG.D("getJWH=  " + devicebean.getJWH());
        LOG.D("getLNG=  " + devicebean.getLNG());
        LOG.D("getLAT=  " + devicebean.getLAT());

        LOG.D("getPhotoID1=  " + devicebean.getPhotoID1());
        LOG.D("getPhotoID2=  " + devicebean.getPhotoID2());
        LOG.D("getPhotoID3=  " + devicebean.getPhotoID3());

        LOG.D("getAddress=  " + devicebean.getAddress());
        LOG.D("getUsage=  " + devicebean.getUsage());
        LOG.D("getOwner=  " + devicebean.getOwner());
        LOG.D("getRepairCompany=  " + devicebean.getRepairCompany());
        LOG.D("getCarrierOperator=  " + devicebean.getCarrierOperator());
        LOG.D("getIP=  " + devicebean.getIP());
        LOG.D("getGateway=  " + devicebean.getGateway());
        LOG.D("getSIM=  " + devicebean.getSIM());
        LOG.D("getAccessType=  " + devicebean.getAccessType());
        LOG.D("getDescription=  " + devicebean.getDescription());

        LOG.D("getReserve1=  " + devicebean.getReserve1());
        LOG.D("getReserve2=  " + devicebean.getReserve2());
        LOG.D("getReserve3=  " + devicebean.getReserve3());
        LOG.D("getReserve4=  " + devicebean.getReserve4());
        LOG.D("getReserve5=  " + devicebean.getReserve5());
        LOG.D("getReserve6=  " + devicebean.getReserve6());
        LOG.D("getReserve7=  " + devicebean.getReserve7());
        LOG.D("getReserve8=  " + devicebean.getReserve8());
        LOG.D("getReserve9=  " + devicebean.getReserve9());
        LOG.D("getReserve10=  " + devicebean.getReserve10());
        LOG.D("getReserve11=  " + devicebean.getReserve11());
        LOG.D("getReserve12=  " + devicebean.getReserve12());
        LOG.D("getReserve13=  " + devicebean.getReserve13());
        LOG.D("getReserve14=  " + devicebean.getReserve14());
        LOG.D("getReserve15=  " + devicebean.getReserve15());
        LOG.D("getReserve16=  " + devicebean.getReserve16());
        LOG.D("------------deviceBean------------");

    }

    private void initData() {

        deviceBean = getIntent().getParcelableExtra("deviceBean");
        DeviceRemark = getIntent().getStringExtra("deviceremark");
        showbean(deviceBean);
        SystemID = getSystemID(deviceBean.getDeviceType());

        txt_devicecode.setText(deviceBean.getDeviceCode());
        txt_devicetype.setText(DeviceRemark);

        txt_deviceno.setText(deviceBean.getSerialNumber());
        LNG = deviceBean.getLNG();
        LAT = deviceBean.getLAT();
        if ("".equals(LNG)) {
            LNG = "0";
        }
        if ("".equals(LAT)) {
            LAT = "0";
        }

        if (!deviceBean.getReserve8().equals("") && !deviceBean.getReserve7().equals("")) {
            MyLat = deviceBean.getReserve8();
            MyLng = deviceBean.getReserve7();
        } else {
            // 将GPS设备采集的原始GPS坐标转换成百度坐标
            LatLng ll = new LatLng(Double.parseDouble(LAT), Double.parseDouble(LNG));
            CoordinateConverter converter = new CoordinateConverter();
            converter.from(CoordinateConverter.CoordType.GPS);
            // sourceLatLng待转换坐标
            converter.coord(ll);
            LatLng desLatLng = converter.convert();
            MyLat = desLatLng.latitude + "";
            MyLng = desLatLng.longitude + "";
        }
        txt_lat.setText(MyLat);
        txt_lng.setText(MyLng);


        txt_deviceaddress.setText(deviceBean.getAddress());
        et_produceNo.setText(deviceBean.getReserve13());
        txt_area.setText(getAreaMC(deviceBean.getAreaID()));
        txt_pcs.setText(getPCSMC(deviceBean.getPCS()));

        //ToastUtil.showShort(mActivity,getAreaMC(deviceBean.getAreaID())+"//"+deviceBean.getAreaID());
        getLtypeList1();
        getLevelList1();
        getListType1();
        getUsageList1();
        if (ltypeMap1 != null) {
            String locationtype = getLtypeValue(deviceBean.getReserve6());
            txt_locationtype.setText(ltypeMap1.get(deviceBean.getReserve6()));
            txt_locationtype.setText(locationtype);
        }
        if (levelMap1 != null) {
            txt_devicelevel.setText(levelMap1.get(deviceBean.getReserve5()));
            String level = getLevelValue(deviceBean.getReserve5());
            txt_devicelevel.setText(level);

        }

        if (userMap1 != null) {
            txt_deviceusag.setText(userMap1.get(deviceBean.getUsage()));
            String userfor = getUserforValue(deviceBean.getUsage());
            txt_deviceusag.setText(userfor);
        }
        if (typeMap1 != null) {
            txt_Reserve3.setText(typeMap1.get(deviceBean.getReserve3()));
            String type = getTypeValue(deviceBean.getReserve3());
            txt_Reserve3.setText(type);
        }

        String value = getParamValue(deviceBean.getOwner());
        txt_owner.setText(value);
        String name = getCompanyName(deviceBean.getRepairCompany());
        txt_repaircompany.setText(name);

        txt_Reserve1.setText(deviceBean.getReserve1());
        txt_Reserve2.setText(deviceBean.getReserve2());
        txt_Reserve4.setText(deviceBean.getReserve4());
        txt_beizhu.setText(deviceBean.getDescription());
        LOG.E("设备最后使用时间" + deviceBean.getReserve10());
        txt_device_last_time.setText(deviceBean.getReserve10());
        Reserve9 = deviceBean.getReserve9();
        AccessType = deviceBean.getAccessType();

        LOG.E("AccessType=" + AccessType);

        if (AccessType.equals("1")) {
            rb_youxian.setChecked(true);
            ll_youxian.setVisibility(View.VISIBLE);
            ll_wuxian.setVisibility(View.GONE);
            rb_wuxian.setVisibility(View.GONE);

            txt_ip.setText(deviceBean.getIP());
            txt_yanma.setText(deviceBean.getMask());
            txt_wangguan.setText(deviceBean.getGateway());
            AccessType = "1";
        }
        if (AccessType.equals("2")) {
            rb_wuxian.setChecked(true);
            ll_youxian.setVisibility(View.GONE);
            ll_wuxian.setVisibility(View.VISIBLE);
            rb_youxian.setVisibility(View.GONE);

            txt_sim.setText(deviceBean.getSIM());
            txt_phone.setText(deviceBean.getPhone());

            String CarrierOperator = deviceBean.getCarrierOperator();
            LOG.E("CarrierOperator=" + CarrierOperator);

            if (CarrierOperator.equals("移动")) {
                rb_yidong.setChecked(true);
                rb_yidong.setVisibility(View.VISIBLE);
                rb_liantong.setVisibility(View.GONE);
                rb_dianxin.setVisibility(View.GONE);
            }
            if (CarrierOperator.equals("联通")) {
                rb_liantong.setChecked(true);
                rb_yidong.setVisibility(View.GONE);
                rb_liantong.setVisibility(View.VISIBLE);
                rb_dianxin.setVisibility(View.GONE);
            }
            if (CarrierOperator.equals("电信")) {
                rb_dianxin.setChecked(true);
                rb_yidong.setVisibility(View.GONE);
                rb_liantong.setVisibility(View.GONE);
                rb_dianxin.setVisibility(View.VISIBLE);
            }

            AccessType = "2";

        }
        if (AccessType.equals("")) {
            rb_weizhi.setVisibility(View.VISIBLE);
            rb_weizhi.setChecked(true);

            rb_youxian.setVisibility(View.GONE);
            rb_wuxian.setVisibility(View.GONE);
            ll_youxian.setVisibility(View.GONE);
            ll_wuxian.setVisibility(View.GONE);

            AccessType = "1";
        }


        String pic = getIntent().getStringExtra("pic");

        if (pic.equals("wu")) {
            Photo1ToBase64 = deviceBean.getPhoto1();
            Photo2ToBase64 = deviceBean.getPhoto2();
            Photo3ToBase64 = deviceBean.getPhoto3();
        }
        if (pic.equals("you")) {
            Photo1ToBase64 = SharedUtil.getValue(mActivity, "Photo1");
            Photo2ToBase64 = SharedUtil.getValue(mActivity, "Photo2");
            Photo3ToBase64 = SharedUtil.getValue(mActivity, "Photo3");
        }


        if (!Photo1ToBase64.equals("")) {
            Bitmap bm = PhotoUtil.base64toBitmap(Photo1ToBase64);
            bm = PhotoUtil.thumbnailBitmap(bm);
            img_photo1.setImageBitmap(bm);
        }
        if (!Photo2ToBase64.equals("")) {
            Bitmap bm = PhotoUtil.base64toBitmap(Photo2ToBase64);
            bm = PhotoUtil.thumbnailBitmap(bm);
            img_photo2.setImageBitmap(bm);
        }
        if (!Photo3ToBase64.equals("")) {
            Bitmap bm = PhotoUtil.base64toBitmap(Photo3ToBase64);
            bm = PhotoUtil.thumbnailBitmap(bm);
            img_photo3.setImageBitmap(bm);
        }


    }


    private String getAreaMC(String AreaID) {
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


    private String getPCSMC(String PCSID) {
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

    private void MyOnClickListener() {


        MyOnClick myOnClick = new MyOnClick();

        txt_modify.setOnClickListener(myOnClick);
        ll_zbar.setOnClickListener(myOnClick);
        ll_baidu.setOnClickListener(myOnClick);

        ll_usage.setOnClickListener(myOnClick);
        ll_devicelevel.setOnClickListener(myOnClick);
        ll_locationtype.setOnClickListener(myOnClick);
        ll_owner.setOnClickListener(myOnClick);
//        ll_company.setOnClickListener(myOnClick);
        ll_Reserve3.setOnClickListener(myOnClick);

        ll_area.setOnClickListener(myOnClick);
        ll_pcs.setOnClickListener(myOnClick);

        img_title.setOnClickListener(myOnClick);
        btn_ok.setOnClickListener(myOnClick);


        img_photo1.setOnClickListener(myOnClick);
        img_photo2.setOnClickListener(myOnClick);
        img_photo3.setOnClickListener(myOnClick);


        rg_yunyingshang.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (rb_yidong.getId() == checkedId) {
                    CarrierOperator = rb_yidong.getText().toString();
                    LOG.D("移动CarrierOperator=" + CarrierOperator);
                }
                if (rb_liantong.getId() == checkedId) {
                    CarrierOperator = rb_liantong.getText().toString();
                    LOG.D("联通CarrierOperator=" + CarrierOperator);
                }
                if (rb_dianxin.getId() == checkedId) {
                    CarrierOperator = rb_dianxin.getText().toString();
                    LOG.D("电信CarrierOperator=" + CarrierOperator);
                }
            }
        });


        rg_accesstype.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (rb_youxian.getId() == checkedId) {
                    AccessType = "1";
                    ll_wuxian.setVisibility(View.GONE);
                    ll_youxian.setVisibility(View.VISIBLE);

                    txt_sim.setText("");
                    txt_phone.setText("");
                    rg_yunyingshang.clearCheck();
                    CarrierOperator = "";
                    LOG.D("有线CarrierOperator=" + CarrierOperator);
                }
                if (rb_wuxian.getId() == checkedId) {
                    AccessType = "2";
                    ll_wuxian.setVisibility(View.VISIBLE);
                    ll_youxian.setVisibility(View.GONE);

                    txt_ip.setText("");
                    txt_yanma.setText("");
                    txt_wangguan.setText("");
                    LOG.D("无线CarrierOperator=" + CarrierOperator);
                }

            }
        });


    }


    private class MyOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            if (v == ll_zbar) {
                Intent intent1 = new Intent(mActivity, CaptureActivity.class);
                startActivityForResult(intent1, 0);
            }

            if (v == txt_modify) {//修改，控件可用
                if (txt_modify.getText().toString().equals("修改")) {
                    txt_modify.setText("取消");
                    btn_ok.setText("确认修改");
                    status = "修改";

                    setViewEnabled();
                } else {
                    txt_modify.setText("修改");
                    btn_ok.setText("返回");
                    status = "详情";
                    setViewUnEnabled();
                }
            }

            if (v == ll_baidu) {

                if (status.equals("安装")) {
                    AreaMC = "";
                    Intent intent1 = new Intent(mActivity, BaiDuMapActivity.class);
                    startActivityForResult(intent1, 4);
                }
                if (status.equals("详情") || status.equals("修改")) {

                    String lat = txt_lat.getText().toString();
                    String lng = txt_lng.getText().toString();

                    String address = txt_deviceaddress.getText().toString();
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


            }

            if (v == ll_usage) {
                DialogUtil.ShowList(mActivity, txt_deviceusag, getUsageList(), "设备用途");
            }
            if (v == img_photo1) {
                if (status.equals("安装")) {
                    photo2(1, name1);

                }

                if (status.equals("修改")) {
                    photo2(1, name1);
                }

                if (status.equals("详情")) {
                    ShowBigPic(mActivity, Photo1ToBase64);
                }
            }

            if (v == img_photo2) {
                if (status.equals("安装")) {
                    photo2(2, name2);

                }
                if (status.equals("修改")) {
                    photo2(2, name2);
                }
                if (status.equals("详情")) {
                    ShowBigPic(mActivity, Photo2ToBase64);
                }
            }

            if (v == img_photo3) {

                if (status.equals("安装")) {

                    photo2(3, name3);

                }
                if (status.equals("修改")) {
                    photo2(3, name3);
                }
                if (status.equals("详情")) {
                    ShowBigPic(mActivity, Photo3ToBase64);
                }
            }
            if (v == ll_area) {

                String LastCity = SharedUtil.getValue(mActivity, "CityName");

                if (txt_deviceaddress.getText().toString().equals("")) {
                    ToastUtil.showShort(mActivity, "请先选择设备安装地址（经纬度）");
                } else {
                    DialogUtil.ShowList(mActivity, txt_area, getAreaList(), LastCity + "区域列表");
                    txt_pcs.setText("");
                }


            }


            if (v == ll_pcs) {
                AreaMC = txt_area.getText().toString();
                if (AreaMC.equals("")) {
                    ToastUtil.showShort(mActivity, "请先选择辖区");
                } else {
                    AreaID = getAreaID(AreaMC);
                    DialogUtil.ShowList(mActivity, txt_pcs, getPCSList(AreaID), AreaMC + "派出所列表");
                }
            }

            if (v == ll_owner) {

                DialogUtil.ShowList(mActivity, txt_owner, getOwnerList(), "产权人");
            }
            if (v == ll_locationtype) {

                DialogUtil.ShowList(mActivity, txt_locationtype, getLtypeList(), "位置类型");
            }
            if (v == ll_devicelevel) {

                DialogUtil.ShowList(mActivity, txt_devicelevel, getLevelList(), "设备级别");
            }

//            if (v == ll_company) {
//                if (txt_area.getText().toString().equals("")) {
//                    ToastUtil.showShort(mActivity, "请先选择辖区");
//                } else {
//                    AreaID = getAreaID(txt_area.getText().toString());
//                    DialogUtil.ShowList(mActivity, txt_repaircompany, Area2Company(AreaID), "运维公司");
//                }
//            }

            if (v == ll_Reserve3) {
                DialogUtil.ShowList(mActivity, txt_Reserve3, getListType(), "基站类型");
            }


            if (v == img_title) {
                if (status.equals("安装")) {
                    DialogUtil.Show(mActivity, "是否要退出当前页面？", new DoOk() {
                        @Override
                        public void goTodo() {

                            noResult();
                            ActivityUtil.FinishActivity(mActivity);
                        }
                    });
                }
                if (status.equals("详情")) {
                    noResult();
                    ActivityUtil.FinishActivity(mActivity);
                }
            }

            if (v == btn_ok) {

                String txt = btn_ok.getText().toString();
                if (txt.equals("确认安装")) {
                    AddDevice();
                }
                if (txt.equals("确认修改")) {
                    ModifyDevice();
                }
                if (txt.equals("返回")) {
                    ActivityUtil.FinishActivity(mActivity);
                }
            }
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


    /**
     * 设备用途
     */
    private List<String> getUsageList() {
        List<String> list3 = null;
        try {
            list3 = new ArrayList<String>();
            userMap = new HashMap<>();
            List<DictionaryBean> list = null;

            list = DB.findAll(DictionaryBean.class);
            for (int i = 0; i < list.size(); i++) {
                LOG.D("List_DictionaryName=" + list.get(i).getDictionaryName());
                LOG.D("List_SystemID=" + list.get(i).getSystemID());
            }
            LOG.D("ZD_PURPOSE    " + SystemID);

            list = DB.selector(DictionaryBean.class).where("DictionaryName", "=", "ZD_PURPOSE").and("SystemID", "=",
                    SystemID).findAll();
            if (list == null) {
                LOG.D("设备用途列表为空");
            }

            LOG.D("设备用途列表 list.size()=" + list.size());
            if (list != null && list.size() > 0) {
                String DictionaryID = list.get(0).getDictionaryID();
                levelMap = new HashMap<>();
                List<ParamBean> list2 = DB.findAll(ParamBean.class);
                for (int i = 0; i < list2.size(); i++) {
                    if (list2.get(i).getDictionaryID().equals(DictionaryID)) {
                        list3.add(list2.get(i).getParamValue());
                        userMap.put(list2.get(i).getParamValue(), list2.get(i).getParamCode());
                    }
                }

            }
        } catch (DbException e) {
            LOG.E("设备用途列表异常=" + e.toString());
        }
        return list3;
    }

    private List<String> getLevelList() {
        List<String> list3 = new ArrayList<String>();
        List<DictionaryBean> list = null;
        try {
            list = DB.selector(DictionaryBean.class).where("DictionaryName", "=", "ZD_DEVICELEVEL").and("SystemID",
                    "=", SystemID).findAll();


            if (list != null && list.size() > 0) {
                String DictionaryID = list.get(0).getDictionaryID();
                levelMap = new HashMap<>();
                //ToastUtil.showShort(mActivity,"DictionaryID="+DictionaryID);
                List<ParamBean> list2 = DB.selector(ParamBean.class).where("DictionaryID", "=", DictionaryID).findAll();

                if (list2.size() > 0) {
                    for (int i = 0; i < list2.size(); i++) {
                        list3.add(list2.get(i).getParamValue());
                        levelMap.put(list2.get(i).getParamValue(), list2.get(i).getParamCode());
                    }
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return list3;
    }


    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    private void getLtypeList1() {
        List<DictionaryBean> list = null;
        try {
            list = DB.selector(DictionaryBean.class).where("DictionaryName", "=", "ZD_LOCATIONTYPE").and("SystemID",
                    "=", SystemID).findAll();

            if (list != null && list.size() > 0) {
                ltypeMap1 = new HashMap<>();
                ltypeMap = new HashMap<>();
                String DictionaryID = list.get(0).getDictionaryID();
                List<ParamBean> list2 = DB.selector(ParamBean.class).where("DictionaryID", "=", DictionaryID).findAll();
                if (list2 != null && list2.size() > 0) {
                    for (int i = 0; i < list2.size(); i++) {
                        ltypeMap1.put(list2.get(i).getParamCode(), list2.get(i).getParamValue());
                        ltypeMap.put(list2.get(i).getParamValue(), list2.get(i).getParamCode());
                    }
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
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


    private void getLevelList1() {


        List<DictionaryBean> list = null;
        try {
            list = DB.selector(DictionaryBean.class).where("DictionaryName", "=", "ZD_DEVICELEVEL").and("SystemID",
                    "=", SystemID).findAll();
            if (list != null && list.size() > 0) {
                String DictionaryID = list.get(0).getDictionaryID();
                levelMap1 = new HashMap<>();
                levelMap = new HashMap<>();
                //ToastUtil.showShort(mActivity,"DictionaryID="+DictionaryID);
                List<ParamBean> list2 = DB.selector(ParamBean.class).where("DictionaryID", "=", DictionaryID).findAll();
                if (list2 != null && list2.size() > 0) {
                    for (int i = 0; i < list2.size(); i++) {
                        levelMap1.put(list2.get(i).getParamCode(), list2.get(i).getParamValue());
                        levelMap.put(list2.get(i).getParamValue(), list2.get(i).getParamCode());
                    }
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private List<String> getLtypeList() {
        List<String> list3 = new ArrayList<String>();
        List<DictionaryBean> list = null;
        try {
            list = DB.selector(DictionaryBean.class).where("DictionaryName", "=", "ZD_LOCATIONTYPE").and("SystemID",
                    "=", SystemID).findAll();

            if (list != null && list.size() > 0) {
                ltypeMap = new HashMap<>();
                String DictionaryID = list.get(0).getDictionaryID();
                //ToastUtil.showShort(mActivity,"DictionaryID="+DictionaryID);
                List<ParamBean> list2 = DB.selector(ParamBean.class).where("DictionaryID", "=", DictionaryID).findAll();
                if (list2 != null && list2.size() > 0) {
                    for (int i = 0; i < list2.size(); i++) {
                        LOG.E("ParamValue" + list2.get(i).getParamValue());
                        list3.add(list2.get(i).getParamValue());
                        ltypeMap.put(list2.get(i).getParamValue(), list2.get(i).getParamCode());

                    }
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return list3;
    }

    private List<String> getOwnerList() {
        List<String> list3 = new ArrayList<String>();
        List<String> list4 = new ArrayList<String>();
        List<DictionaryBean> list = null;
        try {
            list = DB.selector(DictionaryBean.class).where("DictionaryName", "=", "ZD_OWNERS").and("SystemID", "=",
                    SystemID).findAll();
            if (list == null) {
                list = new ArrayList<DictionaryBean>();
                LOG.D("产权人总列表=" + list.size());
            }
            if (list != null && list.size() > 0) {
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


    /**
     * 获取运维公司
     */

    List<RepairCompanyBean> repairCompanyBeanList;

    private List<String> Area2Company(String AreaID) {
        List<String> repairlist = new ArrayList<String>();

        List<Area2ComanyBean> area2CompanyBeanList = null;
        try {
            area2CompanyBeanList = DB.selector(Area2ComanyBean.class).where("AreaID", "=", AreaID).findAll();
            if (area2CompanyBeanList != null && area2CompanyBeanList.size() > 0) {
                for (int i = 0; i < area2CompanyBeanList.size(); i++) {
                    String CompanyID = area2CompanyBeanList.get(i).getCompanyID();
                    repairCompanyBeanList = DB.findAll(RepairCompanyBean.class);
                    if (repairCompanyBeanList == null) {
                        continue;
                    }
                    for (int j = 0; j < repairCompanyBeanList.size(); j++) {
                        if (repairCompanyBeanList.get(j).getCompanyID().equals(CompanyID)) {
                            repairlist.add(repairCompanyBeanList.get(j).getCompanyName());
                        }
                    }
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return repairlist;
    }


    private String getCompanyCode(String companyname) {
        String code = "";
        List<RepairCompanyBean> list = null;
        try {
            List<RepairCompanyBean> list2 = DB.findAll(RepairCompanyBean.class);
            if (list != null) {
                for (RepairCompanyBean rcb : list2) {
                    LOG.E("运维公司代码：" + rcb.getCompanyCode());
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

    private String getCompanyName(String companycode) {
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

    private String getParamCode(String prarmvalue) {
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

    private String getLtypeValue(String prarmcode) {
        String code = "";

        List<DictionaryBean> list = null;
        try {
            list = DB.selector(DictionaryBean.class).where("DictionaryName", "=", "ZD_LOCATIONTYPE").and("SystemID",
                    "=", SystemID).findAll();
            if (list == null) {
                list = new ArrayList<DictionaryBean>();
            }
            String DictionaryID = list.get(0).getDictionaryID();
            List<ParamBean> list2 = DB.selector(ParamBean.class).where("ParamCode", "=", prarmcode).and
                    ("DictionaryID", "=", DictionaryID).findAll();
            if (list2 != null && list2.size() == 1) {
                code = list2.get(0).getParamValue();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return code;
    }

    private String getUserforValue(String prarmcode) {
        String code = "";
        List<DictionaryBean> list = null;
        try {
            list = DB.selector(DictionaryBean.class).where("DictionaryName", "=", "ZD_PURPOSE").and("SystemID", "=",
                    SystemID).findAll();

            if (list != null && list.size() > 0) {
                String DictionaryID = list.get(0).getDictionaryID();
                List<ParamBean> list2 = DB.selector(ParamBean.class).where("ParamCode", "=", prarmcode).and
                        ("DictionaryID", "=", DictionaryID).findAll();

                if (list2 != null && list2.size() == 1) {
                    code = list2.get(0).getParamValue();
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return code;
    }

    private String getTypeValue(String prarmcode) {
        String code = "";
        List<DictionaryBean> list = null;
        try {
            list = DB.selector(DictionaryBean.class).where("DictionaryName", "=", "ZD_DEVICETYPE").and("SystemID",
                    "=", SystemID).findAll();

            if (list != null && list.size() > 0) {
                String DictionaryID = list.get(0).getDictionaryID();
                List<ParamBean> list2 = DB.selector(ParamBean.class).where("ParamCode", "=", prarmcode).and
                        ("DictionaryID", "=", DictionaryID).findAll();
                if (list2 != null && list2.size() == 1) {
                    code = list2.get(0).getParamValue();
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return code;
    }

    private String getLevelValue(String prarmcode) {
        String code = "";
        List<DictionaryBean> list = null;
        try {
            list = DB.selector(DictionaryBean.class).where("DictionaryName", "=", "ZD_DEVICELEVEL").and("SystemID",
                    "=", SystemID).findAll();
            if (list != null && list.size() > 0) {
                String DictionaryID = list.get(0).getDictionaryID();
                List<ParamBean> list2 = DB.selector(ParamBean.class).where("ParamCode", "=", prarmcode).and
                        ("DictionaryID", "=", DictionaryID).findAll();
                if (list2 != null && list2.size() == 1) {
                    code = list2.get(0).getParamValue();
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return code;
    }

    private String getParamValue(String prarmcode) {
        String code = "";
        List<DictionaryBean> list = null;
        try {
            list = DB.selector(DictionaryBean.class).where("DictionaryName", "=", "ZD_OWNERS").and("SystemID", "=",
                    SystemID).findAll();
            if (list != null && list.size() > 0) {
                String DictionaryID = list.get(0).getDictionaryID();
                List<ParamBean> list2 = DB.selector(ParamBean.class).where("ParamCode", "=", prarmcode).and
                        ("DictionaryID", "=", DictionaryID).findAll();
                if (list2 != null && list2.size() == 1) {
                    code = list2.get(0).getParamValue();
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return code;
    }


    /**
     * 添加设备
     */

    private String AreaMC = "", GAJMC = "", PCSMC = "";
    private String DeviceType = "";
    private String DeviceID = "";
    private String DeviceCode = "";
    private String SerialNumber = "";
    private String AreaID = "";
    private String XQ = "";
    private String PCS = "";
    private String JWH = "";
    private String LNG = "";
    private String LAT = "";
    private String Photo1ToBase64 = "";
    private String Photo2ToBase64 = "";
    private String Photo3ToBase64 = "";
    private String Address = "";
    private String Usage = "";
    private String Owner = "";
    private String RepairCompany = "";
    private String CarrierOperator = "";
    private String IP = "";
    private String Mask = "";
    private String Gateway = "";
    private String Phone = "";
    private String SIM = "";
    private String Description = "";
    private String Reserve1 = "";
    private String Reserve2 = "";
    private String Reserve3 = "";
    private String Reserve4 = "";
    private String Reserve5 = "";
    private String Reserve6 = "";
    private String Reserve7 = "";
    private String Reserve8 = "";
    private String Reserve9 = "";
    private String Reserve10 = "";
    private String Reserve11 = "";
    private String Reserve12 = "";
    private String Reserve13 = "";
    private String Reserve14 = "";
    private String Reserve15 = "";
    private String Reserve16 = "";

    private String AccessType = "1";


    @Override
    public int getWallpaperDesiredMinimumWidth() {
        return super.getWallpaperDesiredMinimumWidth();
    }

    private boolean getTextData(String type) {
        DeviceCode = txt_devicecode.getText().toString().trim();
        SerialNumber = txt_deviceno.getText().toString().trim();


        Usage = userMap.get(txt_deviceusag.getText().toString().trim());
        LOG.E("userMap=" + userMap.size() + "   txt_deviceusag=" + txt_deviceusag.getText().toString().trim());

//        LNG = txt_lng.getText().toString().trim();
//        LAT = txt_lat.getText().toString().trim();
        Address = txt_deviceaddress.getText().toString().trim();

        AreaMC = txt_area.getText().toString().trim();
        PCSMC = txt_pcs.getText().toString().trim();


        AreaID = getID(AreaMC, 1);
        PCS = getID(PCSMC, 3);

        LOG.E("AreaID=" + AreaID + "/XQ/" + XQ + "/PCS/" + PCS);
        Owner = txt_owner.getText().toString().trim();
        if (txt_devicelevel.getText().toString().trim() != "" || !txt_devicelevel.getText().toString().trim().equals
                ("")) {
            Reserve5 = levelMap.get(txt_devicelevel.getText().toString().trim());
        }
        if (txt_locationtype.getText().toString().trim() != "" || !txt_locationtype.getText().toString().trim()
                .equals("")) {
            Reserve6 = ltypeMap.get(txt_locationtype.getText().toString().trim());

        }

        RepairCompany = txt_repaircompany.getText().toString().trim();

        IP = txt_ip.getText().toString().trim();
        Mask = txt_yanma.getText().toString().trim();
        Gateway = txt_wangguan.getText().toString().trim();

        Phone = txt_phone.getText().toString().trim();
        SIM = txt_sim.getText().toString().trim();

        Reserve1 = txt_Reserve1.getText().toString().trim();
        Reserve2 = txt_Reserve2.getText().toString().trim();
        Description = txt_beizhu.getText().toString().trim();
        Reserve4 = txt_Reserve4.getText().toString().trim();

        Reserve3 = typeMap.get(txt_Reserve3.getText().toString().trim());

        if (check()) {
            return false;
        } else {
            return true;
        }
    }


    ZProgressHUD zProgressHUD;

    private void AddDevice() {

        if (!getTextData("新增")) {
            return;
        }


        zProgressHUD = new ZProgressHUD(mActivity);
        zProgressHUD.setMessage("正在提交数据请稍后……");
        zProgressHUD.show();

        DeviceBean bean = new DeviceBean();
        if (HomeActivity.IsMapiIn) {
//            ToastUtil.showShort(mActivity,"1StationID"+HomeActivity.StationID);
            bean.setDeviceID(HomeActivity.StationID);
        } else {
//            ToastUtil.showShort(mActivity,"2StationID"+DeviceID);
            bean.setDeviceID(DeviceID);
        }

        LOG.E("DeviceID=" + bean.getDeviceID());
        bean.setDeviceCode(DeviceCode);
        LOG.E("DeviceCode=" + DeviceCode);
        bean.setDeviceType(DeviceType);
        LOG.E("DeviceType=" + DeviceType);
        bean.setSerialNumber(SerialNumber);
        LOG.E("SerialNumber=" + SerialNumber);

        bean.setUsage(Usage);
        LOG.E("Usage=" + Usage);
        bean.setReserve3(Reserve3);
        LOG.E("Reserve3=" + Reserve3);

        bean.setPhotoID1(getUUID());
        bean.setPhotoID2(getUUID());
        bean.setPhotoID3(getUUID());
        LOG.E("getUUID=" + getUUID());

        bean.setPhoto1(Photo1ToBase64);
        bean.setPhoto2(Photo2ToBase64);
        bean.setPhoto3(Photo3ToBase64);

//        bean.setPhoto1("");
//        bean.setPhoto2("");
//        bean.setPhoto3("");

        bean.setLAT(LAT);
        LOG.E("LAT=" + LAT);
        bean.setLNG(LNG);
        LOG.E("LNG=" + LNG);
        bean.setAddress(Address);
        LOG.E("Address=" + Address);
        bean.setAreaID(AreaID);
        LOG.E("AreaID=" + AreaID);
        bean.setXQ(XQ);
        LOG.E("XQ=" + XQ);
        bean.setPCS(PCS);
        LOG.E("派出所PCS=" + PCS);
        bean.setJWH(JWH);
        LOG.E("JWH=" + JWH);

        String Companycode = getCompanyCode(RepairCompany);
        bean.setRepairCompany(Companycode);
        LOG.E("Companycode=" + Companycode);
        String code = getParamCode(Owner);
        bean.setOwner(code);
        LOG.E("code=" + code);
        // ToastUtil.ShortCenter(mActivity,Companycode+"&&&&"+code);


        bean.setAccessType(AccessType);
        LOG.E("AccessType=" + AccessType);
        bean.setMask(Mask);
        LOG.E("Mask=" + Mask);
        bean.setPhone(Phone);
        LOG.E("Phone=" + Phone);
        bean.setCarrierOperator(CarrierOperator);
        LOG.E("CarrierOperator=" + CarrierOperator);
        bean.setGateway(Gateway);
        LOG.E("Gateway=" + Gateway);
        bean.setIP(IP);
        LOG.E("IP=" + IP);
        bean.setSIM(SIM);
        LOG.E("SIM=" + SIM);
        bean.setDescription(Description);
        LOG.E("Description=" + Description);

        bean.setReserve1(Reserve1);
        LOG.E("Reserve1=" + Reserve1);
        bean.setReserve2(Reserve2);
        LOG.E("Reserve2=" + Reserve2);
        bean.setReserve4(Reserve4);
        LOG.E("Reserve4=" + Reserve4);
        bean.setReserve5(Reserve5);
        LOG.E("Reserve5=" + Reserve5);
        bean.setReserve6(Reserve6);
        LOG.E("Reserve6=" + Reserve6);
        Reserve7 = BaiduLNG;
        Reserve8 = BaiduLAT;
        bean.setReserve7(Reserve7);
        LOG.E("Reserve7=" + Reserve7);
        bean.setReserve8(Reserve8);
        LOG.E("Reserve8=" + Reserve8);
        Reserve9 = "3";
        bean.setReserve9(Reserve9);
        LOG.E("Reserve9=" + Reserve9);

        Reserve10 = SharedUtil.getValue(mActivity, "UserName");
        Reserve11 = SharedUtil.getValue(mActivity, "UserPhone");
        bean.setReserve10(Reserve10);
        LOG.E("Reserve10=" + Reserve10);
        bean.setReserve11(Reserve11);
        LOG.E("Reserve11=" + Reserve11);
        bean.setReserve12(Reserve12);
        LOG.E("Reserve12=" + Reserve12);
        bean.setReserve13(et_produceNo.getText().toString().trim());
        LOG.E("Reserve13 produceNo=" + et_produceNo.getText().toString().trim());
        bean.setReserve14(Reserve14);
        LOG.E("Reserve14=" + Reserve14);
        bean.setReserve15(Reserve15);
        LOG.E("Reserve15=" + Reserve15);
        bean.setReserve16(Reserve16);
        LOG.E("Reserve16=" + Reserve16);

        LOG.E("Reserve2=" + Reserve2 + "//");

        String deviceInfo = gson.toJson(bean);
        LOG.E("deviceInfo=" + deviceInfo);

        if (SystemID.equals("")) {
            SystemID = getSystemID(DeviceType);
        }
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("accessToken", SharedUtil.getToken(mActivity));
        map.put("deviceInfo", deviceInfo);
        map.put("systemID", SystemID);

        Log.e(TAG, "【deviceInfo】: "+deviceInfo );

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
                        ActivityUtil.SaveAddress(mActivity, Address, DeviceCode);
                        Intent intent = new Intent(mActivity, HomeActivity.class);
                        startActivity(intent);
                        ZbarUtil.setDeviceClear();
                    } else {
                        ToastUtil.ErrorOrRight(mActivity, "设备安装失败！" + ErrorCode + ErrorMsg, 1);

                        if (ErrorMsg.contains("已存在")) {
                            ll_zbar.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


    }


    private void ModifyDevice() {
        if (!getTextData("修改")) {
            return;
        }
        zProgressHUD = new ZProgressHUD(mActivity);
        zProgressHUD.setMessage("正在提交数据请稍后……");
        zProgressHUD.show();
        LOG.E("DeviceType=" + SharedUtil.getValue(mActivity, "DeviceType"));

        final DeviceBean bean = new DeviceBean();
        showbean(deviceBean);

        bean.setDeviceID(deviceBean.getDeviceID());
        LOG.E("DeviceID=" + deviceBean.getDeviceID());
        bean.setDeviceCode(DeviceCode);
        LOG.E("DeviceCode=" + DeviceCode);
        bean.setDeviceType(deviceBean.getDeviceType());
        LOG.E("DeviceType=" + deviceBean.getDeviceType());

        bean.setUsage(Usage);
        LOG.E("Usage=" + Usage);
        bean.setReserve3(Reserve3);
        LOG.E("Reserve3=" + Reserve3);
        if (Photo1ToBase64.equals("")) {
            bean.setPhotoID1(deviceBean.getPhotoID1());
        } else if (!Photo1ToBase64.equals("")) {
            bean.setPhotoID1(getUUID());
        }
        LOG.E("PhotoID1=" + bean.getPhotoID1());

        if (Photo2ToBase64.equals("")) {
            bean.setPhotoID2(deviceBean.getPhotoID2());
        } else if (!Photo2ToBase64.equals("")) {
            bean.setPhotoID2(getUUID());
        }
        LOG.E("PhotoID2=" + bean.getPhotoID2());
        if (Photo3ToBase64.equals("")) {
            bean.setPhotoID3(deviceBean.getPhotoID3());
        } else if (!Photo3ToBase64.equals("")) {
            bean.setPhotoID3(getUUID());
        }
        LOG.E("PhotoID3=" + bean.getPhotoID3());
        bean.setPhoto1(Photo1ToBase64);
        bean.setPhoto2(Photo2ToBase64);
        bean.setPhoto3(Photo3ToBase64);
        bean.setLAT(LAT);
        LOG.E("LAT=" + LAT);
        bean.setLNG(LNG);
        LOG.E("LNG=" + LNG);
        bean.setAddress(Address);
        LOG.E("Address=" + Address);
        bean.setAreaID(AreaID);
        LOG.E("AreaID=" + AreaID);
        bean.setXQ(XQ);
        LOG.E("XQ=" + XQ);
        bean.setPCS(PCS);
        LOG.E("PCS=" + PCS);
        bean.setJWH(JWH);
        LOG.E("JWH=" + JWH);

        String Companycode = getCompanyCode(RepairCompany);
        bean.setRepairCompany(Companycode);
        LOG.E("RepairCompany=" + RepairCompany);
        String code = getParamCode(Owner);
        bean.setOwner(code);
        LOG.E("Owner=" + Owner);
        //ToastUtil.ShortCenter(mActivity,Companycode+"&&&&"+code);

        bean.setAccessType(AccessType);
        LOG.E("AccessType=" + AccessType);
        bean.setMask(Mask);
        LOG.E("Mask=" + Mask);
        bean.setPhone(Phone);
        LOG.E("Phone=" + Phone);
        bean.setCarrierOperator(CarrierOperator);
        LOG.E("CarrierOperator=" + CarrierOperator);
        bean.setGateway(Gateway);
        LOG.E("Gateway=" + Gateway);
        bean.setIP(IP);
        LOG.E("IP=" + IP);
        bean.setSerialNumber(SerialNumber);
        LOG.E("SerialNumber=" + SerialNumber);
        bean.setSIM(SIM);
        LOG.E("SIM=" + SIM);


        bean.setReserve1(Reserve1);
        LOG.E("Reserve1=" + Reserve1);
        bean.setReserve2(Reserve2);
        LOG.E("Reserve2=" + Reserve2);
        bean.setDescription(Description);
        LOG.E("Description=" + Description);
        bean.setReserve4(Reserve4);
        LOG.E("Reserve4=" + Reserve4);
        bean.setReserve5(Reserve5);
        LOG.E("Reserve5=" + Reserve5);
        bean.setReserve6(Reserve6);
        LOG.E("Reserve6=" + Reserve6);

        Reserve7 = BaiduLNG;
        Reserve8 = BaiduLAT;
        bean.setReserve7(Reserve7);
        LOG.E("Reserve7=" + Reserve7);
        bean.setReserve8(Reserve8);
        LOG.E("Reserve8=" + Reserve8);

        Reserve10 = SharedUtil.getValue(mActivity, "UserName");
        Reserve11 = SharedUtil.getValue(mActivity, "UserPhone");

        bean.setReserve9(Reserve9);
        LOG.E("Reserve9=" + Reserve9);
        bean.setReserve10(Reserve10);
        LOG.E("Reserve10=" + Reserve10);

        bean.setReserve11(Reserve11);
        LOG.E("Reserve11=" + Reserve11);

        bean.setReserve12(Reserve12);
        bean.setReserve13(et_produceNo.getText().toString().trim());
        bean.setReserve14(Reserve14);
        bean.setReserve15(Reserve15);
        bean.setReserve16(Reserve16);


        String deviceInfo = gson.toJson(bean);
        //Log.e("deviceInfo", deviceInfo);
        if (SystemID.equals("")) {
            SystemID = getSystemID(deviceBean.getDeviceType());
        }

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
                        if (!DeviceCode.equals(deviceBean.getDeviceCode())) {
                            SharedUtil.setValue(mActivity, "isCodeChange", DeviceCode);
                        }
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

    /**
     * 生成32位编码
     *
     * @return string
     */
    public String getUUID() {
        String uuid = UUID.randomUUID().toString().trim();
        return uuid;
    }

    private String getID(String MC, int num) {
        String ID = "";
        try {
            if (num == 1) {
                List<CityAreaBean> list = DB.selector(CityAreaBean.class).where("AreaMC", "=", MC).findAll();
                if (list != null && list.size() > 0) {
                    ID = list.get(0).getAreaID();
                }


            }

            if (num == 3) {
                List<CityAreaPCSBean> list = DB.selector(CityAreaPCSBean.class).where("PCSMC", "=", MC).findAll();
                if (list != null && list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        LOG.E("AreaID:" + list.get(i).getAreaID() + "  PCSMC:" + list.get(i).getPCSMC());
                        if (AreaID.equals(list.get(i).getAreaID())) {
                            ID = list.get(i).getPCSID();
                        }
                    }
//                    ID = list.get(0).getPCSID();
                }

            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return ID;
    }

    private boolean check() {
//        getID(PCSMC, 3);
        if (DeviceCode.equals("")) {
            ToastUtil.showShort(mActivity, "设备编号不能为空");
            return true;
        } else if (txt_deviceusag.getText().toString().equals("")) {
            ToastUtil.showShort(mActivity, "设备用途不能为空");
            return true;
        } else if (txt_Reserve3.getText().toString().equals("")) {
            ToastUtil.showShort(mActivity, "基站类型不能为空");
            return true;
        } else if (Photo1ToBase64.equals("") || Photo2ToBase64.equals("") || Photo3ToBase64.equals("")) {
            ToastUtil.showShort(mActivity, "设备照片还没有拍摄");
            return true;
        } else if (LNG.equals("") || LAT.equals("")) {
            ToastUtil.showShort(mActivity, "经纬度不能为空");
            return true;
        } else if (Address.equals("")) {
            ToastUtil.showShort(mActivity, "设备地址不能为空");
            return true;
        } else if (AreaMC.equals("")) {
            ToastUtil.showShort(mActivity, "区域不能为空");
            return true;
        } else if (Owner.equals("")) {
            ToastUtil.showShort(mActivity, "产权人不能为空");
            return true;
        } else if (RepairCompany.equals("")) {
            ToastUtil.showShort(mActivity, "运维公司不能为空");
            return true;
        } else if (!AccessType.equals("1") && !AccessType.equals("2")) {
            ToastUtil.showShort(mActivity, "接入方式只能是有线或无线");
            return true;
        } else if (AccessType.equals("1") && IP.equals("")) {
            ToastUtil.showShort(mActivity, "IP地址不能为空");
            return true;
        } else if (AccessType.equals("1") && !IP.equals("") && !MatchUtil.isIpTrue(IP)) {
            ToastUtil.showShort(mActivity, "IP格式错误");
            return true;
        } else if (AccessType.equals("2") && CarrierOperator.equals("")) {
            ToastUtil.showShort(mActivity, "运营商不能为空");
            return true;
        } else if (AccessType.equals("2") && !Phone.equals("") && !MatchUtil.isMobile(Phone)) {
            ToastUtil.showShort(mActivity, "电话格式不正确");
            return true;
        } else if (PCSMC.equals("")) {
            ToastUtil.showShort(mActivity, "请选择辖区归属派出所");
            return true;
        }

        return false;
    }


    private List<String> getListType() {
        List<String> list3 = new ArrayList<String>();
        List<DictionaryBean> list = null;
        try {
            list = DB.selector(DictionaryBean.class).where("DictionaryName", "=", "ZD_DEVICETYPE").and("SystemID",
                    "=", SystemID).findAll();
            if (list != null && list.size() > 0) {
                String DictionaryID = list.get(0).getDictionaryID();
                typeMap = new HashMap<>();
                List<ParamBean> list2 = DB.selector(ParamBean.class).where("DictionaryID", "=", DictionaryID).findAll();
                if (list2 != null && list2.size() > 0) {
                    for (int i = 0; i < list2.size(); i++) {
                        list3.add(list2.get(i).getParamValue());
                        typeMap.put(list2.get(i).getParamValue(), list2.get(i).getParamCode());
                    }
                }
            }

        } catch (DbException e) {
            e.printStackTrace();
        }
        return list3;
    }


    /**
     * 拍摄照片
     */


    public void photo(int num) {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(openCameraIntent, num);
    }

    public void photo2(int num, String name) {
        PhotoUtils.takePicture(mActivity, name + ":" + num);
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        File  file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
//                + "/files/Pictures/" + name + ".jpg");
//        file.getParentFile().mkdirs();
//
//        //改变Uri  com.xykj.customview.fileprovider注意和xml中的一致
//        Uri uri = FileProvider.getUriForFile(this, "com.tdr.yunwei", file);
//        //添加权限
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//        startActivityForResult(intent, num);

//        Intent intent4 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), name));
//        intent4.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//        startActivityForResult(intent4, num);

    }

    String name1 = "photo1.jpg";
    String name2 = "photo2.jpg";
    String name3 = "photo3.jpg";
    String name4 = "photo4.jpg";

    private static final String PRODUCE_NO_TYPE = "1604";
    private String produceNo;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case REQUEST_SCAN_SERIAL:
                if (resultCode == Activity.RESULT_OK) {
                    String scanResult = data.getStringExtra("result");
                    String strZbar = ZbarUtil.DeviceZbar(mActivity, scanResult);
                    if (TextUtils.isEmpty(strZbar)) {
                        ToastUtil.ErrorOrRight(mActivity, "请扫描正确的设备二维码。", 1);
                    } else {
                        String[] device = strZbar.split(",");
                        Log.e(TAG, "strZbar: "+strZbar );
                        if (device[1].equals(PRODUCE_NO_TYPE)) {
                            produceNo = device[0];
                            et_produceNo.setText(produceNo);
                        }else{
                            ToastUtil.ErrorOrRight(mActivity, "设备类型不匹配", 1);
                        }
                    }

                }

                break;
            case 0:
                if (resultCode == Activity.RESULT_OK) {

                    String inputtype = data.getStringExtra("inputtype");
                    //扫描
                    if (inputtype.equals("zbar")) {
                        String scanResult = data.getStringExtra("result");
                        String strZbar = ZbarUtil.DeviceZbar(mActivity, scanResult);
                        String[] device = strZbar.split(",");
                        if (!strZbar.equals("")) {
                            txt_devicecode.setText(device[0]);
                            String DeviceRemark = ZbarUtil.getSubRemark(DB, device[1]);
                            txt_devicetype.setText(DeviceRemark);
                            DeviceType = device[1];
                            img_zbar.setImageResource(R.mipmap.code_on);
                        }
                    }
                    //手动输入
                    if (inputtype.equals("shoudong")) {

                        String DeviceCode = data.getStringExtra("DeviceCode");
                        txt_devicecode.setText(DeviceCode);

                    }
                }
                break;
            case PhotoUtils.CAMERA_REQESTCODE:
                if (resultCode == RESULT_OK) {
                    int degree = PhotoUtils.readPictureDegree(PhotoUtils.imageFile.getAbsolutePath());
                    Bitmap bitmap = PhotoUtils.rotaingImageView(degree, PhotoUtils.getBitmapFromFile(PhotoUtils
                            .imageFile, 300, 300));
                    String Photoindex[] = PhotoUtils.mPicName.split(":");

                    switch (Photoindex[1]) {
                        case "1":
                            img_photo1.setImageBitmap(bitmap);
                            Photo1ToBase64 = PhotoUtil.bitmapToString(bitmap, mActivity);
                            break;
                        case "2":
                            img_photo2.setImageBitmap(bitmap);
                            Photo2ToBase64 = PhotoUtil.bitmapToString(bitmap, mActivity);
                            break;
                        case "3":
                            img_photo3.setImageBitmap(bitmap);
                            Photo3ToBase64 = PhotoUtil.bitmapToString(bitmap, mActivity);
                            break;
                    }
                }
                break;
            case 4:
                if (resultCode == mActivity.RESULT_OK) {
                    LNG = data.getStringExtra("MapLNG");
                    LAT = data.getStringExtra("MapLAT");
                    Address = data.getStringExtra("Address");
                    BaiduLAT = data.getStringExtra("BaiduMapLAT");
                    BaiduLNG = data.getStringExtra("BaiduMapLNG");
                    if (!status.equals("修改")) {
                        DeviceID = data.getStringExtra("StationID");
                    }
//                    ToastUtil.showShort(mActivity,"Address="+data.getStringExtra("Address"));
                    LOG.D("Address=" + Address);
                    LOG.D("LNG=" + LNG);
                    LOG.D("LAT=" + LAT);
                    LOG.D("BaiduLNG=" + BaiduLNG);
                    LOG.D("BaiduLAT=" + BaiduLAT);
                    LOG.D("StationID=" + DeviceID);
                    if (!BaiduLNG.equals("")) {
                        txt_lng.setText(BaiduLNG);
                        txt_lat.setText(BaiduLAT);
                        txt_deviceaddress.setText(Address);
                        CheckAddress();

                        if (Address.length() >= 9) {
                            Areatxt = Address.substring(6, 9);
                        } else {
                            Areatxt = Address;
                        }
                        AreaMC = getAreaMC2(Areatxt);

//                        if(!AreaMC.equals("")){
//                            PopWin.Show(mActivity, "根据您选择地址，自动给您匹配最佳区域\n[ "+AreaMC+" ]", new PopWin.DoOk() {
//                                @Override
//                                public void goTodo() {
//                                    txt_area.setText(AreaMC);
//                                    txt_pcs.setText("");
//
//                                }
//                            });
//                        }
                        txt_area.setText(AreaMC);
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

    private void CheckAddress() {
        String txt = btn_ok.getText().toString();
        if (txt.equals("确认安装")) {
            if (ActivityUtil.CheckAddressRepeated(mActivity, Address)) {
                showDialog1("您已使用过 （" + Address + "） 这个地址。");
            }
        } else if (txt.equals("确认修改")) {
            if (ActivityUtil.CheckAddressRepeated2(mActivity, Address, DeviceCode)) {
                showDialog1("您已使用过 （" + Address + "） 这个地址。");
            }
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
    //用百度地图得到的地址（6,9）--龙湾区去寻找最合适的区域名称

    private String getAreaMC2(String areamc) {
        List<String> list = getAreaList();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).contains(areamc)) {
                AreaMC = list.get(i);
            }
        }

        return AreaMC;
    }


    /**
     * 获取区域
     */

    private List<String> getAreaList() {
        String l = LastCityID.substring(4, 6);
        LOG.E("LastCityID=" + LastCityID);
        List<CityAreaBean> areaBeanList = null;
        List<String> list = null;
        try {
            if (l == "00" || l.equals("00")) {
                areaBeanList = DB.selector(CityAreaBean.class).where("FAreaID", "like", LastCityID.substring(0, 4) +
                        "%").findAll();
//            areaBeanList = DB.findAllByWhere(CityAreaBean.class, "FAreaID like\"" + LastCityID.substring(0, 4) +
// "%" + "\"");
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


    /**
     * 通过区域名字来找区域ID
     */

    private String getAreaID(String AreaMC) {
        String str = "";
        List<CityAreaBean> areaBeanList = null;
        try {
            areaBeanList = DB.selector(CityAreaBean.class).where("AreaMC", "=", AreaMC).findAll();

            if (areaBeanList != null && areaBeanList.size() > 0) {
                for (int i = 0; i < areaBeanList.size(); i++) {
                    LOG.E("str=" + areaBeanList.get(i).getAreaID());
                }
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

    private List<String> getPCSList(String AreaID) {
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


    Handler mHandler = new Handler();


    private void ScrollToBottom() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //scrollView.fullScroll(ScrollView.FOCUS_FORWARD);
                scrollView.fullScroll(ScrollView.FOCUS_UP);//滚动到顶部
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {

            String txt = btn_ok.getText().toString();

            if (txt.equals("确认安装")) {
                DialogUtil.Show(mActivity, "是否要退出当前页面？", new DoOk() {
                    @Override
                    public void goTodo() {

                        noResult();
                        ActivityUtil.FinishActivity(mActivity);
                    }
                });
            }
            if (txt.equals("返回") || txt.equals("确认修改")) {
                noResult();
                ActivityUtil.FinishActivity(mActivity);
            }


            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void noResult() {
        Intent intent = new Intent();
        intent.putExtra("result", "");
        setResult(RESULT_OK, intent);
    }

    private void hasResult() {
        Intent intent = new Intent();
        intent.putExtra("result", "1");
        setResult(RESULT_OK, intent);
    }
}
