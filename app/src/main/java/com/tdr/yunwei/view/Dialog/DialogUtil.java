package com.tdr.yunwei.view.Dialog;

import android.app.Activity;
import android.widget.TextView;

import com.tdr.yunwei.util.ActivityUtil;

import java.util.List;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
/**
 * Created by Administrator on 2016/9/8.
 */
public class DialogUtil {

    public static void Show(final Activity mActivity, String content, DoOk onBtnClickL) {
        final NormalDialog dialog = new NormalDialog(mActivity);
        dialog.content(content);

        dialog.setCanceledOnTouchOutside(false);

        dialog.setOnBtnClickL(
                new DoOk() {
                    @Override
                    public void goTodo() {
                        dialog.dismiss();

                    }
                },
                onBtnClickL);


    }


    public static AlertDialog.Builder getAlertDialogBuilder(Context context){
        AlertDialog.Builder dialogBuilder = null;
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH){
            dialogBuilder = new Builder(context);
        }else{
            dialogBuilder = new Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        }
        return dialogBuilder;
    }

    public static void CloseActivity(final Activity mActivity) {
        final NormalDialog dialog = new NormalDialog(mActivity);
        dialog.content("是否确定要退出当前页面？");//

        dialog.setCanceledOnTouchOutside(false);

        dialog.setOnBtnClickL(
                new DoOk() {
                    @Override
                    public void goTodo() {
                        dialog.dismiss();

                    }
                },
                new DoOk() {
                    @Override
                    public void goTodo() {
                        dialog.dismiss();
                        ActivityUtil.FinishActivity(mActivity);
                    }
                });


    }


    //List

    public static void ShowList(Activity mActivity,  TextView textView,List<String> listStr,String title){
        NormalListDialog dialog=new NormalListDialog(mActivity,textView,listStr,title);
        dialog.setCanceledOnTouchOutside(false);
    }
    public static void ShowList(Activity mActivity, TextView textView, List<String> listStr, String title, NormalListDialog.NLDListener NLDL){
        NormalListDialog dialog=new NormalListDialog(mActivity,textView,listStr,title,NLDL);
        dialog.setCanceledOnTouchOutside(false);
    }


}
