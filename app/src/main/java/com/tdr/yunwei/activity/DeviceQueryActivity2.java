package com.tdr.yunwei.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tdr.yunwei.R;
import com.tdr.yunwei.YunWeiApplication;
import com.tdr.yunwei.adapter.DeviceQueryAdapter;
import com.tdr.yunwei.adapter.DeviceQueryAdapter2;
import com.tdr.yunwei.adapter.QueryHistoryAdapter;
import com.tdr.yunwei.bean.DASBean;
import com.tdr.yunwei.bean.DeviceBean2;
import com.tdr.yunwei.reviceandbroad.NetChangeReceiver;
import com.tdr.yunwei.util.ActivityUtil;
import com.tdr.yunwei.util.Constants;
import com.tdr.yunwei.util.DBUtils;
import com.tdr.yunwei.util.LOG;
import com.tdr.yunwei.util.MatchUtil;
import com.tdr.yunwei.util.SharedUtil;
import com.tdr.yunwei.util.ToastUtil;
import com.tdr.yunwei.util.WebUtil;
import com.tdr.yunwei.util.ZProgressHUD;
import com.tdr.yunwei.util.ZbarUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/4/20.
 */
public class DeviceQueryActivity2 extends Activity {
    private YunWeiApplication YWA;
    private Activity mActivity;

    DbManager DB;

    private List<DeviceBean2> deviceBeanList;
    private DeviceQueryAdapter2 adapter;

    private EditText et_data;
    private TextView txt_query, txt_previous, txt_next;
    ListView lv;
    LinearLayout ll_page, ll_tip;
    TextView txt_pagenum, txt_tip;

    ImageView image_back, img_clear;

    String QueryDeviceData = "";
    String remark, systemID = "";
    String LastCityID = "";
    private String InType;
    List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_devicequery);

        mActivity = this;
        ActivityUtil.AddActivity(mActivity);
        YWA = YunWeiApplication.getInstance();
//        DB = YWA.getDB();
        DB = x.getDb(DBUtils.getDb());

        InType = getIntent().getStringExtra("in");
        deviceType = getIntent().getStringExtra("type");
        remark = getIntent().getStringExtra("remark");
        LastCityID = SharedUtil.getValue(mActivity, "CityID");
        systemID = getSystemID(deviceType);
        LOG.E("systemID="+systemID);
        SharedUtil.setValue(mActivity, "isCodeChange", "");

        initView();

        GetQueryHistory();
        RegReceiver();
    }

    private String getSystemID(String DeviceType) {
        String SystemID = "412A2A28236441BCA19B32E2F5F71254";
        List<DASBean> list = null;
        try {
            list = DB.selector(DASBean.class).where("DeviceTypeID", "=", DeviceType).and("AreaID", "like", LastCityID.substring(0, 4) + "%").findAll();

            if (list.size() > 0) {
                SystemID = list.get(0).getSystemID();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return SystemID;
    }

    private void initView() {

        lv = (ListView) findViewById(R.id.lv);


        ll_page = (LinearLayout) findViewById(R.id.ll_page);
        ll_tip = (LinearLayout) findViewById(R.id.ll_tip);

        txt_pagenum = (TextView) findViewById(R.id.txt_pagenum);


        et_data = (EditText) findViewById(R.id.et_data);

        txt_query = (TextView) findViewById(R.id.txt_query);
        txt_previous = (TextView) findViewById(R.id.txt_previous);
        txt_next = (TextView) findViewById(R.id.txt_next);

        img_clear = (ImageView) findViewById(R.id.img_clear);
        img_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_data.setText("");
                img_clear.setVisibility(View.GONE);
            }
        });

        txt_tip = (TextView) findViewById(R.id.txt_tip);
        String cityaddress = SharedUtil.getValue(mActivity, "cityaddress");
        String city = SharedUtil.getValue(mActivity, "city");
        String area = SharedUtil.getValue(mActivity, "area");
        String street = SharedUtil.getValue(mActivity, "street");

        if (cityaddress.equals("")) {
            txt_tip.setText("可以模糊查询");
        } else {
            txt_tip.setText("可以模糊查询，例 '" + cityaddress + "' 输入 '" + city +
                    "' 或 '" + area + "' 或 '" + street + "'");
        }
        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.FinishActivity(mActivity);
            }
        });


        txt_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QueryDevice();
            }
        });


        txt_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (pagenum == 1) {
                    ToastUtil.showShort(mActivity, "已经是第一页");
                    return;
                }


                if (isNetTrue) {//有网络

                    if (pagenum <= 0) {
                        pagenum = 1;
                    }

                    startIndex = startIndex - count - 1;
                    pagenum--;
                    pageNext = true;
                }
                QueryDevice();


            }
        });
        txt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (pageNext == false) {
                    ToastUtil.showShort(mActivity, "已经是最后一页");
                    return;
                }

                if (isNetTrue) {
                    startIndex = startIndex + count + 1;
                    pagenum++;

                }
                QueryDevice();


            }
        });

        et_data.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                img_clear.setVisibility(View.VISIBLE);

            }
        });
        deviceBeanList = new ArrayList<>();
        adapter = new DeviceQueryAdapter2(mActivity, deviceBeanList, DB);
        lv.setAdapter(adapter);
        adapter.setClick(new DeviceQueryAdapter2.DeviceQueryClickListener() {
            @Override
            public void OnClick(int Position) {
                if (InType.equals("Search")) {
                    ClickSearch(Position);
                } else if (InType.equals("Binding")) {
                    ClickBinding(Position);
                }
            }
        });

    }

    private void ClickSearch(int position) {
        Intent intent = new Intent(mActivity, NewDeviceAddActivity.class);
        DeviceBean2 devicebean =new DeviceBean2(deviceBeanList.get(position));
        LOG.E(devicebean.getPhoto1().equals("")?"没有照片":"有照片");
        LOG.E("DeviceID="+deviceBeanList.get(position).getDeviceID());

        //图片太大 会崩溃
        if (!devicebean.getPhoto1().equals("") || !devicebean.getPhoto2().equals("")
                || !devicebean.getPhoto3().equals("")) {
            SharedUtil.setValue(mActivity, "Photo1", devicebean.getPhoto1());
            SharedUtil.setValue(mActivity, "Photo2", devicebean.getPhoto2());
            SharedUtil.setValue(mActivity, "Photo3", devicebean.getPhoto3());
            devicebean.setPhoto1("");
            devicebean.setPhoto2("");
            devicebean.setPhoto3("");
            intent.putExtra("pic", "you");
        } else {
            intent.putExtra("pic", "wu");
        }
        intent.putExtra("status", "详情");
        intent.putExtra("deviceremark", ZbarUtil.getSubRemark(DB, devicebean.getDeviceType()));
        intent.putExtra("DeviceType", HomeActivity.type);
        intent.putExtra("deviceBean", devicebean);
        startActivityForResult(intent, 1);
    }

    private void ClickBinding(int position) {
        Intent intent = new Intent(mActivity, DeviceBindingActivity.class);
        DeviceBean2 devicebean =new DeviceBean2(deviceBeanList.get(position));
        LOG.E("1GroudDeviceStatus8="+deviceBeanList.get(position).getGroudDeviceStatus8());
        LOG.E("2GroudDeviceStatus8="+devicebean.getGroudDeviceStatus8());
        devicebean.setPhoto1("");
        devicebean.setPhoto2("");
        devicebean.setPhoto3("");
        intent.putExtra("deviceBean", devicebean);
        intent.putExtra("systemID", systemID);
        startActivity(intent);
    }

    private void GetQueryHistory() {

        String DeviceQueryType = SharedUtil.getValue(mActivity, deviceType);
        if (!DeviceQueryType.equals(deviceType)) {
            return;
        }


        String[] QueryHistory = SharedUtil.getValue(mActivity, "QueryHistory2").split(",");
        list = new ArrayList<String>();

        int size = QueryHistory.length;
        if (size <= 0) {
            return;
        }

        int count = 12;
        if (size > count) {//如果搜索的关键字超过14个，取最新的14个
            for (int i = size - 1; i >= size - count; i--) {

                if (!QueryHistory[i].equals("")) {
                    list.add(QueryHistory[i]);
                }
            }
        } else {

            for (int i = size - 1; i >= 0; i--) {
                if (!QueryHistory[i].equals("")) {
                    list.add(QueryHistory[i]);
                }
            }
        }

        QueryHistoryAdapter adapter = new QueryHistoryAdapter(mActivity, list);
        GridView lv_query = (GridView) findViewById(R.id.lv_query);
        lv_query.setAdapter(adapter);
        lv_query.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                et_data.setText(list.get(position));
                QueryDevice();
            }
        });


    }


    boolean isNetTrue = true;
    boolean pageNext = true;

    int pagenum = 1;

    String deviceCode = "";
    String serialNumber = "";
    String address = "";
    String deviceType = "";
    int startIndex = 1;
    int count = 9;


    /**
     * 查询
     */
    String data = "";

    private void QueryDevice() {
        //获取修改设备后的设备码
        String isCodeChange = SharedUtil.getValue(mActivity, "isCodeChange");
        if (!isCodeChange.equals("")) {//值不为空时判断为修改完设备后返回查询页面并主动调用查询
            et_data.setText(isCodeChange);
            data = isCodeChange;
            SharedUtil.setValue(mActivity, "isCodeChange", "");
        } else {//非修改设备后调用的查询
            data = et_data.getText().toString().trim();
        }

        if (data.equals("")) {
            ToastUtil.ErrorOrRight(mActivity, "请输入查询条件！", 1);
            return;
        }

        if (MatchUtil.isNum(data)) {
            deviceCode = data;
            serialNumber = "";
            address = "";
        } else {
            if (isSerial(data)) {
                serialNumber = data;
                address = "";
                deviceCode = "";
            } else {
                address = data;
                serialNumber = "";
                deviceCode = "";
            }
        }

        if (systemID.equals("")) {
            systemID = getSystemID(deviceType);
        }

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("accessToken", SharedUtil.getToken(mActivity));
        map.put("deviceType", deviceType);
        map.put("deviceCode", deviceCode);
        map.put("serialNumber", serialNumber);
        map.put("address", address);
        map.put("startIndex", startIndex);
        map.put("count", count);
        map.put("systemID", systemID);

        Log.e("startIndex第一次", startIndex + "//");

        final ZProgressHUD zProgressHUD = new ZProgressHUD(mActivity);
        zProgressHUD.setMessage("正在查询……");
        zProgressHUD.show();


        WebUtil.getInstance(mActivity).webRequest(Constants.Sys_QueryDeviceNew, map, new WebUtil.MyCallback() {

            @Override
            public void onSuccess(String result) {
                if (result.equals("-1") || result.equals("-2")) {
                    zProgressHUD.dismiss();
                    isNetTrue = false;
                    return;
                }
                LOG.LE("车辆数据：" + result);
                try {
                    isNetTrue = true;
                    JSONObject jsonObject = new JSONObject(result);
                    String ErrorCode = jsonObject.getString("ErrorCode");
                    String ErrorMsg = jsonObject.getString("ErrorMsg");
                    if (ErrorCode.equals("0")) {


                        String DeviceList = jsonObject.getString("DeviceList");
                        Gson gson = new Gson();
                        deviceBeanList = gson.fromJson(DeviceList, new TypeToken<List<DeviceBean2>>() {
                        }.getType());


                        int size = deviceBeanList.size();
                        LOG.E("deviceBeanList.size()" + deviceBeanList.size());
//                        LOG.E("deviceID"+ deviceBeanList.get(0).getDeviceID());
                        if (size == 0) {
                            ToastUtil.ErrorOrRight(mActivity, "查无数据", 1);

                        } else {
                            ll_page.setVisibility(View.VISIBLE);
                            ll_tip.setVisibility(View.GONE);

                            lv.setVisibility(View.VISIBLE);

                            adapter.UpData(deviceBeanList);
                            if (size < count) {
                                pageNext = false;
                            }
                            txt_pagenum.setText(pagenum + "");
                            //保存搜索关键字
                            saveDate(data);

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

    public static boolean isSerial(String str) {
        boolean Ret = false;
        String[] Serial = str.split("-");
        for (int i = 0; i < Serial.length; i++) {
            LOG.D(i + " Serial=" + Serial[i]);
        }
        int index = 0;
        if (str.contains("-") && str.length() == 25) {
            if (Serial.length == 5) {
                for (int i = 0; i < Serial.length; i++) {
                    if (i == 0) {
                        if (Serial[0].length() == 5 && MatchUtil.isNum(Serial[0])) {
                            index++;
                        }
                    } else {
                        if (Serial[i].length() == 4 && MatchUtil.isNum(Serial[i])) {
                            index++;
                        }
                    }
                }
            }
        }
        if (index == 5) {
            Ret = true;
        }
        LOG.D("Ret=" + Ret);
        return Ret;
    }

    private void saveDate(String data) {


        boolean isexits = true;
        String str = SharedUtil.getValue(mActivity, "QueryHistory2");

        String[] QueryHistory = str.split(",");
        for (int i = 0; i < QueryHistory.length; i++) {
            if (data.equals(QueryHistory[i])) {
                isexits = false;
            }

        }
        if (isexits) {
            str = str + "," + data;
            Log.e("str", str);
            SharedUtil.setValue(mActivity, "QueryHistory2", str);
            SharedUtil.setValue(mActivity, deviceType, deviceType);
        }


    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {

            if (lv.getVisibility() == View.VISIBLE) {
                ListLV();
            } else {
                ActivityUtil.FinishActivity(mActivity);
            }


            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (result.equals("1")) {
            QueryDevice();
        }


    }


    private void ListLV() {
        lv.setVisibility(View.GONE);
        ll_page.setVisibility(View.GONE);
        ll_tip.setVisibility(View.VISIBLE);
        GetQueryHistory();
    }


    private NetChangeReceiver netChangeReceiver;
    private ConnectivityManager connectivityManager;


    private void RegReceiver() {
        connectivityManager = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        netChangeReceiver = new NetChangeReceiver(new NetChangeReceiver.DoNet() {
            @Override
            public void goToDo() {
                isNetTrue = false;

            }
        });
        registerReceiver(netChangeReceiver, filter);
    }

    /**
     * 取消广播
     */

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(netChangeReceiver);
    }

    String result = "";

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case 1:
                if (resultCode == mActivity.RESULT_OK) {
                    result = data.getStringExtra("result");
                }
                break;


        }
    }


}
