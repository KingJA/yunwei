package com.tdr.yunwei.fragment;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.tdr.yunwei.R;
import com.tdr.yunwei.YunWeiApplication;
import com.tdr.yunwei.adapter.ExOpearationAdapter;
import com.tdr.yunwei.bean.DeviceMainTypeBean;
import com.tdr.yunwei.bean.DeviceSubTypeBean;
import com.tdr.yunwei.util.DBUtils;
import com.tdr.yunwei.util.LOG;


import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class OperationsFM extends BaseFragment {


    private ExpandableListView exLV;
    private ExOpearationAdapter exAdapter;

    private List<DeviceMainTypeBean> DMTList;
    private List<DeviceSubTypeBean> DSTList;


    private View mView;
    ScrollView scrollView;

    private DbManager DB;
    private YunWeiApplication YWA;
    private OperationsFM mOperationsFM;


    @Override
    public View initViews() {
        mView = View.inflate(mActivity, R.layout.fm_operations, null);
        YWA = YunWeiApplication.getInstance();
//        DB = YWA.getDB();
        DB= x.getDb(DBUtils.getDb());
        mOperationsFM=this;
        initView(mView);
        return mView;
    }



    private void initView(View v) {

        scrollView = (ScrollView) v.findViewById(R.id.scrollView);

        try {
            DMTList = DB.findAll(DeviceMainTypeBean.class);
            DSTList = DB.findAll(DeviceSubTypeBean.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        if(DMTList==null){
            DMTList=new ArrayList<>();
        }
        if(DSTList==null){
            DSTList=new ArrayList<>();
        }
        Log.e("size==", "" + DMTList.size() + "|||" + DSTList.size());


        ArrayList<ArrayList<DeviceSubTypeBean>> childList = new ArrayList<ArrayList<DeviceSubTypeBean>>();


        for (int i = 0; i < DMTList.size(); i++) {

            String MainID = DMTList.get(i).getMainTypeID();
            String MainReMark = DMTList.get(i).getRemark();

            Log.e("ID", "MainID=" + MainID + "---MainMark==" + MainReMark);

            ArrayList<DeviceSubTypeBean> list = new ArrayList<DeviceSubTypeBean>();

            for (int j = 0; j < DSTList.size(); j++) {
                String MainTypeID = DSTList.get(j).getMainTypeID();
                if (MainID.equals(MainTypeID)) {
                    DeviceSubTypeBean bean = DSTList.get(j);
                    list.add(bean);
                }
            }
            Log.e("list.size()==", list.size() + "");

            childList.add(list);

        }


        exLV = (ExpandableListView) v.findViewById(R.id.exLV);
        exAdapter = new ExOpearationAdapter(mActivity,DMTList, childList, DB);
        exLV.setAdapter(exAdapter);
//        exLV.setGroupIndicator(null);//去掉箭头


        for (int i = 0; i < exAdapter.getGroupCount(); i++) {
            exLV.expandGroup(i);
        }
        setLvHeight(exLV);
    }



    public void setLvHeight(ListView listView) {
        if (listView == null) {
            return;
        }
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        int listItemHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            listItemHeight = listItem.getMeasuredHeight();
            totalHeight += listItemHeight;
        }
        Log.e("listItemHeight", "" + listItemHeight);

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + 8;
        params.height = height;

        listView.setLayoutParams(params);
    }

    @Override
    public void onPause() {
        super.onPause();
//        isshowPB(false);
    }
}
