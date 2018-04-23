package com.tdr.yunwei.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tdr.yunwei.R;
import com.tdr.yunwei.YunWeiApplication;
import com.tdr.yunwei.bean.MyMSG;
import com.tdr.yunwei.bean.RepairCompanyBean;
import com.tdr.yunwei.bean.RepairOrderBean;
import com.tdr.yunwei.fragment.SettingFM;
import com.tdr.yunwei.util.ActivityUtil;
import com.tdr.yunwei.util.Constants;
import com.tdr.yunwei.util.DBUtils;
import com.tdr.yunwei.util.JsonUtil;
import com.tdr.yunwei.util.LOG;
import com.tdr.yunwei.util.SharedUtil;
import com.tdr.yunwei.util.ToastUtil;
import com.tdr.yunwei.util.WebUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.x;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/2/4.
 */

public class MyMsgActivity extends Activity {
    private ImageView iv_back;
    private ListView lv_msg;
    private List<MyMSG> msgdate;
    private DbManager DB;
    private Context mContext;
    private MyAdapter myadapter;
    private Activity mActivity;
    private YunWeiApplication YWA;
    private String getmore="点击加载更多";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_msg);
        ActivityUtil.AddActivity(this);
        DB= x.getDb(DBUtils.getDb());
        YWA= YunWeiApplication.getInstance();
        YWA.setMymsgactivity(this);
        mActivity=this;
        mContext=this.getApplicationContext();
        initview();
    }


    private void initview(){
        iv_back=(ImageView) findViewById(R.id.iv_msg_back);
        lv_msg=(ListView) findViewById(R.id.lv_msg);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(SettingFM.Result);
                ActivityUtil.FinishActivity(mActivity);
            }
        });
        lv_msg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(checkitem(position)){
                    ItemClickGetMore(position);
                }else{
                    ItemClickRead(position);
                }
            }
        });
        msgdate=YWA.getMsgdate();
//        for (int i = 0; i < 10; i++) {
//            MyMSG m=new MyMSG();
//            m.setIsOld(false);
//            m.setID(i);
//            m.setMsgTime(SystemClock.currentThreadTimeMillis()+"");
//            m.setMsgText("msg"+i);
//            m.setMsgTitle("Title"+i);
//            m.setMessageType("1");
//            m.setUserID(SharedUtil.getValue(mActivity, "UserId"));
//            msgdate.add(m);
//        }
        myadapter =new MyAdapter();
        lv_msg.setAdapter(myadapter);
    }
    private int mposition;
    private int mIndex;
    private void ItemClickGetMore(int position) {
        getmore="数据加载中...";
        mposition=position;
        updataItem_getmore(position);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("accessToken", SharedUtil.getToken(mActivity));
        map.put("userID", SharedUtil.getValue(mActivity, "UserId"));
        map.put("messageType", 1);
        String index= SharedUtil.getValue(mActivity, "Index");
        if (index.equals("")) {
            mIndex=1;
        }else{
            mIndex=Integer.parseInt(index);
        }
        map.put("index", mIndex);

        WebUtil.getInstance(mActivity).webRequest(Constants.GetMessage, map, new WebUtil.MyCallback() {

            @Override
            public void onSuccess(String result) {
                if (result.equals("-1")) {
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray ja = jsonObject.getJSONArray("MessageInfoList");
                    List<MyMSG> mymsglist;
                    LOG.D("数据量   "+ja.length());
                    if (ja.length() > 0) {
                        String data = jsonObject.getString("MessageInfoList");
                        mymsglist = (List<MyMSG>) DBUtils.SetListID(DB, JsonUtil.J2L_MyMSG(mActivity, data), MyMSG.class);
                        for (MyMSG m : mymsglist) {
                            msgdate.add(m);
                        }
                        DB.save(mymsglist);
                        LOG.D("          msgdate.size="+msgdate.size());
                        YWA.setMsgdate(msgdate);
                        myadapter.notifyDataSetChanged();
                        getmore="点击获取更多消息";
                        SharedUtil.setValue(mActivity,"Index", String.valueOf(++mIndex));
                    }else{
                        getmore="没有更多的消息了";
                    }
                    updataItem_getmore(mposition);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    private void ItemClickRead(int position){
            String date=msgdate.get(position).getMsgText().toString();
            if(date==null||date.equals("")){
                date="无内容";
            }
            ToastUtil.showShort(mActivity,date);
            msgdate.get(position).setIsOld(true);
            updataItem(position);
            YWA.setMsgdate(msgdate);
            try {
                MyMSG mymsg = DB.selector(MyMSG.class).where("ID", "=",  msgdate.get(position).getID()).findFirst();
                mymsg.setIsOld(true);
                DB.update(mymsg);
                if(mymsg!=null){
                    LOG.E("delete:"+mymsg.getID());
                    DB.delete(mymsg);
                }
                DB.save(mymsg);
                List<MyMSG> l= DB.findAll(MyMSG.class);
                for (MyMSG ls:l){
                    LOG.E("getID="+ls.getID());
                    LOG.E("getIsOld="+ls.getIsOld());
                    LOG.E("getUserID ："+ls.getUserID());
                    LOG.E("getMessageType ："+ls.getMessageType());
                    LOG.E("getMsgTitle="+ls.getMsgTitle());
                    LOG.E("getMsgText="+ls.getMsgText());
                    LOG.E("getMsgTime="+ls.getMsgTime());
                }
            } catch (DbException e) {
                e.printStackTrace();

        }
    }
    private  String getTimeRange(long mTime){
        final int seconds_of_1minute = 60;
        final int seconds_of_30minutes = 30 * 60;
        final int seconds_of_1hour = 60 * 60;
        final int seconds_of_1day = 24 * 60 * 60;
//        final int seconds_of_15days = seconds_of_1day * 15;
        final int seconds_of_30days = seconds_of_1day * 30;
//        final int seconds_of_6months = seconds_of_30days * 6;
//        final int seconds_of_1year = seconds_of_30days * 12;
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        /**获取当前时间*/
        Date  curDate = new  Date(System.currentTimeMillis());
        /**除以1000是为了转换成秒*/
        long   between=(curDate.getTime()- mTime)/1000;
        int   elapsedTime= (int) (between);
        String rtime="";
        if (elapsedTime < seconds_of_1minute) {
            rtime="刚刚";
        }else if (elapsedTime < seconds_of_30minutes) {
            rtime=elapsedTime / seconds_of_1minute + "分钟前";
        }else if (elapsedTime < seconds_of_1hour) {
            rtime="半小时前";
        }else if (elapsedTime < seconds_of_1day) {
            rtime= elapsedTime / seconds_of_1hour + "小时前";
        }else if (elapsedTime < seconds_of_30days) {
            rtime= elapsedTime / seconds_of_1day + "天前";
        }else {
            Date d1=new Date(mTime);
            rtime=sdf2.format(d1);
        }
        return rtime;
    }
    private  void updataItem(int position){
        int firstvisible = lv_msg.getFirstVisiblePosition();
        int lastvisibale = lv_msg.getLastVisiblePosition();
        if(position>=firstvisible&&position<=lastvisibale){
            View view = lv_msg.getChildAt(position - firstvisible);
            MyAdapter.ViewHolder viewHolder = (MyAdapter.ViewHolder) view.getTag();
            if(msgdate.get(position).getIsOld()){
                viewHolder.tvTitle.setTextColor(Color.parseColor("#999999"));
            }else{
                viewHolder.tvTitle.setTextColor(Color.parseColor("#333333"));
            }
        }
    }
    private  void updataItem_getmore(int position){
        LOG.D(position+"加载按钮文本=   "+getmore);
        int firstvisible = lv_msg.getFirstVisiblePosition();
        int lastvisibale = lv_msg.getLastVisiblePosition();
        if(position>=firstvisible&&position<=lastvisibale){
            View view = lv_msg.getChildAt(position - firstvisible);
            MyAdapter.ViewHolder viewHolder = (MyAdapter.ViewHolder) view.getTag();
            viewHolder.tvGetMore.setText(getmore);
        }
    }





    public void refresh(List<MyMSG> list) {
        LOG.D("刷新数据"+list.size());
        getmore="刷新数据";
        msgdate=list;
        myadapter.notifyDataSetChanged();
    }

    class MyAdapter extends BaseAdapter{


        @Override
        public int getCount() {
            return msgdate.size()+1;
        }

        @Override
        public Object getItem(int position) {
            return msgdate.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyAdapter.ViewHolder viewHolder;
            MyMSG mymsg;
            if (position == msgdate.size()) {
                mymsg = null;
            } else {
                mymsg = msgdate.get(position);
            }
            if (convertView == null) {
                viewHolder = new MyAdapter.ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_my_msg, null);
                viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_msg_title);
                viewHolder.tvMSG = (TextView) convertView.findViewById(R.id.tv_msg_text);
                viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_msg_time);
                viewHolder.tvGetMore=(TextView) convertView.findViewById(R.id.tv_getmore);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (MyAdapter.ViewHolder) convertView.getTag();
            }

            if (checkitem(position)) {
                viewHolder.tvGetMore.setVisibility(View.VISIBLE);
                viewHolder.tvTitle.setVisibility(View.GONE);
                viewHolder.tvMSG.setVisibility(View.GONE);
                viewHolder.tvTime.setVisibility(View.GONE);

                viewHolder.tvGetMore.setText(getmore);
            } else {
                if (mymsg.getIsOld()) {
                    viewHolder.tvTitle.setTextColor(Color.parseColor("#999999"));
                } else {
                    viewHolder.tvTitle.setTextColor(Color.parseColor("#333333"));
                }
                viewHolder.tvGetMore.setVisibility(View.GONE);
                viewHolder.tvTitle.setVisibility(View.VISIBLE);
                viewHolder.tvMSG.setVisibility(View.VISIBLE);
                viewHolder.tvTime.setVisibility(View.VISIBLE);

                viewHolder.tvTitle.setText(mymsg.getMsgTitle());
                viewHolder.tvMSG.setText(mymsg.getMsgText());
                viewHolder.tvTime.setText(mymsg.getMsgTime());
            }
            return convertView;
        }

        class ViewHolder {
            TextView tvTitle;
            TextView tvMSG;
            TextView tvTime;
            TextView tvGetMore;
        }
    }
    private boolean checkitem(int position){
        boolean check;
        if(msgdate.size()==0){
            LOG.E("1true   "+position);
            check =true;
        }else if(position==msgdate.size()){
            LOG.E("2true   "+position);
            check =true;
        }else{
            LOG.E("3false   "+position);
            check=false;
        }
        return check;
    }
}
