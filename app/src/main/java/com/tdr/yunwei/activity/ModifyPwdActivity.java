package com.tdr.yunwei.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/4/20.
 */
public class ModifyPwdActivity extends Activity {
    private Activity mActivity;

    private EditText et_oldpwd, et_newpwd, et_newpwd2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_modifypwd);

        mActivity = this;
        ActivityUtil.AddActivity(mActivity);
        title();
        initView();

        BtnOK();
    }

    private void initView() {
        et_oldpwd = (EditText) findViewById(R.id.et_oldpwd);
        et_newpwd = (EditText) findViewById(R.id.et_newpwd);
        et_newpwd2 = (EditText) findViewById(R.id.et_newpwd2);
    }
    private void BtnOK() {
        Button btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setText("确认修改");
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModifyPassword();
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
        tv_title.setText("密码修改");

    }


    /**
     * 3.3.	修改密码 ModifyPassword
     */

    private void ModifyPassword() {

        String oldPwd =et_oldpwd.getText().toString().trim();
        String newPwd=et_newpwd.getText().toString().trim();
        String newPwd2=et_newpwd2.getText().toString().trim();

        if(oldPwd.equals("")){
            ToastUtil.showShort(mActivity, "旧密码不能为空");
        }
        else if (newPwd.equals("")||newPwd2.equals("")){
            ToastUtil.showShort(mActivity, "新密码不能为空");

        }else if (!newPwd.equals(newPwd2)){
            ToastUtil.showShort(mActivity, "两次输入的新密码不一致");
        }
        else {

            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("accessToken", SharedUtil.getToken(mActivity));
            map.put("oldPwd", oldPwd);
            map.put("newPwd", newPwd);

            Log.e("ModifyPassword", "AccessToken222//" + SharedUtil.getToken(mActivity) + "||" + oldPwd + "||"+newPwd);


            WebUtil.getInstance(mActivity).webRequest(Constants.ModifyPassword, map, new WebUtil.MyCallback() {

                @Override
                public void onSuccess(String result) {

                    try {
                        JSONObject jsonObject = new JSONObject(result);

                        String ErrorCode = jsonObject.getString("ErrorCode");
                        String ErrorMsg = jsonObject.getString("ErrorMsg");

                        if (ErrorCode.equals("0")) {
                            ToastUtil.showShort(mActivity, "密码修改成功");
                            Intent intent = new Intent(mActivity, LoginActivity.class);
                            startActivity(intent);
                            ActivityUtil.ExitApp(mActivity);
                        } else {
                            ToastUtil.showShort(mActivity, ErrorMsg);
                        }

                    } catch (JSONException e) {
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
