package com.tdr.yunwei.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.sortlistview.CharacterParser;
import com.sortlistview.PinyinComparator2;
import com.tdr.yunwei.R;
import com.tdr.yunwei.baidumap.BaiDuMapDeviceActivity;
import com.tdr.yunwei.baidumap.BaiduMapNavActivity;
import com.tdr.yunwei.bean.StationArea;
import com.tdr.yunwei.bean.StationCity;
import com.tdr.yunwei.bean.StationInfo;
import com.tdr.yunwei.bean.StationProgramme;
import com.tdr.yunwei.bean.StationUnit;
import com.tdr.yunwei.reviceandbroad.BaiDuSDKReceiver;
import com.tdr.yunwei.util.ActivityUtil;
import com.tdr.yunwei.util.Constants;
import com.tdr.yunwei.util.DBUtils;
import com.tdr.yunwei.util.LOG;
import com.tdr.yunwei.util.SharedUtil;
import com.tdr.yunwei.util.ToastUtil;
import com.tdr.yunwei.util.WebUtil;
import com.tdr.yunwei.util.ZbarUtil;
import com.zbar.lib.CaptureActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/4.
 */

public class DeviceMapActivity extends Activity {
    private LinearLayout ll_bottom_1;
    private LinearLayout ll_bottom_2;
    private LinearLayout ll_programme;
    private LinearLayout ll_area;
    private LinearLayout ll_company;
    private LinearLayout ll_install;
    private LinearLayout ll_navigation;

    private EditText et_search_city;
    private ListView lv_city;
    private ListView lv_drop_menu;
    private TextView tv_station_name;
    private TextView tv_distance;
    private TextView tv_station_location;
//    private TextView tv_navigation;
//    private TextView tv_install;

    private TextView tv_my_location;
    private TextView tv_programme;
    private TextView tv_area;
    private TextView tv_company;
    private TextView tv_my_location_zoom;

    private ImageView iv_programme;
    private ImageView iv_area;
    private ImageView iv_company;

    MapView mMapView;
    BaiduMap mBaiduMap;
    LocationClient mLocClient;
    BaiDuSDKReceiver mReceiver;
    public MyListenner myListener = new MyListenner();
    private MyLocationConfiguration.LocationMode mCurrentMode;
    private Activity mActivity;
    private String Address = "";
    double LAT = -1, LNG = -1;
    boolean isFirstLoc = true;
    /**
     * 城市列表
     */
    private List<StationCity> StationcityList;
    /**
     * 基站所属单位
     */
    private List<StationUnit> StationUnitList;
    /**
     * 基站所属方案
     */
    private List<StationProgramme> StationProgrammeList;
    /**
     * 基站所属辖区
     */
    private List<StationArea> StationAreaList;
    /**
     * 基站列表
     */
    private List<StationInfo> StationInfoList;
    /**
     * 基站数据
     */
    private StationInfo stationinfo;

    private CharacterParser characterParser;
    private PinyinComparator2 pinyinComparator;
    private myAdapter MA1;
    private myAdapter MA2;
    private List<String> menu1;
    private List<String> menu2;
    private boolean b1 = false;
    private boolean b2 = false;
    private boolean b3 = false;
    private String Scityid = "";
    private String Scityname = "";
    private String Sareaname = "";
    private String Spcsname = "";
    private String Sprogrammename = "";
    private String isNearby = "0";
    private ProgressBar pb_getdateing;
    private TextView tv_around_station;
    private String City = "";
    DbManager DB;
    private float Zoom = 19;
    private TextView tv_cancel_search;
    private ImageView iv_search_back;
//    public static final int INITMAPNAVI = 123;
//    private  Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case INITMAPNAVI:
//
//                    break;
//            }
//        }
//    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_map);
        mActivity = this;
        DB = x.getDb(DBUtils.getDb());
        StationcityList = new ArrayList<StationCity>();
        StationUnitList = new ArrayList<StationUnit>();
        StationProgrammeList = new ArrayList<StationProgramme>();
        StationAreaList = new ArrayList<StationArea>();
        StationInfoList = new ArrayList<StationInfo>();
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator2();
        GetStationCityList();
        initview();
        initmap();
        initListener();
//        BNOuterLogUtil.setLogSwitcher(true);
        Scityname = SharedUtil.getValue(mActivity, "CityName");
        Scityid = SharedUtil.getValue(mActivity, "CityID");

    }

    /**
     * 加载控件
     */
    private void initview() {
        pb_getdateing = (ProgressBar) findViewById(R.id.pb_getdateing);

        mMapView = (MapView) findViewById(R.id.bmap_search);
        et_search_city = (EditText) findViewById(R.id.et_search_city);
        iv_search_back = (ImageView) findViewById(R.id.iv_search_back);
        tv_my_location = (TextView) findViewById(R.id.tv_my_location);
        tv_my_location_zoom = (TextView) findViewById(R.id.tv_my_location_zoom);

        tv_cancel_search = (TextView) findViewById(R.id.tv_cancel_search);
        tv_station_name = (TextView) findViewById(R.id.tv_station_name);
        tv_distance = (TextView) findViewById(R.id.tv_distance);
        tv_station_location = (TextView) findViewById(R.id.tv_station_location);
//        tv_navigation = (TextView) findViewById(R.id.tv_navigation);
//        tv_install = (TextView) findViewById(R.id.tv_install);

        ll_programme = (LinearLayout) findViewById(R.id.ll_programme);
        ll_area = (LinearLayout) findViewById(R.id.ll_area);
        ll_company = (LinearLayout) findViewById(R.id.ll_company);

        ll_bottom_1 = (LinearLayout) findViewById(R.id.ll_bottom_1);
        ll_bottom_2 = (LinearLayout) findViewById(R.id.ll_bottom_2);
        ll_install = (LinearLayout) findViewById(R.id.ll_install);
        ll_navigation = (LinearLayout) findViewById(R.id.ll_navigation);

        lv_city = (ListView) findViewById(R.id.lv_city);
        lv_drop_menu = (ListView) findViewById(R.id.lv_drop_menu);

        tv_around_station = (TextView) findViewById(R.id.tv_around_station);

        tv_programme = (TextView) findViewById(R.id.tv_programme);
        tv_area = (TextView) findViewById(R.id.tv_area);
        tv_company = (TextView) findViewById(R.id.tv_company);

        iv_programme = (ImageView) findViewById(R.id.iv_programme);
        iv_area = (ImageView) findViewById(R.id.iv_area);
        iv_company = (ImageView) findViewById(R.id.iv_company);
        menu1 = new ArrayList<String>();
        menu2 = new ArrayList<String>();

        MA1 = new myAdapter(menu1);
        MA2 = new myAdapter(menu2);
        lv_city.setAdapter(MA1);
        lv_drop_menu.setAdapter(MA2);
    }

    /**
     * 初始化地图
     */
    private void initmap() {
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mReceiver = new BaiDuSDKReceiver();
        registerReceiver(mReceiver, iFilter);
        mMapView = (MapView) findViewById(R.id.bmap_search);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();

        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
//        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
//                        mCurrentMode, true, mCurrentMarker));

    }

    /**
     * 加载所有控件监听事件
     */
    private void initListener() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_search_city.getWindowToken(), 0);
        //周边基站按钮
        tv_around_station.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClearAllDate();
                Scityname = SharedUtil.getValue(mActivity, "CityName");
                Scityid = SharedUtil.getValue(mActivity, "CityID");
                isNearby = "1";
                GetStationInfo();
            }
        });
        //导航按钮
        ll_navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showShort(mActivity, "导航开启");
                ToastUtil.showShort(mActivity, "正在计算路线...");
                Intent intent = new Intent(mActivity, BaiduNaviActivity.class);
                intent.putExtra("SLAT", LAT + "");
                intent.putExtra("SLNG", LNG + "");
                intent.putExtra("ELAT", stationinfo.getLAT());
                intent.putExtra("ELNG", stationinfo.getLNG());
                LOG.D("SLAT=" + LAT + "\nSLNG=" + LNG + "\nELAT=" + stationinfo.getLAT() + "\nELNG=" + stationinfo.getLNG());
                startActivity(intent);

//                routeplanToNavi(LAT, LNG, Double.parseDouble(stationinfo.getLAT()), Double.parseDouble(stationinfo.getLNG()));
            }
        });
        //安装按钮
        ll_install.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ToastUtil.showShort(mActivity, "安装");
                HomeActivity.StationID = stationinfo.getStationID();
//                ToastUtil.showShort(mActivity,"StationID"+stationinfo.getStationID());
                Intent intent = new Intent(mActivity, CaptureActivity.class);
                mActivity.startActivityForResult(intent, 1002);
            }
        });
        //返回主界面
        iv_search_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //取消查询
        tv_cancel_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lv_city.setVisibility(View.GONE);
                lv_drop_menu.setVisibility(View.GONE);
                tv_cancel_search.setVisibility(View.GONE);
                et_search_city.setInputType(InputType.TYPE_NULL);
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager.isActive()) {
                    inputMethodManager.hideSoftInputFromWindow(et_search_city.getApplicationWindowToken(), 0);
                }
                ClearAllDate();
            }
        });
        //多选菜单——所属方案
        ll_programme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (b1) {
                    b1 = false;
                } else {
                    b1 = true;
                }
                b2 = false;
                b3 = false;
                menuclick(tv_programme, "所有方案");
            }
        });
        //多选菜单——所属辖区
        ll_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (b2) {
                    b2 = false;
                } else {
                    b2 = true;
                }
                b1 = false;
                b3 = false;
                menuclick(tv_area, "所属辖区");
            }
        });
        //多选菜单——所属单位
        ll_company.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (b3) {
                    b3 = false;
                } else {
                    b3 = true;
                }
                b1 = false;
                b2 = false;
                menuclick(tv_company, "所属单位");
            }
        });
        //城市列表点击事件
        lv_city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isNearby = "0";
                et_search_city.setText(menu1.get(position));
                tv_cancel_search.setVisibility(View.VISIBLE);
                Editable etext = et_search_city.getText();
                et_search_city.setSelection(etext.length());
                StationCity stationcity = StationcityList.get(position);
                Scityid = stationcity.getCityCode();
                Scityname = stationcity.getCityName();
                GetStationInfo();
                GetStationUnitList();
                lv_city.setVisibility(View.GONE);
                menu1.clear();
            }
        });
        //多选菜单列表点击事件
        lv_drop_menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemClick(position);
            }
        });
        //输入框点击事件
        et_search_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu1.clear();
                for (int i = 0; i < StationcityList.size(); i++) {
                    menu1.add(StationcityList.get(i).getCityName());
                }
                MA1.update(menu1);
                lv_city.setVisibility(View.VISIBLE);
            }
        });
        //输入框焦点监听
        et_search_city.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
//                    此处为得到焦点时的处理内容
                    menu1.clear();
                    for (int i = 0; i < StationcityList.size(); i++) {
                        menu1.add(StationcityList.get(i).getCityName());
                    }
                    MA1.update(menu1);
                    lv_city.setVisibility(View.VISIBLE);

                } else {
//                    此处为失去焦点时的处理内容
                    lv_city.setVisibility(View.GONE);
                }

            }

        });
        //输入框内容监听
        et_search_city.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                ToastUtil.showShort(mActivity, "Changed    " + s.toString());
                filterData(s.toString());
                if (!s.toString().equals("")) {
                    tv_cancel_search.setVisibility(View.VISIBLE);
                } else {
                    tv_cancel_search.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //点图点击事件
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            /**
             * 单击地图
             */
            public void onMapClick(LatLng point) {
//                currentPt = point;
                mBaiduMap.hideInfoWindow();
                MapClick();
            }

            /**
             * 单击地图中的POI点
             */
            public boolean onMapPoiClick(MapPoi poi) {
                mBaiduMap.hideInfoWindow();
//                currentPt = poi.getPosition();
                MapPoiClick();
                return false;
            }
        });
        //地图监听
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                Zoom = mapStatus.zoom;
                tv_my_location_zoom.setText(ActivityUtil.getZoom(mapStatus.zoom));
            }
        });
        //地图marker点 点击事件
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mBaiduMap.hideInfoWindow();
                markerClck(marker);
                return false;
            }
        });

    }

    /**
     * marker点击事件
     *
     * @param marker
     */
    private void markerClck(Marker marker) {
        LatLng ll = marker.getPosition();
//                ToastUtil.showShort(mActivity, "LAT=" + ll.latitude + "LON=" + ll.longitude);
        String lat = ll.latitude + "";
        String lon = ll.longitude + "";
        InfoWindow mInfoWindow;
        LatLng pt = null;
        double latitude, longitude;
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;

        View view = LayoutInflater.from(this).inflate(R.layout.map_msg, null); //自定义气泡形状
        TextView textView = (TextView) view.findViewById(R.id.tv_msg);
        pt = new LatLng(latitude + 0.0004, longitude + 0.00005);
        textView.setText(marker.getTitle());
        mInfoWindow = new InfoWindow(view, pt, 0);
        mBaiduMap.showInfoWindow(mInfoWindow);
        for (StationInfo si : StationInfoList) {
            if (lat.equals(si.getLAT()) && lon.equals(si.getLNG())) {
                stationinfo = si;
//                        ToastUtil.showShort(mActivity,"StationID"+stationinfo.getStationID());
                ll_bottom_1.setVisibility(View.GONE);
                ll_bottom_2.setVisibility(View.VISIBLE);
                tv_station_name.setText(si.getStationName());
                tv_station_location.setText(si.getAddress());
                LatLng ll1 = new LatLng(Double.parseDouble(si.getLAT()), Double.parseDouble(si.getLNG()));
                LatLng ll2 = new LatLng(LAT, LNG);
                int mi = (int) DistanceUtil.getDistance(ll1, ll2);
                tv_distance.setText(setdistance(mi));
                LocationMarker(ll);
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager.isActive()) {
                    inputMethodManager.hideSoftInputFromWindow(et_search_city.getApplicationWindowToken(), 0);
                }
            }
        }
    }
    /**
     * 菜单点击事件
     *
     * @param tv
     * @param name
     */
    private void menuclick(TextView tv, String name) {
        if (et_search_city.getText().toString().trim().equals("")) {
            ToastUtil.showShort(mActivity, "请输入城市");
        } else {
            OpenMenu();
            if (!b1 && !b2 && !b3 && !tv.getText().toString().trim().equals(name)) {
                GetStationInfo();
            }
        }
    }

    /**
     * 清空数据
     */
    private void ClearAllDate() {
        et_search_city.setText("");
        tv_programme.setText("所属方案");
        tv_area.setText("所属辖区");
        tv_company.setText("所属单位");

        b1 = false;
        b2 = false;
        b3 = false;

        tv_programme.setTextColor(Color.parseColor("#999999"));
        iv_programme.setBackgroundResource(R.mipmap.pull_unselected);

        tv_area.setTextColor(Color.parseColor("#999999"));
        iv_area.setBackgroundResource(R.mipmap.pull_unselected);

        tv_company.setTextColor(Color.parseColor("#999999"));
        iv_company.setBackgroundResource(R.mipmap.pull_unselected);

        StationUnitList.clear();
        StationProgrammeList.clear();
        StationAreaList.clear();
        StationInfoList.clear();
        stationinfo = null;

        ll_bottom_2.setVisibility(View.GONE);
        ll_bottom_1.setVisibility(View.VISIBLE);

        mBaiduMap.clear();
        Sareaname = "";
        Spcsname = "";
        Sprogrammename = "";
        isNearby = "0";

        Scityname = SharedUtil.getValue(mActivity, "CityName");
        Scityid = SharedUtil.getValue(mActivity, "CityID");
    }

    /**
     * 计算距离
     *
     * @param mi
     * @return
     */
    private String setdistance(int mi) {
        String distance = "";
        if (mi > 1000) {
            distance = mi / 1000 + "公里";
        } else {
            distance = mi + "米";
        }

        return distance;
    }

    /**
     * 菜单item点击事件
     */
    private void ItemClick(int position) {
        if (b1) {
            tv_programme.setText(menu2.get(position));
            if (tv_programme.getText().toString().trim().equals("所有方案")) {
                Sprogrammename = "";
            } else {
                Sprogrammename = StationProgrammeList.get(position).getProgrammeName();
            }
            b1 = false;
        } else if (b2) {
            tv_area.setText(menu2.get(position));
            if (tv_area.getText().toString().trim().equals("所有辖区")) {
                Sareaname = "";
            } else {
                Sareaname = StationAreaList.get(position).getAreaName();
            }
            b2 = false;
        } else if (b3) {
            tv_company.setText(menu2.get(position));
            if (tv_company.getText().toString().trim().equals("所有单位")) {
                Spcsname = "";
            } else {
                Spcsname = StationUnitList.get(position).getPCSName();
            }
            b3 = false;
        }
        setColor();
        setVisibility();
        GetStationInfo();
    }

    /**
     * 打开多选菜单
     */
    private void OpenMenu() {
        setColor();
        setVisibility();
        menu2.clear();
        LOG.D("3  " + StationProgrammeList.size() + "b1=" + b1 + "   " + StationAreaList.size() + "  b2=" + b2 + "   " + StationUnitList.size() + "  b3=" + b3);
        if (b1) {
            for (int j = 0; j < StationProgrammeList.size(); j++) {
                menu2.add(StationProgrammeList.get(j).getProgrammeName());
            }
        } else if (b2) {
            for (int j = 0; j < StationAreaList.size(); j++) {
                menu2.add(StationAreaList.get(j).getAreaName());
            }
        } else if (b3) {
            for (int j = 0; j < StationUnitList.size(); j++) {
                menu2.add(StationUnitList.get(j).getPCSName());
            }
        }
        LOG.D("菜单数据量=" + menu2.size());
        MA2.update(menu2);

    }

    /**
     * 设置菜单字体头颜色及图表
     */
    private void setColor() {
        if (b1) {
            tv_programme.setTextColor(Color.parseColor("#222222"));
            iv_programme.setBackgroundResource(R.mipmap.pull_selected);

            tv_area.setTextColor(Color.parseColor("#999999"));
            iv_area.setBackgroundResource(R.mipmap.pull_unselected);

            tv_company.setTextColor(Color.parseColor("#999999"));
            iv_company.setBackgroundResource(R.mipmap.pull_unselected);

        } else if (b2) {
            tv_programme.setTextColor(Color.parseColor("#999999"));
            iv_programme.setBackgroundResource(R.mipmap.pull_unselected);

            tv_area.setTextColor(Color.parseColor("#222222"));
            iv_area.setBackgroundResource(R.mipmap.pull_selected);

            tv_company.setTextColor(Color.parseColor("#999999"));
            iv_company.setBackgroundResource(R.mipmap.pull_unselected);
        } else if (b3) {
            tv_programme.setTextColor(Color.parseColor("#999999"));
            iv_programme.setBackgroundResource(R.mipmap.pull_unselected);

            tv_area.setTextColor(Color.parseColor("#999999"));
            iv_area.setBackgroundResource(R.mipmap.pull_unselected);

            tv_company.setTextColor(Color.parseColor("#222222"));
            iv_company.setBackgroundResource(R.mipmap.pull_selected);
        }
    }

    /**
     * 设置菜单隐藏显示
     */
    private void setVisibility() {
        if (b1 || b2 || b3) {
            lv_drop_menu.setVisibility(View.VISIBLE);
        } else {
            lv_drop_menu.setVisibility(View.GONE);

            if (tv_programme.getText().equals("所属方案")) {
                tv_programme.setTextColor(Color.parseColor("#999999"));
                iv_programme.setBackgroundResource(R.mipmap.pull_unselected);
            } else {
                tv_programme.setTextColor(Color.parseColor("#222222"));
                iv_programme.setBackgroundResource(R.mipmap.pull_used);
            }

            if (tv_area.getText().equals("所属辖区")) {
                tv_area.setTextColor(Color.parseColor("#999999"));
                iv_area.setBackgroundResource(R.mipmap.pull_unselected);
            } else {
                tv_area.setTextColor(Color.parseColor("#222222"));
                iv_area.setBackgroundResource(R.mipmap.pull_used);
            }

            if (tv_company.getText().equals("所属单位")) {
                tv_company.setTextColor(Color.parseColor("#999999"));
                iv_company.setBackgroundResource(R.mipmap.pull_unselected);
            } else {
                tv_company.setTextColor(Color.parseColor("#222222"));
                iv_company.setBackgroundResource(R.mipmap.pull_used);
            }

        }
    }

    /**
     * 添加marker
     */
    private void setMarker(LatLng point) {
        LOG.D("LAT=" + point.latitude + "LON=" + point.longitude);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.device);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);

    }

//    /**
//     * 添加marker
//     */
//    private void setMarker(List<LatLng> point) {
//
//        //构建Marker图标
//        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.device);
//        //构建MarkerOption，用于在地图上添加Marker
//        List<OverlayOptions> optionlist = new ArrayList<OverlayOptions>();
//        for (LatLng LL : point) {
//            optionlist.add(new MarkerOptions().position(LL).icon(bitmap));
//        }
//        //在地图上添加Marker，并显示
//        mBaiduMap.addOverlays(optionlist);
//        LocationMarker(point.get(0));
//    }

    /**
     * +
     * 添加marker
     */
    private void setMarker(List<StationInfo> List) {
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.device);
        //构建MarkerOption，用于在地图上添加Marker
        List<OverlayOptions> optionlist = new ArrayList<OverlayOptions>();
        List<LatLng> LL = new ArrayList<LatLng>();
        for (int i = 0; i < List.size(); i++) {
            LatLng ll = new LatLng(Double.parseDouble(List.get(i).getLAT()),
                    Double.parseDouble(List.get(i).getLNG()));
            optionlist.add(new MarkerOptions().position(ll).title(List.get(i).getStationName()).icon(bitmap));
            LL.add(ll);
        }
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlays(optionlist);
        LocationMarker(LL.get(0));
    }

    /**
     * 带入POI点点击
     */
    private void MapPoiClick() {
//        ToastUtil.showShort(mActivity, "MapPoiClick");
    }

    /**
     * 地图空白点击
     */
    private void MapClick() {
        stationinfo = new StationInfo();
        ll_bottom_2.setVisibility(View.GONE);
        ll_bottom_1.setVisibility(View.VISIBLE);
    }

    /**
     * 定位到marker
     *
     * @param cenpt
     */
    private void LocationMarker(LatLng cenpt) {
//        //定义地图状态
//        MapStatus mMapStatus = new MapStatus.Builder()
//                .target(cenpt)
//                .zoom(Zoom)
//                .build();
//        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
//        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
//        //改变地图状态
//        mBaiduMap.setMapStatus(mMapStatusUpdate);

        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(cenpt);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

    }

    /**
     * 地图监听
     */
    public class MyListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            LAT = location.getLatitude();
            LNG = location.getLongitude();
            City = location.getCity();
            Address = "";
//            LOG.D(location.getAddress().address==null?"location空":"location非空");
            Address = location.getAddress().address;
            tv_my_location.setText(Address);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(Zoom);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }

    }

    /**
     * 适配器
     */
    class myAdapter extends BaseAdapter {
        private List<String> mList;

        public myAdapter(List<String> list) {
            mList = list;
        }

        public void update(List<String> list) {
            this.mList = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_search_city, null);
                viewHolder.tvmsg = (TextView) convertView.findViewById(R.id.tv_city_name);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tvmsg.setText(mList.get(position));
            return convertView;
        }

        class ViewHolder {
            TextView tvmsg;
        }
    }

    /**
     * 列表查询
     */
    private void filterData(String filterStr) {
        LOG.D("输入 " + filterStr);
        filterStr = filterStr.toLowerCase();
        List<StationCity> filterDateList = new ArrayList<StationCity>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = StationcityList;
        } else {
            filterDateList.clear();
            for (StationCity citylist : StationcityList) {
                String name = citylist.getCityName();
                if (name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())) {
                    filterDateList.add(citylist);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        menu1.clear();
        for (StationCity s : filterDateList) {
            menu1.add(s.getCityName());
        }
        MA1.update(menu1);
    }

    /**
     * 列表排序
     */
    private List<StationCity> filledData(List<StationCity> date) {
        List<StationCity> mSortList = new ArrayList<StationCity>();
        for (StationCity list : date) {
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
     * 获取城市列表
     */
    private void GetStationCityList() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("accessToken", SharedUtil.getToken(mActivity));
        map.put("cityID", SharedUtil.getValue(mActivity, "CityID"));
        String time = SharedUtil.getValue(mActivity, "UpdateStationCity_lastUpdateTime");
        if (time.equals("")) {
            time = Constants.DefaultTime;
        }
        map.put("lastUpdateTime", time);

        WebUtil.getInstance(mActivity).webRequest(Constants.UpdateStationCity, map, new WebUtil.MyCallback() {

            @Override
            public void onSuccess(String result) {
                if (result.equals("-1")) {
                    return;
                }
                Download d = new Download(result);
                d.execute();
            }
        });

    }

    /**
     * 获取所属单位
     */
    private void GetStationUnitList() {
        pb_getdateing.setVisibility(View.VISIBLE);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("accessToken", SharedUtil.getToken(mActivity));
        map.put("cityID", Scityid);
        String time = SharedUtil.getValue(mActivity, "UpdateStationUnit_lastUpdateTime");
        if (time.equals("")) {
            time = Constants.DefaultTime;
        }
        map.put("lastUpdateTime", time);

        WebUtil.getInstance(mActivity).webRequest(Constants.UpdateStationUnit, map, new WebUtil.MyCallback() {

            @Override
            public void onSuccess(String result) {
                if (result.equals("-1")) {
                    return;
                }
                LOG.LE("UpdateStationUnit="+result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray ja = jsonObject.getJSONArray("StationUnitList");
                    StationUnit SU = new StationUnit();
                    SU.setPCSName("所有单位");
                    StationUnitList.clear();
                    StationUnitList.add(SU);
                    if (ja.length() > 0) {
                        JSONObject jb;
                        for (int i = 0; i < ja.length(); i++) {
                            jb = new JSONObject(ja.get(i).toString());
                            SU = new StationUnit();
                            SU.setCityCode(jb.getString("CityCode"));
                            SU.setCityName(jb.getString("CityName"));
                            SU.setPCSName(jb.getString("PCSName"));
                            SU.setIsValid(jb.getString("IsValid"));
                            SU.setLastUpdateTime(jb.getString("LastUpdateTime"));
                            StationUnitList.add(SU);
                        }
                    }
                    LOG.D(StationUnitList.size() + "   数据量   " + ja.length());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                GetStationAreaList();
            }
        });

    }

    /**
     * 获取所属辖区
     */
    private void GetStationAreaList() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("accessToken", SharedUtil.getToken(mActivity));
        map.put("cityID", Scityid);
        String time = SharedUtil.getValue(mActivity, "UpdateStationArea_lastUpdateTime");
        if (time.equals("")) {
            time = Constants.DefaultTime;
        }
        map.put("lastUpdateTime", time);

        WebUtil.getInstance(mActivity).webRequest(Constants.UpdateStationArea, map, new WebUtil.MyCallback() {

            @Override
            public void onSuccess(String result) {
                if (result.equals("-1")) {
                    return;
                }
                LOG.LE("UpdateStationArea="+result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray ja = jsonObject.getJSONArray("StationAreaList");
                    StationArea SA = new StationArea();
                    SA.setAreaName("所有辖区");
                    StationAreaList.clear();
                    StationAreaList.add(SA);
                    if (ja.length() > 0) {
                        JSONObject jb;
                        for (int i = 0; i < ja.length(); i++) {
                            jb = new JSONObject(ja.get(i).toString());
                            SA = new StationArea();
                            SA.setCityCode(jb.getString("CityCode"));
                            SA.setCityName(jb.getString("CityName"));
                            SA.setAreaName(jb.getString("AreaName"));
                            SA.setIsValid(jb.getString("IsValid"));
                            SA.setLastUpdateTime(jb.getString("LastUpdateTime"));
                            StationAreaList.add(SA);
                        }
                    }
                    LOG.D(StationAreaList.size() + "   数据量   " + ja.length());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                GetStationProgrammeList();
            }
        });

    }

    /**
     * 获取所属方案
     */
    private void GetStationProgrammeList() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("accessToken", SharedUtil.getToken(mActivity));
        map.put("cityID", Scityid);
        String time = SharedUtil.getValue(mActivity, "UpdateStationProgramme_lastUpdateTime");
        if (time.equals("")) {
            time = Constants.DefaultTime;
        }
        map.put("lastUpdateTime", time);

        WebUtil.getInstance(mActivity).webRequest(Constants.UpdateStationProgramme, map, new WebUtil.MyCallback() {

            @Override
            public void onSuccess(String result) {
                if (result.equals("-1")) {
                    return;
                }
                LOG.LE("UpdateStationProgramme="+result);
                try {
//                            "CityCode":"330700",
//                            "CityName":"义乌监控",
//                            "ProgrammeName":"义乌基站布点",
//                            "IsValid":"1",
//                            "LastUpdateTime":
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray ja = jsonObject.getJSONArray("StationProgrammeList");
                    StationProgramme SP = new StationProgramme();
                    SP.setProgrammeName("所有方案");
                    StationProgrammeList.clear();
                    StationProgrammeList.add(SP);
                    if (ja.length() > 0) {
                        JSONObject jb;
                        for (int i = 0; i < ja.length(); i++) {
                            jb = new JSONObject(ja.get(i).toString());
                            SP = new StationProgramme();
                            SP.setCityCode(jb.getString("CityCode"));
                            SP.setCityName(jb.getString("CityName"));
                            SP.setProgrammeName(jb.getString("ProgrammeName"));
                            SP.setIsValid(jb.getString("IsValid"));
                            SP.setLastUpdateTime(jb.getString("LastUpdateTime"));
                            StationProgrammeList.add(SP);
                        }
                    }
                    LOG.D(StationProgrammeList.size() + "   数据量   " + ja.length());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pb_getdateing.setVisibility(View.GONE);
            }
        });

    }

    /**
     * 获取点位信息
     */
    private void GetStationInfo() {
        ToastUtil.showShort(mActivity, "开始查询");

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("accessToken", SharedUtil.getToken(mActivity));
        map.put("cityID", Scityid);
        map.put("cityName", Scityname);
        map.put("areaName", Sareaname);
        map.put("pcsName", Spcsname);
        map.put("programmeName", Sprogrammename);
        map.put("lng", LNG + "");
        map.put("lat", LAT + "");
        map.put("isNearby", isNearby);

        WebUtil.getInstance(mActivity).webRequest(Constants.GetStationInfo, map, new WebUtil.MyCallback() {

            @Override
            public void onSuccess(String result) {
                if (result.equals("-1")) {
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray ja = jsonObject.getJSONArray("StationInfoList");
                    mBaiduMap.clear();
                    if (ja.length() > 0) {
                        JSONObject jb;
                        StationInfo SI;

                        StationInfoList.clear();
                        for (int i = 0; i < ja.length(); i++) {
                            jb = new JSONObject(ja.get(i).toString());
                            SI = new StationInfo();
                            SI.setStationID(jb.getString("StationID"));
                            SI.setStationName(jb.getString("StationName"));
                            SI.setCityName(jb.getString("CityName"));
                            SI.setAreaName(jb.getString("AreaName"));
                            SI.setPCSName(jb.getString("PCSName"));
                            SI.setLNG(jb.getString("LNG"));
                            SI.setLAT(jb.getString("LAT"));
                            SI.setAddress(jb.getString("Address"));
                            SI.setStationType(jb.getString("StationType"));
                            SI.setStationClass(jb.getString("StationClass"));
                            SI.setDistance(jb.getString("Distance"));
                            SI.setProgrammeName(jb.getString("ProgrammeName"));

                            StationInfoList.add(SI);
                        }
                        setMarker(StationInfoList);
                    }
                    if (StationInfoList.size() == 0 && et_search_city.getText().toString().trim().equals("")) {
                        ToastUtil.showLong(mActivity, "您周边暂无未安装基站");

                    } else {
                        ToastUtil.showLong(mActivity, "查询到" + ja.length() + "个未安装基站");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    /**
     * 解析城市列表
     */
    private class Download extends AsyncTask<String, Integer, String> {

        private String result;
        List<StationCity> cityBeanList;

        public Download(String result) {
            this.result = result;
            cityBeanList = new ArrayList<StationCity>();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray ja = jsonObject.getJSONArray("StationCityList");
                if (ja.length() > 0) {
                    JSONObject jb;
                    StationCity SC;
                    for (int i = 0; i < ja.length(); i++) {
                        jb = new JSONObject(ja.get(i).toString());
                        SC = new StationCity();
                        SC.setCityCode(jb.getString("CityCode"));
                        SC.setCityName(jb.getString("CityName"));
                        SC.setIsValid(jb.getString("IsValid"));
                        SC.setLastUpdateTime(jb.getString("LastUpdateTime"));
                        cityBeanList.add(SC);
                    }
                }
                LOG.D("数据量   " + ja.length());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "完成";
        }

        /**
         * a-z排序
         */
        @SuppressWarnings("rawtypes")
        Comparator comparator = new Comparator<StationCity>() {
            @Override
            public int compare(StationCity lhs, StationCity rhs) {
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
                if (cityBeanList != null && cityBeanList.size() > 0) {
                    StationcityList = filledData(cityBeanList);
                    Collections.sort(StationcityList, pinyinComparator);
                    LOG.D("Stationcity=" + StationcityList.size());
                }
                if (StationcityList == null) {
                    StationcityList = new ArrayList<>();
                }
            }

        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (lv_city.isShown()) {
//                ToastUtil.showShort(mActivity, "show");
                lv_city.setVisibility(View.GONE);
                et_search_city.setInputType(InputType.TYPE_NULL);
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager.isActive()) {
                    inputMethodManager.hideSoftInputFromWindow(et_search_city.getApplicationWindowToken(), 0);
                }
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case 1002:
                if (resultCode == mActivity.RESULT_OK) {

                    String inputtype = data.getStringExtra("inputtype");
                    //扫描
                    if (inputtype.equals("zbar")) {
                        String scanResult = data.getStringExtra("result");
                        String strZbar = ZbarUtil.DeviceZbar(mActivity, scanResult);
                        if (!strZbar.equals("")) {

                            String[] device = strZbar.split(",");
                            String deviceremark = ZbarUtil.getSubRemark(DB, device[1]);
                            if (!deviceremark.equals(HomeActivity.Remark)) {
                                ToastUtil.ErrorOrRight(mActivity, "扫描设备与[" + HomeActivity.Remark + "]不匹配,无法安装", 1);

                            } else {

                                Intent intent = new Intent(mActivity, DeviceAddActivity.class);
                                intent.putExtra("DeviceCode", device[0]);
                                intent.putExtra("DeviceType", device[1]);
                                intent.putExtra("DeviceRemark", deviceremark);
                                intent.putExtra("status", "安装");
                                mActivity.startActivity(intent);
                                //DeviceIsAdd(device[0], device[1], deviceremark);

                            }
                        }
                    }

                    //手动输入
                    if (inputtype.equals("shoudong")) {

                        String DeviceCode = data.getStringExtra("DeviceCode");
                        Intent intent = new Intent(mActivity, DeviceAddActivity.class);
                        intent.putExtra("DeviceCode", DeviceCode);
                        intent.putExtra("DeviceType", HomeActivity.type);
                        intent.putExtra("DeviceRemark", HomeActivity.Remark);
                        intent.putExtra("status", "安装");
                        LOG.D("DeviceCode=" + DeviceCode);
                        LOG.D("type=" + HomeActivity.type);
                        LOG.D("DeviceRemark=" + HomeActivity.Remark);
                        mActivity.startActivity(intent);
                    }

                }
                break;

        }
    }
}
