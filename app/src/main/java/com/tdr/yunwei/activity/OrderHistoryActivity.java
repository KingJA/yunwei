package com.tdr.yunwei.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tdr.yunwei.R;
import com.tdr.yunwei.bean.RepairOrderBean;
import com.tdr.yunwei.util.ActivityUtil;

/**
 * Created by Administrator on 2016/4/20.
 */
public class OrderHistoryActivity extends Activity {
    private Activity mActivity;

    private TextView txt_devicecode,txt_address,txt_devicetype;
    private TextView txt_username,txt_workorderid,txt_endtime,txt_guzhang;
    private TextView txt_jiedantime,txt_starttime,txt_reason;
    private TextView txt_requestman,txt_requestphone;

    private LinearLayout ll_reason;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_orderhistory);

        mActivity = this;
        ActivityUtil.AddActivity(mActivity);
        title();
        initView();
        BtnOK();

    }


    private void BtnOK() {
        Button btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setText("返回");
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.FinishActivity(mActivity);
            }
        });
    }
    private void initView(){
        RepairOrderBean bean=getIntent().getParcelableExtra("RepairOrderBean");
        String deviceremark=getIntent().getStringExtra("deviceremark");
//        Gson gson=new Gson();
//        Log.e("HistoryBean",gson.toJson(bean));



        txt_devicecode=(TextView)findViewById(R.id.txt_devicecode);
        txt_devicecode.setText(bean.getDeviceCode());



        txt_address=(TextView)findViewById(R.id.txt_address);
        txt_address.setText(bean.getAddress());

        txt_devicetype=(TextView)findViewById(R.id.txt_devicetype);
        txt_devicetype.setText(deviceremark);



        txt_username=(TextView)findViewById(R.id.txt_username);
        txt_username.setText(bean.getWorkSender());

        txt_workorderid=(TextView)findViewById(R.id.txt_workorderid);
        txt_workorderid.setText(bean.getWorkOrderNO());


        txt_jiedantime=(TextView)findViewById(R.id.txt_jiedantime);
        txt_jiedantime.setText(bean.getCheckTime());

        txt_starttime=(TextView)findViewById(R.id.txt_starttime);
        txt_starttime.setText(bean.getStartTime());

        txt_endtime=(TextView)findViewById(R.id.txt_endtime);
        txt_endtime.setText(bean.getEndTime());

        txt_guzhang=(TextView)findViewById(R.id.txt_guzhang);
        txt_guzhang.setText(bean.getFault());


        txt_requestman=(TextView)findViewById(R.id.txt_requestman);
        txt_requestman.setText(bean.getRequestMan());

        txt_requestphone=(TextView)findViewById(R.id.txt_requestphone);
        txt_requestphone.setText(bean.getRequestPhone());



        ll_reason=(LinearLayout)findViewById(R.id.ll_reason);

        if(bean.getStatus().equals("99")){
            ll_reason.setVisibility(View.VISIBLE);
            txt_reason=(TextView)findViewById(R.id.txt_reason);
            txt_reason.setText(bean.getCloseReason());
        }



    }


    /**
     * 标题栏
     */
    private void title(){
        ImageView img_title= (ImageView) findViewById(R.id.image_back);
        img_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.FinishActivity(mActivity);
            }
        });
        TextView tv_title = (TextView) findViewById(R.id.text_title);
        tv_title.setText("历史工单详情");

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

}
