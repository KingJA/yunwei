package com.tdr.yunwei.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class ActivityCloseUtil {

    public static Bitmap mBitmap = null;
    private static ImageView mTopImage;
    private static int top;
    private static float t = 0;




    public static void startActivity(Activity currActivity, Intent intent) {

        View root = currActivity.getWindow().getDecorView().findViewById(android.R.id.content);
        root.setDrawingCacheEnabled(true);
        mBitmap = root.getDrawingCache();
        top = root.getTop();
    }



    public static void cancel(final Activity destActivity) {
       clean(destActivity);
    }

    public static void prepareAnimation(final Activity destActivity) {
        mTopImage = createImageView(destActivity, mBitmap);
    }


    public static void clean(Activity activity) {

        if (mTopImage != null) {
            mTopImage.setLayerType(View.LAYER_TYPE_NONE, null);
            try {
                activity.getWindowManager().removeViewImmediate(mTopImage);
            } catch (Exception ignored) {
            }
        }

        mBitmap = null;
    }

    private static ImageView createImageView(Activity destActivity, Bitmap bmp) {
        MyImageView imageView = new MyImageView(destActivity);
        imageView.setImageBitmap(bmp);

        WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();
        windowParams.gravity = Gravity.TOP;
        windowParams.x = 0;
        windowParams.y = top;
        windowParams.height = bmp.getHeight();
        windowParams.width = bmp.getWidth();
        windowParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        windowParams.format = PixelFormat.TRANSLUCENT;
        windowParams.windowAnimations = 0;
        destActivity.getWindowManager().addView(imageView, windowParams);

        return imageView;
    }


    public static void animate(final Activity destActivity, final int duration) {

        //make sure it is run in main thread
        new Handler().post(new Runnable() {

            @Override
            public void run() {
                //callback UI thread
                final Handler callBack = new Handler() {

                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case 101:
                                mTopImage.invalidate();
                                break;
                            case 102:
                                clean(destActivity);
                                break;
                        }
                    }
                };

                Thread animationThread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        while (t < 1) {
                            t += 0.08;
                            callBack.sendEmptyMessage(101);

                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        callBack.sendEmptyMessage(102);
                    }
                });

                animationThread.start();

            }
        });
    }

    private static class MyImageView extends ImageView {
        private Matrix mMatrix;
        private Paint mPaint = new Paint();

        public MyImageView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (mMatrix != null)
                mMatrix.reset();
            mMatrix = getMatrixs(canvas.getMatrix(), t, getWidth(), getHeight());
            canvas.drawBitmap(mBitmap, mMatrix, mPaint);

        }

    }

    /**
     * t should be 0~1;
     *
     * @param t
     * @param w
     * @param h
     * @return
     */
    private static Matrix getMatrixs(Matrix matrix, float t, int w, int h) {
        float[] src = new float[8];
        float[] dst = new float[8];

        src[0] = 0;
        src[1] = 0;
        src[2] = w;
        src[3] = 0;
        src[4] = 0;
        src[5] = h;
        src[6] = w;
        src[7] = h;

        dst[0] = 0;
        dst[1] = 0.5f * h * t;
        dst[2] = w;
        dst[3] = 0.5f * h * t;
        dst[4] = 0;
        dst[5] = h - 0.5f * h * t;
        dst[6] = w;
        dst[7] = h - 0.5f * h * t;

        matrix.setPolyToPoly(src, 0, dst, 0, 4);
        return matrix;
    }

}