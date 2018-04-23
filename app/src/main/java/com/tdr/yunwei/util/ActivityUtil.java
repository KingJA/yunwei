package com.tdr.yunwei.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;


import com.tdr.yunwei.YunWeiApplication;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActivityUtil {

    public static ArrayList<Activity> activitys = new ArrayList<Activity>();
    public  static String CityID="";
    public static void AddActivity(Activity act) {
        activitys.add(act);

        Log.e("ActivityUtil", "activitys.size()==" + activitys.size() + "//act==" + act);
    }

    public static void ExitApp(Activity act) {
        for (Activity activity : activitys) {
            activity.finish();
        }
        activitys = new ArrayList<Activity>();
    }

    public static void FinishActivity(Activity act) {
        activitys.remove(act);
        act.finish();
    }

    public static String GetVersion(Activity act) {
        String version="";
        try {
            version = act.getPackageManager().getPackageInfo(act.getPackageName(), 0).versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    public static double GetVersionCode(Activity act) {
        double versionCode=1;
        try {
            versionCode = act.getPackageManager().getPackageInfo(act.getPackageName(), 0).versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }
    public static String getFirstLetter(String pinyin) {
        if (TextUtils.isEmpty(pinyin)) return null;
        return pinyin.substring(0, 1);
    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void SaveAddress(Activity mActivity, String address, String deviceid){
        try {
            String js=SharedUtil.getValue(mActivity,"DeviceAddressList");
            JSONArray JA;
            JSONObject JB=new JSONObject();
            JSONObject JB2;
            if(js.equals("")){
                JB2 =new JSONObject();
                JA=new JSONArray();
            }else{
                JB2 =new JSONObject(js);
                JA=JB2.getJSONArray("DeviceInfo");
            }
            if(JA.length()>29){
                JA.remove(0);
            }
            JB.put("address",address);
            JB.put("deviceid",deviceid);
            JA.put(JB);
            JB2=new JSONObject();
            JB2.put("DeviceInfo",JA);

            SharedUtil.setValue(mActivity,"DeviceAddressList",JB2.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static boolean CheckAddressRepeated(Activity mActivity,String address){
        boolean IsRepeat=false;

        try {
            String js= SharedUtil.getValue(mActivity,"DeviceAddressList");
            if(js.equals("")){
                return false;
            }
            JSONObject JSB=new JSONObject(js);
            LOG.D("获取设备信息"+JSB);
            JSONArray JSA=JSB.getJSONArray("DeviceInfo");
            if(JSA==null){
                IsRepeat=false;
            }
            JSONObject JSB2;
            for (int i = 0; i < JSA.length(); i++) {
                JSB2=new JSONObject(JSA.get(i).toString());
                if(JSB2.get("address").equals(address)){
                    IsRepeat=true;
                    i=JSA.length();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return IsRepeat;
    }
    public static boolean CheckAddressRepeated2(Activity mActivity,String address,String deviceid){
        boolean IsRepeat=false;
        String DeviceAddress="";
        try {
            String js=SharedUtil.getValue(mActivity,"DeviceAddressList");
            JSONObject JSB=new JSONObject(js);
            if(js.equals("")){
                return false;
            }
            LOG.D("获取设备信息"+JSB);
            JSONArray JSA=JSB.getJSONArray("DeviceInfo");
            if(JSA==null){
                IsRepeat=false;
            }
            JSONObject JSB2;
            for (int i = 0; i < JSA.length(); i++) {
                JSB2=new JSONObject(JSA.get(i).toString());
                if(JSB2.get("deviceid").equals(deviceid)){
                    DeviceAddress=JSB2.get("address").toString();
                    i=JSA.length();
                }
            }
        if(!DeviceAddress.equals("")){
            if(DeviceAddress.equals(address)){
                IsRepeat=true;
            }
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return IsRepeat;
    }
    @SuppressLint("MissingPermission")
    public static String getDevicePhoneNumber(Activity mActivity) {
        TelephonyManager telephonyManager= (TelephonyManager) mActivity
                .getSystemService(mActivity.TELEPHONY_SERVICE);
        String NativePhoneNumber="";
        NativePhoneNumber=telephonyManager.getLine1Number();
        LOG.D("获取本机号码="+NativePhoneNumber);
        return NativePhoneNumber;
    }

    public static String getPinYinHeadChar(String chines) {
        StringBuffer sb = new StringBuffer();
        sb.setLength(0);
        char[] chars = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] > 128) {
                try {
                    sb.append(PinyinHelper.toHanyuPinyinStringArray(chars[i], defaultFormat)[0].charAt(0));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                sb.append(chars[i]);
            }
        }
        return sb.toString();
    }

    /**
     * 获取汉字字符串的第一个字母
     */
    public static String getPinYinFirstLetter(String str) {
        StringBuffer sb = new StringBuffer();
        sb.setLength(0);
        char c = str.charAt(0);
        String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c);
        if (pinyinArray != null) {
            sb.append(pinyinArray[0].charAt(0));
        } else {
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * 获取汉字字符串的汉语拼音，英文字符不变
     */
    public static String getPinYin(String chines) {
        StringBuffer sb = new StringBuffer();
        sb.setLength(0);
        char[] nameChar = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < nameChar.length; i++) {
            if (nameChar[i] > 128) {
                try {
                    sb.append(PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat)[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                sb.append(nameChar[i]);
            }
        }
        return sb.toString();
    }
    public static String getZoom(float zoom) {
        String Zoom = "";
        if (zoom == 3) {
            Zoom = "(精确到2000公里)";
        }
        if (zoom == 4) {
            Zoom = "(精确到1000公里)";
        }
        if (zoom == 5) {
            Zoom = "(精确到500公里)";
        }
        if (zoom == 6) {
            Zoom = "(精确到200公里)";
        }
        if (zoom == 7) {
            Zoom = "(精确到100公里)";
        }
        if (zoom == 8) {
            Zoom = "(精确到50公里)";
        }
        if (zoom == 9) {
            Zoom = "(精确到25公里)";
        }
        if (zoom == 10) {
            Zoom = "(精确到20公里)";
        }
        if (zoom == 11) {
            Zoom = "(精确到10公里)";
        }
        if (zoom == 12) {
            Zoom = "(精确到5公里)";
        }
        if (zoom == 13) {
            Zoom = "(精确到2公里)";
        }
        if (zoom == 14) {
            Zoom = "(精确到1公里)";
        }
        if (zoom == 15) {
            Zoom = "(精确到500米)";
        }
        if (zoom == 16) {
            Zoom = "(精确到200米)";
        }
        if (zoom == 17) {
            Zoom = "(精确到100米)";
        }
        if (zoom == 18) {
            Zoom = "(精确到50米)";
        }
        if (zoom == 19) {
            Zoom = "(精确到20米)";
        }
        if (zoom == 20) {
            Zoom = "(精确到10米)";
        }
        if (zoom == 21) {
            Zoom = "(精确到5米)";
        }
        return Zoom;
    }
    //    private void showPopupWindow() {
//        // 一个自定义的布局，作为显示的内容
////        View contentView = LayoutInflater.from(mActivity).inflate(R.layout.pop_window, null);
//        View v = new LinearLayout(mActivity);
//        v.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 600));
//        v.setBackgroundColor(Color.parseColor("#5500ff00"));
//        PopupWindow popupwindow = new PopupWindow(v, LinearLayout.LayoutParams.MATCH_PARENT
//                , LinearLayout.LayoutParams.MATCH_PARENT - DipPx.dip2px(mActivity, 44.0f), true);
//        popupwindow.setTouchable(true);
//        popupwindow.setOutsideTouchable(true);
//        popupwindow.setBackgroundDrawable(new BitmapDrawable());
//        popupwindow.setTouchInterceptor(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return false;
//            }
//        });
//        popupwindow.setAnimationStyle(R.style.changebkgAnim);
//        popupwindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
////        popupwindow.showAtLocation(rl_search, Gravity.TOP, 0, DipPx.dip2px(mActivity,44.0f));
//        popupwindow.showAsDropDown(rl_search);
//    }
    public static Context getContext() {
        return YunWeiApplication.getContext();
    }
    /**
     * dp转换为px
     *
     * @param dp
     * @return
     */
    public static int dp2px(int dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5);
    }

    /**
     * px转换为dp
     *
     * @param px
     * @return
     */
    public static int px2dp(int px) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (px / density + 0.5);
    }

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    public static int getScreenWidth() {
        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @return
     */
    public static int getScreenHeight() {
        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

}
