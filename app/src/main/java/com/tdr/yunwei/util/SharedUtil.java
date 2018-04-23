package com.tdr.yunwei.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/20.
 */
public class SharedUtil {


    public static String SharedName="YunWei";
    public static String UserInfo="UserInfo";

    //通过key取值
    public static String getValue(Activity mActivity,String Key) {
        SharedPreferences spf =mActivity.getSharedPreferences(UserInfo, Context.MODE_PRIVATE);
        String value=spf.getString(Key,"");
        return value;
    }

    //通过key存值
    public static void setValue(Activity mActivity,String Key,String Value) {
        SharedPreferences spf =mActivity.getSharedPreferences(UserInfo, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=spf.edit();
        editor.putString(Key,Value);
        editor.commit();
    }
    public static String getToken(Activity mActivity) {
        SharedPreferences spf =mActivity.getSharedPreferences(UserInfo, Context.MODE_PRIVATE);
        String Token=spf.getString("AccessToken","");
        return Token;
    }

    public static void clearUserInfoData(Activity mActivity){
        SharedPreferences spf =mActivity.getSharedPreferences(UserInfo, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=spf.edit();
        editor.clear();
        editor.commit();
        LOG.E("删除UserInfo");
    }





    //通过key取值
    public static String getValueByKey(Activity mActivity,String Key) {
        SharedPreferences spf =mActivity.getSharedPreferences(SharedName, Context.MODE_PRIVATE);
        String value=spf.getString(Key,"");
        return value;
    }

    //通过key存值
    public static void setValueByKey(Activity mActivity,String Key,String Value) {
        SharedPreferences spf =mActivity.getSharedPreferences(SharedName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=spf.edit();
        editor.putString(Key,Value);
        editor.commit();
    }

    public static void clearDataByKey(Activity mActivity){
        SharedPreferences spf =mActivity.getSharedPreferences(SharedName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=spf.edit();
        editor.clear();
        editor.commit();
        LOG.E("删除SharedName");


    }
}
