package com.tdr.yunwei.view.validation;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;


public class MyDensity {
  public static int dp2px(Context context, float dp) {
    Resources r = context.getResources();
    float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    return Math.round(px);
  }
}
