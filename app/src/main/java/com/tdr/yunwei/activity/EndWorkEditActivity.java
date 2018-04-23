package com.tdr.yunwei.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
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
import com.tdr.yunwei.util.SharedUtil;
import com.tdr.yunwei.view.Dialog.DialogUtil;

/**
 * Created by Administrator on 2016/4/20.
 */
public class EndWorkEditActivity extends Activity {
    private Activity mActivity;
    EditText et_endwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_endworkedit);

        mActivity = this;
        ActivityUtil.AddActivity(mActivity);

        initView();
    }

    private void initView() {
        title();

        BtnOK();
    }


    /**
     * 标题栏
     */

    String type = "";
    String beanID="";
    private void title() {

        String title = getIntent().getStringExtra("title");
        type = getIntent().getStringExtra("type");

        ImageView img_title = (ImageView) findViewById(R.id.image_back);
        img_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.CloseActivity(mActivity);
            }
        });
        TextView tv_title = (TextView) findViewById(R.id.text_title);
        tv_title.setText(title);

        et_endwork = (EditText) findViewById(R.id.et_txt);
        et_endwork.setSelection(0);

        beanID=getIntent().getStringExtra("beanID");

        String ID=SharedUtil.getValueByKey(mActivity,"beanID");

        if (type.equals("3")&&beanID.equals(ID)) {
            String str = SharedUtil.getValueByKey(mActivity, "WorkGZContent");
            et_endwork.setText(str);
            et_endwork.setSelection(str.length());
        }
        if(type.equals("4")&&beanID.equals(ID)) {
            String str2 = SharedUtil.getValueByKey(mActivity, "WorkWXContent");
            et_endwork.setText(str2);
            et_endwork.setSelection(str2.length());
        }


    }


    private void BtnOK() {
        Button btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setText("确定");
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = et_endwork.getText().toString().trim();

                if (type.equals("3")) {
                    SharedUtil.setValueByKey(mActivity,"beanID",beanID);
                    SharedUtil.setValueByKey(mActivity, "WorkGZContent", content);
                }
                if (type.equals("4"))  {
                    SharedUtil.setValueByKey(mActivity,"beanID",beanID);
                    SharedUtil.setValueByKey(mActivity, "WorkWXContent", content);
                }

                Intent intent = new Intent();
                intent.putExtra("content", content);
                setResult(RESULT_OK, intent);
                ActivityUtil.FinishActivity(mActivity);

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            DialogUtil.CloseActivity(mActivity);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
