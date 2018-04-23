package com.tdr.yunwei.update;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;


/**
 * Created by Linus_Xie on 2016/3/12.
 */
public class GetVersionCodeAsynckTask extends AsyncTask<String, Integer, Boolean> {

    private Context mContext;
    private Handler mHander;
    private VersionInfo versionInfo;

    public GetVersionCodeAsynckTask(Context mContext, Handler mHander) {
        this.mContext = mContext;
        this.mHander = mHander;
    }

    @Override
    protected Boolean doInBackground(String... params) {

        try {
            versionInfo = getVersionCode(params[0]);

            if (versionInfo != null ) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (result) {
            Message msg = mHander.obtainMessage();
            msg.obj = versionInfo.getVersionCode();
            msg.what = UpdateManager.GetNewCodeSuccess;
            mHander.sendMessage(msg);
        } else {
            mHander.sendEmptyMessage(UpdateManager.GetNewCodeFail);
        }
    }

    protected void onPreExecute() {
        super.onPreExecute();
    }


    /**
     * 获取版本号
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    public VersionInfo getVersionCode(String fileName) throws Exception {

        SoapObject request = new SoapObject(UpdateManager.WEBSERVER_NAMESPACE, UpdateManager.GetCode);


        request.addProperty("FileName", fileName);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.bodyOut = request;

        MyHttpTransportSE ht = new MyHttpTransportSE(UpdateManager.WEBSERVER_URL);
        ht.call(UpdateManager.WEBSERVER_NAMESPACE + UpdateManager.GetCode, envelope);
        String result = ((SoapPrimitive) envelope.getResponse()).toString();
        Log.e("VersionInfo", result);
        return initVersionInfo(result);
    }

    public VersionInfo initVersionInfo(String response) {
        try {
            JSONArray objs = new JSONArray(response);
            JSONObject jsonObject = objs.getJSONObject(0);
            VersionInfo info = new VersionInfo();
            info.setVersionCode(jsonObject.getDouble("android:versionCode"));
            info.setVersionName(jsonObject.getString("android:versionName"));
            info.setPackageName(jsonObject.getString("package"));
            return info;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
