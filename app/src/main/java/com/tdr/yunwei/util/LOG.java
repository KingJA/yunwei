package com.tdr.yunwei.util;

import android.util.Log;

/**
 * Created by Administrator on 2016/12/23.
 */

public class LOG {
    private static boolean isdebug =true;
    private static String TAG="PanZX";
    private static String TAG2="PanZX_Http";
    private static String TAG3="PanZX_JPush";
    public static void D(String msg){
        if(isdebug){
            Log.d(TAG,msg);
        }
    }
    public static void L(String msg){
        if(isdebug){
            Log.e(TAG2,msg);
        }
    }
    public static void J(String msg){
        if(isdebug){
            Log.d(TAG3,msg);
        }
    }
    public static void E(String msg){
        if(isdebug){
            Log.e(TAG,msg);
        }
    }
    //规定每段显示的长
    private static int LOG_MAXLENGTH = 1000;

    public static void LE(String msg) {
        if(msg==null){
            msg="";
        }
        int strLength = msg.length();
        int start = 0;
        int end = LOG_MAXLENGTH;
        for (int i = 0; i < 1000; i++) {
            //剩下的文本还是大于规定长度则继续重复截取并输出
            if (strLength > end) {
                Log.e(TAG + i, msg.substring(start, end));
                start = end;
                end = end + LOG_MAXLENGTH;
            } else {
                Log.e(TAG, msg.substring(start, strLength));
                break;
            }
        }
    }
}
