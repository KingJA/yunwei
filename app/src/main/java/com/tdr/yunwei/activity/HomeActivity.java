package com.tdr.yunwei.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;

import com.tdr.yunwei.R;
import com.tdr.yunwei.YunWeiApplication;
import com.tdr.yunwei.bean.DASBean;
import com.tdr.yunwei.bean.DictionaryBean;
import com.tdr.yunwei.bean.ParamBean;
import com.tdr.yunwei.bean.RepairOrderBean;
import com.tdr.yunwei.fragment.OperationsFM;
import com.tdr.yunwei.fragment.OrderFM;
import com.tdr.yunwei.fragment.SettingFM;
import com.tdr.yunwei.fragment.StatisticalFM;
import com.tdr.yunwei.util.ActivityUtil;
import com.tdr.yunwei.util.DBUtils;
import com.tdr.yunwei.util.LOG;
import com.tdr.yunwei.util.MatchUtil;
import com.tdr.yunwei.util.NewOrderUtil;
import com.tdr.yunwei.util.SharedUtil;
import com.tdr.yunwei.util.ToastUtil;
import com.tdr.yunwei.util.ZbarUtil;
import com.tdr.yunwei.view.CustomRadioGroup;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.List;

/**
 * Created by Administrator on 2016/4/19.
 */
public class HomeActivity extends FragmentActivity {
    private CustomRadioGroup footer;// 仿微信底部菜单
    private int[] itemImage = {
            R.mipmap.home_footer_peranation_of,
            R.mipmap.home_footer_job_off,
            R.mipmap.home_footer_statistic_off,
            R.mipmap.home_footer_setup_off};
    private int[] itemCheckedImage = {
            R.mipmap.home_footer_peranation_on,
            R.mipmap.home_footer_job_on,
            R.mipmap.home_footer_statistic_on,
            R.mipmap.home_footer_setup_on};

    private Fragment fragment;

    private OperationsFM operationsFM;
    private OrderFM orderFM;
    private StatisticalFM statisticalFM;
    private SettingFM settingFM;

    private Activity mActivity;
    DbManager DB;
    NewOrderUtil newOrder;
    private YunWeiApplication YWA;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mActivity = HomeActivity.this;
        YWA = YunWeiApplication.getInstance();
//        DB = YWA.getDB();
        DB= x.getDb(DBUtils.getDb());
//        if(YWA.getCityBeanList()==null||YWA.getCityBeanList().size()==0){
//            getcityliist();
//        }

        ActivityUtil.AddActivity(mActivity);
        getRolePowers();

        MatchUtil.IsHasSD(mActivity);

        newOrder = new NewOrderUtil(mActivity);
        Log.e("Pan", "——————————更新工单1——————————");
        newOrder.getNewOrderList();
        get();

    }


    private void get() {
        List<DictionaryBean> list = null;
        List<ParamBean> list2 = null;
        List<DASBean> list3 = null;
        try {
            list = DB.findAll(DictionaryBean.class);
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    Log.e("DictionaryBean", list.get(i).getDictionaryID() + "//////" + list.get(i).getDictionaryName());
                }
            }
            list2 = DB.findAll(ParamBean.class);
            if (list2 != null) {
                for (int i = 0; i < list2.size(); i++) {
                    Log.e("ParamBean", list2.get(i).getDictionaryID() + "//////" + list2.get(i).getParamValue() + "----"
                            + list2.get(i).getParamCode());
                }
            }
            list3 = DB.findAll(DASBean.class);
            if (list3 != null) {
                for (int i = 0; i < list3.size(); i++) {
                    Log.e("DASBean", list3.get(i).getAreaID() + "//////" + list3.get(i).getSystemID() + "//////" + list3.get(i).getDeviceTypeID());
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
//        newOrder.getNewOrderList();
    }


    public boolean role1 = false, role2 = false, role3 = false, role4 = false;

    private void getRolePowers() {

        if (SharedUtil.getValue(mActivity, "RolePowers").equals("")) {

        } else {


            String[] RolePowers = SharedUtil.getValue(mActivity, "RolePowers").split(",");

            for (int i = 0; i < RolePowers.length; i++) {


                int role = Integer.valueOf(RolePowers[i]);
                if (role == 1) {
                    role1 = true;
                }
                if (role == 2) {
                    role2 = true;
                }
                if (role == 3) {
                    role3 = true;
                }
                if (role == 4) {
                    role4 = true;
                }
            }

            setDefaultFragment();
        }


    }

    FragmentManager fm = getSupportFragmentManager();
    FragmentTransaction transaction = fm.beginTransaction();

    //默认初始化fragment
    private void setDefaultFragment() {

        footer = (CustomRadioGroup) findViewById(R.id.crg_home);
        for (int i = 0; i < itemImage.length; i++) {
            footer.addItem(itemImage[i], itemCheckedImage[i]);
        }


        if (role1 == true) {
            num = 0;
            operationsFM = new OperationsFM();
            fragment = operationsFM;
            transaction.add(R.id.fl_home, operationsFM);
            transaction.commit();

        } else if (role1 == false && role2 == true) {
            num = 1;
            orderFM = new OrderFM();
            fragment = orderFM;
            transaction.add(R.id.fl_home, orderFM);
            transaction.commit();
        } else if (role1 == false && role2 == false && role3 == true) {
            num = 2;
            statisticalFM = new StatisticalFM();
            fragment = statisticalFM;
            transaction.add(R.id.fl_home, statisticalFM);
            transaction.commit();
        } else if (role1 == false && role2 == false && role3 == false && role4 == true) {
            num = 3;
            settingFM = new SettingFM();
            fragment = settingFM;
            transaction.add(R.id.fl_home, settingFM);
            transaction.commit();
        }


        footer.setCheckedIndex(num);
        footer.setOnItemChangedListener(new FooterChange());


    }


    /**
     * 底部栏变化监听
     */
    int num = 0;

    private class FooterChange implements CustomRadioGroup.OnItemChangedListener {

        @Override
        public void onItemChanged() {

            switch (footer.getCheckedIndex()) {
                case 0:
                    if (role1) {
                        if (operationsFM == null) {
                            operationsFM = new OperationsFM();
                        }
                        num = footer.getCheckedIndex();
                        switchContent(operationsFM);

                    } else {

                        ToastUtil.ShortCenter(mActivity, "您无此功能权限");
                        footer.setCheckedIndex(num);
                    }


                    break;
                case 1:
                    if (role2) {
                        if (orderFM == null) {
                            orderFM = new OrderFM();
                        }
                        num = footer.getCheckedIndex();
                        switchContent(orderFM);
                    } else {
                        ToastUtil.ShortCenter(mActivity, "您无此功能权限");
                        footer.setCheckedIndex(num);


                    }
                    break;
                case 2:
                    if (role3) {
                        if (statisticalFM == null) {
                            statisticalFM = new StatisticalFM();
                        }
                        num = footer.getCheckedIndex();
                        switchContent(statisticalFM);
                    } else {
                        ToastUtil.ShortCenter(mActivity, "您无此功能权限");
                        footer.setCheckedIndex(num);
                    }

                    break;
                case 3:
                    if (role4) {
                        if (settingFM == null) {
                            settingFM = new SettingFM();
                        }
                        num = footer.getCheckedIndex();
                        switchContent(settingFM);
                    } else {
                        ToastUtil.ShortCenter(mActivity, "您无此功能权限");
                        footer.setCheckedIndex(num);
                    }

                    break;
            }

        }
    }

    //隐藏显示添加fragment
    public void switchContent(Fragment f) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (!f.isAdded()) {    // 先判断是否被add过
            transaction.hide(fragment).add(R.id.fl_home, f).commit(); // 隐藏当前的fragment，add下一个到Activity中
        } else {
            transaction.hide(fragment).show(f).commit(); // 隐藏当前的fragment，显示下一个
        }
        fragment = f;
    }


    /**
     * 退出程序
     */
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2500) {
                ToastUtil.showShort(mActivity, "再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                ActivityUtil.ExitApp(mActivity);

            }


            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    public static String type = "";
    public static String Remark = "";
    public static String StationID="";
    public static boolean IsMapiIn=false;
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
                            if (!deviceremark.equals(Remark)) {
                                ToastUtil.ErrorOrRight(mActivity, "扫描设备与[" + Remark + "]不匹配,无法安装", 1);
                                LOG.E("deviceremark=" + deviceremark + "\nRemark=" + Remark);
                            } else {
                                HomeActivity.IsMapiIn=false;
                                Intent intent = new Intent(mActivity, DeviceAddActivity.class);
                                intent.putExtra("DeviceCode", device[0]);
                                intent.putExtra("DeviceType", device[1]);
                                intent.putExtra("DeviceRemark", deviceremark);
                                intent.putExtra("status", "安装");
                                mActivity.startActivity(intent);
                                //DeviceIsAdd(device[0], device[1], deviceremark);

                            }
                        }else{
                            ToastUtil.ErrorOrRight(mActivity, "请扫描正确的设备二维码。", 1);
                        }
                    }

                    //手动输入
                    if (inputtype.equals("shoudong")) {

                        String DeviceCode = data.getStringExtra("DeviceCode");
                        Intent intent;

                        if(type.equals("34560")){
                            HomeActivity.IsMapiIn=true;
                            intent = new Intent(mActivity, NewDeviceAddActivity.class);
                        }else{
                            HomeActivity.IsMapiIn=false;
                            intent = new Intent(mActivity, DeviceAddActivity.class);
                        }

                        intent.putExtra("DeviceCode", DeviceCode);
                        intent.putExtra("DeviceType", type);
                        intent.putExtra("DeviceRemark", Remark);
                        intent.putExtra("status", "安装");
                        LOG.E("DeviceCode="+DeviceCode);
                        LOG.E("type="+type);
                        LOG.E("DeviceRemark="+Remark);
                        mActivity.startActivity(intent);
                    }

                }
                break;

            case 1003:
                if (resultCode == mActivity.RESULT_OK) {


                    String inputtype = data.getStringExtra("inputtype");

                    if (inputtype.equals("zbar")) {
                        String scanResult = data.getStringExtra("result");
                        String strZbar = ZbarUtil.DeviceZbar(mActivity, scanResult);
                        if (!strZbar.equals("")) {
                            String[] device = strZbar.split(",");

                            String devicecode = device[0];
                            String deviceremark = ZbarUtil.getSubRemark(DB, device[1]);

                            if (!deviceremark.equals(Remark)) {
                                ToastUtil.ErrorOrRight(mActivity, "扫描设备与[" + Remark + "]不匹配", 1);

                            } else {

                                GoToRepairOrder(devicecode);
                            }
                        }
                    }


                    //手动输入
                    if (inputtype.equals("shoudong")) {

                        String DeviceCode = data.getStringExtra("DeviceCode");

                        GoToRepairOrder(DeviceCode);

                    }
                }
                break;
        }
    }


    private void GoToRepairOrder(String devicecode) {

        String User = SharedUtil.getValue(mActivity, "UserId");
        List<RepairOrderBean> list = null;
        try {
            list = DB.selector(RepairOrderBean.class).where("Status","=",2).where("User","=",User).and("DeviceCode","=",devicecode).findAll();

//        List<RepairOrderBean> list = DB.findAllByWhere(RepairOrderBean.class,
//                " Status='2' and DeviceCode= \"" + devicecode + "\" and User= \"" + User + "\"");

        Log.e("list.size()", "" + list.size());

        if (list.size() == 0) {
            ToastUtil.showShort(mActivity, "设备[" + devicecode + "]可能没有维修工单或没有处于接单状态");

        } else if (devicecode.equals(list.get(0).getDeviceCode())) {

            String status = list.get(0).getStatus();
            if (status.equals("2")) {

                Intent intent = new Intent(mActivity, OrderDetailsActivity.class);
                intent.putExtra("RepairOrderBean", list.get(0));
                intent.putExtra("deviceremark", Remark);
                intent.putExtra("beginstatus", "2");
                mActivity.startActivity(intent);
            } else {
                ToastUtil.ShortCenter(mActivity, "该设备可能没有维修工单或没有处于接单状态");
            }

        }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


//    private void getcityliist(){
//        String time = SharedUtil.getValueByKey(mActivity, "CityList_lastUpdateTime");
//        if (time.equals("") || time.equals("null")) {
//            time = "";
//        }
//        SharedUtil.getValueByKey(mActivity, "CityList_lastUpdateTime");
//        HashMap<String, Object> mapUpdateCity = new HashMap<String, Object>();
//        mapUpdateCity.put("lastUpdateTime", time);
//        WebUtil.getInstance(mActivity).webRequest(Constants.UpdateCity, mapUpdateCity, new WebUtil.MyCallback() {
//            @Override
//            public void onSuccess(String result) {
//                if (!result.equals("-1")) {
//                    Download download1 = new Download(result);
//                    download1.execute();
//                }
//            }
//        });
//    }
//    private ArrayList<CityList> cityBeanList;
//    private class Download extends AsyncTask<String, Integer, String> {
//
//        private String result;
//
//        public Download(String result) {
//            this.result = result;
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            try {
//                Gson gson=new Gson();
//                JSONObject jsonObject = new JSONObject(result);
//                String ErrorCode = jsonObject.getString("ErrorCode");
//                String LastUpdateTime = jsonObject.getString("LastUpdateTime");
//                SharedUtil.setValue(mActivity, "CityList_lastUpdateTime", LastUpdateTime);
//                Log.e("ErrorCode", ErrorCode);
//                String data = jsonObject.getString("CityList");
//                List<CityList> cityList = gson.fromJson(data, new TypeToken<List<CityList>>() {
//                }.getType());
//
//                cityBeanList = new ArrayList<CityList>();
//                for (CityList list : cityList) {
//                    cityBeanList.add(list);
//                }
//            } catch (final Exception e) {
//                e.printStackTrace();
//
//            }
//            return "完成";
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            if (s.equals("完成")) {
//                if (cityBeanList != null && cityBeanList.size() > 0) {
//                    YWA.setCityBeanList(cityBeanList);
//                    for (CityList cl:cityBeanList){
//                        LOG.D("CityName="+cl.getCityName()+"   CityID="+cl.getCityID());
//                    }
//                }
//            }
//
//        }
//
//    }

}