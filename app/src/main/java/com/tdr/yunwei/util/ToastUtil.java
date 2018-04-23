package com.tdr.yunwei.util;


import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tdr.yunwei.R;

public class ToastUtil {
    private static Toast toast=null;


    public static void showShort(Activity act, CharSequence message) {
        if (null == toast) {
            toast = Toast.makeText(act.getApplicationContext(), message, Toast.LENGTH_SHORT);

        }
        else {
            toast.setText(message);
        }
        toast.setGravity(Gravity.BOTTOM, 0, 150);
        toast.show();
    }

    public static void ShortCenter(Context context, CharSequence message) {
        if (null == toast) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);

        } else {
            toast.setText(message);
        }
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

  

   


    public static void showLong(Context context,  CharSequence message) {
        if (null == toast) {
            toast = Toast.makeText(context, message, Toast.LENGTH_LONG);

        } else {
            toast.setText(message);
        }
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }
    public static final void ErrorOrRight(Activity activity, String msg,int num) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_error_layout, null);
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(msg);
        ImageView img_error = (ImageView) layout.findViewById(R.id.mpic_error);
        ImageView img_right = (ImageView) layout.findViewById(R.id.mpic_right);

        if(num==1){
            img_error.setVisibility(View.VISIBLE);
            img_right.setVisibility(View.GONE);
        }
        if(num==2){
            img_error.setVisibility(View.GONE);
            img_right.setVisibility(View.VISIBLE);
        }

        Toast toast = new Toast(activity);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }




}

