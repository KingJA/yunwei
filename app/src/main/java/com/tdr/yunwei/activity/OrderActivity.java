package com.tdr.yunwei.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tdr.yunwei.R;
import com.tdr.yunwei.YunWeiApplication;
import com.tdr.yunwei.bean.RepairOrderBean;
import com.tdr.yunwei.util.ActivityUtil;
import com.tdr.yunwei.util.DBUtils;
import com.tdr.yunwei.util.LOG;
import com.tdr.yunwei.util.NewOrderUtil;
import com.tdr.yunwei.util.SharedUtil;


import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/20.
 */
public class OrderActivity extends Activity {

    public static String ACTION_NAME = "newOrder";

    private Activity mActivity;

    LinearLayout ll_historyorder, ll_currentorder;

    LinearLayout ll_yipai, ll_yijie, ll_weixiuzhong, ll_weixiuwan, ll_guaqi;
    TextView txt_yipai, txt_yijie, txt_weixiuzhong, txt_weixiuwan, txt_guaqi;

    LinearLayout ll_close, ll_norepair;
    TextView txt_close, txt_norepair;
    DbManager DB;
    String title;

    NewOrderUtil newOrder;
    private YunWeiApplication YWA;
    private List<RepairOrderBean> repairCurrentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_order);

        mActivity = this;
        ActivityUtil.AddActivity(mActivity);
        YWA = YunWeiApplication.getInstance();
//        DB = YWA.getDB();
        DB= x.getDb(DBUtils.getDb());

        newOrder = new NewOrderUtil(mActivity);
        Log.e("Pan","——————————更新工单2——————————");
        newOrder.WorkOrderRequest();

        initView();
        title();
        SetText();

        registerBoradcastReceiver();//广播第二种注册方式
    }
    private void getOrderList(){
        try {
            String User = SharedUtil.getValue(mActivity, "UserId");
            List<RepairOrderBean> list= DB.findAll(RepairOrderBean.class);
             repairCurrentList =new ArrayList<RepairOrderBean>();
             if(list!=null){
                 for (int i = 0; i < list.size(); i++) {
                     LOG.E("RepairListID:"+list.get(i).getRepairListID()+"   Status:"+list.get(i).getStatus());
                     if(list.get(i).getUser().equals(User)){
                         repairCurrentList.add(list.get(i));
                     }
                 }
             }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
    private int getOrderForType(String status,int type){
        int index=0;
        for (RepairOrderBean ListR:repairCurrentList){
            if(type==0){
                if(ListR.getStatus().equals(status)){
                    index++;
                }
            }else{
                if(ListR.getStatus().equals("4")||ListR.getStatus().equals("9")){
                    index++;
                }
            }
        }
        return  index;
    }
    @Override
    protected void onStop() {
        super.onStop();
//        newOrder.getNewOrderList();
    }

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(ACTION_NAME);
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }
    //广播接收


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_NAME)) {
                SetText();

            }
        }

    };


    private void initView() {
        ll_yipai = (LinearLayout) findViewById(R.id.ll_yipai);
        ll_yijie = (LinearLayout) findViewById(R.id.ll_yijie);
        ll_weixiuzhong = (LinearLayout) findViewById(R.id.ll_weixiuzhong);
        ll_weixiuwan = (LinearLayout) findViewById(R.id.ll_weixiuwan);
        ll_guaqi = (LinearLayout) findViewById(R.id.ll_guaqi);

        txt_yipai = (TextView) findViewById(R.id.txt_yipai);
        txt_yijie = (TextView) findViewById(R.id.txt_yijie);
        txt_weixiuzhong = (TextView) findViewById(R.id.txt_weixiuzhong);
        txt_weixiuwan = (TextView) findViewById(R.id.txt_weixiuwan);
        txt_guaqi = (TextView) findViewById(R.id.txt_guaqi);


        ll_close = (LinearLayout) findViewById(R.id.ll_close);
        ll_norepair = (LinearLayout) findViewById(R.id.ll_norepair);
        txt_close = (TextView) findViewById(R.id.txt_close);
        txt_norepair = (TextView) findViewById(R.id.txt_norepair);

        ll_currentorder = (LinearLayout) findViewById(R.id.ll_currentorder);
        ll_historyorder = (LinearLayout) findViewById(R.id.ll_historyorder);
    }

    public void SetText() {
        getOrderList();
        txt_yipai.setText("已派工单列表(" + getOrderForType("1",0) + "条)");
        txt_yijie.setText("已接工单列表(" + getOrderForType("2",0) + "条)");
        txt_weixiuzhong.setText("维修中单列表(" + getOrderForType("3",0) + "条)");
        txt_weixiuwan.setText("维修完工单列表(" + getOrderForType("",1) + "条)");
        txt_guaqi.setText("挂起工单列表(" + getOrderForType("10",0) + "条)");

        txt_close.setText("正常维修完关闭工单列表(" + getOrderForType("6",0) + "条)");
        txt_norepair.setText("无法维修工单列表(" + getOrderForType("99",0) + "条)");

        click();
    }


    private void click() {
        ll_yipai.setOnClickListener(new MyOnClick(ll_yipai, "已派工单列表", "1"));
        ll_yijie.setOnClickListener(new MyOnClick(ll_yijie, "已接工单列表", "2"));
        ll_weixiuzhong.setOnClickListener(new MyOnClick(ll_weixiuzhong, "维修中单列表", "3"));
        ll_weixiuwan.setOnClickListener(new MyOnClick(ll_weixiuwan, "维修完工单列表", "4"));
        ll_guaqi.setOnClickListener(new MyOnClick(ll_guaqi, "挂起工单列表", "10"));
        ll_close.setOnClickListener(new MyOnClick(ll_close, "正常维修完关闭工单列表", "6"));
        ll_norepair.setOnClickListener(new MyOnClick(ll_norepair, "无法维修工单列表", "99"));
    }


    private class MyOnClick implements View.OnClickListener {
        private View ll;
        private String ordertitle, status;

        public MyOnClick(View ll, String ordertitle, String status) {
            this.ll = ll;
            this.ordertitle = ordertitle;
            this.status = status;
        }

        @Override
        public void onClick(View v) {
            if (v == ll) {
                    Intent intent = new Intent(mActivity, OrderListActivity.class);
                    intent.putExtra("ordertitle", ordertitle);
                    intent.putExtra("status", status);
                    startActivity(intent);
            }
        }
    }

    /**
     * 标题栏
     */
    private void title() {

        title = getIntent().getStringExtra("title");
        if (title.equals("当前工单")) {
            ll_historyorder.setVisibility(View.GONE);
            ll_currentorder.setVisibility(View.VISIBLE);
        }
        if (title.equals("历史工单")) {
            ll_historyorder.setVisibility(View.VISIBLE);
            ll_currentorder.setVisibility(View.GONE);
        }
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
        SetText();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}
