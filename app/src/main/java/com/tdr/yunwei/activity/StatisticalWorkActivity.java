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
import android.widget.ImageView;
import android.widget.TextView;

import com.tdr.yunwei.R;
import com.tdr.yunwei.util.ActivityUtil;
import com.tdr.yunwei.util.Constants;
import com.tdr.yunwei.util.DateUtil;
import com.tdr.yunwei.util.SharedUtil;
import com.tdr.yunwei.util.ToastUtil;
import com.tdr.yunwei.util.WebUtil;
import com.tdr.yunwei.util.ZProgressHUD;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/4/20.
 */
public class StatisticalWorkActivity extends Activity {
    private Activity mActivity;

    private TextView txt_WorkOrderCnt,txt_AcceptCnt,txt_WorkOrderOverCnt,txt_RepairCnt,txt_AvgRepairCnt;
    private TextView txt_OutTimeCnt,txt_OutTimeRate,txt_SatisfactionRate1,txt_SatisfactionRate2;
    private TextView txt_SatisfactionRate3,txt_SatisfactionRate4,txt_SatisfactionRate5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_statisticalwork);

        mActivity = this;
        ActivityUtil.AddActivity(mActivity);
        title();
        initView();
    }

    private void initView() {

        txt_WorkOrderCnt=(TextView)findViewById(R.id.txt_WorkOrderCnt);
        txt_AcceptCnt=(TextView)findViewById(R.id.txt_AcceptCnt);
        txt_WorkOrderOverCnt=(TextView)findViewById(R.id.txt_WorkOrderOverCnt);

        txt_RepairCnt=(TextView)findViewById(R.id.txt_RepairCnt);
        txt_AvgRepairCnt=(TextView)findViewById(R.id.txt_AvgRepairCnt);

        txt_OutTimeCnt=(TextView)findViewById(R.id.txt_OutTimeCnt);
        txt_OutTimeRate=(TextView)findViewById(R.id.txt_OutTimeRate);

        txt_SatisfactionRate1=(TextView)findViewById(R.id.txt_SatisfactionRate1);
        txt_SatisfactionRate2=(TextView)findViewById(R.id.txt_SatisfactionRate2);
        txt_SatisfactionRate3=(TextView)findViewById(R.id.txt_SatisfactionRate3);
        txt_SatisfactionRate4=(TextView)findViewById(R.id.txt_SatisfactionRate4);
        txt_SatisfactionRate5=(TextView)findViewById(R.id.txt_SatisfactionRate5);





    }


    /**
     * 标题栏
     */
    private void title() {

        String title = getIntent().getStringExtra("title");

        ImageView img_title = (ImageView) findViewById(R.id.image_back);
        img_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.FinishActivity(mActivity);
            }
        });
        TextView tv_title = (TextView) findViewById(R.id.text_title);
        tv_title.setText(title);


        if (title.equals("成果统计(总)")) {
            QueryRepairerStatistics(31);
        }
        if (title.equals("成果统计(当月)")) {
            QueryRepairerStatistics(32);
        }
        if (title.equals("成果统计(本周)")) {
            QueryRepairerStatistics(33);
        }

    }

    String WorkOrderCnt, AcceptCnt, WorkOrderOverCnt, RepairCnt, AvgRepairCnt, OutTimeCnt, OutTimeRate;
    String SatisfactionRate1, SatisfactionRate2, SatisfactionRate3, SatisfactionRate4, SatisfactionRate5;

    private void QueryRepairerStatistics(final int num) {

        final ZProgressHUD zProgressHUD = new ZProgressHUD(mActivity);
        zProgressHUD.setMessage("正在加载数据请稍后……");
        zProgressHUD.show();
        String lasttime = SharedUtil.getValue(mActivity, "StatisticsTime");
        if (lasttime.equals("")) {
            lasttime = Constants.DefaultTime;
        }

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("accessToken", SharedUtil.getToken(mActivity));
        map.put("lastUpdateTime", lasttime);

        Log.e("accessToken",SharedUtil.getToken(mActivity)+"//lasttime"+lasttime);

        WebUtil.getInstance(mActivity).webRequest(Constants.QueryRepairerStatistics, map, new WebUtil.MyCallback() {

            @Override
            public void onSuccess(String result) {
                if (result.equals("-1")||result.equals("-2")) {
                    zProgressHUD.dismiss();
                    return;
                }
                try {

                    zProgressHUD.dismiss();


                    JSONObject jsonObject = new JSONObject(result);

                    String ErrorCode = jsonObject.getString("ErrorCode");
                    String ErrorMsg = jsonObject.getString("ErrorMsg");
                    Log.e("ErrorMsg",ErrorMsg);

                    if (ErrorCode.equals("0")) {
                        String data="";
                        if (num == 31) {
                            data = jsonObject.getString("Total");
                        }
                        if (num == 32) {
                            data = jsonObject.getString("Month");
                        }
                        if (num == 33) {
                            data = jsonObject.getString("Week");
                        }

                        JSONObject json = new JSONObject(data);

                        WorkOrderCnt = json.getString("WorkOrderCnt");
                        AcceptCnt = json.getString("AcceptCnt");
                        WorkOrderOverCnt = json.getString("WorkOrderOverCnt");

                        RepairCnt = json.getString("RepairCnt");

                        AvgRepairCnt = json.getString("AvgRepairCnt");//0.70

                        OutTimeCnt = json.getString("OutTimeCnt");
                        OutTimeRate = json.getString("OutTimeRate");

                        SatisfactionRate1 = json.getString("SatisfactionRate1");
                        SatisfactionRate2 = json.getString("SatisfactionRate2");
                        SatisfactionRate3 = json.getString("SatisfactionRate3");
                        SatisfactionRate4 = json.getString("SatisfactionRate4");
                        SatisfactionRate5 = json.getString("SatisfactionRate5");


                        txt_WorkOrderCnt.setText(WorkOrderCnt+" 次");
                        txt_AcceptCnt.setText(AcceptCnt+" 次");
                        txt_WorkOrderOverCnt.setText(WorkOrderOverCnt+" 次");

                        txt_RepairCnt.setText(RepairCnt+" 次");
                        txt_AvgRepairCnt.setText(AvgRepairCnt+" 次");

                        txt_OutTimeCnt.setText(OutTimeCnt+" 次");
                        txt_OutTimeRate.setText(OutTimeRate+" %");


                        txt_SatisfactionRate1.setText(SatisfactionRate1+" %");
                        txt_SatisfactionRate2.setText(SatisfactionRate2+" %");
                        txt_SatisfactionRate3.setText(SatisfactionRate3+" %");
                        txt_SatisfactionRate4.setText(SatisfactionRate4+" %");
                        txt_SatisfactionRate5.setText(SatisfactionRate5+" %");


                        SharedUtil.setValue(mActivity,"StatisticsTime", DateUtil.getNowDate());

                    } else {
                        ToastUtil.showShort(mActivity, "错误"+ErrorMsg);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

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
