package com.tdr.yunwei.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/4/27.
 */
public class MatchUtil {


    public static String picpath = "";
    public static String sdStatus = Environment.getExternalStorageState();

    public static File filepath;

    public static void IsHasSD(Activity mActivity) {

        //判断SD卡是否挂载：
        if (sdStatus.equals(Environment.MEDIA_MOUNTED)) {
            picpath = Environment.getExternalStorageDirectory() + "/yunwei";//
            filepath = new File(picpath);
            if (!filepath.exists()) {
                filepath.mkdirs();
                //ToastUtil.showShort(mActivity,"SD卡文件夹创建成功"+picpath);
            }
        } else {
            ToastUtil.showShort(mActivity, "SD卡异常");
        }
    }

    /**
     * 判断网络有无
     */
    public static boolean isNetTrue(Activity mActivity) {
        boolean isNet = false;
        ConnectivityManager conManager = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = conManager.getActiveNetworkInfo();
        if (network != null) {
            isNet = conManager.getActiveNetworkInfo().isAvailable();
        }
        return isNet;
    }

    /**
     * 判断是否为合法IP
     *
     * @return the ip
     */
    public static boolean isIpTrue(String ipAddress) {
        String ip = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pattern = Pattern.compile(ip);
        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.matches();
    }

    /**
     * 判断是否为合法子网掩码
     *
     * @return the ip
     */
    public static boolean isMask(String mask) {

        String reg = "/^(254|252|248|240|224|192|128|0)\\.0\\.0\\.0$|" +
                "^(255\\.(254|252|248|240|224|192|128|0)\\.0\\.0)$|" +
                "^(255\\.255\\.(254|252|248|240|224|192|128|0)\\.0)$|" +
                "^(255\\.255\\.255\\.(254|252|248|240|224|192|128|0))$/";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(mask);
        return matcher.matches();
    }

    /**
     * 手机正则表达式
     *
     * @return boolean
     */

    public static boolean isMobile(String mobile) {
        boolean flag = false;
        if (mobile.length() == 0) {
            return false;
        }
        String[] mobiles = mobile.split(",");
        int len = mobiles.length;
        if (len == 1) {
            return Pattern.matches("^((13[0-9])|(14[5,7,9])|(15[^4,\\D])|(17[0,1,3,5-8])|(18[0-9]))\\d{8}$", mobile);
        } else {

            for (int i = 0; i < len; i++) {
                if (isMobile(mobiles[i])) {
                    flag = true;
                } else {
                    flag = false;
                }
            }
        }
        return flag;

    }


    // 校验18位身份证号码
    public static boolean isIDCard18(final String value) {
        if (value == null || value.length() != 18)
            return false;
        if (!value.matches("[\\d]+[X]?"))
            return false;
        String code = "10X98765432";
        int weight[] = new int[]{7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5,
                8, 4, 2, 1};
        int nSum = 0;
        for (int i = 0; i < 17; ++i) {
            nSum += (int) (value.charAt(i) - '0') * weight[i];
        }
        int nCheckNum = nSum % 11;
        char chrValue = value.charAt(17);
        char chrCode = code.charAt(nCheckNum);
        if (chrValue == chrCode)
            return true;
        if (nCheckNum == 2 && (chrValue + ('a' - 'A') == chrCode))
            return true;
        return false;
    }


    public static boolean isNum(String str) {

        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;

    }
}
