package com.tdr.yunwei.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tdr.yunwei.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/27.
 */
public class StringAdapter extends BaseSimpleAdapter<String> {

    private String positionStr;

    public StringAdapter(Activity mActivity, List<String> list, String positionStr) {
        super(mActivity, list);
        this.positionStr = positionStr;
    }

    @Override
    public View simpleGetView(int position, View convertView, ViewGroup parent) {

        ViewHolder mHolder = null;
        if (convertView == null) {

            convertView = View.inflate(mActivity, R.layout.lv_string_item, null);
            mHolder = new ViewHolder(convertView);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }
        mHolder.txt_name.setText(list.get(position).toString());


        ArrayList<ImageView> imgList = new ArrayList<ImageView>();
        for (int i = 0; i < list.size(); i++) {
            imgList.add(mHolder.img_xiala01);
            imgList.get(i).setVisibility(View.INVISIBLE);
        }


        if (positionStr.equals(mHolder.txt_name.getText().toString())) {
            imgList.get(position).setVisibility(View.VISIBLE);
        }


        Log.e("positionStr", positionStr+"//list="+list.size() + "//imgList="+imgList.size());


        return convertView;
    }

    public class ViewHolder {
        TextView txt_name;
        ImageView img_xiala01;

        public ViewHolder(View v) {
            txt_name = (TextView) v.findViewById(R.id.txt_name);
            img_xiala01 = (ImageView) v.findViewById(R.id.img_xiala01);
        }
    }
}
