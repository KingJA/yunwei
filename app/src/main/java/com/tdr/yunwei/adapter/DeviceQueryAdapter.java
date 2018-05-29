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
import com.tdr.yunwei.bean.DeviceBean;
import com.tdr.yunwei.util.LOG;
import com.tdr.yunwei.util.SharedUtil;
import com.tdr.yunwei.util.ZProgressHUD;
import com.tdr.yunwei.util.ZbarUtil;


import org.xutils.DbManager;

import java.util.List;

/**
 * Created by Administrator on 2016/5/13.
 */
public class DeviceQueryAdapter extends BaseSimpleAdapter<DeviceBean> {

    DbManager DB;
    String deviceremark;
    ZProgressHUD zProgressHUD;
    String AccessType="";
    public DeviceQueryAdapter(Activity mActivity, List<DeviceBean> list, DbManager DB) {
        super(mActivity, list);
        this.DB=DB;

    }

    @Override
    public View simpleGetView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder = null;
        if (convertView == null) {

            convertView = View.inflate(mActivity, R.layout.lv_device_item, null);
            mHolder = new ViewHolder(convertView);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }
        final DeviceBean bean=list.get(position);


        deviceremark = ZbarUtil.getSubRemark(DB, bean.getDeviceType());


        mHolder.txt_number.setText("" + (position + 1));
        mHolder.txt_devicecode.setText("设备编号:"+bean.getDeviceCode());
        mHolder.txt_deviceremark.setText("设备类型:"+deviceremark);
        mHolder.txt_deviceaddress.setText("设备地址:"+bean.getAddress());
        mHolder.txt_accesstype.setText(bean.getDeviceType());

        if(bean.getAccessType().equals("1")){
            mHolder.txt_accesstype.setText("有线");
            mHolder.txt_accesstype.setTextColor(Color.BLUE);
        }
        else if(bean.getAccessType().equals("2")){
            mHolder.txt_accesstype.setText("无线");
            mHolder.txt_accesstype.setTextColor(Color.GRAY);
        }
        else {
            mHolder.txt_accesstype.setText("未知");
            mHolder.txt_accesstype.setTextColor(Color.RED);
        }

        mHolder.ll_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//图片太大 会崩溃
                if(!bean.getPhoto1().equals("")||!bean.getPhoto2().equals("")
                        ||!bean.getPhoto3().equals("")){

                    DeviceBean mybean=new DeviceBean();


                    mybean.setDeviceID(bean.getDeviceID());
                    mybean.setDeviceCode(bean.getDeviceCode());
                    mybean.setDeviceType(bean.getDeviceType());
                    mybean.setSerialNumber(bean.getSerialNumber());

                    mybean.setUsage(bean.getUsage());
                    mybean.setReserve3(bean.getReserve3());

                    mybean.setPhotoID1(bean.getPhotoID1());
                    mybean.setPhotoID2(bean.getPhotoID2());
                    mybean.setPhotoID3(bean.getPhotoID3());
                    mybean.setPhoto1("");
                    mybean.setPhoto2("");
                    mybean.setPhoto3("");

                    mybean.setLAT(bean.getLAT());
                    mybean.setLNG(bean.getLNG());
                    mybean.setAddress(bean.getAddress());
                    mybean.setAreaID(bean.getAreaID());
                    mybean.setXQ(bean.getXQ());
                    LOG.D("辖区="+bean.getXQ());
                    mybean.setPCS(bean.getPCS());
                    LOG.D("派出所="+bean.getPCS());
                    mybean.setJWH(bean.getJWH());

                    mybean.setRepairCompany(bean.getRepairCompany());
                    mybean.setOwner(bean.getOwner());

                    mybean.setAccessType(bean.getAccessType());
                    mybean.setMask(bean.getMask());
                    mybean.setPhone(bean.getPhone());
                    mybean.setCarrierOperator(bean.getCarrierOperator());
                    mybean.setGateway(bean.getGateway());
                    mybean.setIP(bean.getIP());
                    mybean.setSIM(bean.getSIM());


                    mybean.setReserve1(bean.getReserve1());
                    mybean.setReserve2(bean.getReserve2());
                    mybean.setReserve4(bean.getReserve4());
                    mybean.setReserve5(bean.getReserve5());
                    mybean.setReserve6(bean.getReserve6());
                    mybean.setReserve7(bean.getReserve7());
                    mybean.setReserve8(bean.getReserve8());
                    mybean.setReserve9(bean.getReserve9());
                    mybean.setReserve10(bean.getReserve10());
                    mybean.setReserve11(bean.getReserve11());
                    mybean.setReserve12(bean.getReserve12());
                    mybean.setReserve13(bean.getReserve13());
                    mybean.setReserve14(bean.getReserve14());
                    mybean.setDescription(bean.getDescription());

                    SharedUtil.setValue(mActivity,"Photo1",bean.getPhoto1());
                    SharedUtil.setValue(mActivity,"Photo2",bean.getPhoto2());
                    SharedUtil.setValue(mActivity,"Photo3",bean.getPhoto3());
                    Intent intent=new Intent(mActivity,DeviceAddActivity.class);
                    intent.putExtra("status", "详情");
                    intent.putExtra("pic","you");
                    intent.putExtra("deviceremark",deviceremark);
                    intent.putExtra("deviceBean",mybean);
                    mActivity.startActivityForResult(intent,1);
                }else {
                    Intent intent=new Intent(mActivity,DeviceAddActivity.class);
                    intent.putExtra("status", "详情");
                    intent.putExtra("pic","wu");
                    intent.putExtra("deviceremark",deviceremark);
                    intent.putExtra("deviceBean",bean);
                    mActivity.startActivityForResult(intent,1);
                }




            }
        });




        return convertView;
    }

    public class ViewHolder {
        TextView txt_number, txt_devicecode,
                txt_deviceaddress, txt_deviceremark,txt_accesstype;

        LinearLayout ll_device;

        public ViewHolder(View v) {
            ll_device=(LinearLayout) v.findViewById(R.id.ll_device);
            txt_number = (TextView) v.findViewById(R.id.txt_number);
            txt_devicecode = (TextView) v.findViewById(R.id.txt_devicecode);

            txt_deviceremark = (TextView) v.findViewById(R.id.txt_deviceremark);
            txt_deviceaddress = (TextView) v.findViewById(R.id.txt_deviceaddress);
            txt_accesstype= (TextView) v.findViewById(R.id.txt_accesstype);
        }
    }
}
