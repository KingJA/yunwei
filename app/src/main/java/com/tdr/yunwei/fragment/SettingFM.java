package com.tdr.yunwei.fragment;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tdr.yunwei.R;
import com.tdr.yunwei.YunWeiApplication;
import com.tdr.yunwei.activity.HomeActivity;
import com.tdr.yunwei.activity.LoginActivity;
import com.tdr.yunwei.activity.ModifyPwdActivity;
import com.tdr.yunwei.activity.MyMsgActivity;
import com.tdr.yunwei.activity.UnBindingActivity;
import com.tdr.yunwei.activity.UpdateActivity;
import com.tdr.yunwei.bean.ApkUpdate;
import com.tdr.yunwei.bean.MyMSG;
import com.tdr.yunwei.update.UpdateManager;
import com.tdr.yunwei.util.ActivityUtil;
import com.tdr.yunwei.util.Constants;
import com.tdr.yunwei.util.LOG;
import com.tdr.yunwei.util.SharedUtil;
import com.tdr.yunwei.util.ToastUtil;
import com.tdr.yunwei.util.WebUtil;
import com.tdr.yunwei.view.Dialog.DialogUtil;
import com.tdr.yunwei.view.Dialog.DoOk;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.ex.DbException;

import java.util.HashMap;
import java.util.List;


public class SettingFM extends BaseFragment {

    private View mView;
    private LinearLayout ll_modifypwd, ll_unbinging, ll_exit, ll_updateapp, ll_msgbox;

    TextView txt_version;
    private TextView tv_newmsg;
    private YunWeiApplication YWA;
    public static final int ChangeDate = 1111;
    public static final int Result = 10;
    private RelativeLayout rl_newmsg;


    @Override
    public View initViews() {
        mView = View.inflate(mActivity, R.layout.fm_setting, null);
        YWA = YunWeiApplication.getInstance();
        YWA.setSettingfm(this);
        initView(mView);
        title(mView);
        getRolePowers();
        return mView;
    }

    private void title(View v) {
        ImageView img_title = (ImageView) v.findViewById(R.id.image_back);
        img_title.setVisibility(View.GONE);
        img_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.FinishActivity(mActivity);
            }
        });
        TextView tv_title = (TextView) v.findViewById(R.id.text_title);


        tv_title.setText("设置");

    }

    boolean role41 = false, role42 = false, role43 = false;

    private void getRolePowers() {
        if (SharedUtil.getValue(mActivity, "RolePowers").equals("")) {

        } else {
            String[] RolePowers = SharedUtil.getValue(mActivity, "RolePowers").split(",");

            for (int i = 0; i < RolePowers.length; i++) {

                int role = Integer.valueOf(RolePowers[i]);
                if (role == 41) {
                    role41 = true;
                }
                if (role == 42) {
                    role42 = true;
                }
            }


        }


    }

    private void initView(View v) {
        ll_modifypwd = (LinearLayout) v.findViewById(R.id.ll_modifypwd);
        ll_unbinging = (LinearLayout) v.findViewById(R.id.ll_unbinging);
        ll_exit = (LinearLayout) v.findViewById(R.id.ll_exit);
        ll_updateapp = (LinearLayout) v.findViewById(R.id.ll_updateapp);
        ll_msgbox = (LinearLayout) v.findViewById(R.id.ll_msgbox);
        rl_newmsg  = (RelativeLayout) v.findViewById(R.id.rl_newmsg);

        tv_newmsg = (TextView) v.findViewById(R.id.tv_newmsg);
        MyOnclick myOnclick = new MyOnclick();
        ll_modifypwd.setOnClickListener(myOnclick);
        ll_unbinging.setOnClickListener(myOnclick);
        ll_exit.setOnClickListener(myOnclick);
        ll_updateapp.setOnClickListener(myOnclick);
        ll_msgbox.setOnClickListener(myOnclick);
        mHandler.sendEmptyMessage(ChangeDate);

        txt_version = (TextView) v.findViewById(R.id.txt_version);
        txt_version.setText("当前版本:" + ActivityUtil.GetVersion(mActivity));

    }
    public void setNewMsgNumber(List<MyMSG> msgdate){
        int num=0;
        for (MyMSG m:msgdate){
            if(!m.getIsOld()){
                num++;
            }
        }
        if(num>0){
            LOG.D("消息数量  "+num);
            rl_newmsg.setVisibility(View.VISIBLE);
            tv_newmsg.setText(num+"");
        }else{
            LOG.D("隐藏");
            rl_newmsg.setVisibility(View.GONE);
        }
    }

    private class MyOnclick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v == ll_modifypwd) {
                if (role41) {
                    Intent intent = new Intent(mActivity, ModifyPwdActivity.class);
                    startActivity(intent);


                } else {
                    ToastUtil.showShort(mActivity, "您没有此权限");
                }
            }
            if (v == ll_unbinging) {
                if (role42) {
                    Intent intent = new Intent(mActivity, UnBindingActivity.class);
                    startActivity(intent);
                } else {
                    ToastUtil.showShort(mActivity, "您没有此权限");
                }


            }

            if (v == ll_updateapp) {
                GetVersionCode();
//                GetVersionCodeAsynckTask asynckTask = new GetVersionCodeAsynckTask(mActivity, mHandler);
//                asynckTask.execute(UpdateManager.ApkName);
            }

            if (v == ll_exit) {
                DialogUtil.Show(mActivity, "是否要退出程序", new DoOk() {
                    @Override
                    public void goTodo() {
//                        ActivityUtil.ExitApp(mActivity);
                        SharedUtil.clearUserInfoData(mActivity);
                        SharedUtil.clearDataByKey(mActivity);
                        try {
                            UpdateActivity.clearTable();
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        SharedUtil.clearUserInfoData(mActivity);
                        SharedUtil.clearDataByKey(mActivity);
                        SharedUtil.setValue(mActivity,"LoginName","");
                        ActivityUtil.ExitApp(mActivity);
                        Intent intent=new Intent(mActivity,LoginActivity.class);
                        startActivity(intent);
                        ActivityUtil.FinishActivity(mActivity);
                    }
                });

            }

            if (v == ll_msgbox) {
                    Intent intent = new Intent(mActivity, MyMsgActivity.class);
                    startActivity(intent);
            }


        }
    }


    private void GetVersionCode(){
       String CityID=SharedUtil.getValue(mActivity,"CityID");
        if(CityID.equals("")){
            return;
        }
        HashMap<String, Object> update = new HashMap<String, Object>();
        update.put("areaID", CityID);
        update.put("deviceType", "1");
        WebUtil.getInstance(mActivity).webRequest(Constants.GetAppVersion, update, new WebUtil.MyCallback() {
            @Override
            public void onSuccess(String result) {
                LOG.D("GetVersionCode= "+result);
                try {
                    JSONObject jsb=new JSONObject(result);
                    String VersionID=jsb.getString("VersionID");
                    String UpdateUrl=jsb.getString("UpdateUrl");
                    String ErrorCode=jsb.getString("ErrorCode");
                    String ErrorMsg=jsb.getString("ErrorMsg");
                    if(ErrorCode.equals("0")){
                        Double versioncode=0.0;
                        try{
                            versioncode = Double.parseDouble(VersionID);
                        }catch (Exception e){
                            LOG.E("版本号格式错误"+VersionID);
                        }
                        if(ActivityUtil.GetVersionCode(mActivity)<versioncode){
                            ApkUpdate apkUpdate=new ApkUpdate(VersionID,UpdateUrl,ErrorCode,ErrorMsg);
                            UpdateManager updateManager = new UpdateManager(mActivity,apkUpdate);
                            updateManager.showNoticeDialog();
                        }else{
                            ToastUtil.ErrorOrRight(mActivity, "无新版本发布", 1);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ChangeDate:
                    setNewMsgNumber(YWA.getMsgdate());
                    break;
                case UpdateManager.GetNewCodeSuccess:// 获取版本号成功
//                    double newVersion = Double.valueOf(msg.obj.toString());
//
//                    try {
//                        // 获取软件版本号，对应AndroidManifest.xml下android:versionCode
//                        double versionCode = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), 0).versionCode;
//                        Log.e("versionCode=", versionCode + "//newVersion=" + newVersion);
//
//                        if (versionCode < newVersion) {//有软件更新
//                            UpdateManager updateManager = new UpdateManager(mActivity);
//                            updateManager.showNoticeDialog();
//                        } else {//无软件更新
//                            ToastUtil.ErrorOrRight(mActivity, "无新版本发布", 1);
//                        }
//                    } catch (PackageManager.NameNotFoundException e) {
//                        e.printStackTrace();
//                    }


                    break;
                case UpdateManager.GetNewCodeFail:
                    Log.e("获取失败", "进入程序");
                    ToastUtil.ErrorOrRight(mActivity, "获取版本失败", 1);
                    break;

            }
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        LOG.D("刷新UI");
        mHandler.sendEmptyMessage(ChangeDate);
    }
}
