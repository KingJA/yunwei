package com.tdr.yunwei.view.validation;

import android.graphics.Color;

/**
 * Created by Administrator on 2014/12/12.
 */
public class MyColors {
  public static boolean isLight(int color) {
    return Math.sqrt(
            Color.red(color) * Color.red(color) * .241 +
                    Color.green(color) * Color.green(color) * .691 +
                    Color.blue(color) * Color.blue(color) * .068) > 130;
  }
}
