package com.tdr.yunwei.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tdr.yunwei.R;
import com.tdr.yunwei.util.ActivityUtil;
import com.tdr.yunwei.util.Constants;
import com.tdr.yunwei.util.SharedUtil;
import com.tdr.yunwei.util.ToastUtil;
import com.tdr.yunwei.util.WebUtil;
import com.tdr.yunwei.util.ZProgressHUD;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.ex.DbException;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/4/20.
 */
public class UnBindingActivity extends Activity {
    private Activity mActivity;
    private ZProgressHUD zProgressHUD;
    private EditText et_oldpwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_unbinding);

        mActivity = this;
        ActivityUtil.AddActivity(mActivity);
        title();
        initView();

        BtnOK();
    }

    private void initView() {
        et_oldpwd = (EditText) findViewById(R.id.et_oldpwd);

    }
    private void BtnOK() {
        Button btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setText("确认解除绑定");
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Unbind();
            }
        });
    }


    /**
     * 标题栏
     */
    private void title() {
        ImageView img_title = (ImageView) findViewById(R.id.image_back);
        img_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.FinishActivity(mActivity);
            }
        });
        TextView tv_title = (TextView) findViewById(R.id.text_title);
        tv_title.setText("手机解除绑定");

    }


    /**
     * 3.4.	手机解除绑定 Unbind
     */

    private void Unbind() {
        zProgressHUD = new ZProgressHUD(mActivity);
        zProgressHUD.setMessage("正在解除绑定...");
        zProgressHUD.show();

        String oldPwd =et_oldpwd.getText().toString().trim();

        if(oldPwd.equals("")){
            zProgressHUD.dismiss();
            ToastUtil.showShort(mActivity, "密码不能为空");
        }else {


            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("accessToken", SharedUtil.getToken(mActivity));
            map.put("pwd", oldPwd);


            WebUtil.getInstance(mActivity).webRequest(Constants.Unbind, map, new WebUtil.MyCallback() {

                @Override
                public void onSuccess(String result) {

                    try {
                        JSONObject jsonObject = new JSONObject(result);

                        String ErrorCode = jsonObject.getString("ErrorCode");
                        String ErrorMsg = jsonObject.getString("ErrorMsg");

                        if (ErrorCode.equals("0")) {
                            ToastUtil.showShort(mActivity, "手机解除绑定成功");

                            SharedUtil.clearUserInfoData(mActivity);
                            SharedUtil.clearDataByKey(mActivity);

//                            UpdateActivity update=new UpdateActivity();
//                            update.clearTable();
                            UpdateActivity.clearTable();
                            SharedUtil.clearUserInfoData(mActivity);
                            SharedUtil.clearDataByKey(mActivity);
                            SharedUtil.setValue(mActivity,"LoginName","");
                            zProgressHUD.dismiss();
                            ActivityUtil.ExitApp(mActivity);
                            Intent intent=new Intent(mActivity,LoginActivity.class);
                            startActivity(intent);
                            ActivityUtil.FinishActivity(mActivity);

                        } else {
                            zProgressHUD.dismiss();
                            ToastUtil.showShort(mActivity, "失败"+ErrorMsg);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
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
