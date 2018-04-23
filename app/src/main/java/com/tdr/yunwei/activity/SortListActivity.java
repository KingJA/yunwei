package com.tdr.yunwei.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sortlistview.CharacterParser;
import com.sortlistview.PinyinComparator;
import com.sortlistview.SideBar;
import com.sortlistview.SortAdapter;
import com.tdr.yunwei.R;
import com.tdr.yunwei.YunWeiApplication;
import com.tdr.yunwei.bean.CityList;
import com.tdr.yunwei.util.ActivityUtil;
import com.tdr.yunwei.util.Constants;
import com.tdr.yunwei.util.LOG;
import com.tdr.yunwei.util.SharedUtil;
import com.tdr.yunwei.util.ToastUtil;
import com.tdr.yunwei.util.WebUtil;
import com.tdr.yunwei.util.ZProgressHUD;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Handler;

public class SortListActivity extends Activity {
    private ListView sortListView;
    private SideBar sideBar;
    private TextView dialog;
    private SortAdapter adapter;
    private EditText mClearEditText;
    private String CityName = "", CityID = "";
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    private List<CityList> SourceDateList;

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;
    ZProgressHUD zProgressHUD;
    private Activity mActivity;
    private ArrayList<CityList> cityBeanList;
    private ImageView iv_back;
    private TextView tv_location;
    private String LocationCity;
    public static final int Location = 1111;
//    private  getLocation gl;
    Gson gson=new Gson();
    private LocationClient mLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sortlist);
        mActivity = this;
        ActivityUtil.AddActivity(mActivity);
        initViews();
        getAddress();
        UpdateCityInfo();

    }
    private  void getAddress(){
        mLocationClient = new LocationClient(getApplicationContext());
        LocationClientOption option = new LocationClientOption();

        option.setIsNeedAddress(true);
        //可选，是否需要地址信息，默认为不需要，即参数为false
        //如果开发者需要获得当前点的地址信息，此处必须为true
        mLocationClient.setLocOption(option);
        //声明LocationClient类
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
                //以下只列举部分获取地址相关的结果信息
                //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

                String addr = bdLocation.getAddrStr();    //获取详细地址信息
                String country = bdLocation.getCountry();    //获取国家
                String province = bdLocation.getProvince();    //获取省份
                String city = bdLocation.getCity();    //获取城市
                String district = bdLocation.getDistrict();    //获取区县
                String street = bdLocation.getStreet();    //获取街道信息
                LocationCity=bdLocation.getCity();
                tv_location.setText(LocationCity);
            }
        });
        LOG.E("mLocationClient.start()");
        mLocationClient.start();
        LOG.E("mLocationClient.start()");
    }
    private android.os.Handler mHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Location:
                    tv_location.setText(LocationCity);
                    break;

            }
        }
    };

    private void UpdateCityInfo() {

        zProgressHUD = new ZProgressHUD(mActivity);
        zProgressHUD.setMessage("获取城市中……");
        zProgressHUD.show();
        String time = SharedUtil.getValueByKey(mActivity, "CityList_lastUpdateTime");
        if (time.equals("") || time.equals("null")) {
            time = "";
        }
        SharedUtil.getValueByKey(mActivity, "CityList_lastUpdateTime");
        HashMap<String, Object> mapUpdateCity = new HashMap<String, Object>();
        mapUpdateCity.put("lastUpdateTime", time);
        WebUtil.getInstance(mActivity).webRequest(Constants.UpdateCity, mapUpdateCity, new WebUtil.MyCallback() {
            @Override
            public void onSuccess(String result) {
                if (!result.equals("-1")) {
                    SortListActivity.Download download1 = new SortListActivity.Download(result);
                    download1.execute();
                } else {
                    if (zProgressHUD.isShowing() == true) {
                        zProgressHUD.dismiss();
                    }
                }
            }
        });

    }

//    public class getLocation extends Thread {
//
//        private volatile boolean flag = true;
//
//        public void stopTask() {
//            flag = false;
//        }
//
//        @Override
//        public void run() {
//            do {
//                LOG.D(flag+" 获取定位城市 "+LocationCity);
//                LocationCity = SharedUtil.getValue(mActivity, "city");
//                mHandler.sendEmptyMessage(Location);
//                SystemClock.sleep(1000);
//            } while (LocationCity.equals("") && flag);
//        }
//
//    }

    private void initViews() {
        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();

        pinyinComparator = new PinyinComparator();
        tv_location = (TextView) findViewById(R.id.tv_location);
//        gl=new getLocation();
//        gl.start();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                do {
//                    Log.d("Pan", "获取定位城市");
//                    LocationCity = SharedUtil.getValue(mActivity, "city");
//                    mHandler.sendEmptyMessage(Location);
//                    SystemClock.sleep(1000);
//                } while (LocationCity.equals(""));
//            }
//        }).start();


        sideBar = (SideBar) findViewById(R.id.sidrbar);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                gl.stopTask();
                ActivityUtil.FinishActivity(mActivity);
            }
        });
        tv_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Check()) {
                    setCity(getCityDate());
                } else {
                    ToastUtil.showShort(mActivity, LocationCity + "：该城市尚未列入服务");
                }
            }
        });
        dialog = (TextView) findViewById(R.id.dialog);
        sideBar.setTextView(dialog);

        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    sortListView.setSelection(position);
                }

            }
        });

        sortListView = (ListView) findViewById(R.id.country_lvcountry);
        sortListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //这里要利用adapter.getItem(position)来获取当前position所对应的对象
                setCity(((CityList) adapter.getItem(position)));
            }
        });


        mClearEditText = (EditText) findViewById(R.id.filter_edit);

        //根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setCity(CityList cl) {
        CityName = cl.getCityName();
        CityID = cl.getCityID();
        SharedUtil.setValue(mActivity, "CityName", CityName);
        SharedUtil.setValue(mActivity, "CityID", CityID);
        LOG.D("CityName="+CityName+"  CityID="+CityID);
        Bundle bundle = new Bundle();
        bundle.putString("cityname", CityName);
        bundle.putString("cityid", CityID);
        Intent intent = new Intent();
        intent.putExtras(bundle);
//        gl.stopTask();
        setResult(RESULT_OK, intent);
        ActivityUtil.FinishActivity(mActivity);
    }

    private boolean Check() {
        boolean ishave = false;
        for (CityList cl : SourceDateList) {
            if (cl.getCityName().equals(LocationCity)) {
                ishave = true;
            }
        }
        return ishave;
    }

    private CityList getCityDate() {
        CityList CL = new CityList();
        for (CityList cl : SourceDateList) {
            if (cl.getCityName().equals(LocationCity)) {
                CL = cl;
            }
        }
        return CL;
    }

    /**
     * 为ListView填充数据
     *
     * @param date
     * @return
     */
    private List<CityList> filledData(List<CityList> date) {
        List<CityList> mSortList = new ArrayList<CityList>();
        for (CityList list : date) {
            String pinyin = characterParser.getSelling(list.getCityName());
            String sortString = pinyin.substring(0, 1).toUpperCase();
            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                list.setPinYing(sortString.toUpperCase());
            } else {
                list.setPinYing("#");
            }
            mSortList.add(list);
        }
        return mSortList;

    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        filterStr = filterStr.toLowerCase();
        List<CityList> filterDateList = new ArrayList<CityList>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = SourceDateList;
        } else {
            filterDateList.clear();
            for (CityList citylist : SourceDateList) {
                String name = citylist.getCityName();
                if (name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())) {
                    filterDateList.add(citylist);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList);
    }

    private class Download extends AsyncTask<String, Integer, String> {

        private String result;

        public Download(String result) {
            this.result = result;
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                JSONObject jsonObject = new JSONObject(result);
                String ErrorCode = jsonObject.getString("ErrorCode");
                String LastUpdateTime = jsonObject.getString("LastUpdateTime");
                SharedUtil.setValue(mActivity, "CityList_lastUpdateTime", LastUpdateTime);
                Log.e("ErrorCode", ErrorCode);
                String data = jsonObject.getString("CityList");
                List<CityList> cityList = gson.fromJson(data, new TypeToken<List<CityList>>() {
                }.getType());

                cityBeanList = new ArrayList<CityList>();
                for (CityList list : cityList) {
                    cityBeanList.add(list);
                }
            } catch (final Exception e) {
                e.printStackTrace();

            }
            return "完成";
        }

        /**
         * a-z排序
         */
        @SuppressWarnings("rawtypes")
        Comparator comparator = new Comparator<CityList>() {
            @Override
            public int compare(CityList lhs, CityList rhs) {
                String a = lhs.getPinYing().substring(0, 1);
                String b = rhs.getPinYing().substring(0, 1);
                int flag = a.compareTo(b);
                if (flag == 0) {
                    return a.compareTo(b);
                } else {
                    return flag;
                }
            }
        };

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("完成")) {
                if (zProgressHUD.isShowing() == true) {
                    zProgressHUD.dismiss();
                }
                if (cityBeanList != null && cityBeanList.size() > 0) {
                    SourceDateList = filledData(cityBeanList);
                    LOG.D( SourceDateList.size() + "=城市列表=" + cityBeanList.size());
                    // 根据a-z进行排序源数据
                    Collections.sort(SourceDateList, pinyinComparator);
                    adapter = new SortAdapter(mActivity, SourceDateList);
                    sortListView.setAdapter(adapter);
//                    YWA.setCityBeanList(SourceDateList);
                }
            }

        }

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
//            gl.stopTask();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}