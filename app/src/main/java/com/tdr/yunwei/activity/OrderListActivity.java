package com.tdr.yunwei.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tdr.yunwei.R;
import com.tdr.yunwei.YunWeiApplication;
import com.tdr.yunwei.adapter.OrderAdapter;
import com.tdr.yunwei.bean.RepairOrderBean;
import com.tdr.yunwei.util.ActivityUtil;
import com.tdr.yunwei.util.DBUtils;
import com.tdr.yunwei.util.DipPx;
import com.tdr.yunwei.util.NewOrderUtil;
import com.tdr.yunwei.util.SharedUtil;
import com.tdr.yunwei.util.ToastUtil;
import com.tdr.yunwei.util.ZbarUtil;


import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Administrator on 2016/4/20.
 */
public class OrderListActivity extends Activity {
    private Activity mActivity;
    private String title, status;
    private List<RepairOrderBean> orderList;
    public List<RepairOrderBean> repairPXList= new ArrayList<RepairOrderBean>();
    public int lvposition=0;

    private DbManager DB;
    NewOrderUtil newOrder;
    public OrderAdapter orderAdapter;
    private YunWeiApplication YWA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_orderlist);

        mActivity = this;
        ActivityUtil.AddActivity(mActivity);

        YWA = YunWeiApplication.getInstance();
//        DB = YWA.getDB();
        DB= x.getDb(DBUtils.getDb());
        title = getIntent().getStringExtra("ordertitle");
        status = getIntent().getStringExtra("status");

        title();
        initLv2Srl();
        newOrder = new NewOrderUtil(mActivity);
        Log.e("Pan","——————————更新工单3——————————");
        newOrder.WorkOrderRequest();
        getOrderList(status);
        registerBoradcastReceiver();
    }
    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(OrderActivity.ACTION_NAME);
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }
    //广播接收


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(OrderActivity.ACTION_NAME)) {
                getOrderList(status);

            }
        }

    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
    /**
     * 标题栏
     */
    private void title() {

        ImageView img_title = (ImageView) findViewById(R.id.image_back);
        img_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.FinishActivity(mActivity);
            }
        });
        TextView tv_title = (TextView) findViewById(R.id.text_title);
        tv_title.setText(title);
    }


    SwipeRefreshLayout srl2;
    ListView lv2;

    private void initLv2Srl() {
        lv2 = (ListView) findViewById(R.id.lv2);
        srl2 = (SwipeRefreshLayout) findViewById(R.id.srl2);

        srl2.setColorSchemeResources(R.color.blue_light_kj);
        srl2.setProgressViewOffset(false, 0, DipPx.dip2px(mActivity, 24));


        srl2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                newOrder.WorkOrderRequest();
                getOrderList(status);
                srl2.setRefreshing(false);
            }
        });

    }


    private void getOrderList(String status) {


        String User = SharedUtil.getValue(mActivity, "UserId");
        try {
        if (status.equals("4")) {

                orderList = DB.selector(RepairOrderBean.class).where("Status","=","4").or("Status","=","9").and("User","=",User).findAll();

//            orderList = DB.findAllByWhere(RepairOrderBean.class,
//                    " Status='4' or Status='9' and User= \"" + User + "\"");
        } else {
            orderList = DB.selector(RepairOrderBean.class).where("Status","=",status).and("User","=",User).findAll();

//            orderList = DB.findAllByWhere(RepairOrderBean.class,
//                    " Status= \"" + status + "\" and User= \"" + User + "\"");

        }
        if(orderList==null){
            orderList=new ArrayList<RepairOrderBean>();
        }
        Log.e("orderList", orderList.size() + "//status==" + status);
        if (orderList.size() > 0) {
            List<String> list = new ArrayList<String>();

            for (int i = 0; i < orderList.size(); i++) {
                String time = orderList.get(i).getSendTime();

                if (time.contains("/")) {
                    time = time.replace("/", "-");
                }

                if (!time.equals("")) {
                    list.add(time);
                }

            }

            sortListDesc(list);
            orderAdapter = new OrderAdapter(mActivity, repairPXList, DB);

            lv2.setAdapter(orderAdapter);

            lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    lvposition=position;
                    RepairOrderBean bean = repairPXList.get(position);

//                    devicebean devicebean=new devicebean();
//                    Log.e("RepairOrderBean",devicebean.toJson(bean));

                    String status = bean.getStatus();
                    String type = bean.getDeviceType();
                    String devicetype = ZbarUtil.getSubRemark(DB, type);

                    if (status.equals("6") || status.equals("99")) {
                        Intent intent = new Intent(mActivity, OrderHistoryActivity.class);
                        intent.putExtra("RepairOrderBean", bean);
                        intent.putExtra("deviceremark", devicetype);
                        mActivity.startActivity(intent);


                    } else {
                        Intent intent = new Intent(mActivity, OrderDetailsActivity.class);
                        intent.putExtra("RepairOrderBean", bean);
                        intent.putExtra("deviceremark", devicetype);
                        intent.putExtra("beginstatus", "");
                        mActivity.startActivity(intent);
                    }
                }
            });

        } else {
            ToastUtil.ShortCenter(mActivity, "无新工单");
        }
    } catch (DbException e) {
        e.printStackTrace();
    }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            ActivityUtil.FinishActivity(mActivity);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(OrderDetailsActivity.isAcceptWorkOrder||EndWorkActivity.isEnd||OrderDetailsActivity.idRepairStart){
            repairPXList.remove(lvposition);
        }

        if(OrderDetailsActivity.isAcceptWorkOrder||EndWorkActivity.isEnd){
           // repairPXList.remove(lvposition);
            orderAdapter.notifyDataSetChanged();
            OrderDetailsActivity.isAcceptWorkOrder=false;
            EndWorkActivity.isEnd=false;
        }
        if(OrderDetailsActivity.idRepairStart){
            //repairPXList.remove(lvposition);
            orderAdapter.notifyDataSetChanged();
            OrderDetailsActivity.idRepairStart=false;
        }
        newOrder.WorkOrderRequest();
        getOrderList(status);
    }


    /**
     * 时间排序，最近在前
     */



    public void sortListDesc(List<String> list) {
        List<String> retlist = new ArrayList<String>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<Long, String> map = new TreeMap<Long, String>();
        String dateStr = "";
        long dateLong = 0;
        for (int i = 0; i < list.size(); i++) {
            dateStr = list.get(i);
            try {
                dateLong = sdf.parse(dateStr).getTime();
                map.put(dateLong, dateStr);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        //Log.e("dateLong",dateLong+"");

        Collection<String> coll = map.values();
        retlist.addAll(coll);
        Collections.reverse(retlist);

        //Log.e("retlist.size",retlist.size()+"");

        repairPXList = new ArrayList<RepairOrderBean>();
        for (int j = 0; j < retlist.size(); j++) {


            String SendTime2 = retlist.get(j);

            for (int i = 0; i < orderList.size(); i++) {
                String SendTime = orderList.get(i).getSendTime();
                if (SendTime.contains("/")) {
                    SendTime = SendTime.replace("/", "-");
                }
                //Log.e("orderSendTime", "==" + SendTime + "//paixutime=" + SendTime2);

                if (SendTime.equals(SendTime2)) {

                    RepairOrderBean bean = new RepairOrderBean();

                    bean.setSendTime(SendTime);
                    bean.setUser(orderList.get(i).getUser());
                    bean.setAddress(orderList.get(i).getAddress());
                    bean.setChangeDeviceCode(orderList.get(i).getChangeDeviceCode());
                    bean.setStartTime(orderList.get(i).getStartTime());
                    bean.setCheckTime(orderList.get(i).getCheckTime());
                    bean.setDescription(orderList.get(i).getDescription());
                    bean.setDeviceCode(orderList.get(i).getDeviceCode());
                    bean.setDeviceID(orderList.get(i).getDeviceID());
                    bean.setDeviceType(orderList.get(i).getDeviceType());
                    bean.setEndTime(orderList.get(i).getEndTime());
                    bean.setFault(orderList.get(i).getFault());
                    bean.setIsUrgent(orderList.get(i).getIsUrgent());
                    bean.setLastUpdateTime(orderList.get(i).getLastUpdateTime());
                    bean.setLimitTime(orderList.get(i).getLimitTime());
                    bean.setMakeTime(orderList.get(i).getMakeTime());
                    bean.setRepairListID(orderList.get(i).getRepairListID());
                    bean.setRepairListNO(orderList.get(i).getRepairListNO());
                    bean.setRequestMan(orderList.get(i).getRequestMan());
                    bean.setRequestPhone(orderList.get(i).getRequestPhone());
                    bean.setStatus(orderList.get(i).getStatus());
                    bean.setWorkOrderID(orderList.get(i).getWorkOrderID());
                    bean.setWorkOrderNO(orderList.get(i).getWorkOrderNO());
                    bean.setWorkSender(orderList.get(i).getWorkSender());
                    bean.setWorkSendTime(orderList.get(i).getWorkSendTime());
                    bean.setWorkSendUserID(orderList.get(i).getWorkSendUserID());
                    bean.setWorkStatus(orderList.get(i).getWorkStatus());
                    bean.setCloseReason(orderList.get(i).getCloseReason());

                    bean.setUser(SharedUtil.getValue(mActivity, "UserId"));

                    repairPXList.add(bean);
                }
            }
        }

        Log.e("repairPXList.size()", repairPXList.size() + "");


    }


}
