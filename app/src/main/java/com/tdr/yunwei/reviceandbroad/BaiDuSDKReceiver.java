package com.tdr.yunwei.reviceandbroad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.tdr.yunwei.util.ToastUtil;

/**
 * Created by Administrator on 2016/5/20.
 */
public class BaiDuSDKReceiver extends BroadcastReceiver {


    /**
     * 构造广播监听类，监听SDK key 验证以及网络异常广播
     */

    @Override
    public void onReceive(Context context, Intent intent) {
        String s = intent.getAction();
        Log.e("key", s);
        if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
            Log.e("Pan", "验证出错 key="+s+"  SDK"+SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
//            ToastUtil.ShortCenter(context, "key 验证出错！");
        }
        if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK)) {
            //ToastUtil.ShortCenter(context, "key 验证成功！");
        }
        if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
            ToastUtil.ShortCenter(context, "网络出错！");
        }
    }
}