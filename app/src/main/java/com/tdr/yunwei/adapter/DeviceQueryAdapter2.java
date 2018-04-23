package com.tdr.yunwei.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tdr.yunwei.R;
import com.tdr.yunwei.activity.DeviceAddActivity;
import com.tdr.yunwei.activity.HomeActivity;
import com.tdr.yunwei.activity.NewDeviceAddActivity;
import com.tdr.yunwei.bean.DeviceBean;
import com.tdr.yunwei.bean.DeviceBean2;
import com.tdr.yunwei.util.LOG;
import com.tdr.yunwei.util.SharedUtil;
import com.tdr.yunwei.util.ZProgressHUD;
import com.tdr.yunwei.util.ZbarUtil;

import org.xutils.DbManager;

import java.util.List;

/**
 * Created by Administrator on 2016/5/13.
 */
public class DeviceQueryAdapter2 extends BaseSimpleAdapter<DeviceBean2> {

    DbManager DB;
    String deviceremark;
    ZProgressHUD zProgressHUD;
    String AccessType = "";
    DeviceQueryClickListener DQCL;
    public void setClick(DeviceQueryClickListener dqcl){
        DQCL=dqcl;
    }
    public interface DeviceQueryClickListener {
       void OnClick(int Position);
    }

    public DeviceQueryAdapter2(Activity mActivity, List<DeviceBean2> list, DbManager DB) {
        super(mActivity, list);
        this.DB = DB;

    }
    public void UpData(List<DeviceBean2> list){
        super.list=list;
        notifyDataSetChanged();
    }

    @Override
    public View simpleGetView(final int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder = null;
        if (convertView == null) {

            convertView = View.inflate(mActivity, R.layout.lv_device_item, null);
            mHolder = new ViewHolder(convertView);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }
        final DeviceBean2 bean = list.get(position);


        deviceremark = ZbarUtil.getSubRemark(DB, bean.getDeviceType());


        mHolder.txt_number.setText("" + (position + 1));
        mHolder.txt_devicecode.setText("设备编号:" + bean.getDeviceCode());
        mHolder.txt_deviceremark.setText("设备类型:" + deviceremark);
        mHolder.txt_deviceaddress.setText("设备地址:" + bean.getAddress());
        mHolder.txt_accesstype.setText(bean.getDeviceType());

        if (bean.getAccessType().equals("1")) {
            mHolder.txt_accesstype.setText("有线");
            mHolder.txt_accesstype.setTextColor(Color.BLUE);
        } else if (bean.getAccessType().equals("2")) {
            mHolder.txt_accesstype.setText("无线");
            mHolder.txt_accesstype.setTextColor(Color.GRAY);
        } else {
            mHolder.txt_accesstype.setText("未知");
            mHolder.txt_accesstype.setTextColor(Color.RED);
        }

        mHolder.ll_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DQCL.OnClick(position);

            }
        });


        return convertView;
    }

    public class ViewHolder {
        TextView txt_number, txt_devicecode,
                txt_deviceaddress, txt_deviceremark, txt_accesstype;

        LinearLayout ll_device;

        public ViewHolder(View v) {
            ll_device = (LinearLayout) v.findViewById(R.id.ll_device);
            txt_number = (TextView) v.findViewById(R.id.txt_number);
            txt_devicecode = (TextView) v.findViewById(R.id.txt_devicecode);

            txt_deviceremark = (TextView) v.findViewById(R.id.txt_deviceremark);
            txt_deviceaddress = (TextView) v.findViewById(R.id.txt_deviceaddress);
            txt_accesstype = (TextView) v.findViewById(R.id.txt_accesstype);
        }
    }
}
