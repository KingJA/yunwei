package com.tdr.yunwei.baidumap;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
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
import com.baidu.mapapi.model.inner.GeoPoint;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.navisdk.adapter.BNOuterLogUtil;
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.baidu.navisdk.adapter.BaiduNaviManager.NaviInitListener;
import com.baidu.navisdk.adapter.BaiduNaviManager.RoutePlanListener;
import com.tdr.yunwei.R;
import com.tdr.yunwei.reviceandbroad.BaiDuSDKReceiver;
import com.tdr.yunwei.util.ActivityUtil;
import com.tdr.yunwei.util.LOG;
import com.tdr.yunwei.util.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class BaiDuMapDeviceActivity extends Activity {
    public static List<Activity> activityList = new LinkedList<Activity>();
    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private MyLocationConfiguration.LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;

    MapView mMapView;
    BaiduMap mBaiduMap;
    boolean isFirstLoc = true; // 是否首次定位

    BaiDuSDKReceiver mReceiver;
    Activity mActivity;
    TextView txt_go;
    private String status="";
    private TextView tv_ok;
    private Double DeviceLAT,DeviceLNG;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidumapdevice);

        mActivity = this;
        ActivityUtil.AddActivity(mActivity);
        txt_go = (TextView) findViewById(R.id.txt_go);
        tv_ok= (TextView) findViewById(R.id.tv_ok);
        title();

        // 注册 SDK 广播监听者
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mReceiver = new BaiDuSDKReceiver();
        registerReceiver(mReceiver, iFilter);

        mGeoCoder = GeoCoder.newInstance();

        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
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
        option.setIsNeedAddress(true);
        mLocClient.setLocOption(option);
        mLocClient.start();


        DeviceLAT = Double.valueOf(getIntent().getStringExtra("DeviceLAT"));
        DeviceLNG = Double.valueOf(getIntent().getStringExtra("DeviceLNG"));
        status = getIntent().getStringExtra("status");
        address = getIntent().getStringExtra("DeviceAddress");
        if(status.equals("修改")){
            tv_ok.setVisibility(View.VISIBLE);
        }else{
            tv_ok.setVisibility(View.GONE);
        }
//        GPSLocation gpsBean = GPSUtil.gps_to_bd(DeviceLAT, DeviceLNG);
        LatLng deviceLATLNG = new LatLng(DeviceLAT,DeviceLNG);
        initOverMap(deviceLATLNG);



        txt_go.setText(address);
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GPSLocation gpsBean = GPSUtil.bd_to_gps(LAT, LNG);
//                GPSLocation gpsBean2= GPSUtil.gcj_encrypt(gpsBean.getLat(),gpsBean.getLon());
                LOG.D("getLat="+gpsBean.getLat());
                LOG.D("getLon="+gpsBean.getLon());
                LOG.D("LAT="+LAT);
                LOG.D("LNG="+LNG);
                LOG.D("address="+address);
                goback(gpsBean.getLat() + "",gpsBean.getLon() + "",address,LAT+"",LNG+"","");

            }
        });
        //txt_go.setVisibility(View.GONE);
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public boolean onMapPoiClick(MapPoi poi) {
                return true;
            }

            @Override
            public void onMapClick(LatLng point) {
                if(status.equals("修改")){
                    addNewPoint(point);
                }
            }
        });

    }
    private void LocationMarker(LatLng cenpt) {
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(cenpt);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

    }

    /**
     * 标题栏
     */
    TextView tv_title;

    private void title() {

        ImageView img_title = (ImageView) findViewById(R.id.image_back);
        img_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.FinishActivity(mActivity);
            }
        });
        tv_title = (TextView) findViewById(R.id.text_title);
        tv_title.setText("地图选安装点");

    }
    private void goback(String lat,String lng,String address,String bdlat,String bdlng,String stationid){
        Intent intent = new Intent();
        intent.putExtra("MapLAT", lat);
        intent.putExtra("MapLNG", lng);
        intent.putExtra("Address", address);
        intent.putExtra("BaiduMapLAT", bdlat);
        intent.putExtra("BaiduMapLNG", bdlng);
        intent.putExtra("StationID", stationid);
//        ToastUtil.showShort(mActivity,"返回Address="+address);
        setResult(RESULT_OK, intent);
        ActivityUtil.FinishActivity(mActivity);
    }
    /**
     * 定位手机的位置
     */


    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            if (isFirstLoc) {
                isFirstLoc = false;
            LAT = location.getLatitude();
            LNG = location.getLongitude();

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(LAT)
                    .longitude(LNG).build();
            mBaiduMap.setMyLocationData(locData);

            LatLng MyLATLNG = new LatLng(LAT, LNG);





                MapStatus.Builder builder = new MapStatus.Builder();

                LatLng latLng = new LatLng(DeviceLAT, DeviceLNG);

                double juli = DistanceUtil.getDistance(MyLATLNG, latLng);

                int mi = (int) juli;
                if(!status.equals("修改")){
                    tv_title.setText("设备距离您的位置大约" + mi + "米");
                }

                //两个坐标的中点

                LatLng centerLatLng = new LatLng((DeviceLAT + LAT) / 2, (DeviceLNG + LNG) / 2);


                if (mi <= 1000) {
                    builder.target(centerLatLng).zoom(20.0f); //初始化地图比例大小   值越大 比例越大 更详细
                }
                if (mi <= 2000 && mi > 1000) {
                    builder.target(centerLatLng).zoom(19.0f);
                }
                if (mi <= 3000 && mi > 2000) {
                    builder.target(centerLatLng).zoom(18.0f);
                }
                if (mi <= 4000 && mi > 3000) {
                    builder.target(centerLatLng).zoom(17.0f);
                }
                if (mi <= 5000 && mi > 4000) {
                    builder.target(centerLatLng).zoom(16.0f);
                }
                if (mi <= 7000 && mi > 5000) {
                    builder.target(centerLatLng).zoom(15.0f);
                }

                if (mi <= 10000 && mi > 7000) {
                    builder.target(centerLatLng).zoom(14.5f);
                }
                if (mi <= 15000 && mi > 10000) {
                    builder.target(centerLatLng).zoom(14.0f);
                }
                if (mi <= 20000 && mi > 15000) {
                    builder.target(centerLatLng).zoom(13.0f);
                }
                if (mi <= 30000 && mi > 20000) {
                    builder.target(centerLatLng).zoom(12.0f);
                }
                if (mi <= 40000 && mi > 30000) {
                    builder.target(centerLatLng).zoom(11.0f);
                }
                if (mi > 40000) {
                    builder.target(centerLatLng).zoom(10.0f);
                }
                if(mi>100000){
                    LocationMarker(latLng);
                }else{
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                }


            }


        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    double LAT, LNG;
    String  MyAddress = "";
    BitmapDescriptor bdpcs = BitmapDescriptorFactory.fromResource(R.mipmap.device);

    private void initOverMap(LatLng latLng) {
        MarkerOptions mok=  new MarkerOptions()
                .position(latLng)
                .icon(bdpcs);
        mBaiduMap.addOverlay(mok);

    }


    GeoCoder mGeoCoder;


    String address = "";

//    private String LATLNGToAddress(LatLng ptCenter) {
//
//        mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(ptCenter));
//        mGeoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
//            @Override
//            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
//
//            }
//
//            @Override
//            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
//                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
//                    ToastUtil.showShort(mActivity, "抱歉，未能找到结果");
//                    return;
//                }
//
//                address = result.getAddress();
//            }
//        });
//        return address;
//    }

    MarkerOptions marker_NP = null;
    BitmapDescriptor bdNp = BitmapDescriptorFactory.fromResource(R.mipmap.bs_selected);
    private Marker mMarkerBS;
    GeoCoder mSearch = null;
    private void addNewPoint(LatLng point) {


        mBaiduMap.clear();

        marker_NP = new MarkerOptions().position(point).icon(bdNp).zIndex(9).draggable(true);
        mMarkerBS = (Marker) mBaiduMap.addOverlay(marker_NP);

        mSearch = GeoCoder.newInstance();//实例化一个地理编码查询对象
        mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {


                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(mActivity, "抱歉，未能找到结果", Toast.LENGTH_SHORT).show();
                    return;
                }

                LNG=result.getLocation().longitude;
                LAT=result.getLocation().latitude;
                address = result.getAddress();
                txt_go.setText(address);
                LOG.E("LNG= "+LNG);
                LOG.E("LAT= "+LAT);
                LOG.E("Address= "+address);

            }

            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(mActivity, "抱歉，未能找到结果", Toast.LENGTH_SHORT).show();
                    return;
                }

                LNG=result.getLocation().longitude;
                LAT=result.getLocation().latitude;
                address = result.getAddress();
                txt_go.setText(address);
                LOG.E("LNG= "+LNG);
                LOG.E("LAT= "+LAT);
                LOG.E("Address= "+address);


            }
        });
        mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(point));
    }
    //导航

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

        unregisterReceiver(mReceiver);
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();

        if (mGeoCoder != null) {
            mGeoCoder.destroy();
        }

        bdpcs.recycle();
        mMapView = null;

        super.onDestroy();
    }
}