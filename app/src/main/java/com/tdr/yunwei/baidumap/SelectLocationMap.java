package com.tdr.yunwei.baidumap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
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
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.tdr.yunwei.R;
import com.tdr.yunwei.reviceandbroad.BaiDuSDKReceiver;
import com.tdr.yunwei.util.ActivityUtil;
import com.tdr.yunwei.util.LOG;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;


/**
 * Created by Administrator on 2017/7/17.
 */
@ContentView(R.layout.activity_select_location_map)
public class SelectLocationMap extends Activity implements BaiduMap.OnMapClickListener {
    @ViewInject(R.id.tv_submit)
    private TextView tv_submit;
    @ViewInject(R.id.image_back)
    private ImageView image_back;
    @ViewInject(R.id.tv_address)
    private TextView tv_address;


    public MyLocationListenner myListener = new MyLocationListenner();

    private MyLocationData locData;
    private int mCurrentDirection = 0;
    boolean isFirstLoc = true; // 是否首次定位


    // 定位相关
    LocationClient mLocClient;
    private MyLocationConfiguration.LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    MapView mMapView;
    BaiduMap mBaiduMap;
    Activity mActivity;
    private String Address = "";
    private Marker marker;
    private GeoCoder mSearch;
    private double LNG = 0.0;
    private double LAT = 0.0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        mActivity = this;
        ActivityUtil.AddActivity(mActivity);
        intimap();

        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ok(LAT + "", LNG + "",Address);
            }
        });
        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ok("", "", "");
            }
        });

    }

    private void intimap() {
        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setOnMapClickListener(this);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        option.setIsNeedAddress(true);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }


    private void goback(String lat, String lng, String address, String bdlat, String bdlng, String stationid) {
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

    private void addmarker(LatLng latLng) {
        mBaiduMap.clear();
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.mipmap.bs_selected);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(latLng)
                .icon(bitmap)
                .zIndex(9)
                .draggable(true);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);

        LNG = latLng.longitude;
        LAT = latLng.latitude;

        marker = (Marker) (mBaiduMap.addOverlay(option));
        mSearch = GeoCoder.newInstance();//实例化一个地理编码查询对象
        ReverseGeoCodeOption op = new ReverseGeoCodeOption();//设置反地理编码位置坐标
        op.location(latLng);

        mSearch.reverseGeoCode(op);
        mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(mActivity, "抱歉，未能找到结果", Toast.LENGTH_SHORT).show();
                    return;
                }

                Address = result.getAddress();
                tv_address.setText(Address);
                LOG.E("LNG= " + LNG);
                LOG.E("LAT= " + LAT);
                LOG.E("Address= " + Address);
            }

            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {


            }
        });
    }

    private void ok( String bdlat, String bdlng ,String address) {
        Intent intent = new Intent();
        intent.putExtra("Address", address);
        intent.putExtra("BaiduMapLAT", bdlat);
        intent.putExtra("BaiduMapLNG", bdlng);
        setResult(RESULT_OK, intent);
        ActivityUtil.FinishActivity(mActivity);
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }


            LAT = location.getLatitude();
            LOG.E("LAT=" + LAT);
            LNG = location.getLongitude();
            LOG.E("LNG=" + LNG);
            Address = location.getAddrStr();
            LOG.E("Address=" + Address);
            tv_address.setText(Address);
            locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;

                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
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
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        LOG.E("onMapClick");
        addmarker(latLng);
    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
    }
}
