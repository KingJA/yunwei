package com.tdr.yunwei.fragment;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.tdr.yunwei.R;
import com.tdr.yunwei.activity.OrderActivity;
import com.tdr.yunwei.util.NewOrderUtil;
import com.tdr.yunwei.util.SharedUtil;
import com.tdr.yunwei.util.ToastUtil;


public class OrderFM extends BaseFragment {

    private View mView;

    @Override
    public View initViews() {
        mView = View.inflate(mActivity, R.layout.fm_order, null);

        getRolePowers();


        initView(mView);
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        newOrder = new NewOrderUtil(mActivity);
        Log.e("Pan","——————————更新工单0——————————");
        newOrder.WorkOrderRequest();
    }

    boolean role21 = false, role22 = false;

    private void getRolePowers() {

        if (SharedUtil.getValue(mActivity, "RolePowers").equals("")) {
        } else {

            String[] RolePowers = SharedUtil.getValue(mActivity, "RolePowers").split(",");

            for (int i = 0; i < RolePowers.length; i++) {

                int role = Integer.valueOf(RolePowers[i]);
                if (role == 21) {
                    role21 = true;
                }
                if (role == 22) {
                    role22 = true;
                }
            }
        }


    }

    LinearLayout ll_current, ll_history;
    NewOrderUtil newOrder;
    private void initView(View v) {
        ll_current = (LinearLayout) v.findViewById(R.id.ll_current);
        ll_history = (LinearLayout) v.findViewById(R.id.ll_history);


        Myclick myOnclick = new Myclick();
        ll_current.setOnClickListener(myOnclick);
        ll_history.setOnClickListener(myOnclick);

    }


    private class Myclick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v == ll_current) {
                if (role21) {
                    Intent intent = new Intent(mActivity, OrderActivity.class);
                    intent.putExtra("title", "当前工单");
                    startActivity(intent);
                } else {
                    ToastUtil.showShort(mActivity, "您没有此权限");
                }


            }
            if (v == ll_history) {
                if (role22) {
                    Intent intent = new Intent(mActivity, OrderActivity.class);
                    intent.putExtra("title", "历史工单");
                    startActivity(intent);

                } else {
                    ToastUtil.showShort(mActivity, "您没有此权限");
                }

            }
        }
    }

}
