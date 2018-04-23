package com.tdr.yunwei.util;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 处理照片工具类
 * Created by Linus_Xie on 2017/3/16.
 */

public class PhotoUtils {
    public final static int CAMERA_REQESTCODE = 2017;//照相机回调值

    public static File imageFile;//照片临时文件
    public static String mCurrentPhotoPath;//临时文件地址
    public static String mPicName;

    public static void takePicture(Activity mActivity, String picName) {
        mPicName=picName;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(mActivity.getPackageManager()) != null) {//判断是否有相机应用
            try {
                imageFile = createImageFile(mActivity, picName);//创建临时图片文件
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (imageFile != null) {
                Uri photoURI = FileProvider.getUriForFile(mActivity, "com.tdr.yunwei", imageFile);
                List<ResolveInfo> resInfoList = mActivity.getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    mActivity.grantUriPermission(packageName, photoURI,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                mActivity.startActivityForResult(takePictureIntent, CAMERA_REQESTCODE);
            }
        }
    }

    private static File createImageFile(Activity mActivity, String picName) throws IOException {
        File storageDir = mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir, picName + ".jpg");
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
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

    /**
     * 旋转图片
     *
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    /**
     * 读取本地图片
     *
     * @param dst
     * @param width
     * @param height
     * @return
     */
    @SuppressWarnings("deprecation")
    public static Bitmap getBitmapFromFile(File dst, int width, int height) {
        if (null != dst && dst.exists()) {
            BitmapFactory.Options opts = null;
            if (width > 0 && height > 0) {
                opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(dst.getPath(), opts);
                // 计算图片缩放比例
                final int minSideLength = Math.min(width, height);
                opts.inSampleSize = computeSampleSize(opts, minSideLength, width * height);
                opts.inJustDecodeBounds = false;
                opts.inInputShareable = true;
                opts.inPurgeable = true;
            }
            try {
                return BitmapFactory.decodeFile(dst.getPath(), opts);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (true) {
                if (roundedSize >= initialSize)
                    return roundedSize;
                roundedSize <<= 1;
            }
        }

        return 8 * ((initialSize + 7) / 8);
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128
                : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }
}
