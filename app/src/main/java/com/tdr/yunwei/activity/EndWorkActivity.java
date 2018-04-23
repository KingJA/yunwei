package com.tdr.yunwei.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tdr.yunwei.R;
import com.tdr.yunwei.YunWeiApplication;
import com.tdr.yunwei.bean.DASBean;
import com.tdr.yunwei.bean.DictionaryBean;
import com.tdr.yunwei.bean.ParamBean;
import com.tdr.yunwei.bean.RepairOrderBean;
import com.tdr.yunwei.util.ActivityUtil;
import com.tdr.yunwei.util.Constants;
import com.tdr.yunwei.util.DBUtils;
import com.tdr.yunwei.util.DateUtil;
import com.tdr.yunwei.util.LOG;
import com.tdr.yunwei.util.PhotoUtil;
import com.tdr.yunwei.util.PhotoUtils;
import com.tdr.yunwei.util.SharedUtil;
import com.tdr.yunwei.util.ToastUtil;
import com.tdr.yunwei.util.WebUtil;
import com.tdr.yunwei.util.ZbarUtil;
import com.tdr.yunwei.view.Dialog.DialogUtil;
import com.zbar.lib.CaptureActivity;

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
 * Created by Administrator on 2016/4/20.
 */
public class EndWorkActivity extends Activity {
    private Activity mActivity;
    private LinearLayout ll_status, ll_faulttype, ll_fault, ll_remark, ll_zbar;

    private TextView txt_status, txt_faulttype, txt_fault, txt_remark, txt_changedevicecode;

    ImageView img_photo;
    String picToBase64 = "";
    RepairOrderBean bean;
    DbManager DB;
    private YunWeiApplication YWA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_endwork);

        mActivity = this;
        ActivityUtil.AddActivity(mActivity);
        YWA = YunWeiApplication.getInstance();
//        DB = YWA.getDB();
        DB= x.getDb(DBUtils.getDb());
        bean = getIntent().getParcelableExtra("RepairOrderBean");

        DateUtil.getNowDate(mActivity, "OverTime");

        SystemID = getSystemID(bean.getDeviceType());

        initView();

        BtnOK();

        title();

    }

    /**
     * 标题栏
     */
    private void title() {


        ImageView img_title = (ImageView) findViewById(R.id.image_back);
        img_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.CloseActivity(mActivity);
            }
        });
        TextView tv_title = (TextView) findViewById(R.id.text_title);
        tv_title.setText("设备维修");


    }

    private void initView() {


        txt_status = (TextView) findViewById(R.id.txt_status);
        txt_faulttype = (TextView) findViewById(R.id.txt_faulttype);
        txt_fault = (TextView) findViewById(R.id.txt_fault);
        txt_remark = (TextView) findViewById(R.id.txt_remark);
        txt_changedevicecode = (TextView) findViewById(R.id.txt_changedevicecode);
        img_photo = (ImageView) findViewById(R.id.img_photo);

        ll_status = (LinearLayout) findViewById(R.id.ll_status);
        ll_faulttype = (LinearLayout) findViewById(R.id.ll_faulttype);
        ll_fault = (LinearLayout) findViewById(R.id.ll_fault);
        ll_remark = (LinearLayout) findViewById(R.id.ll_remark);
        ll_zbar = (LinearLayout) findViewById(R.id.ll_zbar);

        MyOnclick myOnclick = new MyOnclick();
        ll_remark.setOnClickListener(myOnclick);
        ll_fault.setOnClickListener(myOnclick);
        ll_status.setOnClickListener(myOnclick);
        ll_faulttype.setOnClickListener(myOnclick);
        ll_zbar.setOnClickListener(myOnclick);
        img_photo.setOnClickListener(myOnclick);

        FaultTypeList = getFaultTypeList();
        ZhuangTaiList = getZhuangTaiList();
    }

    private class MyOnclick implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            if (v == ll_fault) {
                Intent intent1 = new Intent(mActivity, EndWorkEditActivity.class);
                intent1.putExtra("title", "故障描述");
                intent1.putExtra("type", "3");
                intent1.putExtra("beanID", bean.getRepairListID());
                startActivityForResult(intent1, 3);


            }
            if (v == ll_remark) {

                Intent intent1 = new Intent(mActivity, EndWorkEditActivity.class);
                intent1.putExtra("title", "本次维修说明");
                intent1.putExtra("type", "4");
                intent1.putExtra("beanID", bean.getRepairListID());
                startActivityForResult(intent1, 4);
            }


            if (v == ll_status) {


                DialogUtil.ShowList(mActivity, txt_status, ZhuangTaiList, "完成状态");
            }
            if (v == ll_faulttype) {

                DialogUtil.ShowList(mActivity, txt_faulttype, FaultTypeList, "故障类型");
            }
            if (v == ll_zbar) {
                Intent intent = new Intent(mActivity, CaptureActivity.class);
                startActivityForResult(intent, 5);
            }
            if (v == img_photo) {
                photo2(1, name1);
            }
        }
    }

    public void photo(int num) {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(openCameraIntent, num);
    }

    String name1 = "endwork.jpg";

    public void photo2(int num, String name) {
        PhotoUtils.takePicture(mActivity, name+":"+num);

//        Intent intent4 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        Uri uri = Uri.fromFile(new File(Environment
//                .getExternalStorageDirectory(), name));
//        intent4.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//
//        startActivityForResult(intent4, num);

    }

    private List<String> FaultTypeList;
    private List<String> ZhuangTaiList;

    private List<String> getFaultTypeList() {
        FaultTypeList = new ArrayList<String>();
        List<DictionaryBean> list = null;
        try {
            list = DB.selector(DictionaryBean.class).where("DictionaryName", "=", "ZD_FAULTTYPE").and("SystemID", "=", SystemID).findAll();

            if (list.size() > 0) {
                String DictionaryID = list.get(0).getDictionaryID();
                List<ParamBean> list2 = DB.selector(ParamBean.class).where("DictionaryID", "=", DictionaryID).findAll();
                if (list2.size() > 0) {
                    for (int i = 0; i < list2.size(); i++) {
                        FaultTypeList.add(list2.get(i).getParamValue());
                    }
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return FaultTypeList;
    }

    private String getFaultTypeID(String ParamValue) {
        String ID = "";
        //List<DictionaryBean> list = DB.findAllByWhere(DictionaryBean.class, "DictionaryName ='ZD_FAULTTYPE'");
        List<ParamBean> list2 = null;
        try {
            list2 = DB.selector(ParamBean.class).where("ParamValue", "=", ParamValue).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (list2.size() > 0) {
            ID = list2.get(0).getParamID();
        }

        return ID;

    }


    String SystemID = "";

    private String getSystemID(String DeviceType) {
        String SystemID = "";
        String LastCityID = SharedUtil.getValue(mActivity,"CityID");
        List<DASBean> list = null;
        try {
            list = DB.selector(DASBean.class).where("DeviceTypeID", "=", DeviceType).and("AreaID", "=", LastCityID).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (list.size() > 0) {
            SystemID = list.get(0).getSystemID();
        }
        return SystemID;
    }

    private String getParamCode(String prarmvalue) {
        String code = "";
        List<DictionaryBean> list = null;
        try {
            List<DictionaryBean> list3  = DB.selector(DictionaryBean.class).findAll();
            for (DictionaryBean dictionaryBean : list3) {
                LOG.E("DictionaryName="+dictionaryBean.getDictionaryName());
                LOG.E("ystemID="+dictionaryBean.getSystemID());
            }
            LOG.E("ZD_FAULTTYPE");
            LOG.E("SystemID="+SystemID);
            list = DB.selector(DictionaryBean.class).where("DictionaryName", "=", "ZD_FAULTTYPE").and("SystemID", "=", SystemID).findAll();


            String DictionaryID = list.get(0).getDictionaryID();
            List<ParamBean> list2 = DB.selector(ParamBean.class).where("ParamValue", "=", prarmvalue).and("DictionaryID", "=", DictionaryID).findAll();

            if (list2.size() == 1) {
                code = list2.get(0).getParamCode();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        return code;
    }


    private List<String> getZhuangTaiList() {
        List<String> list = new ArrayList<String>();
        list.add("完成");
        list.add("无法维修");

        return list;
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        switch (requestCode) {
            case PhotoUtils.CAMERA_REQESTCODE:
                if (resultCode == RESULT_OK) {
                    int degree = PhotoUtils.readPictureDegree(PhotoUtils.imageFile.getAbsolutePath());
                    Bitmap bitmap = PhotoUtils.rotaingImageView(degree,PhotoUtils.getBitmapFromFile(PhotoUtils.imageFile, 300, 300));
                    img_photo.setImageBitmap(bitmap);
                    picToBase64 = PhotoUtil.bitmapToString(bitmap, mActivity);

//                    File file1 = new File(Environment.getExternalStorageDirectory() + "/" + name1);
//                    Bitmap bm = PhotoUtil.getSmallBitmap(file1.getAbsolutePath());
//                    picToBase64 = PhotoUtil.bitmapToString(file1.getAbsolutePath(), mActivity);
//                    bm = PhotoUtil.thumbnailBitmap(bm);
//                    img_photo.setImageBitmap(bm);
                }
                break;

            case 3:
                if (resultCode == mActivity.RESULT_OK) {
                    txt_fault.setText(data.getStringExtra("content"));
                }
                break;
            case 4:
                if (resultCode == mActivity.RESULT_OK) {
                    txt_remark.setText(data.getStringExtra("content"));
                }
                break;

            case 5:
                if (resultCode == mActivity.RESULT_OK) {

                    String inputtype = data.getStringExtra("inputtype");
                    if (inputtype.equals("zbar")) {

                        String scanResult = data.getStringExtra("result");
                        String strZbar = ZbarUtil.DeviceZbar(mActivity, scanResult);
                        if (!strZbar.equals("")) {

                            String[] device = strZbar.split(",");

                            if (device[1].equals(bean.getDeviceType())) {
                                Sys_ChangeDevice(device[0], device[1]);
                            } else {
                                ToastUtil.ErrorOrRight(mActivity, "你扫描的设备类型与维修设备类型不一致", 1);
                            }


                        }
                    }
                    //手动输入
                    if (inputtype.equals("shoudong")) {

                        String DeviceCode = data.getStringExtra("DeviceCode");
                        Sys_ChangeDevice(DeviceCode, bean.getDeviceType());

                    }

                }
                break;


            default:
                break;
        }
    }


    private void BtnOK() {
        Button btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setText("提交");
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RepairOver();
            }
        });
    }


    /**
     * 5.5.	结束维修
     */

    String repairListStatus = "";
    String faultType = "";
    String fault = "";
    String remark = "";
    String changeDeviceCode = "";
    String overTime = DateUtil.getNowDate();
    String Status = "";

    private void RepairOver() {

        repairListStatus = txt_status.getText().toString();

        if (repairListStatus.equals("完成")) {
            repairListStatus = "1";
        } else if (repairListStatus.equals("无法维修")) {
            repairListStatus = "0";
        }

        String Type = txt_faulttype.getText().toString();

        // faultType = getFaultTypeID(Type);
        faultType = getParamCode(Type);

        fault = txt_fault.getText().toString();
        remark = txt_remark.getText().toString();
        changeDeviceCode = txt_changedevicecode.getText().toString();

        overTime = SharedUtil.getValueByKey(mActivity, "OverTime");

        Log.e("repairListStatus", repairListStatus);

        if (repairListStatus.equals("")) {
            ToastUtil.showShort(mActivity, "请选择故障维修状态");
            DialogUtil.ShowList(mActivity, txt_status, ZhuangTaiList, "完成状态");
        } else if (Type.equals("")) {
            ToastUtil.showShort(mActivity, "请填写故障类型");
            DialogUtil.ShowList(mActivity, txt_faulttype, FaultTypeList, "故障类型");

        } else {

            final HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("accessToken", SharedUtil.getToken(mActivity));
            map.put("repairListID", bean.getRepairListID());
            map.put("workOrderID", bean.getWorkOrderID());
            map.put("deviceType", bean.getDeviceType());
            map.put("deviceCode", bean.getDeviceCode());
            map.put("overTime", overTime);
            map.put("changeDeviceCode", changeDeviceCode);
            map.put("repairListStatus", repairListStatus);
            map.put("faultType", faultType);
            map.put("fault", fault);
            map.put("remark", remark);
            map.put("photo", picToBase64);
            map.put("lng", SharedUtil.getValue(mActivity, "lng"));
            map.put("lat", SharedUtil.getValue(mActivity, "lat"));

            Log.e("overTime", overTime);


            WebUtil.getInstance(mActivity).webRequest(Constants.RepairOver, map, new WebUtil.MyCallback() {

                @Override
                public void onSuccess(String result) {

                    try {
                        JSONObject jsonObject = new JSONObject(result);

                        String ErrorCode = jsonObject.getString("ErrorCode");
                        String ErrorMsg = jsonObject.getString("ErrorMsg");

                        if (ErrorCode.equals("0")) {

                            ActivityUtil.FinishActivity(mActivity);
                            ToastUtil.showShort(mActivity, "提交成功");
                            LOG.E("repairListStatus:"+repairListStatus);
                            if (repairListStatus.equals("1")) {
                                RepairOrderBean person = DB.selector(RepairOrderBean.class).where("RepairListID", "=", bean.getRepairListID()).findFirst();
                                person.setStatus("4");
                                if(person!=null){
                                    LOG.E("delete:"+person.getRepairListID());
                                    DB.delete(person);
                                }
                                DB.save(person);

                                List<RepairOrderBean> ll = DB.findAll(RepairOrderBean.class);
                                Log.v("aa", "aa" + ll.size());
                            }
                            if (repairListStatus.equals("0")) {

                                RepairOrderBean person = DB.selector(RepairOrderBean.class).where("RepairListID", "=", bean.getRepairListID()).findFirst();
                                person.setStatus("3");
                                if(person!=null){
                                    LOG.E("delete:"+person.getRepairListID());
                                    DB.delete(person);
                                }
                                DB.save(person);
                            }
                            isEnd = true;

                        } else {
                            ToastUtil.showShort(mActivity, ErrorMsg);
                            isEnd = false;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
            });
        }


    }

    public static boolean isEnd = false;


    private void Sys_ChangeDevice(final String devicecode, String deviceType) {

        final HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("accessToken", SharedUtil.getToken(mActivity));
        map.put("deviceType", deviceType);
        map.put("oldDeviceID", bean.getDeviceType());
        map.put("newDeviceID", devicecode);
        map.put("newSerialNumber", "");
        map.put("systemID", SystemID);

        LOG.E("ChangeDevice:"+map.toString());
        WebUtil.getInstance(mActivity).webRequest(Constants.Sys_ChangeDevice, map, new WebUtil.MyCallback() {

            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);

                    String ErrorCode = jsonObject.getString("ErrorCode");
                    String ErrorMsg = jsonObject.getString("ErrorMsg");

                    if (ErrorCode.equals("0")) {

                        ToastUtil.ErrorOrRight(mActivity, "更换成功", 2);
                        txt_changedevicecode.setText(devicecode);
                    } else {
                        ToastUtil.ErrorOrRight(mActivity, ErrorMsg, 1);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            DialogUtil.CloseActivity(mActivity);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
