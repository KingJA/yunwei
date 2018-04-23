package com.tdr.yunwei.reviceandbroad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Administrator on 2016/4/13.
 */
public class NetChangeReceiver extends BroadcastReceiver {
    DoNet doNet;

    public NetChangeReceiver(DoNet doNet) {
        this.doNet = doNet;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
            boolean isBreak = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            if (!isBreak) {// 有网络
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = connectivityManager.getActiveNetworkInfo();
                if (info != null && info.isAvailable()) {

                }
            } else {//无网络

                doNet.goToDo();
            }
        }
    }

    public interface DoNet {
        void goToDo();
    }


}