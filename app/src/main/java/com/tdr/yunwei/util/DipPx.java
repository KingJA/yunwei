package com.tdr.yunwei.util;

import android.app.Activity;
import android.content.Context;
import android.view.WindowManager;

public class DipPx {

	public static int dip2px(Activity mActivity, float dpValue) {
		final float scale = mActivity.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}


	public static int px2dip(Activity mActivity, float pxValue) {
		final float scale = mActivity.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	

	public static int getScreenWidth(Activity mActivity) {
		WindowManager wm = (WindowManager) mActivity
                .getSystemService(Context.WINDOW_SERVICE);
		return wm.getDefaultDisplay().getWidth();
	}
	

	public static int getScreenHeight(Activity mActivity) {
		WindowManager wm = (WindowManager) mActivity
                .getSystemService(Context.WINDOW_SERVICE);
		return wm.getDefaultDisplay().getHeight();
	}
}
