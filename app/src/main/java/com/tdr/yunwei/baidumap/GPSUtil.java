package com.tdr.yunwei.baidumap;

/**
 * Created by Administrator on 2016/7/29.
 */
public class GPSUtil {

    private static double PI = 3.14159265358979324;
    private static double x_pi = 3.14159265358979324 * 3000.0 / 180.0;

    public static void main(String[] args) {

        double lat=27.498721;
        double lng=120.453218;

        GPSLocation bean=GPSUtil.bd_to_gps(lat,lng);
        System.out.println("百度输入："+lat+","+lng+"     GPS输出："+bean.getLat()+","+bean.getLon());

    }

    protected static GPSLocation delta(double lat, double lon)
    {
        // Krasovsky 1940
        //
        // a = 6378245.0, 1/f = 298.3
        // b = a * (1 - f)
        // ee = (a^2 - b^2) / a^2;
        double a = 6378245.0; //  a: 卫星椭球坐标投影到平面地图坐标系的投影因子。
        double ee = 0.00669342162296594323; //  ee: 椭球的偏心率。
        double dLat = transformLat(lon - 105.0, lat - 35.0);
        double dLon = transformLon(lon - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * PI;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double SqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * SqrtMagic) * PI);
        dLon = (dLon * 180.0) / (a / SqrtMagic * Math.cos(radLat) * PI);
        GPSLocation gps = new GPSLocation();
        gps.setLat(dLat);
        gps.setLon(dLon);
        return gps;
    }

    //WGS-84 to GCJ-02
    /// <summary>
    /// 从GPS坐标转到国测局坐标
    /// </summary>
    /// <param name="wgsLat">纬度</param>
    /// <param name="wgsLon">经度</param>
    /// <returns></returns>
    public static GPSLocation gcj_encrypt(double wgsLat, double wgsLon)
    {
        if (outOfChina(wgsLat, wgsLon)) {
            GPSLocation gps = new GPSLocation();
            gps.setLat(wgsLat);
            gps.setLon(wgsLon);
            return gps;
        }

        GPSLocation d = delta(wgsLat, wgsLon);
        GPSLocation gps = new GPSLocation();
        gps.setLat(wgsLat + d.getLat());
        gps.setLon(wgsLon + d.getLon());
        return gps;
    }

    //GCJ-02 to WGS-84
    /// <summary>
    /// 从国测局坐标转到GPS坐标
    /// </summary>
    /// <param name="gcjLat">纬度</param>
    /// <param name="gcjLon">经度</param>
    /// <returns></returns>
    public static GPSLocation gcj_decrypt(double gcjLat, double gcjLon)
    {
        if (outOfChina(gcjLat, gcjLon)) {

            GPSLocation gps = new GPSLocation();
            gps.setLat(gcjLat);
            gps.setLon(gcjLon);
            return gps;
        }

        GPSLocation d = delta(gcjLat, gcjLon);
        GPSLocation gps = new GPSLocation();
        gps.setLat(gcjLat - d.getLat());
        gps.setLon(gcjLon - d.getLon());
        return gps;
    }

    //GCJ-02 to WGS-84 exactly
    /// <summary>
    /// 从国测局坐标转到GPS坐标,更准确
    /// </summary>
    /// <param name="gcjLat">纬度</param>
    /// <param name="gcjLon">经度</param>
    /// <returns></returns>
    public static GPSLocation gcj_decrypt_exact(double gcjLat, double gcjLon)
    {
        double initDelta = 0.01f;
        double threshold = 0.000000001;
        double dLat = initDelta, dLon = initDelta;
        double mLat = gcjLat - dLat, mLon = gcjLon - dLon;
        double pLat = gcjLat + dLat, pLon = gcjLon + dLon;
        double wgsLat, wgsLon, i = 0;
        while (true)
        {
            wgsLat = (mLat + pLat) / 2;
            wgsLon = (mLon + pLon) / 2;
            GPSLocation tmp = gcj_encrypt(wgsLat, wgsLon);
            dLat = tmp.getLat() - gcjLat;
            dLon = tmp.getLon() - gcjLon;
            if ((Math.abs(dLat) < threshold) && (Math.abs(dLon) < threshold))
                break;

            if (dLat > 0) pLat = wgsLat; else mLat = wgsLat;
            if (dLon > 0) pLon = wgsLon; else mLon = wgsLon;

            if (++i > 10000) break;
        }
        //console.log(i);
        GPSLocation gps = new GPSLocation();
        gps.setLat(wgsLat);
        gps.setLon(wgsLon);
        return gps;
    }

    //GCJ-02 to BD-09
    /// <summary>
    /// 从国测局坐标转到百度坐标
    /// </summary>
    /// <param name="gcjLat">纬度</param>
    /// <param name="gcjLon">经度</param>
    /// <returns></returns>
    public static GPSLocation bd_encrypt(double gcjLat, double gcjLon)
    {
        double x = gcjLon, y = gcjLat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
        double bdLon = z * Math.cos(theta) + 0.0065;
        double bdLat = z * Math.sin(theta) + 0.006;

        GPSLocation gps = new GPSLocation();
        gps.setLat(bdLat);
        gps.setLon(bdLon);
        return gps;
    }

    //WGS-84 to BD-09
    /// <summary>
    /// GPS坐标转到百度坐标
    /// </summary>
    /// <param name="wgsLat">纬度</param>
    /// <param name="wgsLon">经度</param>
    /// <returns></returns>
    public static GPSLocation gps_to_bd(double wgsLat, double wgsLon)
    {
        GPSLocation gcjData = gcj_encrypt(wgsLat, wgsLon);
        return bd_encrypt(gcjData.getLat(), gcjData.getLon());
    }
    //BD-09 to GCJ-02
    /// <summary>
    /// 百度坐标转到国测局坐标
    /// </summary>
    /// <param name="bdLat">纬度</param>
    /// <param name="bdLon">经度</param>
    /// <returns></returns>
    public static GPSLocation bd_decrypt(double bdLat, double bdLon)
    {
        double x = bdLon - 0.0065, y = bdLat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
        double gcjLon = z * Math.cos(theta);
        double gcjLat = z * Math.sin(theta);
        GPSLocation gps = new GPSLocation();
        gps.setLat(gcjLat);
        gps.setLon(gcjLon);
        return gps;
    }

    //BD-09 to WGS-84
    /// <summary>
    /// 百度坐标转到GPS坐标
    /// </summary>
    /// <param name="bdLat">纬度</param>
    /// <param name="bdLon">经度</param>
    /// <returns></returns>
    public static GPSLocation bd_to_gps(double bdLat, double bdLon)
    {
        GPSLocation bdGCJ = bd_decrypt(bdLat, bdLon);
        return gcj_decrypt(bdGCJ.getLat(), bdGCJ.getLon());
    }

    //WGS-84 to Web mercator
    //mercatorLat -> y mercatorLon -> x
    /// <summary>
    /// GPS坐标转平面坐标
    /// </summary>
    /// <param name="wgsLat">纬度</param>
    /// <param name="wgsLon">经度</param>
    /// <returns></returns>
    public static GPSLocation mercator_encrypt(double wgsLat, double wgsLon)
    {
        double x = wgsLon * 20037508.34 / 180.0;
        double y = Math.log(Math.tan((90.0 + wgsLat) * PI / 360.0)) / (PI / 180.0);
        y = y * 20037508.34 / 180.0;
        GPSLocation gps = new GPSLocation();
        gps.setLat(y);
        gps.setLon(x);
        return gps;

            /*
            if ((Math.abs(wgsLon) > 180 || Math.abs(wgsLat) > 90))
                return null;
            var x = 6378137.0 * wgsLon * 0.017453292519943295;
            var a = wgsLat * 0.017453292519943295;
            var y = 3189068.5 * Math.log((1.0 + Math.Sin(a)) / (1.0 - Math.Sin(a)));
            return {'lat' : y, 'lon' : x};
            //*/
    }

    // Web mercator to WGS-84
    // mercatorLat -> y mercatorLon -> x
    /// <summary>
    /// 平面坐标车GPS坐标
    /// </summary>
    /// <param name="mercatorLat">纬度</param>
    /// <param name="mercatorLon">经度</param>
    /// <returns></returns>
    public static GPSLocation mercator_decrypt(double mercatorLat, double mercatorLon)
    {
        double x = mercatorLon / 20037508.34 * 180.0;
        double y = mercatorLat / 20037508.34 * 180.0;
        y = 180 / PI * (2 * Math.atan(Math.exp(y * PI / 180.0)) - PI / 2);

        GPSLocation gps = new GPSLocation();
        gps.setLat(y);
        gps.setLon(x);
        return gps;

            /*
            if (Math.abs(mercatorLon) < 180 && Math.abs(mercatorLat) < 90)
                return null;
            if ((Math.abs(mercatorLon) > 20037508.3427892) || (Math.abs(mercatorLat) > 20037508.3427892))
                return null;
            var a = mercatorLon / 6378137.0 * 57.295779513082323;
            var x = a - (Math.floor(((a + 180.0) / 360.0)) * 360.0);
            var y = (1.5707963267948966 - (2.0 * Math.atan(Math.exp((-1.0 * mercatorLat) / 6378137.0)))) * 57.295779513082323;
            return {'lat' : y, 'lon' : x};
            //*/
    }
    // two point's distance
    /// <summary>
    /// 计算两点的直线距离
    /// </summary>
    /// <param name="latA">A点纬度</param>
    /// <param name="lonA">A点经度</param>
    /// <param name="latB">B点纬度</param>
    /// <param name="lonB">B点经度</param>
    /// <returns></returns>
    public static double distance(double latA, double lonA, double latB, double lonB)
    {
        double earthR = 6371000.0;
        double x = Math.cos(latA* PI / 180.0) * Math.cos(latB * PI / 180.0) * Math.cos((lonA - lonB) * PI / 180.0);
        double y = Math.sin(latA * PI / 180.0) * Math.sin(latB * PI / 180.0);
        double s = x + y;
        if (s > 1) s = 1;
        if (s < -1) s = -1;
        double alpha = Math.acos(s);
        double distance = alpha * earthR;
        return distance;
    }

    protected static boolean outOfChina(double lat, double lon)
    {
        if (lon < 72.004 || lon > 137.8347)
            return true;
        if (lat < 0.8293 || lat > 55.8271)
            return true;
        return false;
    }

    protected static double transformLat(double x, double y)
    {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * PI) + 40.0 * Math.sin(y / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * PI) + 320 * Math.sin(y * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    protected static double transformLon(double x, double y)
    {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * PI) + 40.0 * Math.sin(x / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * PI) + 300.0 * Math.sin(x / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
    }

}

