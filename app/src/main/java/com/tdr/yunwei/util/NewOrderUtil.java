package com.tdr.yunwei.util;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.tdr.yunwei.R;
import com.tdr.yunwei.YunWeiApplication;
import com.tdr.yunwei.activity.OrderActivity;
import com.tdr.yunwei.activity.OrderListActivity;
import com.tdr.yunwei.bean.RepairOrderBean;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/6/25.
 */
public class NewOrderUtil {


    public NewOrderUtil(Activity mActivity) {
        this.DB= x.getDb(DBUtils.getDb());
        this.mActivity = mActivity;
        this.YWA= YunWeiApplication.getInstance();
        this.WorkOrderSize=YWA.getWorkOrderSize();
    }


    private List<RepairOrderBean> repairList = null;
    private DbManager DB;
    private Activity mActivity;
    private YunWeiApplication YWA;
    String string = "";
    String status="";
    int size = 0;
    int WorkOrderSize;
    public void getNewOrderList() {
        final int time = 5 * 60 * 1000;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        WorkOrderRequest();
                        Thread.sleep(time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void WorkOrderRequest() {
        DB=x.getDb(DBUtils.getDb());
        String lasttime = SharedUtil.getValue(mActivity, "OrderUpdateTime");


        if (lasttime.equals("")) {
            lasttime = Constants.DefaultTime;
        }

        final HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("accessToken", SharedUtil.getToken(mActivity));
        map.put("userID", SharedUtil.getValue(mActivity, "UserId"));
        map.put("lastUpdateTime", lasttime);

//        Log.e("lasttime", lasttime+"  accessToken="+SharedUtil.getToken(mActivity)+
//        "  userID=="+SharedUtil.getValue(mActivity, "UserId"));


        WebUtil.getInstance(mActivity).webRequest(Constants.WorkOrderRequest, map, new WebUtil.MyCallback() {
            @Override
            public void onSuccess(String result) {
                LOG.E("检查工单更新："+CheckDate(result));
                CheckDate(result);
                    Download5 download5 = new Download5(result);
                    download5.execute();

            }
        });
    }
    private boolean CheckDate(String result){

        WorkOrderSize=YWA.getWorkOrderSize();
        try {
            JSONObject jsonObject = new JSONObject(result);
            String data = jsonObject.getString("RepairList");
            JSONArray array = new JSONArray(data);
            int size =array.length();
            LOG.E(WorkOrderSize+"工单跟新数据条数="+size);
            if(size!=0&&size!=WorkOrderSize){
                YWA.setWorkOrderSize(size);
                SharedUtil.setValue(mActivity,"WorkOrderSize",String.valueOf(size));
                return true;
            }else{
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    private class Download5 extends AsyncTask<String, Integer, String> {

        private String result;

        public Download5(String result) {
            this.result = result;

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                LOG.E("2");
                JSONObject jsonObject = new JSONObject(result);
                int ErrorCode = jsonObject.getInt("ErrorCode");
                if (ErrorCode == 0) {

                    SharedUtil.setValue(mActivity, "isOrderExist", "isOrderExist");
                    String data = jsonObject.getString("RepairList");
                    JSONArray array = new JSONArray(data);
                    String ID = "";
                    size = array.length();


                    for (int i = 0; i < size; i++) {
                        JSONObject json = array.getJSONObject(i);

                        RepairOrderBean bean = new RepairOrderBean();

                        ID = json.getString("RepairListID");
                        bean.setRepairListID(ID);
                        bean.setRepairListNO(json.getString("RepairListNO"));
                        bean.setMakeTime(json.getString("MakeTime"));

                        bean.setDeviceType(json.getString("DeviceType"));
                        bean.setDeviceID(json.getString("DeviceID"));
                        bean.setDeviceCode(json.getString("DeviceCode"));

                        bean.setRequestMan(json.getString("RequestMan"));
                        bean.setRequestPhone(json.getString("RequestPhone"));
                        bean.setDescription(json.getString("Description"));
                        bean.setLimitTime(json.getString("LimitTime"));
                        bean.setCloseReason(json.getString("CloseReason"));

                        bean.setSendTime(json.getString("SendTime"));
                        bean.setCheckTime(json.getString("CheckTime"));
                        bean.setStartTime(json.getString("StartTime"));
                        bean.setEndTime(json.getString("EndTime"));

                        bean.setIsUrgent(json.getString("IsUrgent"));
                        String sta=json.getString("Status");
                        bean.setStatus(sta);
                        if(sta.equals("1")){
                            status="1";
                        }

                        bean.setFaultType(json.getString("FaultType"));
                        bean.setFault(json.getString("Fault"));
                        bean.setChangeDeviceCode(json.getString("ChangeDeviceCode"));
                        bean.setLastUpdateTime(json.getString("LastUpdateTime"));
                        bean.setAddress(json.getString("Address"));

                        String WorkOrderInfo = json.getString("WorkOrderInfo");

                        JSONObject json2 = new JSONObject(WorkOrderInfo);
                        bean.setWorkSender(json2.getString("Sender"));
                        bean.setWorkSendTime(json2.getString("SendTime"));
                        bean.setWorkOrderNO(json2.getString("WorkOrderNO"));
                        bean.setWorkOrderID(json2.getString("WorkOrderID"));
                        bean.setWorkSendUserID(json2.getString("SendUserID"));
                        bean.setWorkStatus(json2.getString("Status"));

                        bean.setUser(SharedUtil.getValue(mActivity, "UserId"));

                        repairList = DB.findAll(RepairOrderBean.class);
                        if(repairList==null){
                            repairList=new ArrayList<RepairOrderBean>();
                        }
                        LOG.E("repairList:"+repairList.size());
                        for (int j = 0; j < repairList.size(); j++) {
                            if (ID.equals(repairList.get(j).getRepairListID())) {
                                RepairOrderBean person = DB.selector(RepairOrderBean.class).where("RepairListID", "=", ID).findFirst();
                                if(person!=null){
                                    LOG.E("delete:"+person.getRepairListID());
                                    DB.delete(person);
                                }
                            }
                        }
                        DB.save(bean);
                       List<RepairOrderBean>  Listp = DB.findAll(RepairOrderBean.class);
                        LOG.E("Listp:"+Listp.size());
                        for (int j = 0; j <Listp.size() ; j++) {
                            LOG.E("getAddress:"+Listp.get(j).getAddress());
                        }
                    }
                    string = "0";
                }
            } catch (JSONException e) {
                e.printStackTrace();

            } catch (DbException e) {
                e.printStackTrace();
            }

            return string;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.equals("0")) {

                ToastUtil.showShort(mActivity, "更新"+size+"条工单");

                Intent mIntent = new Intent(OrderActivity.ACTION_NAME);
                mActivity.sendBroadcast(mIntent);//发送广播

                if(status.equals("1")) {
                    ShowNotification("系统有新工单派发给您!",R.mipmap.logo);
                }
                DateUtil.getNowDate(mActivity, "OrderUpdateTime");
            }
        }
    }




    //通知栏
    private void ShowNotification(String ContentText,int logo) {

        //第一步：获取NotificationManager
        NotificationManager nm = (NotificationManager) mActivity.getSystemService(Context.NOTIFICATION_SERVICE);

        //第二步：定义Notification
        Intent intent = new Intent(mActivity, OrderListActivity.class);
        intent.putExtra("ordertitle", "已派工单列表");
        intent.putExtra("status", "1");
        //PendingIntent是待执行的Intent
        PendingIntent pi = PendingIntent.getActivity(mActivity, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notification = new Notification.Builder(mActivity)
                .setContentTitle("工单")//设置标题
                .setContentText(ContentText)//设置内容
                .setSmallIcon(logo)  //设置图标
                .setWhen(System.currentTimeMillis())//设置发送时间
                .setAutoCancel(true)//设置点击后通知自动清除
                .setContentIntent(pi)//设置通知将要启动的程序
                .build();

        //把通知放在正在运行栏目中
        //.setDefaults(Notification.DEFAULT_SOUND)//设置默认声音

        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        //第三步：启动通知栏，第一个参数是一个通知的唯一标识
        nm.notify(0, notification);
    }
}
