package com.tdr.yunwei.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tdr.yunwei.R;
import com.tdr.yunwei.adapter.PhotoListAdapter;
import com.tdr.yunwei.baidumap.SelectLocationMap;
import com.tdr.yunwei.bean.CityAreaBean;
import com.tdr.yunwei.bean.CityAreaPCSBean;
import com.tdr.yunwei.bean.DictionaryBean;
import com.tdr.yunwei.bean.ParamBean;
import com.tdr.yunwei.util.ActivityUtil;
import com.tdr.yunwei.util.Constants;
import com.tdr.yunwei.util.DBUtils;
import com.tdr.yunwei.util.LOG;
import com.tdr.yunwei.util.PhotoUtil;
import com.tdr.yunwei.util.PhotoUtils;
import com.tdr.yunwei.util.SharedUtil;
import com.tdr.yunwei.util.ToastUtil;
import com.tdr.yunwei.util.WebUtil;
import com.tdr.yunwei.util.ZProgressHUD;
import com.tdr.yunwei.view.Dialog.DialogUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Administrator on 2017/7/16.
 */
@ContentView(R.layout.activity_select_location)
public class SelectLocation extends Activity implements View.OnClickListener {

    @ViewInject(R.id.image_back)
    private ImageView image_back;
    @ViewInject(R.id.RL_DeviceType)
    private RelativeLayout RL_DeviceType;
    @ViewInject(R.id.TV_DeviceType)
    private TextView TV_DeviceType;
    @ViewInject(R.id.rv_PhotoList)
    private RecyclerView RV_PhotoList;
    @ViewInject(R.id.IV_Location)
    private ImageView IV_Location;
    @ViewInject(R.id.TV_LNG)
    private TextView TV_LNG;
    @ViewInject(R.id.TV_LAT)
    private TextView TV_LAT;
    @ViewInject(R.id.TV_Address)
    private TextView TV_Address;
    @ViewInject(R.id.RL_XQ)
    private RelativeLayout RL_XQ;
    @ViewInject(R.id.TV_XQ)
    private TextView TV_XQ;
    @ViewInject(R.id.RL_PCS)
    private RelativeLayout RL_PCS;
    @ViewInject(R.id.TV_PCS)
    private TextView TV_PCS;
    @ViewInject(R.id.ET_Remarks)
    private EditText ET_Remarks;
    @ViewInject(R.id.TV_Submit)
    private TextView TV_Submit;

    private Activity mActivity;
    private DbManager DB;

    private String LastCityID = "";
    private String AreaMC = "";
    private String AreaID = "";
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<Bitmap> drawablelist;
    private PhotoListAdapter PLA;
    private final int CAMERA_REQESTCODE = 2018;
    private final int MAP_RESULT = 100;
    private String PhotoName = "";
    private String Address = "";
    private String BaiduLAT = "";
    private String BaiduLNG = "";
    private ZProgressHUD zProgressHUD;
    private List<String> DeviceType = new ArrayList<String>();
    private Map<String, String> DeviceTypeMap = new HashMap<String, String>();
    private List<String> XQList = new ArrayList<String>();
    private String CityName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        ActivityUtil.AddActivity(mActivity);
        setClick();
        initdate();
    }

    private void setClick() {
        image_back.setOnClickListener(this);
        RL_DeviceType.setOnClickListener(this);
        IV_Location.setOnClickListener(this);
        RL_XQ.setOnClickListener(this);
        RL_PCS.setOnClickListener(this);
        TV_Submit.setOnClickListener(this);
    }

    private void initdate() {
        mActivity = this;
        DB = x.getDb(DBUtils.getDb());
        LastCityID = SharedUtil.getValue(mActivity, "CityID");
        CityName = SharedUtil.getValue(mActivity, "CityName");
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        RV_PhotoList.setLayoutManager(linearLayoutManager);
        drawablelist = new ArrayList<Bitmap>();
        RV_PhotoList.addItemDecoration(new RecyclerViewItemDecoration());
        PLA = new PhotoListAdapter(mActivity);
        PLA.setOnItemClickLitener(new PhotoListAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(String tage, int position) {
                PhotoName = "SelectLocation:" + position + ":" + tage;
                photo(PhotoName);
            }

            @Override
            public void onItemClearClick(View view, int position) {
                drawablelist.remove(position);
                PLA.UpDate(drawablelist);
            }
        });
        RV_PhotoList.setAdapter(PLA);
        getListType();
        getAreaList();
    }

    public void photo(String name) {
        PhotoUtils.takePicture(mActivity, name);
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory()+"/YunWei", name));
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//        startActivityForResult(intent, CAMERA_REQESTCODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LOG.E("requestCode=" + requestCode + "   resultCode=" + resultCode);
        switch (requestCode) {
            case PhotoUtils.CAMERA_REQESTCODE:
                if (resultCode == RESULT_OK) {
//                    File file = new File(Environment.getExternalStorageDirectory() + "/YunWei/" + PhotoName);
//                    Bitmap bm = PhotoUtil.getSmallBitmap(file.getAbsolutePath());
////                    Photo3ToBase64 = PhotoUtil.bitmapToString(file.getAbsolutePath(), mActivity);
//                    bm = PhotoUtil.thumbnailBitmap(bm);
//                    String Photoindex[] = PhotoName.split(":");
                    int degree = PhotoUtils.readPictureDegree(PhotoUtils.imageFile.getAbsolutePath());
                    Bitmap bitmap = PhotoUtils.rotaingImageView(degree,PhotoUtils.getBitmapFromFile(PhotoUtils.imageFile, 300, 300));
                    String Photoindex[] = PhotoUtils.mPicName.split(":");
                    String photoname = Photoindex[0];
                    String position = Photoindex[1];
                    String tage = Photoindex[2];
                    LOG.E("name:" + PhotoName);
                    for (String s : Photoindex) {
                        LOG.E("s:" + s);
                    }
                    if (tage.equals("Add")) {
                        drawablelist.add(bitmap);
                        LOG.E("添加照片");
                    } else {
                        drawablelist.set(Integer.parseInt(position), bitmap);
                    }
                    PLA.UpDate(drawablelist);
                }
                break;
            case MAP_RESULT:
                Address = data.getStringExtra("Address");
                BaiduLAT = data.getStringExtra("BaiduMapLAT");
                BaiduLNG = data.getStringExtra("BaiduMapLNG");
                LOG.E("Address=" + Address);
                LOG.E("BaiduLAT=" + BaiduLAT);
                LOG.E("BaiduLNG=" + BaiduLNG);
                TV_LAT.setText(BaiduLAT);
                TV_LNG.setText(BaiduLNG);
                TV_Address.setText(Address);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.RL_DeviceType:
                DialogUtil.ShowList(mActivity, TV_DeviceType, DeviceType, "基站类型");
                break;
            case R.id.IV_Location:
                Intent intent1 = new Intent(mActivity, SelectLocationMap.class);
                startActivityForResult(intent1, MAP_RESULT);
                break;
            case R.id.RL_XQ:
                DialogUtil.ShowList(mActivity, TV_XQ, XQList, "区域列表");
                break;
            case R.id.RL_PCS:
                AreaMC = TV_XQ.getText().toString();
                if (AreaMC.equals("")) {
                    ToastUtil.showShort(mActivity, "请先选择辖区");
                } else {
                    AreaID = getAreaID(AreaMC);
                    DialogUtil.ShowList(mActivity, TV_PCS, getPCSList(AreaID), AreaMC + "派出所列表");
                }
                break;
            case R.id.TV_Submit:
                if (CheckDate()) {
                    submit();
                }
                break;
        }
    }

    private boolean CheckDate() {
        String DeviceType = TV_DeviceType.getText().toString().trim();
        String lng = TV_LNG.getText().toString().trim();
        String lat = TV_LAT.getText().toString().trim();
        String address = TV_Address.getText().toString().trim();
        String xq =   TV_XQ.getText().toString().trim();
        String pcs =   TV_PCS.getText().toString().trim();
        if (DeviceType.equals("")) {
            ToastUtil.showShort(mActivity, "请选择基站类型");
            return false;
        }
        if (PLA.getPhotoNum() < 1) {
            ToastUtil.showShort(mActivity, "请拍摄至少一张现场照片");
            return false;
        }
        if(lng.equals("")||lat.equals("")||address.equals("")){
            ToastUtil.showShort(mActivity, "请通过地图选择安装位置");
            return false;
        }
        if (xq.equals("")) {
            ToastUtil.showShort(mActivity, "请选择所属辖区");
            return false;
        }
        if (pcs.equals("")) {
            ToastUtil.showShort(mActivity, "请选择所属单位");
            return false;
        }
        return true;
    }

    /**
     * 生成32位编码
     *
     * @return string
     */
    private String getUUID() {
        String uuid = UUID.randomUUID().toString().trim();
        return uuid;
    }

    private void submit() {
        zProgressHUD = new ZProgressHUD(mActivity);
        zProgressHUD.setMessage("正在提交数据请稍后……");
        zProgressHUD.show();
        String token = SharedUtil.getToken(mActivity);
        String DeviceType = DeviceTypeMap.get(TV_DeviceType.getText().toString().trim());
        String PCS = getPCSID(TV_PCS.getText().toString());
        String PhotoID1 = "";
        String PhotoID2 = "";
        String PhotoID3 = "";
        String Photo1 = "";
        String Photo2 = "";
        String Photo3 = "";
        for (int i = 0; i < drawablelist.size(); i++) {
            Bitmap bit = drawablelist.get(i);
            switch (i) {
                case 0:
                    PhotoID1 = getUUID();
                    Photo1 = PhotoUtil.bitmapToString(bit, mActivity);
                    break;
                case 1:
                    PhotoID2 = getUUID();
                    Photo2 = PhotoUtil.bitmapToString(bit, mActivity);
                    break;
                case 2:
                    PhotoID3 = getUUID();
                    Photo3 = PhotoUtil.bitmapToString(bit, mActivity);
                    break;
            }
        }
        String Remark = ET_Remarks.getText().toString().trim();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("accessToken", token);
        LOG.E("accessToken=" + token);

        map.put("stationType", DeviceType);
        LOG.E("stationType=" + DeviceType);

        map.put("cityID", LastCityID);
        LOG.E("cityID=" + LastCityID);

        map.put("cityName", CityName);
        LOG.E("cityName=" + CityName);

        map.put("areaName", AreaID);
        LOG.E("areaName=" + AreaID);

        map.put("PCSName", PCS);
        LOG.E("PCSName=" + PCS);

        map.put("lng", BaiduLNG);
        LOG.E("lng=" + BaiduLNG);

        map.put("lat", BaiduLAT);
        LOG.E("lat=" + BaiduLAT);

        map.put("address", Address);
        LOG.E("address=" + Address);

        map.put("PhotoID1", PhotoID1);
        LOG.E("PhotoID1=" + PhotoID1);

        map.put("PhotoID2", PhotoID2);
        LOG.E("PhotoID2=" + PhotoID2);

        map.put("PhotoID3", PhotoID3);
        LOG.E("PhotoID3=" + PhotoID3);

        map.put("Photo1", Photo1);
        map.put("Photo2", Photo2);
        map.put("Photo3", Photo3);

        map.put("remark", Remark);
        LOG.E("remark=" + PhotoID3);

        WebUtil.getInstance(mActivity).webRequest(Constants.AddSelectStationInfo, map, new WebUtil.MyCallback() {
            @Override
            public void onSuccess(String result) {
                zProgressHUD.dismiss();
                LOG.E("result=" + result);
                if (result.equals("-1")) {
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String ErrorCode = jsonObject.getString("ErrorCode");
                    String ErrorMsg = jsonObject.getString("ErrorMsg");
                    if (ErrorCode.equals("0")) {
                        ToastUtil.ErrorOrRight(mActivity, "选点成功！", 2);
                        finish();
                    } else {
                        ToastUtil.ErrorOrRight(mActivity, "选点失败！" + ErrorCode + ErrorMsg, 1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 获取设备类型
     *
     * @return
     */
    private void getListType() {
        try {
            List<DictionaryBean> list = DB.selector(DictionaryBean.class).where("DictionaryName", "=", "ZD_DEVICETYPE").findAll();
            if (list != null && list.size() > 0) {
                String DictionaryID = list.get(0).getDictionaryID();

                List<ParamBean> list2 = DB.selector(ParamBean.class).where("DictionaryID", "=", DictionaryID).findAll();
                if (list2 != null && list2.size() > 0) {
                    for (int i = 0; i < list2.size(); i++) {
                        DeviceType.add(list2.get(i).getParamValue());
                        DeviceTypeMap.put(list2.get(i).getParamValue(), list2.get(i).getParamCode());
                    }
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取区域
     */
    private void getAreaList() {
        String l = LastCityID.substring(4, 6);
        LOG.E("LastCityID=" + LastCityID);
        List<CityAreaBean> areaBeanList = null;

        try {
            if (l == "00" || l.equals("00")) {
                areaBeanList = DB.selector(CityAreaBean.class).where("FAreaID", "like", LastCityID.substring(0, 4) + "%").findAll();
            } else {
                areaBeanList = DB.selector(CityAreaBean.class).where("AreaID", "=", LastCityID).findAll();
            }
            if (areaBeanList == null) {
                areaBeanList = new ArrayList<CityAreaBean>();
            }
            for (int i = 0; i < areaBeanList.size(); i++) {
                XQList.add(areaBeanList.get(i).getAreaMC());
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
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

    private String getPCSID(String MC) {
        String ID = "";
        try {
            List<CityAreaPCSBean> list = DB.selector(CityAreaPCSBean.class).where("PCSMC", "=", MC).findAll();
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    LOG.E("AreaID:" + list.get(i).getAreaID() + "  PCSMC:" + list.get(i).getPCSMC());
                    if (AreaID.equals(list.get(i).getAreaID())) {
                        ID = list.get(i).getPCSID();
                    }
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return ID;
    }

    public class RecyclerViewItemDecoration extends RecyclerView.ItemDecoration {
        /**
         * @param outRect 边界
         * @param view    recyclerView ItemView
         * @param parent  recyclerView
         * @param state   recycler 内部数据管理
         */
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            //设定底部边距为1px
            outRect.set(10, 0, 10, 0);
        }

    }

}
