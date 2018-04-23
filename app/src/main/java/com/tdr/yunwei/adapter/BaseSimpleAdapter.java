package com.tdr.yunwei.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * 项目名称：物联网城市防控(警用版)
 * 类描述：ListView,GridView基类
 * 创建人：KingJA
 * 创建时间：2016/3/24 9:13
 * 修改备注：
 */
public abstract class BaseSimpleAdapter<T> extends BaseAdapter {
    protected Activity mActivity;
    protected List<T> list;

    public BaseSimpleAdapter(Activity mActivity, List<T> list) {

        this.mActivity = mActivity;
        this.list = list;
    }




    public void setData(List<T> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }

    public void addData(List<T> list) {
        this.list.addAll(list);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return simpleGetView(position, convertView, parent);
    }

    public abstract View simpleGetView(int position, View convertView, ViewGroup parent);
}
