package com.tdr.yunwei.util;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;

import com.tdr.yunwei.YunWeiApplication;
import com.tdr.yunwei.bean.MyMSG;
import com.tdr.yunwei.bean.ParamBean;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2016/12/23.
 */

public class JPushCustomReceiver  extends BroadcastReceiver {

    private YunWeiApplication YWA= YunWeiApplication.getInstance();
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        LOG.J( "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            LOG.J(  "[MyReceiver] 接收Registration Id : " + regId);
            YWA.setRegistrationID(regId);
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            LOG.J(  "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            LOG.J( "[MyReceiver] 接收到推送下来的通知");

            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            LOG.J(  "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
            updatemsg(context,bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            LOG.J(  "[MyReceiver] 用户点击打开了通知");
            //打开自定义的Activity

        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            LOG.J(  "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            LOG.J(  "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);

        } else {
            LOG.J(  "[MyReceiver] Unhandled intent - " + intent.getAction());

        }
    }
    private void updatemsg(Context context, Bundle bundle) {
        DbManager DB = x.getDb(DBUtils.getDb());
        try {
            List<MyMSG> list = DB.findAll(MyMSG.class);
            if(list==null){
                list=new ArrayList<MyMSG>();
            }
            LOG.E("list="+list.size());
            for (int i = 0; i < list.size(); i++) {
                LOG.E("getID="+list.get(i).getID());
                LOG.E("getIsOld="+list.get(i).getIsOld());
                LOG.E("getUserID="+list.get(i).getUserID());
                LOG.E("getMessageType="+list.get(i).getMessageType());
                LOG.E("getMsgTitle="+list.get(i).getMsgTitle());
                LOG.E("getMsgText="+list.get(i).getMsgText());
                LOG.E("getMsgTime="+list.get(i).getMsgTime());
            }
            MyMSG mymsg = new MyMSG();

            JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
            Iterator<String> it = json.keys();
            while (it.hasNext()) {
                String myKey = it.next().toString();
                if (myKey.equals("Title")) {
                    mymsg.setMsgTitle(json.optString(myKey));
                }
                if (myKey.equals("MSG")) {
                    mymsg.setMsgText(json.optString(myKey));
                }
                if (myKey.equals("MessageType")) {
                    mymsg.setMessageType(json.optString(myKey));
                }
                if (myKey.equals("InTime")) {
                    mymsg.setMsgTime(json.optString(myKey));
                }
                LOG.E(myKey + " === " + json.optString(myKey));
            }

            mymsg.setIsOld(false);
            mymsg.setUserID(SharedUtil.getValue((Activity) context,"UserId"));
            list.add(mymsg);
            list= (List<MyMSG>) DBUtils.SetListID(DB,list,MyMSG.class);
//            for (MyMSG m:list){
//                LOG.E("getID="+m.getID());
//                LOG.E("getIsOld="+m.getIsOld());
//                LOG.E("getMsgTitle="+m.getMsgTitle());
//                LOG.E("getMsgText="+m.getMsgText());
//                LOG.E("getMsgTime="+m.getMsgTime());
//            }
            DB.delete(MyMSG.class);
            DB.save(list);

            YWA.MSGdate();
        } catch (JSONException e) {
            LOG.J("Get message extra JSON error!");
        } catch (DbException e) {
            LOG.J("更新推送数据出错："+e.toString());
        }
    }
    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            }else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    LOG.J( "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it =  json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " +json.optString(myKey) + "]");
                        LOG.E(myKey+" === "+json.optString(myKey));
                    }
                } catch (JSONException e) {
                    LOG.J(  "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

}
