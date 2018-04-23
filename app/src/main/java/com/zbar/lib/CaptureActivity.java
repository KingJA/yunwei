package com.zbar.lib;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tdr.yunwei.R;
import com.tdr.yunwei.util.ActivityUtil;
import com.tdr.yunwei.util.ToastUtil;
import com.tdr.yunwei.view.Dialog.DialogUtil;
import com.tdr.yunwei.view.Dialog.DoOk;
import com.zbar.lib.camera.CameraManager;
import com.zbar.lib.decode.CaptureActivityHandler;
import com.zbar.lib.decode.InactivityTimer;

import java.io.IOException;

/**
 * 作者: 陈涛(1076559197@qq.com)
 * <p/>
 * 时间: 2014年5月9日 下午12:25:31
 * <p/>
 * 版本: V_1.0.0
 * <p/>
 * 描述: 扫描界面
 */
public class CaptureActivity extends Activity implements Callback {

    private CaptureActivityHandler handler;
    private boolean hasSurface;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.50f;
    private boolean vibrate;
    private int x = 0;
    private int y = 0;
    private int cropWidth = 0;
    private int cropHeight = 0;
    private RelativeLayout mContainer = null;
    private RelativeLayout mCropLayout = null;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getCropWidth() {
        return cropWidth;
    }

    public void setCropWidth(int cropWidth) {
        this.cropWidth = cropWidth;
    }

    public int getCropHeight() {
        return cropHeight;
    }

    public void setCropHeight(int cropHeight) {
        this.cropHeight = cropHeight;
    }

    Activity mActivity;
    String DeviceCode="";

    LinearLayout ll_input;

    EditText et_code;
    TextView txt_title;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.zbar_activity);
        mActivity = CaptureActivity.this;
        ActivityUtil.AddActivity(mActivity);

        // 初始化 CameraManager
        CameraManager.init(getApplication());
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);

        mContainer = (RelativeLayout) findViewById(R.id.capture_containter);
        mCropLayout = (RelativeLayout) findViewById(R.id.capture_crop_layout);

        ImageView mQrLineView = (ImageView) findViewById(R.id.capture_scan_line);
        ScaleAnimation animation = new ScaleAnimation(1.0f, 1.0f, 0.0f, 1.0f);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(1200);
        mQrLineView.startAnimation(animation);


        TextView btn_shoudong = (TextView) findViewById(R.id.btn_shoudong);
        btn_shoudong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Show();
            }
        });


        ll_input = (LinearLayout) findViewById(R.id.ll_input);

        ImageView img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_input.setVisibility(View.GONE);
                mContainer.setVisibility(View.VISIBLE);
            }
        });


        et_code = (EditText) findViewById(R.id.et_code);
        et_code.setInputType(EditorInfo.TYPE_CLASS_NUMBER);

        TextView txt_ok = (TextView) findViewById(R.id.txt_ok);
        txt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DeviceCode= et_code.getText().toString();
                if(!DeviceCode.equals("")){

                    DialogUtil.Show(mActivity, "您输入的设备编码是\n\n" + DeviceCode, new DoOk() {
                        @Override
                        public void goTodo() {
                            Intent intent = new Intent();
                            intent.putExtra("DeviceCode", DeviceCode);
                            intent.putExtra("inputtype", "shoudong");
                            setResult(RESULT_OK, intent);
                            ActivityUtil.FinishActivity(mActivity);
                        }
                    });


                }

                else {
                    ToastUtil.ErrorOrRight(mActivity,"设备编号不能为空",1);

                }

            }
        });

        txt_title=(TextView)findViewById(R.id.txt_title);

    }


    public void Show() {
        View contentView = LayoutInflater.from(mActivity).inflate(R.layout.pop_shoudong, null);

        TextView txt_xuhao = (TextView) contentView.findViewById(R.id.txt_xuhao);
        TextView txt_code = (TextView) contentView.findViewById(R.id.txt_code);

        final PopupWindow popWin = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);


        popWin.setTouchable(true);
        popWin.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        popWin.setTouchInterceptor(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return false;
            }
        });


        popWin.setBackgroundDrawable(mActivity.getResources().getDrawable(R.color.transparent));


        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 0.4f;
        mActivity.getWindow().setAttributes(lp);
        popWin.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
                lp.alpha = 1f;
                mActivity.getWindow().setAttributes(lp);

            }
        });



        txt_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWin.dismiss();
                txt_title.setText("设备编号");
                mContainer.setVisibility(View.GONE);
                ll_input.setVisibility(View.VISIBLE);


            }
        });


        popWin.showAtLocation(contentView, Gravity.BOTTOM, 0, 20);


    }

    boolean flag = true;

    protected void light() {
        if (flag == true) {
            flag = false;
            // 开闪光灯
            CameraManager.get().openLight();
        } else {
            flag = true;
            // 关闪光灯
            CameraManager.get().offLight();
        }

    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.capture_preview);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    public void handleDecode(String result) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();

        Intent intent = new Intent();
        intent.putExtra("result", result);
        intent.putExtra("inputtype", "zbar");
        setResult(RESULT_OK, intent);
        ActivityUtil.FinishActivity(mActivity);

        //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();

        // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
        //handler.sendEmptyMessage(R.id.restart_preview);
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);

            Point point = CameraManager.get().getCameraResolution();
            int width = point.y;
            int height = point.x;

            int x = mCropLayout.getLeft() * width / mContainer.getWidth();
            int y = mCropLayout.getTop() * height / mContainer.getHeight();

            int cropWidth = mCropLayout.getWidth() * width
                    / mContainer.getWidth();
            int cropHeight = mCropLayout.getHeight() * height
                    / mContainer.getHeight();

            setX(x);
            setY(y);
            setCropWidth(cropWidth);
            setCropHeight(cropHeight);

        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(CaptureActivity.this);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public Handler getHandler() {
        return handler;
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };
}