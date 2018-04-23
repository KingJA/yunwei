package com.tdr.yunwei.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tdr.yunwei.R;

import java.util.List;

/**
 * Created by Administrator on 2016/4/27.
 */
public class QueryHistoryAdapter extends BaseSimpleAdapter<String> {


    public QueryHistoryAdapter(Activity mActivity, List<String> list) {
        super(mActivity, list);
    }

    @Override
    public View simpleGetView(int position, View convertView, ViewGroup parent) {

        ViewHolder mHolder = null;
        if (convertView == null) {

            convertView = View.inflate(mActivity, R.layout.lv_string_item2, null);
            mHolder = new ViewHolder(convertView);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }
        mHolder.txt_name.setText(list.get(position).toString());

        return convertView;
    }

    public class ViewHolder {
        TextView txt_name;

        public ViewHolder(View v) {
            txt_name = (TextView) v.findViewById(R.id.txt_name);
        }
    }
}
