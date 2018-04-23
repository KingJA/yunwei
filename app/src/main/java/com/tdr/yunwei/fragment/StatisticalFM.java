package com.tdr.yunwei.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tdr.yunwei.R;
import com.tdr.yunwei.activity.StatisticalWorkActivity;
import com.tdr.yunwei.util.ActivityUtil;
import com.tdr.yunwei.util.SharedUtil;
import com.tdr.yunwei.util.ToastUtil;

public class StatisticalFM extends BaseFragment {

    private View mView;

    private LinearLayout ll_total, ll_month, ll_week;

    @Override
    public View initViews() {
        mView = View.inflate(mActivity, R.layout.fm_statistical, null);
        initView(mView);
        title(mView);
        getRolePowers();
        return mView;
    }

    private void title(View v) {
        ImageView img_title = (ImageView) v.findViewById(R.id.image_back);
        img_title.setVisibility(View.GONE);
        img_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.FinishActivity(mActivity);
            }
        });
        TextView tv_title = (TextView) v.findViewById(R.id.text_title);
        tv_title.setText("工作统计");

    }

    boolean role31 = false, role32 = false, role33 = false;

    private void getRolePowers() {
        if (SharedUtil.getValue(mActivity, "RolePowers").equals("")) {


        } else {
            String[] RolePowers = SharedUtil.getValue(mActivity, "RolePowers").split(",");

            for (int i = 0; i < RolePowers.length; i++) {

                int role = Integer.valueOf(RolePowers[i]);
                if (role == 31) {
                    role31 = true;
                }
                if (role == 32) {
                    role32 = true;
                }
                if (role == 33) {
                    role33 = true;
                }

            }


        }


    }

    private void initView(View v) {
        ll_total = (LinearLayout) v.findViewById(R.id.ll_total);
        ll_month = (LinearLayout) v.findViewById(R.id.ll_month);
        ll_week = (LinearLayout) v.findViewById(R.id.ll_week);


        MyOnclick myOnclick = new MyOnclick();
        ll_total.setOnClickListener(myOnclick);
        ll_month.setOnClickListener(myOnclick);
        ll_week.setOnClickListener(myOnclick);
    }


    private class MyOnclick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v == ll_total) {
                if (role31) {
                    Intent intent = new Intent(mActivity, StatisticalWorkActivity.class);
                    intent.putExtra("title", "成果统计(总)");
                    startActivity(intent);

                } else {
                    ToastUtil.showShort(mActivity, "您没有此权限");
                }


            }
            if (v == ll_month) {
                if (role32) {
                    Intent intent = new Intent(mActivity, StatisticalWorkActivity.class);
                    intent.putExtra("title", "成果统计(当月)");
                    startActivity(intent);

                } else {
                    ToastUtil.showShort(mActivity, "您没有此权限");
                }


            }
            if (v == ll_week) {
                if (role33) {
                    Intent intent = new Intent(mActivity, StatisticalWorkActivity.class);
                    intent.putExtra("title", "成果统计(本周)");
                    startActivity(intent);

                } else {
                    ToastUtil.showShort(mActivity, "您没有此权限");
                }


            }


        }
    }

}
