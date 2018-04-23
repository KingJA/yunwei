package com.tdr.yunwei.baidumap;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.baidu.mapapi.map.LogoPosition;
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
import com.tdr.yunwei.activity.HomeActivity;
import com.tdr.yunwei.bean.DeviceInfo;
import com.tdr.yunwei.reviceandbroad.BaiDuSDKReceiver;
import com.tdr.yunwei.util.ActivityUtil;
import com.tdr.yunwei.util.Constants;
import com.tdr.yunwei.util.DipPx;
import com.tdr.yunwei.util.LOG;
import com.tdr.yunwei.util.PermissionsActivity;
import com.tdr.yunwei.util.PermissionsCheckerUtil;
import com.tdr.yunwei.util.SharedUtil;
import com.tdr.yunwei.util.ToastUtil;
import com.tdr.yunwei.util.WebUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BaiDuMapActivity extends AppCompatActivity {

    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private MyLocationConfiguration.LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    private String Address = "";
    private String mAddress = "";
    double LAT = -1, LNG = -1;
    double mLAT = -1, mLNG = -1;
    BitmapDescriptor bdNp = BitmapDescriptorFactory.fromResource(R.mipmap.bs_selected);
    private Marker mMarkerBS;
    MarkerOptions marker_NP = null;

    private static final int accuracyCircleFillColor = 0xAAFFFF88;
    private static final int accuracyCircleStrokeColor = 0xAA00FF00;

    MapView mMapView;
    BaiduMap mBaiduMap;
    boolean isFirstLoc = true; // 是否首次定位
    BDLocation mLocation;
    LogoPosition locData = null;
    TextView txt_ok;
    BaiDuSDKReceiver mReceiver;
    Activity mActivity;
    PermissionsCheckerUtil pcu;
    private TextView tv_myloc;
    private ListView lv_device_location;
    private LinearLayout ll_device_list;
    private MyAdapter myadapter;
    private List<DeviceInfo> DeviceInfoList;
    private int index=-1;
    private String StationID="";
    private List<LatLng> pointlist;
    public static final int SHOWLISTVIEW = 1234;
    public  List<MarkerOptions> mkolist;
    private BitmapDescriptor bitmap1;
    private BitmapDescriptor bitmap2;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOWLISTVIEW:
                    myadapter.update(DeviceInfoList);
                    if(DeviceInfoList.size()>0){
                        ll_device_list.setVisibility(View.VISIBLE);
                    }else{
                        ll_device_list.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidumap);

        mActivity = BaiDuMapActivity.this;
        ActivityUtil.AddActivity(mActivity);
        DeviceInfoList=new ArrayList<DeviceInfo>();
        pointlist =new ArrayList<>();
//        ToastUtil.showShort(mActivity,HomeActivity.IsMapiIn==true?"IsMapiIn=true":"IsMapiIn=false");
        initview();
        pcu=new PermissionsCheckerUtil(this);
        title();
        TxtClick();
        // 注册 SDK 广播监听者
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mReceiver = new BaiDuSDKReceiver();
        registerReceiver(mReceiver, iFilter);


        // 地图初始化

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


        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;


        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public boolean onMapPoiClick(MapPoi poi) {
                mBaiduMap.hideInfoWindow();
                return false;
            }

            @Override
            public void onMapClick(LatLng point) {
                mBaiduMap.hideInfoWindow();
                addNewPoint(point);
            }
        });
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Markerclick(marker);
                return true;
            }
        });

    }
    private void LocationMarker(LatLng cenpt) {
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(cenpt);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

    }
    private void Markerclick(Marker marker) {
        LAT=mLAT;
        LNG=mLNG;
        Address=mAddress;
        tv_myloc.setText(Address);

        LatLng p = marker.getPosition();

        for (int i = 0; i <pointlist.size() ; i++) {
            if (p.longitude==pointlist.get(i).longitude&&p.latitude==pointlist.get(i).latitude) {
                StationID= DeviceInfoList.get(i).getStationID();
                index=i;
                UpdateMarker(marker);
                LocationMarker(p);

//                double latitude, longitude;
//                latitude = marker.getPosition().latitude;
//                longitude = marker.getPosition().longitude;
//
//                View view = LayoutInflater.from(this).inflate(R.layout.map_msg, null); //自定义气泡形状
//                TextView textView = (TextView) view.findViewById(R.id.tv_msg);
//                LatLng pt = new LatLng(latitude + 0.0004, longitude + 0.00005);
//                textView.setText(marker.getTitle());
//                InfoWindow mInfoWindow = new InfoWindow(view, pt, 0);
//                mBaiduMap.showInfoWindow(mInfoWindow);
                myadapter.notifyDataSetChanged();
//                getMarkerAddress(p);
            }
        }
    }
    private void getMarkerAddress(LatLng latlng){
        GeoCoder geoCoder = GeoCoder.newInstance();
        //设置反地理编码位置坐标
        ReverseGeoCodeOption op = new ReverseGeoCodeOption();
        op.location(latlng);
        //发起反地理编码请求(经纬度->地址信息)
        geoCoder.reverseGeoCode(op);
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                //获取点击的坐标地址
                LNG=result.getLocation().longitude;
                LAT=result.getLocation().latitude;
                Address = result.getAddress();
            }

            @Override
            public void onGetGeoCodeResult(GeoCodeResult arg0) {
            }
        });
    }

    private void initview(){
        mMapView = (MapView) findViewById(R.id.bmapView);
        tv_myloc = (TextView) findViewById(R.id.tv_myloc);
        txt_ok = (TextView) findViewById(R.id.txt_ok);
        lv_device_location= (ListView) findViewById(R.id.lv_device_location);
        ll_device_list= (LinearLayout) findViewById(R.id.ll_device_list);
        if(!HomeActivity.IsMapiIn){
            bitmap1= BitmapDescriptorFactory.fromResource(R.mipmap.device);
            bitmap2 = BitmapDescriptorFactory.fromResource(R.mipmap.device_select);
            mkolist=new ArrayList<MarkerOptions>();
            myadapter=new MyAdapter(DeviceInfoList);
            lv_device_location.setAdapter(myadapter);
            if(DeviceInfoList.size()==0){
                ll_device_list.setVisibility(View.GONE);
            }
        }else{
            ll_device_list.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();

        // 缺少权限时, 进入权限配置页面
        if (pcu.lacksPermissions()) {
            PermissionsActivity.startActivityForResult(this, PermissionsActivity.REQUEST_CODE,pcu.PERMISSIONS);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
        if (requestCode ==  PermissionsActivity.REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
            ActivityUtil.FinishActivity(mActivity);
        }
    }


    private void TxtClick() {
        txt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String city=SharedUtil.getValue(mActivity, "City");
                String address = tv_myloc.getText().toString();
                Log.e("LAT", "" + LAT);

                if (address.equals("")) {
                    ToastUtil.ShortCenter(mActivity, "请先选择地址");
                }
               else {
                    //ToastUtil.ShortCenter(mActivity, LAT+"");
                    GPSLocation gpsBean = GPSUtil.bd_to_gps(LAT, LNG);
                    GPSLocation gpsBean2= GPSUtil.gcj_encrypt(gpsBean.getLat(),gpsBean.getLon());
                    LOG.E("火星坐标Lat="+gpsBean2.getLat());
                    LOG.E("火星坐标Lon="+gpsBean2.getLon());
                    ok(gpsBean.getLat() + "",gpsBean.getLon() + "",address,LAT+"",LNG+"",StationID);

                }

            }
        });

        lv_device_location.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(index==position){
                    index=-1;
                    StationID="";
                }else{
                    StationID= DeviceInfoList.get(position).getStationID();
                    index=position;
                    LocationMarker(pointlist.get(position));
                }
                UpdateMarker(index);
                myadapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * +
     * 添加marker
     */
    private void setMarker(List<LatLng> point, List<DeviceInfo> deviceInfoList) {
        mBaiduMap.clear();
        //构建Marker列表
        for (int i = 0; i < point.size(); i++) {
            MarkerOptions mok=  new MarkerOptions()
                    .position(point.get(i))
                    .icon(bitmap1)
                    .title(deviceInfoList.get(i).getStationID());
            mkolist.add(mok);
        }

        //构建MarkerOption，用于在地图上添加Marker
        List<OverlayOptions> optionlist = new ArrayList<OverlayOptions>();
        for (int i = 0; i <mkolist.size() ; i++) {
            optionlist.add(mkolist.get(i));
        }
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlays(optionlist);
    }
    private void UpdateMarker(Marker marker) {
        mBaiduMap.clear();
        List<OverlayOptions> optionlist = new ArrayList<OverlayOptions>();
        for (int i = 0; i < mkolist.size(); i++) {
            if(marker.getTitle().equals(mkolist.get(i).getTitle())){
                mkolist.get(i).icon(bitmap2);
            }else{
                mkolist.get(i).icon(bitmap1);
            }
            optionlist.add(mkolist.get(i));
        }
        mBaiduMap.addOverlays(optionlist);
    }
    private void UpdateMarker(int index) {
        mBaiduMap.clear();
        List<OverlayOptions> optionlist = new ArrayList<OverlayOptions>();
        if(index==-1){
            for (int i = 0; i < mkolist.size(); i++) {
                mkolist.get(i).icon(bitmap1);
                optionlist.add(mkolist.get(i));
            }
        }else if(index==-2){
            for (int i = 0; i < mkolist.size(); i++) {
                optionlist.add(mkolist.get(i));
            }
        }else{
            for (int i = 0; i < mkolist.size(); i++) {
                if(index==i){
                    mkolist.get(i).icon(bitmap2);
                }else{
                    mkolist.get(i).icon(bitmap1);
                }
                optionlist.add(mkolist.get(i));
            }
        }
        mBaiduMap.addOverlays(optionlist);
    }

    /**
     * 标题栏
     */
    private void title() {

        ImageView img_title = (ImageView) findViewById(R.id.image_back);
        img_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ok("","","","","","");
            }
        });
        TextView tv_title = (TextView) findViewById(R.id.text_title);
        if(HomeActivity.IsMapiIn){
            tv_title.setText("设备地址");
        }else{
            tv_title.setText("地图选安装点");
        }

    }

    private void ok(String lat,String lng,String address,String bdlat,String bdlng,String stationid){
        Intent intent = new Intent();
        intent.putExtra("MapLAT", lat);
        intent.putExtra("MapLNG", lng);
        intent.putExtra("Address", address);
        intent.putExtra("BaiduMapLAT", bdlat);
        intent.putExtra("BaiduMapLNG", bdlng);
        intent.putExtra("StationID", stationid);
        setResult(RESULT_OK, intent);
        ActivityUtil.FinishActivity(mActivity);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            ok("","","","","","");

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    /**
     * 定位SDK监听函数
     */

    GeoCoder mSearch = null;

    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }

            if (isFirstLoc) {
                isFirstLoc = false;

                //刚开始地位的数据
                LAT = location.getLatitude();
                LNG = location.getLongitude();
                Address=location.getAddress().address.substring(2);

                mLAT = location.getLatitude();
                mLNG = location.getLongitude();
                mAddress=location.getAddress().address.substring(2);
                tv_myloc.setText(Address);
                if(!HomeActivity.IsMapiIn){
                    GetNearbyStationInfo();
                }
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(location.getRadius())
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(100).latitude(LAT)
                        .longitude(LNG).build();
                mBaiduMap.setMyLocationData(locData);


                LatLng ptCenter = new LatLng(LAT, LNG);
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ptCenter).zoom(15.0f);//初始化地图比例大小
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));


            }


        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }




    class MyAdapter extends BaseAdapter {
        private List<DeviceInfo> mList;

        public MyAdapter(List<DeviceInfo> list) {
            mList = list;
        }

        public void update(List<DeviceInfo> list) {
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
                convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_device_location, null);
                viewHolder.Title = (TextView) convertView.findViewById(R.id.tv_device_name);
                viewHolder.Location = (TextView) convertView.findViewById(R.id.tv_device_location);
                viewHolder.Distance = (TextView) convertView.findViewById(R.id.tv_device_distance);
                viewHolder.Selecter = (ImageView) convertView.findViewById(R.id.iv_device_selecter);
                convertView.setLayoutParams(new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DipPx.dip2px(mActivity,50)));
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            DeviceInfo DI= mList.get(position);
            if(index==position){
                viewHolder.Title.setTextColor(Color.parseColor("#80d1ff"));
                viewHolder.Location.setTextColor(Color.parseColor("#80d1ff"));
                viewHolder.Distance.setTextColor(Color.parseColor("#80d1ff"));
                viewHolder.Selecter.setVisibility(View.VISIBLE);
            }else{
                viewHolder.Title.setTextColor(Color.parseColor("#222222"));
                viewHolder.Location.setTextColor(Color.parseColor("#222222"));
                viewHolder.Distance.setTextColor(Color.parseColor("#222222"));
                viewHolder.Selecter.setVisibility(View.GONE);
            }
            viewHolder.Title.setText(DI.getStationName());
            viewHolder.Location.setText(DI.getAddress());
            String juli="";
            if(DI.getDistance().contains(".")){
                juli=DI.getDistance().substring(0,DI.getDistance().indexOf(".")+2)+"米";
            }else{
                juli=DI.getDistance()+"米";
            }
            viewHolder.Distance.setText(juli);
            return convertView;
        }

        class ViewHolder {
            TextView Title;
            TextView Location;
            TextView Distance;
            ImageView Selecter;
        }
    }

    private void GetNearbyStationInfo() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("accessToken", SharedUtil.getToken(mActivity));
        map.put("cityID", SharedUtil.getValue(mActivity, "CityID"));
        map.put("lng", LNG+"");
        map.put("lat", LAT+"");

        WebUtil.getInstance(mActivity).webRequest(Constants.GetNearbyStationInfo, map, new WebUtil.MyCallback() {

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
    private class Download extends AsyncTask<String, Integer, String> {

        private String result;

        public Download(String result) {
            this.result = result;

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray ja = jsonObject.getJSONArray("StationInfoList");
                DeviceInfo DI;
                if (ja.length() > 0) {
                    JSONObject jb;
                    LatLng LL;
                    for (int i = 0; i < ja.length(); i++) {
                        jb = new JSONObject(ja.get(i).toString());
                        DI = new DeviceInfo();
                        DI.setStationID(jb.getString("StationID"));
                        DI.setStationName(jb.getString("StationName"));
                        DI.setCityName(jb.getString("CityName"));
                        DI.setAreaName(jb.getString("AreaName"));
                        DI.setPCSName(jb.getString("PCSName"));
                        DI.setLNG(jb.getString("LNG"));
                        DI.setLAT(jb.getString("LAT"));
                        DI.setAddress(jb.getString("Address"));
                        DI.setStationType(jb.getString("StationType"));
                        DI.setStationClass(jb.getString("StationClass"));
                        DI.setDistance(jb.getString("Distance"));
                        LL=new LatLng(Double.parseDouble(DI.getLAT()),Double.parseDouble(DI.getLNG()));
                        DeviceInfoList.add(DI);
                        pointlist.add(LL);
                    }
                }
                LOG.D(DeviceInfoList.size() + "   数据量   " + ja.length());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "完成";
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("完成")) {
                if (DeviceInfoList != null && DeviceInfoList.size() > 0) {
                    mHandler.sendEmptyMessage(SHOWLISTVIEW);
                    setMarker(pointlist,DeviceInfoList);
                }
            }

        }

    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }



    @Override
    protected void onDestroy() {

        unregisterReceiver(mReceiver);
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        if (mSearch != null) {
            mSearch.destroy();
        }
        mMapView = null;

        super.onDestroy();
    }
    private void addNewPoint(LatLng point) {
        LOG.E("addNewPoint"+point.latitude+"    "+point.longitude);
//        index=-1;
//        StationID="";
        mBaiduMap.clear();
        if(mkolist!=null&&mkolist.size()!=0){
            UpdateMarker(-2);
        }
        if(myadapter!=null){
            myadapter.notifyDataSetChanged();
        }

        marker_NP = new MarkerOptions().position(point).icon(bdNp).zIndex(9).draggable(true);
        mMarkerBS = (Marker) mBaiduMap.addOverlay(marker_NP);

        mSearch = GeoCoder.newInstance();//实例化一个地理编码查询对象
//        ReverseGeoCodeOption op = new ReverseGeoCodeOption();//设置反地理编码位置坐标
//        op.location(point);
//        mSearch.reverseGeoCode(op);

        mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                LOG.E("onGetReverseGeoCodeResult");

                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(mActivity, "抱歉，未能找到结果", Toast.LENGTH_SHORT).show();
                    return;
                }

                LNG=result.getLocation().longitude;
                LAT=result.getLocation().latitude;
                Address = result.getAddress();
                tv_myloc.setText(Address);
                LOG.E("LNG= "+LNG);
                LOG.E("LAT= "+LAT);
                LOG.E("Address= "+Address);

            }

            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
                LOG.E("onGetGeoCodeResult");
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(mActivity, "抱歉，未能找到结果", Toast.LENGTH_SHORT).show();
                    return;
                }
                LNG=result.getLocation().longitude;
                LAT=result.getLocation().latitude;
                Address = result.getAddress();
                tv_myloc.setText(Address);
                LOG.E("LNG= "+LNG);
                LOG.E("LAT= "+LAT);
                LOG.E("Address= "+Address);



            }
        });

    }
}
