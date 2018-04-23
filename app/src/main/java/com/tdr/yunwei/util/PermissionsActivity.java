package com.tdr.yunwei.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tdr.yunwei.R;

/**
 * Created by Administrator on 2016/9/6.
 */
public class PermissionsActivity extends Activity{

    private Activity mActivity;

    public static final int REQUEST_CODE = 0;

    public static final int PERMISSIONS_GRANTED = 0; // 权限授权
    public static final int PERMISSIONS_DENIED = 1; // 权限拒绝

    private static final int PERMISSION_REQUEST_CODE = 0; // 系统权限管理页面的参数

    private static final String EXTRA_PERMISSIONS ="extra_permission"; // 权限参数
    PermissionsCheckerUtil pcu;
    private static final String PACKAGE_URL_SCHEME = "package:"; // 方案

    private boolean isRequireCheck; // 是否需要系统权限检测
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() == null || !getIntent().hasExtra(EXTRA_PERMISSIONS)) {
            throw new RuntimeException("PermissionsActivity需要使用静态startActivityForResult方法启动!");
        }

        setContentView(R.layout.activity_permissions);
        mActivity = this;
        ActivityUtil.AddActivity(mActivity);
        pcu = new PermissionsCheckerUtil(this);
        isRequireCheck = true;
        title();
        initView();
    }



    private void initView(){

    }
    // 启动当前权限页面的公开接口
    public static void startActivityForResult(Activity activity, int requestCode, String... permissions) {
        Intent intent = new Intent(activity, PermissionsActivity.class);
        intent.putExtra(EXTRA_PERMISSIONS, permissions);
        ActivityCompat.startActivityForResult(activity, intent, requestCode, null);
    }

    @Override protected void onResume() {
        super.onResume();
        if (isRequireCheck) {
            String[] permissions = getPermissions();
            if (pcu.lacksPermissions()) {
                requestPermissions(permissions); // 请求权限
            } else {
                allPermissionsGranted(); // 全部权限都已获取
            }
        } else {
            isRequireCheck = true;
        }
    }

    // 返回传递的权限参数
    private String[] getPermissions() {
        return getIntent().getStringArrayExtra(EXTRA_PERMISSIONS);
    }


    // 请求权限兼容低版本
    private void requestPermissions(String... permissions) {
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
    }

    // 全部权限均已获取
    private void allPermissionsGranted() {
        setResult(PERMISSIONS_GRANTED);
       ActivityUtil.FinishActivity(mActivity);
    }


    /**
     * 用户权限处理,
     * 如果全部获取, 则直接过.
     * 如果权限缺失, 则提示Dialog.
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && hasAllPermissionsGranted(grantResults)) {
            isRequireCheck = true;
            allPermissionsGranted();
        } else {
            isRequireCheck = false;
            showMissingPermissionDialog();
        }
    }

    // 含有全部的权限
    private boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }


    // 显示缺失权限提示
    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PermissionsActivity.this);
        builder.setTitle("权限缺失提示");
        builder.setMessage("");

        // 拒绝, 退出应用
        builder.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                setResult(PERMISSIONS_DENIED);
                finish();
            }
        });

        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                startAppSettings();
            }
        });

        builder.show();
    }

    // 启动应用的设置
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + getPackageName()));
        startActivity(intent);
    }

    /**
     * 标题栏
     */
    private void title(){
        ImageView img_title= (ImageView) findViewById(R.id.image_back);
        img_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.FinishActivity(mActivity);
            }
        });
        TextView tv_title = (TextView) findViewById(R.id.text_title);
        tv_title.setText("检查权限");

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            ActivityUtil.FinishActivity(mActivity);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
