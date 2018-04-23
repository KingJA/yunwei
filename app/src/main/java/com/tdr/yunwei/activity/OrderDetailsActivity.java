package com.tdr.yunwei.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tdr.yunwei.R;
import com.tdr.yunwei.YunWeiApplication;
import com.tdr.yunwei.baidumap.BaiDuMapDeviceActivity;
import com.tdr.yunwei.bean.DeviceBean;
import com.tdr.yunwei.bean.RepairOrderBean;
import com.tdr.yunwei.util.ActivityUtil;
import com.tdr.yunwei.util.Constants;
import com.tdr.yunwei.util.DBUtils;
import com.tdr.yunwei.util.DateUtil;
import com.tdr.yunwei.util.LOG;
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
import org.xutils.x;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/4/28.
 */
public class OrderDetailsActivity extends Activity {
    private Activity mActivity;

    private TextView txt_orderno, txt_devicetype, txt_devicecode, txt_deviceaddress;
    private TextView txt_bztime, txt_bzren, txt_bzphone, txt_bzdescription, txt_isurgent;
    private TextView txt_paino, txt_pairen, txt_paitime, txt_endtime;

    private LinearLayout ll_deviceaddress, ll_jiedan, ll_startwork, ll_endwork, ll_callphone;


    private RepairOrderBean bean;

    private String Status = "";
    private DbManager DB;
    String devicetype = "";
    String ID="";


    public static boolean isAcceptWorkOrder=false;
    public static boolean idRepairStart=false;
    private YunWeiApplication YWA;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_orderdetails);

        mActivity = OrderDetailsActivity.this;
        ActivityUtil.AddActivity(mActivity);

        YWA = YunWeiApplication.getInstance();
//        DB = YWA.getDB();
        DB= x.getDb(DBUtils.getDb());
        bean = getIntent().getParcelableExtra("RepairOrderBean");
        devicetype = getIntent().getStringExtra("deviceremark");

        SharedUtil.setValue(mActivity,"DeviceType",devicetype);

        ID=bean.getRepairListID();

        Log.e("UserID--getWorkSender",bean.getWorkSender());


        initView();
        getRolePowers();
        DateUtil.getNowDate(mActivity,"acceptTime");
        DateUtil.getNowDate(mActivity,"startTime");
    }

    boolean role211 = false, role212 = false, role213 = false;

    private void getRolePowers() {
        String[] RolePowers = SharedUtil.getValue(mActivity, "RolePowers").split(",");

        for (int i = 0; i < RolePowers.length; i++) {
            int role = Integer.valueOf(RolePowers[i]);
            if (role == 211) {
                role211 = true;
            }
            if (role == 212) {
                role212 = true;
            }
            if (role == 213) {
                role213 = true;
            }

        }


    }

    private void initView() {
        title();


        txt_orderno = (TextView) findViewById(R.id.txt_orderno);

        txt_devicetype = (TextView) findViewById(R.id.txt_devicetype);
        txt_devicecode = (TextView) findViewById(R.id.txt_devicecode);
        txt_deviceaddress = (TextView) findViewById(R.id.txt_deviceaddress);

        txt_bztime = (TextView) findViewById(R.id.txt_bztime);
        txt_bzren = (TextView) findViewById(R.id.txt_bzren);
        txt_bzphone = (TextView) findViewById(R.id.txt_bzphone);
        txt_bzdescription = (TextView) findViewById(R.id.txt_bzdescription);
        txt_isurgent = (TextView) findViewById(R.id.txt_isurgent);

        txt_paino = (TextView) findViewById(R.id.txt_paino);
        txt_pairen = (TextView) findViewById(R.id.txt_pairen);
        txt_paitime = (TextView) findViewById(R.id.txt_paitime);
        txt_endtime = (TextView) findViewById(R.id.txt_endtime);


        txt_orderno.setText(bean.getRepairListNO());
        txt_devicetype.setText(devicetype);
        txt_devicecode.setText(bean.getDeviceCode());


        txt_bztime.setText(bean.getMakeTime());
        txt_bzren.setText(bean.getRequestMan());
        txt_bzphone.setText(bean.getRequestPhone());
        txt_bzdescription.setText(bean.getDescription());


        if (bean.getIsUrgent().equals("1")) {
            txt_isurgent.setText("加急");
        } else {
            txt_isurgent.setText("不加急");
        }

        txt_paino.setText(bean.getWorkSender());
        txt_pairen.setText(bean.getWorkOrderNO());
        txt_paitime.setText(bean.getWorkSendTime());
        txt_endtime.setText(bean.getLimitTime());

        txt_deviceaddress.setText(bean.getAddress());


        ll_jiedan = (LinearLayout) findViewById(R.id.ll_jiedan);
        ll_startwork = (LinearLayout) findViewById(R.id.ll_startwork);
        ll_endwork = (LinearLayout) findViewById(R.id.ll_endwork);
        ll_callphone = (LinearLayout) findViewById(R.id.ll_callphone);
        ll_deviceaddress = (LinearLayout) findViewById(R.id.ll_deviceaddress);
        MyOnclick myOnclick = new MyOnclick();
        ll_jiedan.setOnClickListener(myOnclick);
        ll_startwork.setOnClickListener(myOnclick);
        ll_endwork.setOnClickListener(myOnclick);
        ll_deviceaddress.setOnClickListener(myOnclick);
        ll_callphone.setOnClickListener(myOnclick);


        setViewEnabled();


        String begin = "";
        begin = getIntent().getStringExtra("beginstatus");
        if (begin.equals("1")) {
            Show(1);
        }
        if (begin.equals("2")) {
            Show(2);
        }


    }


    private void setViewEnabled() {
        Status = bean.getStatus();

        Log.e("Status",Status);
        ll_jiedan.setEnabled(false);
        ll_startwork.setEnabled(false);
        ll_endwork.setEnabled(false);

        if (Status.equals("1")) {
            ll_jiedan.setEnabled(true);
            ll_jiedan.setBackgroundResource(R.mipmap.bottom_bg_on);
        } else if (Status.equals("2")) {
            ll_startwork.setEnabled(true);
            ll_startwork.setBackgroundResource(R.mipmap.bottom_bg_on);
        } else if (Status.equals("3")||Status.equals("9")) {
            ll_endwork.setEnabled(true);
            ll_endwork.setBackgroundResource(R.mipmap.bottom_bg_on);
        }

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
        tv_title.setText("工单详情");

    }

    private class MyOnclick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v == ll_jiedan) {

                if (role211) {

                    AcceptWorkOrder();

                } else {
                    ToastUtil.showShort(mActivity, "您没有此权限");
                }
            }
            if (v == ll_startwork) {
                if (role212) {


                    Intent intent = new Intent(mActivity, CaptureActivity.class);
                    startActivityForResult(intent, 1002);


                } else {
                    ToastUtil.showShort(mActivity, "您没有此权限");
                }
            }
            if (v == ll_endwork) {
                if (role213) {

                    EndWork();

                } else {
                    ToastUtil.showShort(mActivity, "您没有此权限");
                }

            }
            if (v == ll_deviceaddress) {

                //根据编号查询设备经纬度 进行定位

                //getLatLng();

            }

            if (v == ll_callphone) {
                String number = txt_bzphone.getText().toString();
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));

                if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                startActivity(intent);
            }
        }
    }
    private List<DeviceBean> deviceBeanList;
    String LAT="",LNG="",Address="";
    private void getLatLng(){

        String deviceType=bean.getDeviceType();
        String deviceCode=bean.getDeviceCode();

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("accessToken", SharedUtil.getToken(mActivity));
        map.put("deviceType", deviceType);
        map.put("deviceCode", deviceCode);
        map.put("serialNumber", "");
        map.put("address", "");
        map.put("startIndex", 0);
        map.put("count", 10);

        final ZProgressHUD zProgressHUD = new ZProgressHUD(mActivity);
        zProgressHUD.setMessage("正在查询设备经纬度……");
        zProgressHUD.show();


        WebUtil.getInstance(mActivity).webRequest(Constants.Sys_QueryDevice, map, new WebUtil.MyCallback() {

            @Override
            public void onSuccess(String result) {
                if (result.equals("-1") || result.equals("-2")) {
                    zProgressHUD.dismiss();
                    return;
                }
                try {


                    JSONObject jsonObject = new JSONObject(result);

                    String ErrorCode = jsonObject.getString("ErrorCode");
                    String ErrorMsg = jsonObject.getString("ErrorMsg");


                    if (ErrorCode.equals("0")) {


                        String DeviceList = jsonObject.getString("DeviceList");
                        Gson gson=new Gson();
                        deviceBeanList = gson.fromJson(DeviceList, new TypeToken<List<DeviceBean>>() {
                        }.getType());


                        int size = deviceBeanList.size();
                        if (size == 1&&bean.getDeviceCode().equals(deviceBeanList.get(0).getDeviceCode())) {
                            LAT=deviceBeanList.get(0).getLAT();
                            LNG=deviceBeanList.get(0).getLNG();
                            Address=deviceBeanList.get(0).getAddress();

                            ShowPop(LAT,LNG,Address);


                        } else {

                            ToastUtil.ErrorOrRight(mActivity, "查无数据", 1);
                        }


                    } else {
                        ToastUtil.ErrorOrRight(mActivity, ErrorMsg, 1);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    zProgressHUD.dismiss();
                }
            }
        });


    }

    public void ShowPop(final String LAT,final String LNG, final String Address) {
        View contentView = LayoutInflater.from(mActivity).inflate(R.layout.pop_latlng, null);

        TextView txt_lat = (TextView) contentView.findViewById(R.id.txt_lat);
        TextView txt_lng = (TextView) contentView.findViewById(R.id.txt_lng);
        TextView txt_address = (TextView) contentView.findViewById(R.id.txt_address);
        TextView txt_cancel= (TextView) contentView.findViewById(R.id.txt_cancel);
        TextView txt_ok = (TextView) contentView.findViewById(R.id.txt_ok);

        txt_lat.setText(LAT);
        txt_lng.setText(LNG);
        txt_address.setText(Address);

        final PopupWindow popWin = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);


        popWin.setTouchable(true);
        popWin.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        popWin.setTouchInterceptor(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return false;
            }
        });


        popWin.setBackgroundDrawable(mActivity.getResources().getDrawable(R.color.transparent));


        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 0.4f;
        mActivity.getWindow().setAttributes(lp);
        popWin.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
                lp.alpha = 1f;
                mActivity.getWindow().setAttributes(lp);

            }
        });


        //按钮监听

        txt_cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popWin.dismiss();
			}
		});

        txt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWin.dismiss();
                Intent intent = new Intent(mActivity, BaiDuMapDeviceActivity.class);
                intent.putExtra("DeviceLAT", LAT);
                intent.putExtra("DeviceLNG", LNG);
                intent.putExtra("DeviceAddress", Address);
                startActivity(intent);

            }
        });


        popWin.showAtLocation(contentView, Gravity.BOTTOM, 0, 20);


    }




    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case 1002:
                if (resultCode == mActivity.RESULT_OK) {
                    String inputtype = data.getStringExtra("inputtype");
                    if (inputtype.equals("zbar")) {
                        String scanResult = data.getStringExtra("result");
                        String strZbar = ZbarUtil.DeviceZbar(mActivity, scanResult);

                        if (!strZbar.equals("")) {

                            String[] device = strZbar.split(",");
                            String devicecode = device[0];
                            if (devicecode.equals(bean.getDeviceCode())) {
                                RepairStart();
                            } else {
                                ToastUtil.showShort(mActivity, "你扫描的设备编号与工单设备编号不一致");
                            }
                        }
                    }
                    //手动输入
                    if (inputtype.equals("shoudong")) {

                        String DeviceCode = data.getStringExtra("DeviceCode");
                        if (DeviceCode.equals(bean.getDeviceCode())) {
                            RepairStart();
                        } else {
                            ToastUtil.showShort(mActivity, "你输入的设备编号与工单设备编号不一致");
                        }

                    }
                }
                break;


        }
    }


    /**
     * 5.3.	接单
     */


    private void AcceptWorkOrder() {



        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("accessToken", SharedUtil.getToken(mActivity));
        map.put("userID", SharedUtil.getValue(mActivity, "UserId"));
        map.put("repairListID", ID);
        map.put("workOrderID", bean.getWorkOrderID());
        map.put("acceptTime", SharedUtil.getValueByKey(mActivity,"acceptTime"));


        WebUtil.getInstance(mActivity).webRequest(Constants.AcceptWorkOrder, map, new WebUtil.MyCallback() {

            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String ErrorCode = jsonObject.getString("ErrorCode");
                    if (ErrorCode.equals("0")) {
                        ToastUtil.showShort(mActivity, "接单成功");
                        ll_jiedan.setBackgroundResource(R.mipmap.bottom_bg_off);
                        ll_jiedan.setEnabled(false);

                        ll_startwork.setBackgroundResource(R.mipmap.bottom_bg_on);
                        ll_startwork.setEnabled(true);


                        RepairOrderBean person = DB.selector(RepairOrderBean.class).where("RepairListID", "=", ID).findFirst();
                        person.setStatus("2");
                        if(person!=null){
                            LOG.E("delete:"+person.getRepairListID());
                            DB.delete(person);
                        }
                        DB.save(person);
                        isAcceptWorkOrder=true;

                    } else {
                        ToastUtil.showShort(mActivity, "接单失败");
                        isAcceptWorkOrder=false;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    /**
     * 5.4.	开始维修
     */


    boolean isRepairStart = false;

    private void RepairStart() {


        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("accessToken", SharedUtil.getToken(mActivity));
        map.put("repairListID", ID);
        map.put("workOrderID", bean.getWorkOrderID());
        map.put("deviceType", bean.getDeviceType());
        map.put("deviceCode", bean.getDeviceCode());
        map.put("serialNO", "1");
        map.put("startTime", SharedUtil.getValueByKey(mActivity,"startTime"));

        Log.e("accessToken", SharedUtil.getToken(mActivity) + "repairListID=" + bean.getRepairListID()
                + "workOrderID=" + bean.getWorkOrderID() + "deviceType=" + bean.getDeviceType() +
                "startTime=" + SharedUtil.getValueByKey(mActivity,"startTime") + "deviceCode=" + bean.getDeviceCode());


        WebUtil.getInstance(mActivity).webRequest(Constants.RepairStart, map, new WebUtil.MyCallback() {

            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);

                    String ErrorCode = jsonObject.getString("ErrorCode");
                    String ErrorMsg = jsonObject.getString("ErrorMsg");

                    Log.e("ErrorMsg", ErrorMsg);
                    if (ErrorCode.equals("0")) {

                        ll_startwork.setBackgroundResource(R.mipmap.bottom_bg_off);
                        ll_startwork.setEnabled(false);

                        ll_endwork.setBackgroundResource(R.mipmap.bottom_bg_on);
                        ll_endwork.setEnabled(true);

                        isRepairStart = true;

                        RepairOrderBean person = DB.selector(RepairOrderBean.class).where("RepairListID", "=", ID).findFirst();
                        person.setStatus("3");
                        if(person!=null){
                            LOG.E("delete:"+person.getRepairListID());
                            DB.delete(person);
                        }
                        DB.save(person);
                        idRepairStart=true;

                    } else {
                        ToastUtil.showShort(mActivity, "开始维修失败" + ErrorMsg);
                        isRepairStart = false;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void EndWork() {
        ll_endwork.setBackgroundResource(R.mipmap.bottom_bg_off);
        ll_endwork.setEnabled(false);

        Intent intent = new Intent(mActivity, EndWorkActivity.class);
        intent.putExtra("RepairOrderBean", bean);
        startActivity(intent);
        ActivityUtil.FinishActivity(mActivity);
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


    @Override
    protected void onStart() {
        super.onStart();

    }

    String title = "";

    private void Show(final int status) {


        if (status == 1) {
            title = "此工单还没有接单，是否接单并开始维修？";
        }

        if (status == 2) {
            title = "此工单已被接，是否开始维修？";
        }



        Builder builder = new Builder(mActivity);
        builder.setTitle("");
        builder.setMessage(title);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();


                if (status == 1) {
                    AcceptWorkOrder();
                    RepairStart();
                }

                if (status == 2) {
                    RepairStart();
                }

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        Dialog noticeDialog = builder.create();
        noticeDialog.show();
    }
}