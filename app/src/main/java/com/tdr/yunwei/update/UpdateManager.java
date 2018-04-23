package com.tdr.yunwei.update;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.PaintDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tdr.yunwei.R;
import com.tdr.yunwei.activity.LoginActivity;
import com.tdr.yunwei.bean.ApkUpdate;
import com.tdr.yunwei.util.LOG;
import com.tdr.yunwei.util.ToastUtil;


import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

/**
 * Created by Linus_Xie on 2016/3/12.
 */
public class UpdateManager {


    //、、、、、、、、、、、、、、、、需要修改的地方
    public static final String ApkName = "YunWei.apk";
    //、、、、、、、、、、、、、、、、


    public static final String WEBSERVER_NAMESPACE = "http://tempuri.org/";
    public static final String UPDATE_SERVER = "http://dmi.tdr-cn.com/newestapk/" + ApkName;// APK下载地址
    public static final String WEBSERVER_URL = "http://dmi.tdr-cn.com/WebServiceAPKRead.asmx";// 获取版本号Webservice方法

    public static final String GetCode = "GetCode";

    public static final int GetNewCodeSuccess = 0;
    public static final int GetNewCodeFail = 1;
    public static final int isCancel = 0;


    /* 下载中 */
    private static final int DOWNLOAD = 1;
    /* 下载结束 */
    private static final int DOWNLOAD_FINISH = 2;
    private ApkUpdate mApkUpdate;
    /* 下载保存路径 */
    /* 记录进度条数量 */
    private int progress;
    /* 是否取消更新 */
    private boolean cancelUpdate = false;

    private Activity mActivity;
    /* 更新进度条 */
    private ProgressBar mProgress;
    private Dialog mDownloadDialog;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 正在下载
                case DOWNLOAD:
                    // 设置进度条位置
                    mProgress.setProgress(progress);
                    break;
                case DOWNLOAD_FINISH:
                    // 安装文件
                    installApk();
                    break;

                default:
                    break;
            }
        }

        ;
    };


    public UpdateManager(Activity mActivity) {
        this.mActivity = mActivity;
        IsHasSD();

    }

    public UpdateManager(Activity mActivity, ApkUpdate apkUpdate) {
        this.mActivity = mActivity;
        this.mApkUpdate = apkUpdate;
        IsHasSD();

    }


    public String mSavePath = Environment.getExternalStorageDirectory() + "/app_yunwei";
    public String sdStatus = Environment.getExternalStorageState();

    public File filepath;

    public void IsHasSD() {

        //判断SD卡是否挂载：
        if (sdStatus.equals(Environment.MEDIA_MOUNTED)) {

            filepath = new File(mSavePath);
            if (!filepath.exists()) {
                filepath.mkdir();
                if (filepath.mkdir()) {
                    //ToastUtil.showShort(mActivity, "SD卡文件夹创建成功" + mSavePath);
                }

            }
        } else {
            ToastUtil.showShort(mActivity, "SD卡异常");
        }
    }

    /**
     * 显示软件更新对话框
     */
    TextView txt_content, txt_cancel;

    LinearLayout ll_ok;
    Dialog D;
    public void showNoticeDialog() {
        View contentView = LayoutInflater.from(mActivity).inflate(
                R.layout.update_app, null);

        D = new Dialog(mActivity);
        D.setCanceledOnTouchOutside(false);

        txt_content = (TextView) contentView.findViewById(R.id.txt_content);
        TextView update_cancel = (TextView) contentView.findViewById(R.id.update_cancel);
        TextView update_ok = (TextView) contentView.findViewById(R.id.update_ok);

        txt_cancel = (TextView) contentView.findViewById(R.id.txt_cancel);
        ll_ok = (LinearLayout) contentView.findViewById(R.id.ll_ok);


        //软件更新描述
        txt_content.setText(mApkUpdate.getErrorMsg());


        update_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFile();
            }
        });

        update_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                D.dismiss();
            }
        });

        txt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                D.dismiss();
            }
        });
        D.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK&& event.getAction() == KeyEvent.ACTION_UP) {
                    ToastUtil.showShort(mActivity,"系统正在更新...");
                }
                return true;
            }
        });
        D.setContentView(contentView);
        D.show();
    }

    private void downloadFile() {
        String url = mApkUpdate.getUpdateUrl();
        String path = mSavePath + "/" + ApkName;
        LOG.D("url=" + url);
        LOG.D("path=" + path);
        RequestParams requestParams = new RequestParams(url);
        requestParams.setSaveFilePath(path);
        x.http().get(requestParams, new Callback.ProgressCallback<File>() {
            @Override
            public void onWaiting() {
            }

            @Override
            public void onStarted() {
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                ll_ok.setVisibility(View.GONE);
                String CURRENT = String.format("%.2f", (Double.valueOf(current) / 1024 / 1024)) + "MB";
                String TOTAL = String.format("%.2f", (Double.valueOf(total) / 1024 / 1024)) + "MB";
                txt_content.setText("正在下载，进度：" + CURRENT + "/" + TOTAL);
            }

            @Override
            public void onSuccess(File result) {
                mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                D.dismiss();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
                ToastUtil.ErrorOrRight(mActivity, "下载失败，请检查网络和SD卡" + ex.toString(), 1);
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }
//    private void downApk() {
//        FinalHttp fh = new FinalHttp();
//
//        fh.download(mApkUpdate.getUpdateUrl(), mSavePath + "/" + ApkName, new AjaxCallBack<File>() {
//            @Override
//            public void onFailure(Throwable t, int errorNo, String strMsg) {
//                super.onFailure(t, errorNo, strMsg);
//                ToastUtil.ErrorOrRight(mActivity, "失败:读取SD卡权限没有赋予" + strMsg + "--\n" + mSavePath + "/" + ApkName, 2);
//            }
//
//            @Override
//            public void onLoading(long count, long current) {
//                super.onLoading(count, current);
//
//                ll_ok.setVisibility(View.GONE);
//
//                String min = String.format("%.2f", (Double.valueOf(current) / 1024 / 1024)) + "MB";
//                String total = String.format("%.2f", (Double.valueOf(count) / 1024 / 1024)) + "MB";
//                txt_content.setText("正在下载，进度：" + min + "/" + total);
//            }
//
//            @Override
//            public void onSuccess(File file) {
//                super.onSuccess(file);
//                mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
//                popWin.dismiss();
//            }
//
//
//        });
//    }


    /**
     * 安装APK文件
     */
    private void installApk() {
        File apkfile = new File(mSavePath, ApkName);
        if (!apkfile.exists()) {
            return;
        }
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");
        mActivity.startActivity(i);
    }
}
