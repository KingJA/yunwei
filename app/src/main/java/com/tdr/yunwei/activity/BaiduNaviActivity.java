package com.tdr.yunwei.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.widget.Toast;

import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.tdr.yunwei.R;
import com.tdr.yunwei.baidumap.BaiduMapNavActivity;
import com.tdr.yunwei.util.LOG;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/24.
 */

public class BaiduNaviActivity extends Activity{
    private Activity mActivity;
    String authinfo = null;
    private Double SLAT,SLNG,ELAT,ELNG;
    private boolean Result=false;
    private boolean isrun=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi);
        mActivity=this;

        LOG.D("SLAT="+getIntent().getStringExtra("SLAT")
                +"\nSLNG" +getIntent().getStringExtra("SLNG")
                +"\nELAT"+getIntent().getStringExtra("ELAT")
                +"\nELNG"+getIntent().getStringExtra("ELNG"));

        SLAT = Double.valueOf(getIntent().getStringExtra("SLAT"));
        SLNG = Double.valueOf(getIntent().getStringExtra("SLNG"));
        ELAT = Double.valueOf(getIntent().getStringExtra("ELAT"));
        ELNG = Double.valueOf(getIntent().getStringExtra("ELNG"));

        if (initDirs()) {
            initNavi();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isrun){
                    if(Result){
                        isrun=false;
                        routeplanToNavi(SLAT,SLNG,ELAT,ELNG);
                    }else{
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();


    }
    /**
     * 开启导航
     *
     * @param sLNG 出发LNG
     * @param sLAT 出发LAT
     * @param eLNG 终点LNG
     * @param eLAT 终点LAT
     */
    private void routeplanToNavi(double sLNG, double sLAT, double eLNG, double eLAT) {
        BNRoutePlanNode sNode = new BNRoutePlanNode(sLAT, sLNG, "起点", null, BNRoutePlanNode.CoordinateType.BD09LL);
        BNRoutePlanNode eNode = new BNRoutePlanNode(eLAT, eLNG, "终点", null, BNRoutePlanNode.CoordinateType.BD09LL);
        LOG.D("sLAT=" + sLAT);
        LOG.D("sLNG=" + sLNG);
        LOG.D("eLAT=" + eLAT);
        LOG.D("eLNG=" + eLNG);
        if (sNode != null && eNode != null) {
            List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
            list.add(sNode);
            list.add(eNode);
            BaiduNaviManager.getInstance().launchNavigator(this, list, 1, true, new DemoRoutePlanListener(sNode));
        }
    }

    public static final String ROUTE_PLAN_NODE = "routePlanNode";
    public static final String SHOW_CUSTOM_ITEM = "showCustomItem";
    public static final String RESET_END_NODE = "resetEndNode";
    public static final String VOID_MODE = "voidMode";
    public static List<Activity> activityList = new LinkedList<Activity>();

    /**
     * 路线计算监听
     */
    public class DemoRoutePlanListener implements BaiduNaviManager.RoutePlanListener {

        private BNRoutePlanNode mBNRoutePlanNode = null;

        public DemoRoutePlanListener(BNRoutePlanNode node) {
            mBNRoutePlanNode = node;
        }

        @Override
        public void onJumpToNavigator() {
        /*
         * 设置途径点以及resetEndNode会回调该接口
         */
            for (Activity ac : activityList) {

                if (ac.getClass().getName().endsWith("BaiduMapNavActivity")) {
                    return;
                }
            }
            LOG.D("打开导航界面");
            Intent intent = new Intent(BaiduNaviActivity.this, BaiduMapNavActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(ROUTE_PLAN_NODE, (BNRoutePlanNode) mBNRoutePlanNode);
            intent.putExtras(bundle);
            startActivity(intent);

        }

        @Override
        public void onRoutePlanFailed() {
            // TODO Auto-generated method stub
            Toast.makeText(BaiduNaviActivity.this, "算路失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 验证导航key
     */
    private void initNavi() {
        BaiduNaviManager.getInstance().init(this, mSDCardPath, APP_FOLDER_NAME, new BaiduNaviManager.NaviInitListener() {
            @Override
            public void onAuthResult(int status, String msg) {
                if (0 == status) {
                    authinfo = "key校验成功!";
                } else {
                    authinfo = "key校验失败, " + msg;
                }
//                Toast.makeText(mActivity, "key校验", Toast.LENGTH_SHORT).show();
                BaiduNaviActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(mActivity, authinfo, Toast.LENGTH_LONG).show();
                    }
                });
            }

            public void initSuccess() {
                Toast.makeText(mActivity, "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
                initSetting();
            }

            public void initStart() {
                Toast.makeText(mActivity, "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
            }

            public void initFailed() {
                Toast.makeText(mActivity, "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
            }


        }, null, ttsHandler, ttsPlayStateListener);

    }

    /**
     * 内部TTS播报状态回调接口
     */
    private BaiduNaviManager.TTSPlayStateListener ttsPlayStateListener = new BaiduNaviManager.TTSPlayStateListener() {

        @Override
        public void playEnd() {
                finish();
//            showToastMsg("TTSPlayStateListener : TTS play end");
        }

        @Override
        public void playStart() {
//            showToastMsg("TTSPlayStateListener : TTS play start");
        }
    };
    /**
     * 内部TTS播报状态回传handler
     */
    private Handler ttsHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.what;
            switch (type) {
                case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG: {
//                    showToastMsg("Handler : TTS play start");
                    break;
                }
                case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG: {
//                    showToastMsg("Handler : TTS play end");
                    finish();
                    break;
                }
                default:
                    break;
            }
        }
    };
    private String mSDCardPath = null;
    private static final String APP_FOLDER_NAME = "";

    /**
     * 创建TTS语音缓存目录
     *
     * @return
     */
    private  boolean initDirs() {
        mSDCardPath = getSdcardDir();
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * 获取CD卡目录
     *
     * @return
     */
    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

    /**
     * 载入导航设置
     */
    private void initSetting() {
        Result=true;
        // 设置是否双屏显示
        BNaviSettingManager.setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
        // 设置导航播报模式
        BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
        // 是否开启路况
        BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
    }

}
