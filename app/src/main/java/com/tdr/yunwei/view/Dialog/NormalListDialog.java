package com.tdr.yunwei.view.Dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tdr.yunwei.R;

import java.util.ArrayList;
import java.util.List;

public class NormalListDialog extends BaseDialog<NormalListDialog> {
    /**
     * ListView
     */
    private ListView mLv;
    /**
     * title
     */
    private TextView mTvTitle;
    /**
     * corner radius,dp(圆角程度,单位dp)
     */
    private float mCornerRadius = 5;
    /**
     * title background color(标题背景颜色)
     */
    private int mTitleBgColor = Color.parseColor("#303030");
    /**
     * title text(标题)
     */
    private String mTitle = "提示";
    /**
     * title textcolor(标题颜色)
     */
    private int mTitleTextColor = Color.parseColor("#ffffff");
    /**
     * title textsize(标题字体大小,单位sp)
     */
    private float mTitleTextSize = 16.5f;
    /**
     * ListView background color(ListView背景色)
     */
    private int mLvBgColor = Color.parseColor("#ffffff");
    /**
     * divider color(ListView divider颜色)
     */
    private int mDividerColor = Color.LTGRAY;
    /**
     * divider height(ListView divider高度)
     */
    private float mDividerHeight = 0.8f;
    /**
     * item press color(ListView item按住颜色)
     */
    private int mItemPressColor = Color.parseColor("#ffcccccc");
    /**
     * item textcolor(ListView item文字颜色)
     */
    private int mItemTextColor = Color.parseColor("#303030");
    /**
     * item textsize(ListView item文字大小)
     */
    private float mItemTextSize = 15f;
    /** item extra padding(ListView item额外padding) */

    /**
     * enable title show(是否显示标题)
     */
    private boolean mIsTitleShow = true;
    /**
     * adapter(自定义适配器)
     */
    private BaseAdapter mAdapter;
    /**
     * operation items(操作items)
     */
    private List<String> mContents;
    private LayoutAnimationController mLac;


    private TextView textView;
    private String posStr;
    private NLDListener Nldlistener;
    public interface NLDListener{
        void onSelect(String str);
    }
    public NormalListDialog(Context context, TextView textView,
                               List<String> baseItems, String title,NLDListener NLDL) {
        super(context);
        this.textView = textView;
        Nldlistener =NLDL;
        if (baseItems.size() > 0) {
            this.mContents = baseItems;
        } else {
            mContents = new ArrayList<>();
            mContents.add("查无数据");
        }
        this.posStr = textView.getText().toString();

        init(context);

        title(title);


        int color = context.getResources().getColor(R.color.title_bg);
        titleBgColor(color);
        int txtcolor = context.getResources().getColor(R.color.txt666);
        itemTextColor(txtcolor);

        show(R.style.mystyle);


    }

    public NormalListDialog(Context context, TextView textView,
                            List<String> baseItems, String title) {
        super(context);
        this.textView = textView;

        if (baseItems.size() > 0) {
            this.mContents = baseItems;
        } else {
            mContents = new ArrayList<>();
            mContents.add("查无数据");
        }
        this.posStr = textView.getText().toString();

        init(context);

        title(title);


        int color = context.getResources().getColor(R.color.title_bg);
        titleBgColor(color);
        int txtcolor = context.getResources().getColor(R.color.txt666);
        itemTextColor(txtcolor);

        show(R.style.mystyle);
    }


    private void init(Context context) {
        widthScale(0.8f);

        /** LayoutAnimation */
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0f,//正数 --从左到右，负数--从右到左
                Animation.RELATIVE_TO_PARENT, 0f,
                Animation.RELATIVE_TO_PARENT, -2f,//正数 --从下到上，负数--从上到下
                Animation.RELATIVE_TO_PARENT, 0f);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.setDuration(500);
        mLac = new LayoutAnimationController(animation, 0.2f);
        mLac.setInterpolator(new DecelerateInterpolator());

    }

    int padding10 = dp2px(10);

    @Override
    public View onCreateView() {


        LinearLayout ll_container = new LinearLayout(mContext);
        ll_container.setOrientation(LinearLayout.VERTICAL);
        ll_container.setPadding(padding10, padding10, padding10, padding10);

        /** title */
        mTvTitle = new TextView(mContext);
        mTvTitle.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        mTvTitle.setSingleLine(true);
        mTvTitle.setGravity(Gravity.CENTER);
        mTvTitle.setPadding(padding10, padding10, 0, padding10);

        ll_container.addView(mTvTitle);

        /** listview */
        mLv = new ListView(mContext);
        mLv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        mLv.setCacheColorHint(Color.TRANSPARENT);
        mLv.setFadingEdgeLength(0);
        mLv.setVerticalScrollBarEnabled(false);
        mLv.setSelector(new ColorDrawable(Color.TRANSPARENT));

        ll_container.addView(mLv);

        return ll_container;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setUiBeforShow() {
        /** title */
        float radius = dp2px(mCornerRadius);
        mTvTitle.setBackgroundDrawable(CornerUtils.cornerDrawable(
                mTitleBgColor, new float[]{radius, radius, radius,
                        radius, 0, 0, 0, 0}));
        mTvTitle.setText(mTitle);
        mTvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTitleTextSize);
        mTvTitle.setTextColor(mTitleTextColor);
        mTvTitle.setVisibility(mIsTitleShow ? View.VISIBLE : View.GONE);

        /** listview */
        mLv.setDivider(new ColorDrawable(mDividerColor));
        mLv.setDividerHeight(dp2px(mDividerHeight));

        if (mIsTitleShow) {
            mLv.setBackgroundDrawable(CornerUtils.cornerDrawable(mLvBgColor,
                    new float[]{0, 0, 0, 0, radius, radius, radius, radius}));
        } else {
            mLv.setBackgroundDrawable(CornerUtils.cornerDrawable(mLvBgColor, radius));
        }

        if (mAdapter == null) {
            mAdapter = new ListDialogAdapter();
        }

        mLv.setAdapter(mAdapter);
        mLv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String str = mContents.get(position);
                if (!str.equals("查无数据")) {
                    textView.setText(str);
                    posStr = str;
                    if(Nldlistener!=null){
                        Nldlistener.onSelect(str);
                    }
                } else {
                    textView.setText("");
                    posStr = "";
                    if(Nldlistener!=null) {
                        Nldlistener.onSelect("");
                    }
                }
                NormalListDialog.this.dismiss();
            }
        });

        mLv.setLayoutAnimation(mLac);
    }

    /**
     * set title background color(设置标题栏背景色) @return NormalListDialog
     */
    public void titleBgColor(int titleBgColor) {
        mTitleBgColor = titleBgColor;
    }

    /**
     * set title text(设置标题内容)
     */
    public void title(String title) {
        mTitle = title;
    }

    /**
     * set title textsize(设置标题字体大小)
     */
    public void titleTextSize_SP(float titleTextSize_SP) {
        mTitleTextSize = titleTextSize_SP;
    }

    /**
     * set title textcolor(设置标题字体颜色)
     */
    public void titleTextColor(int titleTextColor) {
        mTitleTextColor = titleTextColor;
    }

    /***
     * enable title show(设置标题是否显示)
     */
    public void isTitleShow(boolean isTitleShow) {
        mIsTitleShow = isTitleShow;
    }

    /**
     * set ListView background color(设置ListView背景)
     */
    public void lvBgColor(int lvBgColor) {
        mLvBgColor = lvBgColor;
    }

    /**
     * set corner radius(设置圆角程度,单位dp)
     */
    public void cornerRadius(float cornerRadius_DP) {
        mCornerRadius = cornerRadius_DP;
    }

    /**
     * set divider color(ListView divider颜色)
     */
    public void dividerColor(int dividerColor) {
        mDividerColor = dividerColor;
    }

    /**
     * set divider height(ListView divider高度)
     */
    public void dividerHeight(float dividerHeight_DP) {
        mDividerHeight = dividerHeight_DP;
    }

    /**
     * set item press color(item按住颜色)
     */
    public void itemPressColor(int itemPressColor) {
        mItemPressColor = itemPressColor;
    }

    /**
     * set item textcolor(item字体颜色)
     */
    public void itemTextColor(int itemTextColor) {
        mItemTextColor = itemTextColor;
    }

    /**
     * set item textsize(item字体大小)
     */
    public void itemTextSize(float itemTextSize_SP) {
        mItemTextSize = itemTextSize_SP;
    }


    /**
     * set layoutAnimation(设置layout动画 ,传入null将不显示layout动画)
     */
    public NormalListDialog layoutAnimation(LayoutAnimationController lac) {
        mLac = lac;
        return this;
    }

    class ListDialogAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mContents.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressWarnings("deprecation")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final String item = mContents.get(position);

            int left = dp2px(10);
            int top = dp2px(10);
            int right = dp2px(10);
            int bottom = dp2px(10);

            LinearLayout llItem = new LinearLayout(mContext);
            llItem.setOrientation(LinearLayout.HORIZONTAL);
            llItem.setGravity(Gravity.CENTER_VERTICAL);

            ImageView img = new ImageView(mContext);
            img.setLayoutParams(new LinearLayout.LayoutParams(left, left));
            img.setImageResource(R.mipmap.radiobox_om);


            llItem.addView(img);

            TextView tvItem = new TextView(mContext);
            tvItem.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            tvItem.setSingleLine(true);
            tvItem.setTextColor(mItemTextColor);
            tvItem.setTextSize(TypedValue.COMPLEX_UNIT_SP, mItemTextSize);
            tvItem.setPadding(left, 0, 0, 0);
            llItem.addView(tvItem);
            float radius = dp2px(mCornerRadius);
            if (mIsTitleShow) {
                llItem.setBackgroundDrawable((CornerUtils.listItemSelector(
                        radius, Color.TRANSPARENT, mItemPressColor,
                        position == mContents.size() - 1)));
            } else {
                llItem.setBackgroundDrawable(CornerUtils.listItemSelector(
                        radius, Color.TRANSPARENT, mItemPressColor,
                        mContents.size(), position));
            }


            llItem.setPadding(left, top, right, bottom);

            tvItem.setText(item);
            img.setVisibility(!item.equals(posStr) ? View.INVISIBLE : View.VISIBLE);

            return llItem;
        }
    }
}
