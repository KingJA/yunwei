package com.tdr.yunwei.util;

import android.app.Activity;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.ServiceConnection;
import org.ksoap2.transport.ServiceConnectionSE;
import org.xutils.x;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebUtil {
    private static Activity mActivity;
    private static WebUtil instance;
    private static ExecutorService cachedThreadPool;

    private WebUtil() {
    }


    public static WebUtil getInstance(Activity mActivity) {
        WebUtil.mActivity = mActivity;
        if (WebUtil.instance == null) {
            cachedThreadPool = Executors.newCachedThreadPool();
            return new WebUtil();
        }
        return instance;
    }

    public void webRequest(final String name, final HashMap<String, Object> map, final MyCallback callback) {
        try {
            LOG.D("上传数据 "+name+"      "+map.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final String webJson = info(name, map);

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.onSuccess(webJson);
                                LOG.L( name + "----" + webJson);
                            }
                        }

                    });


                } catch (SocketTimeoutException timeoutException) {
                    timeoutException.printStackTrace();
                    mActivity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.onSuccess("-1");
                            }
                            ToastUtil.showLong(mActivity, "网络连接超时");
                        }
                    });


                } catch (ConnectException connectException) {
                    connectException.printStackTrace();
                    mActivity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.onSuccess("-1");
                            }
                            ToastUtil.showLong(mActivity, "网络连接错误");
                        }
                    });


                } catch (final Exception e) {
                    e.printStackTrace();
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.onSuccess("-1");
                            }
                            LOG.L("异常信息："+ e.toString());
                            ToastUtil.showLong(mActivity, "异常信息：" + e.toString());
                        }
                    });


                }

            }
        });
    }


    public interface MyCallback {
        void onSuccess(String result);
    }


    //发送数据以及接收返回的数据
    public  String info(String name,Map<String,Object> param)throws Exception {
        SoapObject request = new SoapObject(Constants.WEB_NAMESPACE,name);

        for(Map.Entry<String,Object> entry:param.entrySet()){
            request.addProperty(entry.getKey(),entry.getValue());
        }

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.bodyOut = request;

        MyHttpTransportSE ht = new MyHttpTransportSE(Constants.WEB_URL);
        ht.call(Constants.WEB_NAMESPACE+name, envelope);

        // 获取返回的结果

        SoapPrimitive soapPrimitive = (SoapPrimitive) envelope.getResponse();
        LOG.D(name+"-----"+soapPrimitive.toString());
        return soapPrimitive.toString();

    };


    //设置网络连接路径以及超时
    public class MyHttpTransportSE extends HttpTransportSE {

        private int timeout = 60*1000;//20秒



        public MyHttpTransportSE(String url) {
            super(url);
        }


        protected ServiceConnection getServiceConnection() throws IOException {
            ServiceConnectionSE serviceConnection = new ServiceConnectionSE(
                    this.url, timeout);
            return serviceConnection;
        }
    }
}
