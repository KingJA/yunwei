package com.tdr.yunwei.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2016/9/13.
 */
public class PhotoUtil {
    // 根据路径获得图片并压缩，返回bitmap用于显示
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options); // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 800, 800); // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    //计算图片的缩放值
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }


    //把bitmap转换成String
    public static String bitmapToString(String filePath, Activity mActivity) {
        Bitmap bm = getSmallBitmap(filePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] b = baos.toByteArray();
        //ToastUtil.ShortCenter(mActivity,baos.toByteArray().length/1024+"KB");
        return Base64.encodeToString(b, Base64.DEFAULT);
    }
    //把bitmap转换成String
    public static String bitmapToString(Bitmap bit, Activity mActivity) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] b = baos.toByteArray();
        //ToastUtil.ShortCenter(mActivity,baos.toByteArray().length/1024+"KB");
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    /* 压缩图片，处理某些手机拍照角度旋转的问题 */
    public static String compressImage(String filePath, String fileName, int q) {
        Bitmap bm = getSmallBitmap(filePath);
        int degree = readPictureDegree(filePath);
        if (degree != 0) {//旋转照片角度
            bm = rotateBitmap(bm, degree);
        }
        File outputFile = new File(filePath, fileName);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(outputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bm.compress(Bitmap.CompressFormat.JPEG, q, out);
        return outputFile.getPath();
    }


    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }


    public static Bitmap rotateBitmap(Bitmap bitmap, int degress) {
        if (bitmap != null) {
            Matrix m = new Matrix();
            m.postRotate(degress);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
            return bitmap;
        }
        return bitmap;
    }


    /*
     * 将base64字符串转换为bitmap  显示在界面上
     */
    public static Bitmap base64toBitmap(String string) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap thumbnailBitmap(Bitmap bitmap) {

        int new_width = 300;
        int new_height = 300;

        Bitmap new_bitmap = ThumbnailUtils.extractThumbnail(//缩略图
                bitmap, new_width, new_height);
        return new_bitmap;
    }
}
