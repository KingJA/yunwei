package com.tdr.yunwei.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tdr.yunwei.R;
import com.tdr.yunwei.bean.RepairOrderBean;
import com.tdr.yunwei.util.ZbarUtil;


import org.xutils.DbManager;

import java.util.List;

/**
 * Created by Administrator on 2016/4/26.
 */
public class OrderAdapter extends BaseSimpleAdapter<RepairOrderBean> {
    DbManager DB;
    ViewHolder mHolder = null;
    public OrderAdapter(Activity mActivity, List<RepairOrderBean> list, DbManager DB) {
        super(mActivity, list);
        this.DB = DB;


    }

    @Override
    public View simpleGetView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            convertView = View.inflate(mActivity, R.layout.lv_todayorder_item, null);
            mHolder = new ViewHolder(convertView);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }
        final RepairOrderBean bean = list.get(position);

        Gson gson=new Gson();
        String deviceInfo = gson.toJson(bean);
        Log.e("RepairOrderBean", deviceInfo);

        mHolder.txt_workno.setText(bean.getRepairListNO());
        mHolder.txt_devicecode.setText(bean.getDeviceCode());
        mHolder.txt_deviceaddress.setText(bean.getAddress());

        String type = bean.getDeviceType();
        String devicetype = ZbarUtil.getSubRemark(DB, type);
        mHolder.txt_devicetype.setText(devicetype);

        final String Status = bean.getStatus();


        if (Status.equals("1")) {
            mHolder.txt_working_on.setText("已派单");
        }
        if (Status.equals("2")) {
            mHolder.txt_working_on.setText("已接单");
        }
        if (Status.equals("3")) {
            mHolder.txt_working_on.setText("维修中");
        }
        if (Status.equals("4")) {
            mHolder.txt_working_on.setText("维修完");
        }
        if (Status.equals("9")) {
            mHolder.txt_working_on.setText("未成功");
        }
        if (Status.equals("10")) {
            mHolder.txt_working_on.setText("挂起");
        }

        if (Status.equals("6")) {
            mHolder.txt_working_on.setText("关闭");
            mHolder.img_working_on.setImageResource(R.mipmap.working_off);
        }
        if (Status.equals("99")) {
            mHolder.txt_working_on.setText("无法维修");
            mHolder.img_working_on.setImageResource(R.mipmap.working_off);
        }

        return convertView;
    }


    public class ViewHolder {
        TextView txt_devicecode, txt_workno, txt_devicetype, txt_deviceaddress, txt_working_on;
        ImageView img_working_on,img_numberbg;

        public ViewHolder(View v) {

            txt_devicecode = (TextView) v.findViewById(R.id.txt_devicecode);
            txt_workno = (TextView) v.findViewById(R.id.txt_workno);
            txt_devicetype = (TextView) v.findViewById(R.id.txt_devicetype);
            txt_deviceaddress = (TextView) v.findViewById(R.id.txt_deviceaddress);
            txt_working_on = (TextView) v.findViewById(R.id.txt_working_on);

            img_working_on = (ImageView) v.findViewById(R.id.img_working_on);
            img_numberbg= (ImageView) v.findViewById(R.id.img_numberbg);
        }
    }
}
