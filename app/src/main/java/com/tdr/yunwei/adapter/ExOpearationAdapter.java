package com.tdr.yunwei.adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tdr.yunwei.R;
import com.tdr.yunwei.activity.BaiduNaviActivity;
import com.tdr.yunwei.activity.DeviceAddActivity;
import com.tdr.yunwei.activity.DeviceMapActivity;
import com.tdr.yunwei.activity.DeviceQueryActivity;
import com.tdr.yunwei.activity.DeviceQueryActivity2;
import com.tdr.yunwei.activity.HomeActivity;
import com.tdr.yunwei.activity.NewDeviceAddActivity;
import com.tdr.yunwei.activity.SelectLocation;
import com.tdr.yunwei.bean.DeviceMainTypeBean;
import com.tdr.yunwei.bean.DeviceSubTypeBean;
import com.tdr.yunwei.fragment.OperationsFM;
import com.tdr.yunwei.util.DipPx;
import com.tdr.yunwei.util.LOG;
import com.tdr.yunwei.util.SharedUtil;
import com.tdr.yunwei.util.ToastUtil;
import com.zbar.lib.CaptureActivity;


import org.xutils.DbManager;

import java.util.ArrayList;
import java.util.List;

public class ExOpearationAdapter extends BaseExpandableListAdapter {

    Activity mActivity;
    List<DeviceMainTypeBean> parentList;
    ArrayList<ArrayList<DeviceSubTypeBean>> childList;
    DbManager DB;



    public ExOpearationAdapter(Activity mActivity,  List<DeviceMainTypeBean> parentList,
                               ArrayList<ArrayList<DeviceSubTypeBean>> childList, DbManager DB) {
        this.mActivity = mActivity;
        this.parentList = parentList;
        this.childList = childList;
        this.DB = DB;


    }


    @Override
    public int getGroupCount() {
        return parentList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return parentList.get(groupPosition);
    }

    @Override

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }


    @Override
    public int getChildrenCount(int groupPosition) {
        return childList.get(groupPosition).size();
    }


    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childList.get(groupPosition).get(childPosition);
    }


    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {


        ParentHoler parentHoler;

        if (convertView == null) {
            convertView = View.inflate(mActivity, R.layout.ex_city_item, null);
            parentHoler = new ParentHoler(convertView);
            convertView.setTag(parentHoler);
        } else {
            parentHoler = (ParentHoler) convertView.getTag();
        }

        DeviceMainTypeBean mainBean = parentList.get(groupPosition);
        parentHoler.txt_ex_name.setText(mainBean.getRemark());
        LinearLayout.LayoutParams LP= new LinearLayout.LayoutParams(DipPx.dip2px(mActivity,20), DipPx.dip2px(mActivity,10));
        LP.rightMargin=DipPx.dip2px(mActivity,25);
        LinearLayout.LayoutParams LP2= new LinearLayout.LayoutParams(DipPx.dip2px(mActivity,10), DipPx.dip2px(mActivity,20));
        LP2.rightMargin=DipPx.dip2px(mActivity,30);
        if(isExpanded){
            parentHoler.iv_expanded.setLayoutParams(LP);
            parentHoler.iv_expanded.setBackgroundResource(R.mipmap.down);
        }else{
            parentHoler.iv_expanded.setLayoutParams(LP2);
            parentHoler.iv_expanded.setBackgroundResource(R.mipmap.right);
        }
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final ChildHoler childHoler;

        if (convertView == null) {
            convertView = View.inflate(mActivity, R.layout.lv_city_item, null);
            childHoler = new ChildHoler(convertView);
            convertView.setTag(childHoler);
        } else {
            childHoler = (ChildHoler) convertView.getTag();
        }


        final DeviceSubTypeBean DSTBean = childList.get(groupPosition).get(childPosition);

        childHoler.txt_name.setText(DSTBean.getRemark());

        childHoler.ll_city.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getRolePowers();
                final int[] location = new int[2];
                childHoler.img_city.getLocationOnScreen(location);

                int x = location[0];
                int y = location[1];

                childHoler.img_city.setBackgroundResource(R.mipmap.home_more);
                HomeActivity.type=DSTBean.getSubTypeID();
                HomeActivity.Remark=DSTBean.getRemark();
                showPopWin(childHoler.ll_city, childHoler.img_city, DSTBean);


                Log.e("ll_city", "x=" + x + "y=" + y + "//type="+HomeActivity.type);

                LOG.E("ForPoliceUse="+DSTBean.getForPoliceUse());
                LOG.E("IsValid="+DSTBean.getIsValid());
                LOG.E("LastUpdateTime="+DSTBean.getLastUpdateTime());
                LOG.E("MainTypeID="+DSTBean.getMainTypeID());
                LOG.E("Remark="+DSTBean.getRemark());
                LOG.E("SubTypeID="+DSTBean.getSubTypeID());
                LOG.E("ID="+DSTBean.getID());
            }
        });
        childHoler.lv_ll.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    class ChildHoler {
        LinearLayout lv_ll, ll_city;
        TextView txt_name;
        ImageView img_city;

        public ChildHoler(View view) {
            lv_ll = (LinearLayout) view.findViewById(R.id.lv_ll);
            ll_city = (LinearLayout) view.findViewById(R.id.ll_city);
            txt_name = (TextView) view.findViewById(R.id.txt_name);
            img_city = (ImageView) view.findViewById(R.id.img_city);
        }
    }

    class ParentHoler {
        TextView txt_ex_name;
        ImageView iv_expanded;
        public ParentHoler(View view) {
            txt_ex_name = (TextView) view.findViewById(R.id.txt_ex_name);
            iv_expanded= (ImageView) view.findViewById(R.id.iv_expanded);
        }
    }


    public static boolean role11 = false, role12 = false, role13 = false;

    private void getRolePowers() {
        if (SharedUtil.getValue(mActivity, "RolePowers").equals("")) {

        } else {
            String[] RolePowers = SharedUtil.getValue(mActivity, "RolePowers").split(",");

            for (int i = 0; i < RolePowers.length; i++) {
                int role = Integer.valueOf(RolePowers[i]);
                if (role == 11) {
                    role11 = true;
                }
                if (role == 12) {
                    role12 = true;
                }
                if (role == 13) {
                    role13 = true;
                }
                            }
        }
    }
    private PopupWindow popupWindow;
    private RelativeLayout RL_Search,RL_Install,RL_Repair,RL_Cased,RL_Navigation,RL_Binding,RL_Back;
    private void showPopWin(View view,  final ImageView img, DeviceSubTypeBean DSTBean) {

        View contentView = LayoutInflater.from(mActivity).inflate(
                R.layout.pop_opearations2, null);

        RL_Search = (RelativeLayout) contentView.findViewById(R.id.RL_Search);
        RL_Install = (RelativeLayout) contentView.findViewById(R.id.RL_Install);
        RL_Repair = (RelativeLayout) contentView.findViewById(R.id.RL_Repair);
        RL_Cased = (RelativeLayout) contentView.findViewById(R.id.RL_Cased);
        RL_Navigation = (RelativeLayout) contentView.findViewById(R.id.RL_Navigation);
        RL_Binding = (RelativeLayout) contentView.findViewById(R.id.RL_Binding);

        RL_Back = (RelativeLayout) contentView.findViewById(R.id.RL_Back);
        RL_Cased.setVisibility(View.GONE);
        if(DSTBean.getSubTypeID().equals("34560")){
            RL_Binding.setVisibility(View.VISIBLE);
        }else{
            RL_Binding.setVisibility(View.GONE);
        }
        MyPopClick mypop = new MyPopClick(DSTBean);
        RL_Search.setOnClickListener(mypop);
        RL_Install.setOnClickListener(mypop);
        RL_Repair.setOnClickListener(mypop);
        RL_Cased.setOnClickListener(mypop);
        RL_Navigation.setOnClickListener(mypop);
        RL_Binding.setOnClickListener(mypop);
        RL_Back.setOnClickListener(mypop);

        popupWindow = new PopupWindow(contentView,ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setTouchable(true);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        popupWindow.setTouchInterceptor(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;

            }
        });
        popupWindow.setBackgroundDrawable(mActivity.getResources().getDrawable(R.color.transparent));

        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 0.7f;
        mActivity.getWindow().setAttributes(lp);
        popupWindow.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
                lp.alpha = 1f;
                mActivity.getWindow().setAttributes(lp);

                img.setBackgroundResource(R.mipmap.home_more_off);
            }
        });

        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        contentView.measure(widthSpec, heightSpec);
        int heightView = contentView.getMeasuredHeight();
        int widthView = contentView.getMeasuredWidth();

//        popupWindow.showAtLocation(view, 0, DipPx.getScreenWidth(mActivity) / 2 + 100, y - heightView / 2 + 30);//DipPx.getScreenHeight(mActivity)/2-100);

//        popupWindow.showAsDropDown(view, 0, 0);
        popupWindow.setAnimationStyle(R.style.AnimationBottomFade);
        popupWindow.showAtLocation(mActivity.getLayoutInflater().inflate(R.layout.fm_operations, null), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
    }
    private class MyPopClick implements OnClickListener {

        DeviceSubTypeBean DSTBean;

        public MyPopClick(DeviceSubTypeBean DSTBean) {
            this.DSTBean = DSTBean;
        }

        @Override
        public void onClick(View v) {
            if (v == RL_Search) {//11
                if (role11 == true) {
                    Intent intent;
                    if(DSTBean.getSubTypeID().equals("34560")){
                        intent = new Intent(mActivity, DeviceQueryActivity2.class);
                    }else{
                        intent = new Intent(mActivity, DeviceQueryActivity.class);
                    }
                    intent.putExtra("in","Search");
                    intent.putExtra("type",DSTBean.getSubTypeID());
                    intent.putExtra("remark",DSTBean.getRemark());

                    mActivity.startActivity(intent);

                } else {
                    ToastUtil.ShortCenter(mActivity, "您无此功能权限");
                }
            }
            if (v == RL_Install) {//12
                if (role12 == true) {
                    Intent intent = new Intent(mActivity, CaptureActivity.class);

                    mActivity.startActivityForResult(intent, 1002);
                } else {
                    ToastUtil.ShortCenter(mActivity, "您无此功能权限");
                }
            }
            if (v == RL_Repair) {//13
                if (role13 == true) {
                    String isOrderExist = SharedUtil.getValue(mActivity, "isOrderExist");
                    if (isOrderExist.equals("isOrderExist")) {
                        Intent intent = new Intent(mActivity, CaptureActivity.class);
                        mActivity.startActivityForResult(intent, 1003);
                    } else {
                        ToastUtil.ShortCenter(mActivity, "本手机还没有工单，请进入工单页面刷新数据！");
                    }

                } else {
                    ToastUtil.ShortCenter(mActivity, "您无此功能权限");
                }
            }
            if(v==RL_Cased){//
                if (role12 == true) {
                    Intent intent = new Intent(mActivity, SelectLocation.class);
                    HomeActivity.IsMapiIn=true;
                    mActivity.startActivity(intent);
                } else {
                    ToastUtil.ShortCenter(mActivity, "您无此功能权限");
                }
            }
            if(v==RL_Navigation){//12
                if (role12 == true) {
                    mActivity.startActivity(new Intent(mActivity, DeviceMapActivity.class));
                } else {
                    ToastUtil.ShortCenter(mActivity, "您无此功能权限");
                }
            }
            if(v==RL_Binding){//
                if (role12 == true) {
                    Intent intent = new Intent(mActivity, DeviceQueryActivity2.class);
                    intent.putExtra("in","Binding");
                    intent.putExtra("type",DSTBean.getSubTypeID());
                    intent.putExtra("remark",DSTBean.getRemark());
                    mActivity.startActivity(intent);
                } else {
                    ToastUtil.ShortCenter(mActivity, "您无此功能权限");
                }
            }
            if(v==RL_Back){

            }
            popupWindow.dismiss();
        }

    }


//    PopupWindow popupWindow;
//
//    ImageView img_search, img_install, img_repair,img_navigation;
//
//    private void showPopWin(View view, int y, final ImageView img, DeviceSubTypeBean DSTBean) {
//
//        View contentView = LayoutInflater.from(mActivity).inflate(
//                R.layout.pop_opearations, null);
//
//        img_search = (ImageView) contentView.findViewById(R.id.img_search);
//        img_install = (ImageView) contentView.findViewById(R.id.img_install);
//        img_repair = (ImageView) contentView.findViewById(R.id.img_repair);
//        img_navigation = (ImageView) contentView.findViewById(R.id.img_navigation);
//
//
//        MyPopClick mypop = new MyPopClick(DSTBean);
//        img_search.setOnClickListener(mypop);
//        img_install.setOnClickListener(mypop);
//        img_repair.setOnClickListener(mypop);
//        img_navigation.setOnClickListener(mypop);
//
//
//
//        popupWindow = new PopupWindow(contentView,
//                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
//
//        popupWindow.setTouchable(true);
//        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//
//        popupWindow.setTouchInterceptor(new OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return false;
//
//            }
//        });
//
//
//        popupWindow.setBackgroundDrawable(mActivity.getResources().getDrawable(
//                R.color.transparent));
//
//
//        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
//        lp.alpha = 0.7f;
//        mActivity.getWindow().setAttributes(lp);
//        popupWindow.setOnDismissListener(new OnDismissListener() {
//
//            @Override
//            public void onDismiss() {
//                WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
//                lp.alpha = 1f;
//                mActivity.getWindow().setAttributes(lp);
//
//                img.setBackgroundResource(R.mipmap.home_more_off);
//            }
//        });
//
//        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//        contentView.measure(widthSpec, heightSpec);
//        int heightView = contentView.getMeasuredHeight();
//        int widthView = contentView.getMeasuredWidth();
//
//        popupWindow.showAtLocation(view, 0, DipPx.getScreenWidth(mActivity) / 2 + 100, y - heightView / 2 + 30);//DipPx.getScreenHeight(mActivity)/2-100);
//
//    }

//    private class MyPopClick implements OnClickListener {
//
//        DeviceSubTypeBean DSTBean;
//
//        public MyPopClick(DeviceSubTypeBean DSTBean) {
//            this.DSTBean = DSTBean;
//        }
//
//        @Override
//        public void onClick(View v) {
//            if (v == img_search) {
//                if (role11 == true) {
//                    Intent intent = new Intent(mActivity, DeviceQueryActivity.class);
//                    intent.putExtra("type",DSTBean.getSubTypeID());
//                    intent.putExtra("remark",DSTBean.getRemark());
//
//                    mActivity.startActivity(intent);
//
//                } else {
//                    ToastUtil.ShortCenter(mActivity, "您无此功能权限");
//                }
//            }
//            if (v == img_install) {
//                if (role12 == true) {
//                    Intent intent = new Intent(mActivity, CaptureActivity.class);
//                    HomeActivity.IsMapiIn=false;
//                    mActivity.startActivityForResult(intent, 1002);
//                } else {
//                    ToastUtil.ShortCenter(mActivity, "您无此功能权限");
//                }
//            }
//            if (v == img_repair) {
//                if (role13 == true) {
//
//                    String isOrderExist = SharedUtil.getValue(mActivity, "isOrderExist");
//                    if (isOrderExist.equals("isOrderExist")) {
//                        Intent intent = new Intent(mActivity, CaptureActivity.class);
//                        mActivity.startActivityForResult(intent, 1003);
//                    } else {
//                        ToastUtil.ShortCenter(mActivity, "本手机还没有工单，请进入工单页面刷新数据！");
//                    }
//
//                } else {
//                    ToastUtil.ShortCenter(mActivity, "您无此功能权限");
//                }
//            }
//            if(v==img_navigation){
//                if (role13 == true) {
//                    Intent intent = new Intent(mActivity, DeviceMapActivity.class);
//                    HomeActivity.IsMapiIn=true;
//                    mActivity.startActivity(intent);
//                } else {
//                    ToastUtil.ShortCenter(mActivity, "您无此功能权限");
//                }
//            }
//
//
//            popupWindow.dismiss();
//        }
//
//    }


}

