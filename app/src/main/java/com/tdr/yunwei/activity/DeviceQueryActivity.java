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
import com.tdr.yunwei.adapter.QueryHistoryAdapter;
import com.tdr.yunwei.bean.DASBean;
import com.tdr.yunwei.bean.DeviceBean;
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
public class DeviceQueryActivity extends Activity {
    private YunWeiApplication YWA ;
    private Activity mActivity;

    DbManager DB;

    private List<DeviceBean> deviceBeanList;
    private DeviceQueryAdapter adapter;

    private EditText et_data;
    private TextView txt_query, txt_previous, txt_next;
    ListView lv;
    LinearLayout ll_page, ll_tip;
    TextView txt_pagenum, txt_tip;

    ImageView image_back, img_clear;

    String QueryDeviceData = "";
    String remark, systemID = "";
    String LastCityID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_devicequery);

        mActivity = this;
        ActivityUtil.AddActivity(mActivity);
        YWA = YunWeiApplication.getInstance();
//        DB = YWA.getDB();
        DB= x.getDb(DBUtils.getDb());
        deviceType = getIntent().getStringExtra("type");
        remark = getIntent().getStringExtra("remark");
        LastCityID = SharedUtil.getValue(mActivity,"CityID");
        systemID = getSystemID(deviceType);

        SharedUtil.setValue(mActivity, "isCodeChange","");

        initView();

        GetQueryHistory();
        RegReceiver();
    }

    private String getSystemID(String DeviceType) {
        String SystemID = "412A2A28236441BCA19B32E2F5F71254";
        List<DASBean> list = null;
        try {
            list = DB.selector(DASBean.class).where("DeviceTypeID","=",DeviceType).and("AreaID","like", LastCityID.substring(0,4)+"%").findAll();

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


    }

    List<String> list;

    private void GetQueryHistory() {

        String DeviceQueryType = SharedUtil.getValue(mActivity, deviceType);
        if (!DeviceQueryType.equals(deviceType)) {
            return;
        }


        String[] QueryHistory = SharedUtil.getValue(mActivity, "QueryHistory").split(",");
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
        String isCodeChange=SharedUtil.getValue(mActivity, "isCodeChange");
        if(!isCodeChange.equals("")){//值不为空时判断为修改完设备后返回查询页面并主动调用查询
            et_data.setText(isCodeChange);
            data=isCodeChange;
            SharedUtil.setValue(mActivity,"isCodeChange","");
        }else{//非修改设备后调用的查询
            data= et_data.getText().toString().trim();
        }

        if (data.equals("")) {
            ToastUtil.ErrorOrRight(mActivity, "请输入查询条件！", 1);
            return;
        }

        if (MatchUtil.isNum(data)) {
            deviceCode = data;
            serialNumber="";
            address="";
        }else{
            if(isSerial(data)){
                serialNumber = data;
                address="";
                deviceCode="";
            }else{
                address = data;
                serialNumber="";
                deviceCode="";
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


        WebUtil.getInstance(mActivity).webRequest(Constants.Sys_QueryDevice, map, new WebUtil.MyCallback() {

            @Override
            public void onSuccess(String result) {
                if (result.equals("-1") || result.equals("-2")) {
                    zProgressHUD.dismiss();
                    isNetTrue = false;
                    return;
                }
                try {

                    isNetTrue = true;

                    JSONObject jsonObject = new JSONObject(result);

                    String ErrorCode = jsonObject.getString("ErrorCode");
                    String ErrorMsg = jsonObject.getString("ErrorMsg");


                    if (ErrorCode.equals("0")) {


                        String DeviceList = jsonObject.getString("DeviceList");
                        Gson gson=new Gson();
                        deviceBeanList = gson.fromJson(DeviceList, new TypeToken<List<DeviceBean>>() {
                        }.getType());


                        int size = deviceBeanList.size();
                        Log.e("deviceBeanList.size()", "" + deviceBeanList.size());
                        if (size == 0) {
                            ToastUtil.ErrorOrRight(mActivity, "查无数据", 1);

                        } else {
                            Log.d("Pan","Reserve1="+deviceBeanList.get(0).getReserve1());
                            Log.d("Pan","Reserve2="+deviceBeanList.get(0).getReserve2());
                            Log.d("Pan","Reserve3="+deviceBeanList.get(0).getReserve3());
                            Log.d("Pan","Reserve4="+deviceBeanList.get(0).getReserve4());
                            Log.d("Pan","Reserve5="+deviceBeanList.get(0).getReserve5());
                            Log.d("Pan","Reserve6="+deviceBeanList.get(0).getReserve6());
                            Log.d("Pan","Reserve7="+deviceBeanList.get(0).getReserve7());
                            Log.d("Pan","Reserve8="+deviceBeanList.get(0).getReserve8());
                            Log.d("Pan","Reserve9="+deviceBeanList.get(0).getReserve9());
                            Log.d("Pan","Reserve10="+deviceBeanList.get(0).getReserve10());
                            Log.d("Pan","Reserve11="+deviceBeanList.get(0).getReserve11());
                            Log.d("Pan","Reserve12="+deviceBeanList.get(0).getReserve12());

                            ll_page.setVisibility(View.VISIBLE);
                            ll_tip.setVisibility(View.GONE);

                            lv.setVisibility(View.VISIBLE);
                            adapter = new DeviceQueryAdapter(mActivity, deviceBeanList, DB);
                            lv.setAdapter(adapter);

                            if (size < count) {
                                pageNext = false;
                            }
                            txt_pagenum.setText(pagenum + "");
                            //保存搜索关键字
                            saveDate(data);

                            Log.e("deviceBeanList", deviceBeanList.get(0).getReserve1() + "//"
                                    + deviceBeanList.get(0).getReserve2() + "//" + deviceBeanList.get(0).getReserve3());


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

    public static boolean isSerial(String str){
        boolean Ret=false;
        String[] Serial =str.split("-");
        for (int i = 0; i <Serial.length ; i++) {
            LOG.D(i+" Serial="+Serial[i]);
        }
        int index=0;
        if(str.contains("-")&&str.length()==25){
            if(Serial.length==5){
                for (int i = 0; i <Serial.length ; i++) {
                    if(i==0){
                        if(Serial[0].length()==5&&MatchUtil.isNum(Serial[0])){
                            index++;
                        }
                    }else{
                        if(Serial[i].length()==4&&MatchUtil.isNum(Serial[i])){
                            index++;
                        }
                    }
                }
            }
        }
        if(index==5){
            Ret=true;
        }
        LOG.D("Ret="+Ret);
        return Ret;
    }

    private void saveDate(String data) {


        boolean isexits = true;
        String str = SharedUtil.getValue(mActivity, "QueryHistory");

        String[] QueryHistory = str.split(",");
        for (int i = 0; i < QueryHistory.length; i++) {
            if (data.equals(QueryHistory[i])) {
                isexits = false;
            }

        }
        if (isexits) {
            str = str + "," + data;
            Log.e("str", str);
            SharedUtil.setValue(mActivity, "QueryHistory", str);
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
