package com.tdr.yunwei.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tdr.yunwei.R;
import com.tdr.yunwei.bean.CityAreaBean;
import java.util.List;

/**
 * Created by Administrator on 2016/4/27.
 */
public class CityAdapter extends BaseSimpleAdapter<CityAreaBean> {


    public CityAdapter(Activity mActivity, List<CityAreaBean> list) {
        super(mActivity, list);
    }

    @Override
    public View simpleGetView(int position, View convertView, ViewGroup parent) {

        ViewHolder mHolder = null;
        if (convertView == null) {

            convertView = View.inflate(mActivity, R.layout.gv_city_item, null);
            mHolder = new ViewHolder(convertView);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }
        mHolder.txt_city.setText(list.get(position).getAreaMC());




        return convertView;
    }

    public class ViewHolder {
        TextView txt_city;

        public ViewHolder(View v) {
            txt_city = (TextView) v.findViewById(R.id.txt_city);
        }
    }
}
