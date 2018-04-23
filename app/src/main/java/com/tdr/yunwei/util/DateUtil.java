package com.tdr.yunwei.util;

import android.app.Activity;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {
    static String times = "";

    public static String getNowDate() {

        TimeThread t=new TimeThread();
        if(!t.isAlive()){
            t.start();
        }


        return times;
    }


    private static class TimeThread extends Thread{

        @Override
        public void run() {
            try {
                TimeZone.setDefault(TimeZone.getTimeZone("GMT+8")); // 时区设置--北京时间
                URL url = new URL("http://www.114time.com/");       //取得资源对象
                URLConnection uc = url.openConnection();
                uc.connect();                                       //发出连接
                long ld = uc.getDate();                             //取得网站日期时间
                Date date = new Date(ld);                           //转换为标准时间对象
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                times = sdf.format(date);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void getNowDate(final Activity mActivity, final String Key) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeZone.setDefault(TimeZone.getTimeZone("GMT+8")); // 时区设置--北京时间
                    URL url = new URL("http://www.114time.com/");       //取得资源对象
                    URLConnection uc = url.openConnection();
                    uc.connect();                                       //发出连接
                    long ld = uc.getDate();                             //取得网站日期时间
                    Date date =new Date(ld);

                    //转换为标准时间对象
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String times  = sdf.format(date);

                    Log.e("现在网络时间",times+"//ld=="+ld);


                    SharedUtil.setValueByKey(mActivity,Key,times);
                    SharedUtil.setValue(mActivity,Key,times);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }



}
